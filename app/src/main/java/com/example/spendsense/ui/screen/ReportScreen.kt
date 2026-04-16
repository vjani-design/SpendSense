package com.example.spendsense.ui.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spendsense.ui.theme.*
import com.example.spendsense.utils.ChartCaptureUtils
import com.example.spendsense.utils.PdfReportGenerator
import com.example.spendsense.viewmodel.TransactionViewModel
import com.example.spendsense.ui.components.IncomeExpensePieChart
import com.example.spendsense.ui.components.MonthlyBarChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart

@Composable
fun ReportsScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel
) {

    val context = LocalContext.current
    val budget by transactionViewModel.budget.collectAsState()
    val transactions by transactionViewModel.transactions.collectAsState()
    val income by transactionViewModel.income.collectAsState()
    val expense by transactionViewModel.expense.collectAsState()
    val balance by transactionViewModel.balance.collectAsState()

    var pieChartView by remember { mutableStateOf<PieChart?>(null) }
    var barChartView by remember { mutableStateOf<BarChart?>(null) }

    val mostSpent = remember(transactions) {
        transactionViewModel.getMostSpentCategory()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .appBackground()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text("Reports", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Income: ₹$income")
                Text("Expense: ₹$expense")
                Text("Balance: ₹$balance")
                Text("Most Spent: $mostSpent")
                Text("Transactions: ${transactions.size}")
            }
        }

        Spacer(Modifier.height(20.dp))

        // ================= PIE CHART =================
        IncomeExpensePieChart(
            income = income.toFloat(),
            expense = expense.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            onChartReady = {
                pieChartView = it
            }
        )

        Spacer(Modifier.height(16.dp))

        // ================= BAR CHART =================
        MonthlyBarChart(
            transactions = transactions,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            onChartReady = {
                barChartView = it
            }
        )

        Spacer(Modifier.height(20.dp))

        // ================= DOWNLOAD PDF =================
        Button(
            onClick = {

                try {

                    // 🔥 CAPTURE BITMAPS HERE (THIS WAS MISSING)
                    val pieBitmap: Bitmap? = pieChartView?.let {
                        ChartCaptureUtils.captureView(it)
                    }

                    val barBitmap: Bitmap? = barChartView?.let {
                        ChartCaptureUtils.captureView(it)
                    }

                    val uri = PdfReportGenerator.generateReport(
                        context = context,
                        transactions = transactions,
                        income = income,
                        expense = expense,
                        balance = balance,
                        budget = budget,
                        mostSpent = mostSpent,
                        pieBitmap = pieBitmap,   // ✅ NOW WORKS
                        barBitmap = barBitmap    // ✅ NOW WORKS
                    )

                    if (uri != null) {
                        Toast.makeText(context, "PDF saved in Downloads", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "PDF failed", Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Error creating PDF", Toast.LENGTH_LONG).show()
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Download PDF")
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}