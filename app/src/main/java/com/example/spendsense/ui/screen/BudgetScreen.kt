package com.example.spendsense.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spendsense.viewmodel.TransactionViewModel
import com.example.spendsense.ui.theme.*

@Composable
fun BudgetScreen(
    navController: NavController,
    viewModel: TransactionViewModel
) {

    val context = LocalContext.current
    val currentBudget by viewModel.budget.collectAsState()
    var budgetInput by remember { mutableStateOf("") }

    LaunchedEffect(currentBudget) {
        budgetInput = if (currentBudget > 0) currentBudget.toString() else ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .neonBackground()   // 🌌 matching your app background
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // TITLE CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glass()
                .padding(20.dp)
        ) {
            Text(
                text = "💰 Set Monthly Budget",
                style = MaterialTheme.typography.headlineMedium,
                color = White
            )
        }

        Spacer(Modifier.height(20.dp))

        // INPUT FIELD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glass()
                .padding(10.dp)
        ) {
            TextField(
                value = budgetInput,
                onValueChange = { budgetInput = it },
                label = { Text("Enter Budget Amount") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    focusedIndicatorColor = NeonPurple,
                    unfocusedIndicatorColor = White.copy(0.3f),
                    cursorColor = NeonPink
                )
            )
        }

        Spacer(Modifier.height(20.dp))

        // SAVE BUTTON
        Button(
            onClick = {
                val amt = budgetInput.toDoubleOrNull()

                if (amt == null || amt <= 0) {
                    Toast.makeText(context, "Enter valid amount", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                viewModel.setBudget(amt)

                Toast.makeText(context, "Budget Saved 💜", Toast.LENGTH_SHORT).show()

                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleMain
            )
        ) {
            Text("Save Budget", color = White)
        }
    }
}