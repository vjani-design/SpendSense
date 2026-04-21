package com.example.spendsense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.spendsense.ui.theme.*
import com.example.spendsense.viewmodel.TransactionViewModel
import com.example.spendsense.ui.components.IncomeExpensePieChart
import com.example.spendsense.ui.components.MonthlyBarChart

@Composable
fun AnalyticsScreen(
    navController: NavController,
    viewModel: TransactionViewModel = viewModel()
) {

    val transactions by viewModel.transactions.collectAsState()

    val expenseList = remember(transactions) {
        transactions.filter { it.type.uppercase() == "EXPENSE" }
    }

    val incomeList = remember(transactions) {
        transactions.filter { it.type.uppercase() == "INCOME" }
    }

    val totalExpense = remember(expenseList) {
        expenseList.sumOf { it.amount }
    }

    val totalIncome = remember(incomeList) {
        incomeList.sumOf { it.amount }
    }

    val balance = totalIncome - totalExpense

    val categoryMap = remember(expenseList) {
        expenseList
            .groupBy { it.category }
            .mapValues { it.value.sumOf { t -> t.amount } }
    }

    val mostSpent = remember(categoryMap) {
        categoryMap.maxByOrNull { it.value }?.key ?: "None"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .appBackground()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "📊 Analytics",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------- SUMMARY ----------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = "💰 Income: ₹${"%.2f".format(totalIncome)}" ,
                            color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "💸 Expense: ₹${"%.2f".format(totalExpense)}",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "📊 Balance: ₹${"%.2f".format(balance)}",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "🔥 Top Category: $mostSpent",
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Income vs Expense",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        IncomeExpensePieChart(transactions)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Category-wise Spending",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .appGlass(),
            contentAlignment = Alignment.Center
        ) {

            if (categoryMap.isEmpty()) {
                Text(
                    "No data available",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            } else {
                Column {
                    categoryMap.forEach { entry ->
                        Text(
                            text = "${entry.key} : ${entry.value}",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Monthly Trend",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        MonthlyBarChart(transactions)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                "Back",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}