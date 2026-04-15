package com.example.spendsense.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spendsense.model.Transaction
import com.example.spendsense.ui.theme.*
import com.example.spendsense.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

@Composable
fun EditTransactionScreen(
    transactionId: String,
    navController: NavController,
    transactionViewModel: TransactionViewModel
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val transactions by transactionViewModel.transactions.collectAsState()
    val transaction = transactions.find { it.id == transactionId }

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    LaunchedEffect(transaction) {
        transaction?.let {
            amount = it.amount.toString()
            note = it.note
        }
    }

    if (transaction == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .appBackground(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Loading...",
                color = MaterialTheme.colorScheme.primary
            )
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .appBackground()
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .appGlass()
                .padding(20.dp)
        ) {

            Text(
                text = "Edit Transaction",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                colors = customTextFieldColors()
            )

            Spacer(Modifier.height(12.dp))

            TextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth(),
                colors = customTextFieldColors()
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()

                    if (amt == null) {
                        Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val updated = transaction.copy(
                        amount = amt,
                        note = note
                    )

                    scope.launch {
                        transactionViewModel.update(updated)
                        Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Update")
            }
        }
    }
}