package com.example.spendsense.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.spendsense.model.Transaction

@Composable
fun TransactionItem(transaction: Transaction) {

    val isExpense = transaction.type == "expense"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(transaction.category)
                Text(
                    transaction.note,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                "₹${transaction.amount}",
                color = if (isExpense) Color.Red else Color.Green
            )
        }
    }
}