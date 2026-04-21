package com.example.spendsense.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val formattedDate = remember(transaction.date) {
        transaction.date?.toDate()?.let {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
        } ?: "No Date"
    }

    val amountColor = if (isExpense) Color(0xFFD50000) else Color(0xFF00C853)
    val sign = if (isExpense) "-" else "+"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 🔹 ICON BOX
        Surface(
            modifier = Modifier.size(54.dp),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = when (transaction.category) {
                        "Food" -> "🍴"
                        "Salary" -> "💼"
                        "Shopping" -> "🛍"
                        "Bills" -> "💡"
                        else -> "💰"
                    },
                    fontSize = 24.sp // 🔥 22 → 24
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 🔹 TEXT CONTENT
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = transaction.category,
                fontSize = 20.sp, // 🔥 18 → 20
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = transaction.note.ifEmpty { transaction.type },
                    fontSize = 16.sp, // 🔥 14 → 16
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )

                if (transaction.paymentMethod.isNotEmpty()) {

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = transaction.paymentMethod,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                            fontSize = 13.5.sp, // 🔥 11.5 → 13.5
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formattedDate,
                fontSize = 14.5.sp, // 🔥 12.5 → 14.5
                letterSpacing = 0.4.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // 🔹 RIGHT SIDE
        Column(
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text = sign,
                fontSize = 17.sp, // 🔥 15 → 17
                color = amountColor
            )

            Text(
                text = "₹${transaction.amount}",
                fontSize = 20.sp, // 🔥 18 → 20
                fontWeight = FontWeight.Bold,
                color = amountColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {

                IconButton(
                    onClick = { onEdit(transaction) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { onDelete(transaction.id) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFD50000),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}