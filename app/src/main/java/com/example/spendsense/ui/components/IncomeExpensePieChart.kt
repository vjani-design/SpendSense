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

@Composable
fun IncomeExpensePieChart(
    income: Float,
    expense: Float,
    modifier: Modifier = Modifier,
    onChartReady: (PieChart) -> Unit = {}
){
    val isDark = ThemeManager.isDarkTheme   // ✅ FIXED

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->

            val chart = PieChart(context)

            onChartReady(chart)

            val entries = listOf(
                PieEntry(income, "Income"),
                PieEntry(expense, "Expense")
            )

            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(
                    Color.parseColor("#4CAF50"),
                    Color.parseColor("#F44336")
                )
                valueTextSize = 14f
                valueTextColor = if (isDark) Color.WHITE else Color.BLACK
                sliceSpace = 3f
                valueLinePart1Length = 0.4f
                valueLinePart2Length = 0.4f
            }

            val data = PieData(dataSet)

            data.setValueFormatter(PercentFormatter(chart))
            chart.setUsePercentValues(true)

            chart.data = data
            chart.isDrawHoleEnabled = false
            chart.setEntryLabelColor(if (isDark) Color.WHITE else Color.BLACK)
            chart.setEntryLabelTextSize(12f)

            chart.legend.textColor =
                if (isDark) Color.WHITE else Color.BLACK

            chart.description.isEnabled = false

            chart.animateY(1000)
            chart.invalidate()

            chart
        }
    )
}