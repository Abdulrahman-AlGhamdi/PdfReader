package com.android.pdfreader.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.pdfreader.databinding.ActivityMainBinding
import com.android.pdfreader.utils.getScreenWidth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PdfAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        val layoutManager  = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.pdfList.layoutManager = layoutManager
        binding.pdfList.addItemDecoration(itemDecoration)
        openRenderer("sample_pdf", assets.open("sample_pdf.pdf"))
    }

    private fun openRenderer(
        fileName: String,
        fileInputStream: InputStream
    ) = lifecycleScope.launch(Dispatchers.IO) {

        val file        = File(cacheDir, "$fileName.pdf")
        val output      = FileOutputStream(file)
        val buffer      = ByteArray(DEFAULT_BUFFER_SIZE)
        var size        = fileInputStream.read(buffer)

        while (size != -1) {
            output.write(buffer, 0, size)
            size = fileInputStream.read(buffer)
        }

        fileInputStream.close()
        output.close()

        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
        val pdfRenderer    = PdfRenderer(fileDescriptor)
        val width          = getScreenWidth(this@MainActivity)
        val bitmapList     = mutableListOf<Bitmap>()

        for (x in 0 until pdfRenderer.pageCount) {
            val page   = pdfRenderer.openPage(x)
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
        }

        adapter = PdfAdapter(bitmapList)
        withContext(Dispatchers.Main) { binding.pdfList.adapter = adapter }
    }
}