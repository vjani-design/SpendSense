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
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.*
import com.example.spendsense.ui.theme.ThemeManager

@Composable
fun MonthlyBarChart(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier,
    onChartReady: (BarChart) -> Unit = {}
) {
    val isDark = ThemeManager.isDarkTheme

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp),
        factory = { context ->

            val chart = BarChart(context)
            onChartReady(chart)

            if (transactions.isEmpty()) return@AndroidView chart

            // 🔥 GROUP BY MONTH
            val grouped = transactions
                .filter { it.date != null }
                .groupBy { t ->
                    val cal = Calendar.getInstance()
                    cal.time = t.date!!.toDate()
                    cal.get(Calendar.MONTH) + 1
                }

            val months = grouped.keys.sorted()

            val incomeEntries = mutableListOf<BarEntry>()
            val expenseEntries = mutableListOf<BarEntry>()

            // 🔥 IMPORTANT: Use INDEX (not month number)
            months.forEachIndexed { index, month ->

                val list = grouped[month] ?: emptyList()

                val income = list.filter {
                    it.type.uppercase() == "INCOME"
                }.sumOf { it.amount }

                val expense = list.filter {
                    it.type.uppercase() == "EXPENSE"
                }.sumOf { it.amount }

                val x = index.toFloat()

                incomeEntries.add(BarEntry(x, income.toFloat()))
                expenseEntries.add(BarEntry(x, expense.toFloat()))
            }

            val incomeDataSet = BarDataSet(incomeEntries, "Income").apply {
                color = Color.parseColor("#4CAF50")
                valueTextColor = if (isDark) Color.WHITE else Color.BLACK
                valueTextSize = 10f
            }

            val expenseDataSet = BarDataSet(expenseEntries, "Expense").apply {
                color = Color.parseColor("#F44336")
                valueTextColor = if (isDark) Color.WHITE else Color.BLACK
                valueTextSize = 10f
            }

            val barData = BarData(incomeDataSet, expenseDataSet)

            val groupSpace = 0.2f
            val barSpace = 0.05f
            val barWidth = 0.35f

            barData.barWidth = barWidth
            chart.data = barData

            // 🔥 CRITICAL FIX (THIS WAS YOUR BUG)
            val groupCount = months.size
            val startX = 0f

            chart.xAxis.axisMinimum = startX
            chart.xAxis.axisMaximum = startX + groupCount

            chart.groupBars(startX, groupSpace, barSpace)

            // 🔥 MONTH LABELS
            val monthNames = listOf(
                "Jan","Feb","Mar","Apr","May","Jun",
                "Jul","Aug","Sep","Oct","Nov","Dec"
            )

            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    val month = months.getOrNull(index)
                    return if (month != null && month in 1..12)
                        monthNames[month - 1]
                    else ""
                }
            }

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
            chart.legend.textColor = if (isDark) Color.WHITE else Color.BLACK

            // 🔥 OPTIONAL (improves visibility)
            chart.setVisibleXRangeMaximum(6f)
            chart.moveViewToX(0f)

            chart.animateY(1000)
            chart.invalidate()

            chart
        }
    )
}