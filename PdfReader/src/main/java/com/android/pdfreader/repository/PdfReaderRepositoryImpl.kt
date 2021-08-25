package com.android.pdfreader.repository

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import com.android.pdfreader.repository.PdfReaderRepositoryImpl.PdfStatus.Complete
import com.android.pdfreader.repository.PdfReaderRepositoryImpl.PdfStatus.Prepared
import com.android.pdfreader.utils.getScreenWidth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

internal class PdfReaderRepositoryImpl(
    private val activity: Activity
) : PdfReaderRepository {

    private lateinit var bitmapList: MutableList<Bitmap>

    override fun openRenderer(fileInputStream: InputStream) = flow {
        val file           = createCacheFile(fileInputStream)
        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
        val pdfRenderer    = PdfRenderer(fileDescriptor)

        if (!::bitmapList.isInitialized) {
            bitmapList = mutableListOf()
            for (x in 0 until pdfRenderer.pageCount)
                this.emit(Prepared(openPdfPages(pdfRenderer, x, bitmapList)))
            this.emit(Complete(bitmapList))
        } else this.emit(Complete(bitmapList))

    }.flowOn(Dispatchers.IO)

    private fun createCacheFile(inputStream: InputStream): File {
        val file   = File(activity.cacheDir, "file.pdf")

        return if (!file.exists()) {
            val output = FileOutputStream(file)
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var size   = inputStream.read(buffer)

            while (size != -1) {
                output.write(buffer, 0, size)
                size = inputStream.read(buffer)
            }

            inputStream.close()
            output.flush()
            output.close()
            file
        } else file
    }

    private fun openPdfPages(pdfRenderer: PdfRenderer, x: Int, bitmapList: MutableList<Bitmap>): Int {
        val width      = getScreenWidth(activity)
        val page       = pdfRenderer.openPage(x)
        val percentage = ((x.toDouble() / pdfRenderer.pageCount) * 100).toInt()
        val bitmap     = createBitmap(width, (width.toFloat() / page.width * page.height).toInt(), ARGB_8888)
        val canvas     = Canvas(bitmap)

        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        bitmapList.add(bitmap)
        page.close()

        return percentage
    }

    sealed class PdfStatus {
        object Idle : PdfStatus()
        data class Prepared(val progress: Int) : PdfStatus()
        data class Complete(val bitmapList: List<Bitmap>) : PdfStatus()
    }
}