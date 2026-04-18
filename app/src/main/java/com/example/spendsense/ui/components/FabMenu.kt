package com.example.spendsense.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spendsense.ui.theme.GradientEnd
import com.example.spendsense.ui.theme.GradientStart

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
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(listOf(GradientStart, GradientEnd)) // ✅ login gradient
                    )
                    .clickable { expanded = !expanded },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White
                )
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

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                Brush.horizontalGradient(listOf(GradientStart, GradientEnd)) // ✅ gradient
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}