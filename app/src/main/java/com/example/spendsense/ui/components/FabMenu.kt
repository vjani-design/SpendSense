package com.example.spendsense.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun FabMenu(navController: NavController) {

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.padding(16.dp)
        ) {

            // 🔽 Expandable Options
            AnimatedVisibility(visible = expanded) {

                Column(horizontalAlignment = Alignment.End) {

                    FabItem(
                        text = "Add Expense",
                        icon = Icons.Default.Receipt
                    ) {
                        navController.navigate("add_expense")
                        expanded = false
                    }

                    FabItem(
                        text = "Add Income",
                        icon = Icons.Default.AttachMoney
                    ) {
                        navController.navigate("add_income")
                        expanded = false
                    }

                    FabItem(
                        text = "Set Budget",
                        icon = Icons.Default.AccountBalanceWallet
                    ) {
                        navController.navigate("budget")
                        expanded = false
                    }

                    FabItem(
                        text = "Reports",
                        icon = Icons.Default.BarChart
                    ) {
                        navController.navigate("reports")
                        expanded = false
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ➕ Main FAB Button
            FloatingActionButton(
                onClick = { expanded = !expanded }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}

@Composable
fun FabItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {

    Spacer(modifier = Modifier.height(8.dp))

    ExtendedFloatingActionButton(
        text = { Text(text) },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = text
            )
        },
        onClick = onClick
    )
}