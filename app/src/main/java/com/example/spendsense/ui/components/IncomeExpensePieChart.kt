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

            // ✅ GROUPING (income → description, expense → category)
            val categoryMap = transactions
                .groupBy { tx ->
                    if (tx.type == "income") {
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

            // ✅ COLOR SYSTEM (shades)
            val incomeColors = listOf(
                "#4CAF50", "#66BB6A", "#81C784", "#A5D6A7"
            )

            val expenseColors = listOf(
                "#F44336", "#EF5350", "#E57373", "#EF9A9A"
            )

            val colors = entries.mapIndexed { index, entry ->
                if (entry.label.startsWith("Income"))
                    Color.parseColor(incomeColors[index % incomeColors.size])
                else
                    Color.parseColor(expenseColors[index % expenseColors.size])
            }

            val dataSet = PieDataSet(entries, "").apply {
                this.colors = colors
                valueTextSize = 14f
                valueTextColor = if (isDark) Color.WHITE else Color.BLACK
                sliceSpace = 3f
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

            // ✅ smooth animation (no rotation issue)
            chart.animateY(800, com.github.mikephil.charting.animation.Easing.EaseInOutQuad)

            chart.invalidate()

            chart
        }
    )
}