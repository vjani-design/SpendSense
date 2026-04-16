package com.example.spendsense.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.spendsense.ui.theme.*
import com.example.spendsense.utils.CsvExporter
import com.example.spendsense.viewmodel.TransactionViewModel

@Composable
fun ReportsScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel
) {

    val transactions by transactionViewModel.transactions.collectAsState()
    val income by transactionViewModel.income.collectAsState()
    val expense by transactionViewModel.expense.collectAsState()
    val balance by transactionViewModel.balance.collectAsState()

    val context = LocalContext.current

    val mostSpent = remember(transactions) {
        transactionViewModel.getMostSpentCategory()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .appBackground()
            .verticalScroll(rememberScrollState())   // ✅ FIX ADDED HERE
            .padding(16.dp)
    ) {

        // ---------------- TITLE ----------------
        Text(
            text = "Reports",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(16.dp))

        // ---------------- SUMMARY CARD ----------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    "Total Transactions: ${transactions.size}",
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    "Income: ₹$income",
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    "Expense: ₹$expense",
                    color = MaterialTheme.colorScheme.error
                )

                Text(
                    "Balance: ₹$balance",
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Most Spent: $mostSpent",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- EXPORT BUTTON ----------------
        Button(
            onClick = {

                val file = CsvExporter.exportToFile(context, transactions)

                if (file != null) {

                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        file
                    )

                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    context.startActivity(
                        Intent.createChooser(intent, "Share CSV Report")
                    )

                } else {
                    Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Share Report")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------------- BACK BUTTON ----------------
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Back")
        }
    }
}