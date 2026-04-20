package com.example.spendsense.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spendsense.model.Transaction
import com.example.spendsense.viewmodel.TransactionViewModel
import com.example.spendsense.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel
) {

    // ✅ FIXED: proper theme usage

    var selectedDateMillis by remember {
        mutableStateOf(System.currentTimeMillis())
    }

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var category by remember { mutableStateOf("Food") }
    var paymentMethod by remember { mutableStateOf("Cash") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis
    )

    val formattedDate = remember(selectedDateMillis) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            .format(Date(selectedDateMillis))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .appBackground()
            .verticalScroll(rememberScrollState())   // 👈 ADD THIS
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "💸 Add Expense",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(20.dp))

        // DESCRIPTION
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .appGlass(),
            colors = customTextFieldColors()
        )

        Spacer(Modifier.height(12.dp))

        // CATEGORY
        val categories = listOf("Food", "Travel", "Shopping", "Bills", "Other")
        var expandedCategory by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = { expandedCategory = !expandedCategory }
        ) {
            TextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .appGlass(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                },
                colors = customTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false }
            ) {
                categories.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            category = it
                            expandedCategory = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // PAYMENT
        val paymentOptions = listOf("Cash", "UPI", "Card")
        var expandedPayment by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expandedPayment,
            onExpandedChange = { expandedPayment = !expandedPayment }
        ) {
            TextField(
                value = paymentMethod,
                onValueChange = {},
                readOnly = true,
                label = { Text("Payment Method") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .appGlass(),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPayment)
                },
                colors = customTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expandedPayment,
                onDismissRequest = { expandedPayment = false }
            ) {
                paymentOptions.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            paymentMethod = it
                            expandedPayment = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // AMOUNT
        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier
                .fillMaxWidth()
                .appGlass(),
            colors = customTextFieldColors()
        )

        Spacer(Modifier.height(20.dp))

        // DATE
        Text(
            text = "📅 $formattedDate",
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .clickable { showDatePicker = true }
                .padding(8.dp)
        )

        Spacer(Modifier.height(20.dp))

        // BUTTON
        Button(
            onClick = {
                val amt = amount.toDoubleOrNull()

                if (amt == null || amt <= 0.0) {
                    Toast.makeText(context, "Enter valid amount", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val tx = Transaction(
                    id = "",
                    type = "expense",                 // ✅ lowercase (IMPORTANT)
                    category = category,
                    amount = amt,
                    description = description,        // ✅ FIXED
                    paymentMethod = paymentMethod,
                    timestamp = selectedDateMillis
                )
                scope.launch {
                    transactionViewModel.add(tx)

                    Toast.makeText(context, "Expense added 💸", Toast.LENGTH_SHORT).show()

                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                "Add Expense",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        // DATE PICKER
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        selectedDateMillis =
                            datePickerState.selectedDateMillis ?: selectedDateMillis
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}