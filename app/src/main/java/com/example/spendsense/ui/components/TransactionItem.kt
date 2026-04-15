package com.example.spendsense.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.spendsense.model.Transaction
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: (String) -> Unit,
    onEdit: (Transaction) -> Unit
) {

    val isExpense = transaction.type.equals("EXPENSE", true)

    // ✅ FIXED: use timestamp instead of date
    val formattedDate = remember(transaction.timestamp) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            .format(Date(transaction.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = transaction.note,
                    style = MaterialTheme.typography.bodySmall
                )

                if (transaction.paymentMethod.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Payment: ${transaction.paymentMethod}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Column {

                Text(
                    text = "₹${transaction.amount}",
                    color = if (isExpense) Color.Red else Color.Green
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row {

                    IconButton(onClick = { onEdit(transaction) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }

                    IconButton(onClick = { onDelete(transaction.id) }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}