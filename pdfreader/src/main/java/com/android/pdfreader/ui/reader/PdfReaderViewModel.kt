package com.android.pdfreader.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.pdfreader.repository.PdfReaderRepository
import com.android.pdfreader.repository.PdfReaderRepositoryImpl.PdfStatus
import com.android.pdfreader.repository.PdfReaderRepositoryImpl.PdfStatus.Idle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.InputStream

internal class PdfReaderViewModel(
    private val pdfReaderRepository: PdfReaderRepository
) : ViewModel() {

    private var _pdfStatus: MutableStateFlow<PdfStatus> = MutableStateFlow(Idle)
    val pdfStatus get() = _pdfStatus

    fun getPdfStatus(inputStream: InputStream) = viewModelScope.launch {
        pdfReaderRepository.openRenderer(inputStream).collect {
            _pdfStatus.value = it
        }
    }
}