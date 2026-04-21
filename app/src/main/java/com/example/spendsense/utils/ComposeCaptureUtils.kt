package com.example.spendsense.utils

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

object ComposeCaptureUtils {

    fun captureComposable(
        context: Context,
        composable: @Composable () -> Unit
    ): Bitmap {

        val composeView = ComposeView(context).apply {
            setContent {
                composable()
            }
        }

        // 🔥 Measure & layout
        composeView.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.AT_MOST)
        )

        composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)

        // 🔥 Create bitmap
        val bitmap = Bitmap.createBitmap(
            composeView.measuredWidth,
            composeView.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = android.graphics.Canvas(bitmap)
        composeView.draw(canvas)

        return bitmap
    }
}