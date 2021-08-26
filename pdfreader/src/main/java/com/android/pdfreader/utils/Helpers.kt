package com.android.pdfreader.utils

import android.app.Activity
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import com.google.android.material.snackbar.Snackbar

internal fun getScreenWidth(activity: Activity) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = activity.windowManager.currentWindowMetrics
        windowMetrics.bounds.width()
    } else {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }

internal fun showSnackBar(view: View, message: String, anchorView: View? = null) =
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).apply {
        anchorView?.let { this.anchorView = it }
    }.show()