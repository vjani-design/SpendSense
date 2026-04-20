package com.example.spendsense.ui.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
fun AddIncomeScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel
) {

    var selectedDateMillis by remember {
        mutableStateOf(System.currentTimeMillis())
    }

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // ✅ FIX ADDED HERE
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "💰 Add Income",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

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

        Spacer(modifier = Modifier.height(12.dp))

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

        Spacer(modifier = Modifier.height(20.dp))

        // DATE
        Text(
            text = "📅 $formattedDate",
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .clickable { showDatePicker = true }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

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
                    type = "income",                 // ⚠️ lowercase (important)
                    category = "Income",
                    amount = amt,
                    description = description,       // ✅ FIX HERE
                    timestamp = selectedDateMillis
                )

                scope.launch {
                    transactionViewModel.add(tx)

                    Toast.makeText(context, "Income added 💰", Toast.LENGTH_SHORT).show()

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
            )
        ) {
            Text(
                text = "Add Income",
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