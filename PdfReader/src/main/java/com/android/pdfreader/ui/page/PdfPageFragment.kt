package com.android.pdfreader.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.android.pdfreader.databinding.FragmentPdfPageBinding

internal class PdfPageFragment : Fragment() {

    private var _binding: FragmentPdfPageBinding? = null
    private val binding get() = _binding!!
    private val argument: PdfPageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPdfPageBinding.inflate(inflater, container, false)

        binding.page.setImageBitmap(argument.bitmap)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}