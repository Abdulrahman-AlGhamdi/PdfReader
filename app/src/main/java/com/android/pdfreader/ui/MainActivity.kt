package com.android.pdfreader.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.pdfreader.R
import com.android.pdfreader.manager.PdfReaderManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PdfReaderManager.getManagerInstance().showPdf(this, assets.open("sample_pdf.pdf"))
    }
}