package com.example.spendsense.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.example.spendsense.ui.theme.*

@Composable
fun IncomeExpensePieChart(
    income: Float,
    expense: Float
) {
    val total = (income + expense).takeIf { it > 0 } ?: 1f

    val incomePercent = (income / total) * 100f
    val expensePercent = (expense / total) * 100f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 🎯 CENTERED PIE CHART
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {

            Canvas(modifier = Modifier.size(220.dp)) {

                val incomeAngle = (income / total) * 360f
                val expenseAngle = (expense / total) * 360f

                // 💙 INCOME (Neon Blue)
                drawArc(
                    color = NeonBlue,
                    startAngle = 0f,
                    sweepAngle = incomeAngle,
                    useCenter = true,
                    size = Size(size.width, size.height)
                )

                // 💖 EXPENSE (Neon Pink)
                drawArc(
                    color = NeonPink,
                    startAngle = incomeAngle,
                    sweepAngle = expenseAngle,
                    useCenter = true,
                    size = Size(size.width, size.height)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📊 LABELS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Income: ${incomePercent.toInt()}%",
                color = NeonBlue
            )

            Text(
                text = "Expense: ${expensePercent.toInt()}%",
                color = NeonPink
            )
        }
    }
}