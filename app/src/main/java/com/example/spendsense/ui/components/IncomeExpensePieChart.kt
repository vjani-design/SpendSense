package com.example.spendsense.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter

@Composable
fun IncomeExpensePieChart(income: Float, expense: Float) {

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->

            val chart = PieChart(context)

            val entries = listOf(
                PieEntry(income, "Income"),
                PieEntry(expense, "Expense")
            )

            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(
                    Color.parseColor("#4CAF50"), // Green
                    Color.parseColor("#F44336")  // Red
                )

                valueTextSize = 14f
                valueTextColor = Color.WHITE

                // ✅ Better spacing for visibility
                sliceSpace = 3f
                valueLinePart1Length = 0.4f
                valueLinePart2Length = 0.4f
            }

            val data = PieData(dataSet)

            // ✅ SHOW %
            data.setValueFormatter(PercentFormatter(chart))
            chart.setUsePercentValues(true)

            chart.data = data

            // ✅ REMOVE WHITE CENTER (MAKE FULL PIE)
            chart.isDrawHoleEnabled = false

            // ✅ FIX LABEL VISIBILITY
            chart.setEntryLabelColor(Color.WHITE)
            chart.setEntryLabelTextSize(12f)

            // ✅ FIX LEGEND TEXT COLOR (BOTTOM LEFT)
            chart.legend.textColor = Color.WHITE

            // ✅ REMOVE DESCRIPTION
            chart.description.isEnabled = false

            // ✅ KEEP ROTATION (NO CHANGE)
            chart.animateY(1000)

            chart.invalidate()

            chart
        }
    )
}