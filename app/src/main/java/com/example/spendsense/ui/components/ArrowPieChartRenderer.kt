package com.example.spendsense.ui.components

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.renderer.PieChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.cos
import kotlin.math.sin

class ArrowPieChartRenderer(
    chart: PieChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : PieChartRenderer(chart, animator, viewPortHandler) {

    private val linePaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 3f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 30f   // slightly reduced for safety
        isAntiAlias = true
    }

    override fun drawValues(c: Canvas) {

        val center = mChart.centerCircleBox
        val radius = mChart.radius
        val data = mChart.data ?: return
        val dataSet = data.getDataSet()

        val total = data.yValueSum

        var angle = mChart.rotationAngle

        for (i in 0 until dataSet.entryCount) {

            val entry = dataSet.getEntryForIndex(i) as PieEntry
            val value = entry.y

            val sliceAngle = value / total * 360f
            val angleMiddle = angle + sliceAngle / 2f

            val radians = Math.toRadians(angleMiddle.toDouble())

            // point on pie edge
            val x = center.x + radius * cos(radians).toFloat()
            val y = center.y + radius * sin(radians).toFloat()

            // shorter line (fix overflow)
            val lineLength = 50f
            val endX = x + lineLength * cos(radians).toFloat()
            val endY = y + lineLength * sin(radians).toFloat()

            // shorter horizontal offset
            val horizontalOffset = if (endX > center.x) 80f else -80f
            val finalX = endX + horizontalOffset

            // draw lines
            c.drawLine(x, y, endX, endY, linePaint)
            c.drawLine(endX, endY, finalX, endY, linePaint)

            // label
            val percent = (value / total) * 100f
            val label = "${entry.label} (${String.format("%.1f", percent)}%)"
            val textWidth = textPaint.measureText(label)

            // screen boundaries
            val screenRight = mViewPortHandler.contentRight()

            val textX = if (endX > center.x) {
                val xPos = finalX + 10f
                if (xPos + textWidth > screenRight)
                    screenRight - textWidth - 10f
                else xPos
            } else {
                val xPos = finalX - textWidth - 10f
                if (xPos < 0f) 10f else xPos
            }

            c.drawText(label, textX, endY, textPaint)

            angle += sliceAngle
        }
    }
}