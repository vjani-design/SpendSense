package com.example.spendsense.utils

import android.graphics.Bitmap
import android.view.View

object ChartUtils {

    fun captureView(view: View): Bitmap? {
        return try {
            val bitmap = Bitmap.createBitmap(
                view.width,
                view.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(bitmap)
            view.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}