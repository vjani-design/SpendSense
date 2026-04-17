package com.example.spendsense.ui.components

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.spendsense.model.Transaction
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import java.util.*
import com.example.spendsense.ui.theme.ThemeManager   // ✅ ADD THIS

@Composable
fun MonthlyBarChart(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
    onChartReady: (BarChart) -> Unit = {}
) {
    val isDark = ThemeManager.isDarkTheme   // ✅ FIXED

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp),
        factory = { context ->

            val chart = BarChart(context)

            onChartReady(chart)
            if (transactions.isEmpty()) return@AndroidView chart

            val grouped = transactions.groupBy { t ->
                val cal = Calendar.getInstance()
                cal.timeInMillis = t.timestamp
                cal.get(Calendar.MONTH) + 1
            }

            val incomeEntries = mutableListOf<BarEntry>()
            val expenseEntries = mutableListOf<BarEntry>()

            grouped.forEach { (month, list) ->

                val income = list.filter { it.type == "INCOME" }
                    .sumOf { it.amount }

                val expense = list.filter { it.type == "EXPENSE" }
                    .sumOf { it.amount }

                incomeEntries.add(BarEntry(month.toFloat(), income.toFloat()))
                expenseEntries.add(BarEntry(month.toFloat(), expense.toFloat()))
            }

            val incomeDataSet = BarDataSet(incomeEntries, "Income").apply {
                color = Color.parseColor("#4CAF50")
                valueTextColor = if (isDark) Color.WHITE else Color.BLACK   // ✅ FIX
                valueTextSize = 10f
            }

            val expenseDataSet = BarDataSet(expenseEntries, "Expense").apply {
                color = Color.parseColor("#F44336")
                valueTextColor = if (isDark) Color.WHITE else Color.BLACK   // ✅ FIX
                valueTextSize = 10f
            }

            val barData = BarData(incomeDataSet, expenseDataSet)

            val groupSpace = 0.2f
            val barSpace = 0.05f
            val barWidth = 0.35f

            barData.barWidth = barWidth

            chart.data = barData

            chart.xAxis.axisMinimum = 0f
            chart.xAxis.axisMaximum = grouped.size + 1f
            chart.groupBars(0f, groupSpace, barSpace)

            chart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textColor = if (isDark) Color.WHITE else Color.BLACK
            }

            chart.axisLeft.textColor = if (isDark) Color.WHITE else Color.BLACK
            chart.axisRight.isEnabled = false

            chart.description.isEnabled = false
            chart.legend.isEnabled = true

            chart.legend.textColor =
                if (isDark) Color.WHITE else Color.BLACK

            chart.animateY(1000)
            chart.invalidate()

            chart
        }
    )
}