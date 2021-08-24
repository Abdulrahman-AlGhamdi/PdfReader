package com.android.pdfreader.manager

import android.app.Activity
import android.content.Intent
import com.android.pdfreader.ui.PdfReaderActivity
import java.io.InputStream

class PdfReaderManager {

    internal lateinit var inputStream: InputStream

    fun showPdf(activity: Activity, inputStream: InputStream) {
        this.inputStream = inputStream
        activity.startActivity(Intent(activity, PdfReaderActivity::class.java))
    }

    companion object {
        private lateinit var manager: PdfReaderManager

        fun getManagerInstance(): PdfReaderManager {
            return if (::manager.isInitialized) manager
            else {
                manager = PdfReaderManager()
                manager
            }
        }
    }
}