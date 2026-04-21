package com.example.spendsense.utils

import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import com.example.spendsense.model.Transaction
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.*
import com.itextpdf.kernel.pdf.*
import com.itextpdf.layout.*
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.*
import com.itextpdf.layout.borders.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

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
        barBitmap: Bitmap?,
        userEmail: String
    ): Uri? {

        val resolver = context.contentResolver
        val fileName = "SpendSense_Report_${System.currentTimeMillis()}.pdf"

        val outputStream: OutputStream
        val uri: Uri?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: return null

            outputStream = resolver.openOutputStream(uri) ?: return null
        } else {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            outputStream = FileOutputStream(file)
            uri = Uri.fromFile(file)
        }

        val pdf = PdfDocument(PdfWriter(outputStream))
        val document = Document(pdf)

        // COLORS
        val blue = DeviceRgb(30, 70, 120)
        val border = DeviceRgb(220, 220, 220)
        val green = DeviceRgb(56, 142, 60)
        val red = DeviceRgb(211, 47, 47)
        val grayText = DeviceRgb(120, 120, 120)

        val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        // ================= HEADER =================
        document.add(
            Paragraph("SpendSense Financial Report")
                .setFontSize(26f)
                .setBold()
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(blue)
                .setPaddingTop(15f)
                .setPaddingBottom(15f)
                .setTextAlignment(TextAlignment.CENTER)
        )

        document.add(
            Paragraph("Your finances. Your future. Our insight.")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12f)
                .setFontColor(grayText)
        )

        document.add(Paragraph("\n"))

        // ================= USER ROW =================
        val userTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
            .useAllAvailableWidth()

        userTable.addCell(cell("User: $userEmail", grayText))
        userTable.addCell(cell("Generated on: $date", grayText, TextAlignment.RIGHT))

        document.add(userTable)
        document.add(Paragraph("\n"))

        // ================= SUMMARY CARDS =================
        val summary = Table(UnitValue.createPercentArray(4)).useAllAvailableWidth()

        fun card(title: String, value: String, color: DeviceRgb): Cell {
            return Cell()
                .setPadding(12f)
                .setBorder(SolidBorder(border, 0.5f))
                .add(Paragraph(title).setFontSize(12f).setFontColor(grayText))
                .add(
                    Paragraph(value)
                        .setFontSize(20f)
                        .setBold()
                        .setFontColor(color)
                )
        }

        summary.addCell(card("Balance", "₹$balance", blue))
        summary.addCell(card("Income", "₹$income", green))
        summary.addCell(card("Expense", "₹$expense", red))
        summary.addCell(card("Budget", "₹$budget", blue))

        document.add(summary)
        document.add(Paragraph("\n"))

        // ================= CHARTS =================
        val chartTable = Table(UnitValue.createPercentArray(2)).useAllAvailableWidth()

        fun centeredChart(bitmap: Bitmap?): Cell {
            val stream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)

            val image = Image(ImageDataFactory.create(stream.toByteArray()))
                .scaleToFit(300f, 230f)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)

            return Cell()
                .setPadding(10f)
                .setBorder(SolidBorder(border, 0.5f))
                .add(image)
        }

        chartTable.addCell(centeredChart(pieBitmap))
        chartTable.addCell(centeredChart(barBitmap))

        document.add(chartTable)
        document.add(Paragraph("\n"))

        // ================= INSIGHTS =================
        val percent = if (income != 0.0) ((expense / income) * 100).toInt() else 0

        document.add(
            Div()
                .setBorder(SolidBorder(border, 0.5f))
                .setPadding(12f)
                .add(Paragraph("Insights").setBold().setFontSize(16f))
                .add(Paragraph("• You spent ₹$expense").setFontSize(13f))
                .add(Paragraph("• Highest spending: $mostSpent").setFontSize(13f))
                .add(Paragraph("• Expenses = $percent% of income").setFontSize(13f))
        )

        document.add(Paragraph("\n"))

        // ================= TRANSACTIONS =================
        document.add(Paragraph("Transactions").setBold().setFontSize(16f))

        val table = Table(UnitValue.createPercentArray(floatArrayOf(2f, 3f, 2f, 2f)))
            .useAllAvailableWidth()

        fun header(text: String): Cell {
            return Cell()
                .setBackgroundColor(blue)
                .add(
                    Paragraph(text)
                        .setBold()
                        .setFontSize(14f)
                        .setFontColor(ColorConstants.WHITE)
                        .setTextAlignment(TextAlignment.CENTER)
                )
        }

        table.addHeaderCell(header("Type"))
        table.addHeaderCell(header("Category"))
        table.addHeaderCell(header("Amount"))
        table.addHeaderCell(header("Date"))

        transactions.forEachIndexed { i, t ->

            val bg = if (i % 2 == 0) DeviceRgb(250, 250, 250) else DeviceRgb(240, 240, 240)
            val color = if (t.type == "income") green else red

            table.addCell(
                Cell().setBackgroundColor(bg).add(
                    Paragraph(t.type)
                        .setFontColor(color)
                        .setTextAlignment(TextAlignment.CENTER)
                )
            )

            table.addCell(
                Cell().setBackgroundColor(bg).add(
                    Paragraph(t.category)
                        .setTextAlignment(TextAlignment.CENTER)
                )
            )

            table.addCell(
                Cell().setBackgroundColor(bg).add(
                    Paragraph("₹${t.amount}")
                        .setFontColor(color)
                        .setTextAlignment(TextAlignment.CENTER)
                )
            )

            table.addCell(
                Cell().setBackgroundColor(bg).add(
                    Paragraph(date)
                        .setTextAlignment(TextAlignment.CENTER)
                )
            )
        }

        document.add(table)

        document.add(Paragraph("\n"))

        // ================= SUMMARY (LAST) =================
        document.add(
            Div()
                .setBorder(SolidBorder(border, 0.5f))
                .setPadding(12f)
                .add(Paragraph("Summary").setBold().setFontSize(16f))
                .add(
                    Paragraph(
                        if (balance >= 0)
                            "Your spending is under control and financially healthy."
                        else
                            "Your expenses exceeded income."
                    ).setFontSize(13f)
                )
        )

        document.add(Paragraph("\n"))

        // ================= FOOTER =================
        document.add(
            Paragraph("Generated by SpendSense • Stay financially smart")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(grayText)
                .setFontSize(11f)
        )

        document.close()
        outputStream.close()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.IS_PENDING, 0)
            }
            resolver.update(uri!!, values, null, null)
        }

        return uri
    }

    private fun cell(text: String, color: DeviceRgb, align: TextAlignment = TextAlignment.LEFT): Cell {
        return Cell()
            .setBorder(Border.NO_BORDER)
            .add(
                Paragraph(text)
                    .setFontColor(color)
                    .setFontSize(11f)
                    .setTextAlignment(align)
            )
    }
}