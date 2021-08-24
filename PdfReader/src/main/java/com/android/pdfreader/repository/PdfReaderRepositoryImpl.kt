package com.android.pdfreader.repository

import android.app.Activity
import android.graphics.Bitmap
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
import java.io.InputStream

internal class PdfReaderRepositoryImpl(
    private val activity: Activity
) : PdfReaderRepository {

    override fun openRenderer(fileInputStream: InputStream) = flow {
        val file   = java.io.File(activity.cacheDir, "file.pdf")
        val output = java.io.FileOutputStream(file)
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var size   = fileInputStream.read(buffer)

        while (size != -1) {
            output.write(buffer, 0, size)
            size = fileInputStream.read(buffer)
        }

        fileInputStream.close()
        output.close()

        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
        val pdfRenderer    = PdfRenderer(fileDescriptor)
        val width          = getScreenWidth(activity)
        val bitmapList     = mutableListOf<Bitmap>()

        for (x in 0 until pdfRenderer.pageCount) {
            val page       = pdfRenderer.openPage(x)
            val percentage = ((x.toDouble() / pdfRenderer.pageCount) * 100).toInt()

            val bitmap = Bitmap.createBitmap(
                width,
                (width.toFloat() / page.width * page.height).toInt(),
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            bitmapList.add(bitmap)
            page.close()

            this.emit(Prepared(percentage))
        }

        this.emit(Complete(bitmapList))

    }.flowOn(Dispatchers.IO)

    sealed class PdfStatus {
        object Idle : PdfStatus()
        data class Prepared(val progress: Int) : PdfStatus()
        data class Complete(val bitmapList: List<Bitmap>) : PdfStatus()
    }
}