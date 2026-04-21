package com.example.spendsense.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

fun saveBitmapAsPdf(context: Context, bitmap: Bitmap) {

    val pdf = PdfDocument()

    val pageInfo = PdfDocument.PageInfo.Builder(
        bitmap.width,
        bitmap.height,
        1
    ).create()

    val page = pdf.startPage(pageInfo)
    page.canvas.drawBitmap(bitmap, 0f, 0f, null)
    pdf.finishPage(page)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "SpendSense_Report.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            values
        )

        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { os ->
                pdf.writeTo(os)
            }
        }

    } else {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "SpendSense_Report.pdf"
        )
        FileOutputStream(file).use { fos ->
            pdf.writeTo(fos)
        }
    }

    pdf.close()
}