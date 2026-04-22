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
import com.example.spendsense.model.Transaction
import com.example.spendsense.ui.theme.ThemeManager

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

            onChartReady(chart)   // ✅ IMPORTANT

            chart.renderer = ArrowPieChartRenderer(
                chart,
                chart.animator,
                chart.viewPortHandler
            )

            // ✅ Group data
            val categoryMap = transactions
                .groupBy { tx ->
                    if (tx.type.equals("INCOME", true)) {
                        "Income - ${tx.description}"
                    } else {
                        "Expense - ${tx.category}"
                    }
                }
                .mapValues { it.value.sumOf { tx -> tx.amount }.toFloat() }

            val entries = categoryMap.map {
                PieEntry(it.value, it.key)
            }

            val colors = entries.map { entry ->
                if (entry.label.startsWith("Income", true))
                    Color.parseColor("#4CAF50")
                else
                    Color.parseColor("#F44336")
            }

            val dataSet = PieDataSet(entries, "").apply {
                this.colors = colors
                valueTextColor = Color.TRANSPARENT // hide default values
            }

            val data = PieData(dataSet)

            chart.apply {
                setUsePercentValues(true)
                this.data = data

                isDrawHoleEnabled = false

                setExtraOffsets(40f, 20f, 40f, 20f)   // ✅ prevents arrows going out

                // ✅ Step 3 (IMPORTANT)
                setDrawEntryLabels(false)

                // ❌ Disable legend (we use arrows instead)
                legend.isEnabled = false

                description.isEnabled = false

                animateY(800)
                invalidate()
            }

            chart
        }
    )
}