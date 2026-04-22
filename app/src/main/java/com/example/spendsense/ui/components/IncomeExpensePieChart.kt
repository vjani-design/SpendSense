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
import com.example.spendsense.ui.theme.ThemeManager   // ✅ ADD THIS IMPORT
import com.example.spendsense.model.Transaction

@Composable
fun IncomeExpensePieChart(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
    onChartReady: (PieChart) -> Unit = {}
) {
    val isDark = ThemeManager.isDarkTheme

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),

        factory = { context ->

            val chart = PieChart(context)
            onChartReady(chart)

            // ✅ FIXED GROUPING
            val categoryMap = transactions
                .groupBy { tx ->
                    if (tx.type.equals("INCOME", ignoreCase = true)) {
                        "Income - ${tx.description}"
                    } else {
                        "Expense - ${tx.category}"
                    }
                }
                .mapValues { entry ->
                    entry.value.sumOf { it.amount }.toFloat()
                }

            val entries = categoryMap.map {
                PieEntry(it.value, it.key)
            }

            // ✅ COLORS (CORRECT LOGIC)
            val colors = entries.map { entry ->
                if (entry.label.startsWith("Income", ignoreCase = true))
                    Color.parseColor("#4CAF50")   // GREEN
                else
                    Color.parseColor("#F44336")   // RED
            }

            val dataSet = PieDataSet(entries, "").apply {
                this.colors = colors
                valueTextSize = 14f
                valueTextColor = if (isDark) Color.WHITE else Color.BLACK
                sliceSpace = 2f
            }

            val data = PieData(dataSet)
            data.setValueFormatter(PercentFormatter(chart))

            chart.setUsePercentValues(true)
            chart.data = data
            chart.isDrawHoleEnabled = false

            // 🚫 Disable labels inside pie
            chart.setDrawEntryLabels(false)

// (you already have percent enabled 👍)
            chart.setUsePercentValues(true)

// ✅ Move labels to legend BELOW
            val legend = chart.legend
            legend.isEnabled = true
            legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
            legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
            legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
            legend.setDrawInside(false)
            legend.isWordWrapEnabled = true

            legend.textColor = if (isDark) Color.WHITE else Color.BLACK
            legend.textSize = 12f

            chart.legend.isEnabled = true
            chart.legend.textColor =
                if (isDark) Color.WHITE else Color.BLACK

            chart.description.isEnabled = false

            chart.animateY(800)
            chart.invalidate()

            chart
        }
    )
}