package com.android.pdfreader.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.pdfreader.repository.PdfReaderRepositoryImpl

internal class PdfReaderProviderFactory(
    private val pdfReaderRepositoryImpl: PdfReaderRepositoryImpl
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PdfReaderViewModel(pdfReaderRepositoryImpl) as T
    }
}