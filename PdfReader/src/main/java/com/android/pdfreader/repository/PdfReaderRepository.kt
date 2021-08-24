package com.android.pdfreader.repository

import com.android.pdfreader.repository.PdfReaderRepositoryImpl.PdfStatus
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

internal interface PdfReaderRepository {

    fun openRenderer(fileInputStream: InputStream): Flow<PdfStatus>
}