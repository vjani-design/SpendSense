package com.example.spendsense.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spendsense.viewmodel.TransactionViewModel
import com.example.spendsense.viewmodel.BudgetType
import com.example.spendsense.ui.theme.*

@Composable
fun BudgetScreen(
    navController: NavController,
    viewModel: TransactionViewModel
) {

    val context = LocalContext.current
    val currentBudget by viewModel.budget.collectAsState()

    var budgetInput by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(BudgetType.MONTHLY) }

    LaunchedEffect(currentBudget) {
        budgetInput = if (currentBudget > 0) "%.2f".format(currentBudget) else ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .appBackground()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "💰 Set Budget",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        // ================= TOGGLE =================
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedType == BudgetType.WEEKLY)
                                MaterialTheme.colorScheme.primary
                            else Transparent
                        )
                        .clickable { selectedType = BudgetType.WEEKLY }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Weekly",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (selectedType == BudgetType.MONTHLY)
                                MaterialTheme.colorScheme.primary
                            else Transparent
                        )
                        .clickable { selectedType = BudgetType.MONTHLY }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Monthly",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ================= INPUT =================
        TextField(
            value = budgetInput,
            onValueChange = { budgetInput = it },
            label = { Text("Budget") },
            modifier = Modifier
                .fillMaxWidth()
                .appGlass(),
            colors = customTextFieldColors()
        )

        Spacer(Modifier.height(25.dp))

        // ================= SAVE BUTTON =================
        Button(
            onClick = {
                val amt = budgetInput.toDoubleOrNull()

                if (amt == null || amt <= 0) {
                    Toast.makeText(context, "Invalid amount", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                viewModel.setBudget(amt, selectedType)

                Toast.makeText(context, "Budget Saved", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                "Save Budget",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}