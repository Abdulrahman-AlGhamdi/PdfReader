package com.android.pdfreader.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.pdfreader.databinding.ActivityPdfReaderBinding
import com.android.pdfreader.manager.PdfReaderManager
import com.android.pdfreader.repository.PdfReaderRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class PdfReaderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfReaderBinding
    private lateinit var viewModel: PdfReaderViewModel
    private lateinit var adapter: PdfAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        val factory = PdfReaderProviderFactory(PdfReaderRepositoryImpl(this))
        viewModel   = ViewModelProvider(this, factory)[PdfReaderViewModel::class.java]

        val layoutManager  = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
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
                    binding.progressBar.progress = it.progress
                }
                is PdfReaderRepositoryImpl.PdfStatus.Complete -> {
                    binding.progressBar.visibility = View.GONE
                    binding.pdfList.visibility = View.VISIBLE
                    adapter = PdfAdapter(it.bitmapList)
                    withContext(Dispatchers.Main) { binding.pdfList.adapter = adapter }
                }
            }
        }
    }
}