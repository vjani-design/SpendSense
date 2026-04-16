package com.example.spendsense.utils

import android.graphics.Bitmap
import android.view.View
import androidx.core.view.drawToBitmap

object ChartCaptureUtils {

    // ✅ REAL & WORKING capture (View-based)
    fun captureView(view: View): Bitmap {
        return view.drawToBitmap(Bitmap.Config.ARGB_8888)
    }
}