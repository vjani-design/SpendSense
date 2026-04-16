package com.example.spendsense.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.spendsense.model.Transaction
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import java.io.ByteArrayOutputStream

object PdfReportGenerator {

    fun generateReport(
        context: Context,
        transactions: List<Transaction>,
        income: Double,
        expense: Double,
        balance: Double,
        budget: Double,
        mostSpent: String,
        pieBitmap: Bitmap?,
        barBitmap: Bitmap?
    ): Uri? {

        return try {

            val resolver = context.contentResolver

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "SpendSense_Report_${System.currentTimeMillis()}.pdf")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)

                // 🔥 REQUIRED (Android 10+)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            val uri: Uri?

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

                // ✅ Android 10+
                uri = resolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    contentValues
                )

            } else {

                // ✅ Below Android 10
                val file = java.io.File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "SpendSense_Report_${System.currentTimeMillis()}.pdf"
                )

                uri = Uri.fromFile(file)
            }

            if (uri == null) {
                Log.e("PDF", "❌ URI is null")
                return null
            }

            val outputStream = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                resolver.openOutputStream(uri!!)
            } else {
                java.io.FileOutputStream(java.io.File(uri!!.path!!))
            }

            if (outputStream == null) {
                Log.e("PDF", "❌ OutputStream is null")
                return null
            }

            val writer = PdfWriter(outputStream)
            val pdf = PdfDocument(writer)
            val document = Document(pdf)

            // ================= HEADER =================
            document.add(Paragraph("SPENDSENSE FINANCIAL REPORT\n"))
            document.add(Paragraph("------------------------------\n"))

            // ================= SUMMARY =================
            document.add(Paragraph("Income: ₹$income"))
            document.add(Paragraph("Expense: ₹$expense"))
            document.add(Paragraph("Balance: ₹$balance"))
            document.add(Paragraph("Budget: ₹$budget"))
            document.add(Paragraph("Most Spent: $mostSpent"))
            document.add(Paragraph("Transactions: ${transactions.size}\n"))

            // ================= PIE CHART =================
            pieBitmap?.let { bmp ->
                if (bmp.width > 0 && bmp.height > 0) {
                    try {
                        val stream = ByteArrayOutputStream()
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)

                        val imageData = ImageDataFactory.create(stream.toByteArray())
                        val image = Image(imageData)

                        document.add(Paragraph("Income vs Expense\n"))
                        image.scaleToFit(500f, 500f)
                        document.add(image)

                    } catch (e: Exception) {
                        Log.e("PDF", "Pie chart error: ${e.message}")
                    }
                }
            }

            // ================= BAR CHART =================
            barBitmap?.let { bmp ->
                if (bmp.width > 0 && bmp.height > 0) {
                    try {
                        val stream = ByteArrayOutputStream()
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)

                        val imageData = ImageDataFactory.create(stream.toByteArray())
                        val image = Image(imageData)

                        document.add(Paragraph("\nMonthly Trend\n"))
                        image.scaleToFit(500f, 500f)
                        document.add(image)

                    } catch (e: Exception) {
                        Log.e("PDF", "Bar chart error: ${e.message}")
                    }
                }
            }

            // ================= TRANSACTIONS =================
            document.add(Paragraph("\nTRANSACTIONS\n"))

            transactions.forEach {
                document.add(
                    Paragraph("${it.type} | ${it.category} | ₹${it.amount}")
                )
            }

            // ================= CLOSE =================
            document.close()
            outputStream.close()

            // 🔥 VERY IMPORTANT: MARK FILE AS COMPLETE
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            Log.d("PDF", "✅ Saved successfully: $uri")

            uri

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PDF", "❌ Error: ${e.message}")
            null
        }
    }
}