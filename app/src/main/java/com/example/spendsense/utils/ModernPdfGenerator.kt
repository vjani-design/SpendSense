package com.example.spendsense.utils

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore

data class Transaction(
    val type: String,
    val category: String,
    val amount: Double
)

fun generateModernReport(
    context: Context,
    userEmail: String,
    income: Double,
    expense: Double,
    budget: Double,
    transactions: List<Transaction>
) {

    val pdf = PdfDocument()
    val page = pdf.startPage(
        PdfDocument.PageInfo.Builder(1200, 1800, 1).create()
    )
    val canvas = page.canvas

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    val primary = Color.parseColor("#4F46E5")
    val green = Color.parseColor("#16A34A")
    val red = Color.parseColor("#DC2626")
    val blue = Color.parseColor("#2563EB")

    canvas.drawColor(Color.WHITE)

    // HEADER
    paint.color = primary
    canvas.drawRect(0f, 0f, 1200f, 180f, paint)

    paint.color = Color.WHITE
    paint.textSize = 50f
    paint.isFakeBoldText = true
    canvas.drawText("SpendSense Financial Report", 50f, 100f, paint)

    paint.textSize = 28f
    paint.isFakeBoldText = false
    canvas.drawText("User: $userEmail", 50f, 150f, paint)

    var y = 240f

    // BALANCE
    val balance = income - expense
    drawCard(canvas, 50f, y, 1150f, y + 140f, blue)

    paint.color = Color.WHITE
    paint.textSize = 32f
    canvas.drawText("Balance", 80f, y + 50f, paint)

    paint.textSize = 48f
    paint.isFakeBoldText = true
    canvas.drawText("₹${balance.toInt()}", 80f, y + 110f, paint)

    y += 180f

    // STATS
    drawStatBox(canvas, 50f, y, "Income", income, green)
    drawStatBox(canvas, 400f, y, "Expense", expense, red)
    drawStatBox(canvas, 750f, y, "Budget", budget, blue)

    y += 170f

    // BAR CHART
    paint.color = Color.BLACK
    paint.textSize = 40f
    paint.isFakeBoldText = true
    canvas.drawText("Income vs Expense", 50f, y, paint)

    y += 50f

    val maxVal = maxOf(income, expense).takeIf { it > 0 } ?: 1.0
    val chartHeight = 300f

    val incomeHeight = (income / maxVal * chartHeight).toFloat()
    val expenseHeight = (expense / maxVal * chartHeight).toFloat()

    paint.color = green
    canvas.drawRect(150f, y + (chartHeight - incomeHeight), 300f, y + chartHeight, paint)

    paint.color = red
    canvas.drawRect(400f, y + (chartHeight - expenseHeight), 550f, y + chartHeight, paint)

    paint.color = Color.BLACK
    paint.textSize = 28f
    canvas.drawText("Income", 150f, y + chartHeight + 40f, paint)
    canvas.drawText("Expense", 400f, y + chartHeight + 40f, paint)

    y += chartHeight + 120f

    // PIE CHART
    paint.textSize = 40f
    paint.isFakeBoldText = true
    canvas.drawText("Spending Breakdown", 50f, y, paint)

    y += 50f

    drawPieChart(canvas, 350f, y + 180f, 140f, income, expense)

    paint.textSize = 28f
    paint.isFakeBoldText = false

    paint.color = green
    canvas.drawText("Income", 550f, y + 140f, paint)

    paint.color = red
    canvas.drawText("Expense", 550f, y + 200f, paint)

    y += 350f

    // SUMMARY
    paint.color = Color.BLACK
    paint.textSize = 40f
    paint.isFakeBoldText = true
    canvas.drawText("Summary & Insights", 50f, y, paint)

    y += 60f

    paint.textSize = 28f
    paint.isFakeBoldText = false

    val percent = if (income > 0) (expense / income) * 100 else 0.0

    canvas.drawText("• Earned ₹${income.toInt()}", 70f, y, paint)
    y += 40f
    canvas.drawText("• Spent ₹${expense.toInt()} (${percent.toInt()}%)", 70f, y, paint)
    y += 40f
    canvas.drawText("• Budget status: OK", 70f, y, paint)

    y += 80f

    // TABLE
    paint.textSize = 40f
    paint.isFakeBoldText = true
    canvas.drawText("Transactions", 50f, y, paint)

    y += 60f

    val colTypeX = 80f
    val colCategoryX = 350f
    val colAmountX = 850f

    paint.color = primary
    canvas.drawRect(50f, y, 1150f, y + 60f, paint)

    paint.color = Color.WHITE
    paint.textSize = 30f
    canvas.drawText("Type", colTypeX, y + 40f, paint)
    canvas.drawText("Category", colCategoryX, y + 40f, paint)
    canvas.drawText("Amount", colAmountX, y + 40f, paint)

    y += 70f

    paint.color = Color.BLACK
    paint.textSize = 28f
    paint.isFakeBoldText = false

    transactions.forEach {
        val category = if (it.category.length > 12)
            it.category.take(12) + "..."
        else it.category

        canvas.drawText(it.type, colTypeX, y, paint)
        canvas.drawText(category, colCategoryX, y, paint)
        canvas.drawText("₹${it.amount.toInt()}", colAmountX, y, paint)

        y += 50f
    }

    pdf.finishPage(page)

    // SAVE TO DOWNLOADS
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        val values = android.content.ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "SpendSense_Report.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            val os = context.contentResolver.openOutputStream(it)
            pdf.writeTo(os)
            os?.close()
        }

    } else {
        val file = java.io.File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "SpendSense_Report.pdf"
        )
        val fos = java.io.FileOutputStream(file)
        pdf.writeTo(fos)
        fos.close()
    }

    pdf.close()
}
fun drawCard(canvas: Canvas, l: Float, t: Float, r: Float, b: Float, color: Int) {
    val p = Paint(Paint.ANTI_ALIAS_FLAG)
    p.color = color
    canvas.drawRoundRect(l, t, r, b, 25f, 25f, p)
}

fun drawStatBox(canvas: Canvas, x: Float, y: Float, title: String, value: Double, color: Int) {
    val p = Paint(Paint.ANTI_ALIAS_FLAG)
    p.color = color
    canvas.drawRoundRect(x, y, x + 300f, y + 120f, 25f, 25f, p)

    p.color = Color.WHITE
    p.textSize = 26f
    canvas.drawText(title, x + 20f, y + 40f, p)

    p.textSize = 36f
    p.isFakeBoldText = true
    canvas.drawText("₹${value.toInt()}", x + 20f, y + 90f, p)
}

fun drawPieChart(canvas: Canvas, cx: Float, cy: Float, r: Float, income: Double, expense: Double) {
    val p = Paint(Paint.ANTI_ALIAS_FLAG)
    val total = income + expense
    if (total == 0.0) return

    val angle = (income / total * 360).toFloat()
    val rect = RectF(cx - r, cy - r, cx + r, cy + r)

    p.color = Color.parseColor("#16A34A")
    canvas.drawArc(rect, 0f, angle, true, p)

    p.color = Color.parseColor("#DC2626")
    canvas.drawArc(rect, angle, 360f - angle, true, p)
}