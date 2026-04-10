package com.example.spendsense.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spendsense.model.Transaction
import com.example.spendsense.viewmodel.TransactionViewModel
import com.example.spendsense.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel
) {

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val context = LocalContext.current

    val categories = listOf("Food", "Travel", "Bills", "Shopping", "Other")
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .neonBackground()   // 🌌 upgraded background
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // ---------------- TITLE ----------------
        Text(
            text = "💸 Add Expense",
            color = White,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(20.dp))

        // ---------------- DESCRIPTION ----------------
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Note / Description") },
            modifier = Modifier
                .fillMaxWidth()
                .glass(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedTextColor = White,
                unfocusedTextColor = White,
                cursorColor = NeonPurple,
                focusedIndicatorColor = NeonPink,
                unfocusedIndicatorColor = White.copy(0.3f)
            )
        )

        Spacer(Modifier.height(12.dp))

        // ---------------- AMOUNT ----------------
        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .glass(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedTextColor = White,
                unfocusedTextColor = White,
                cursorColor = NeonPurple,
                focusedIndicatorColor = NeonPink,
                unfocusedIndicatorColor = White.copy(0.3f)
            )
        )

        Spacer(Modifier.height(12.dp))

        // ---------------- CATEGORY ----------------
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {

            TextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .glass(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    cursorColor = NeonPurple,
                    focusedIndicatorColor = NeonPink,
                    unfocusedIndicatorColor = White.copy(0.3f)
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ---------------- BUTTON ----------------
        Button(
            onClick = {
                val amt = amount.toDoubleOrNull()

                if (amt == null || amt <= 0.0) {
                    Toast.makeText(context, "Enter valid amount", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val tx = Transaction(
                    id = "",
                    type = "expense",
                    amount = amt,
                    category = selectedCategory,
                    note = description
                )

                transactionViewModel.add(tx)

                Toast.makeText(context, "Expense added 💸", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleMain
            )
        ) {
            Text("Add Expense", color = White)
        }
    }
}