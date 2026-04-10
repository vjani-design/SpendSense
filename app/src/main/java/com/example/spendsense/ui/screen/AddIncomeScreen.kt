package com.example.spendsense.ui.screen

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

@Composable
fun AddIncomeScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel
) {

    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .neonBackground()   // 🌌 upgraded background
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // ---------------- TITLE ----------------
        Text(
            text = "💰 Add Income",
            color = White,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(20.dp))

        // ---------------- DESCRIPTION ----------------
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
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

        Spacer(Modifier.height(20.dp))

        // ---------------- BUTTON ----------------
        Button(
            onClick = {
                val amt = amount.toDoubleOrNull()

                if (amt == null || amt <= 0.0) {
                    Toast.makeText(context, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val tx = Transaction(
                    id = "",
                    type = "income",
                    amount = amt,
                    note = description
                )

                transactionViewModel.add(tx)

                Toast.makeText(context, "Income added 💰", Toast.LENGTH_SHORT).show()

                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleMain
            )
        ) {
            Text("Add Income", color = White)
        }
    }
}