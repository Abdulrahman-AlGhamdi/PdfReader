package com.android.pdfreader.ui.reader

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.pdfreader.databinding.FragmentPdfReaderBinding
import com.android.pdfreader.manager.PdfReaderManager
import com.android.pdfreader.repository.PdfReaderRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PdfReaderFragment : Fragment() {

    private var _binding: FragmentPdfReaderBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PdfReaderViewModel
    private lateinit var readerAdapter: PdfReaderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPdfReaderBinding.inflate(inflater, container, false)

        init()

        return binding.root
    }

    private fun init() {
        val factory = PdfReaderProviderFactory(PdfReaderRepositoryImpl(requireActivity()))
        viewModel   = ViewModelProvider(viewModelStore, factory)[PdfReaderViewModel::class.java]

        val layoutManager  = LinearLayoutManager(requireContext())
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.pdfList.layoutManager = layoutManager
        binding.pdfList.addItemDecoration(itemDecoration)

        getPdfStatus()
        viewModel.getPdfStatus(PdfReaderManager.getManagerInstance().inputStream)
    }

    private fun getPdfStatus() = lifecycleScope.launch(Dispatchers.Main) {
        viewModel.pdfStatus.collect {
            when (it) {
                is PdfReaderRepositoryImpl.PdfStatus.Prepared -> {
                    binding.pdfList.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        binding.progressBar.setProgress(it.progress, true)
                    else binding.progressBar.progress = it.progress
                }
                is PdfReaderRepositoryImpl.PdfStatus.Complete -> {
                    binding.progressBar.visibility = View.GONE
                    binding.pdfList.visibility = View.VISIBLE
                    readerAdapter = PdfReaderAdapter(it.bitmapList)
                    withContext(Dispatchers.Main) { binding.pdfList.adapter = readerAdapter }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}