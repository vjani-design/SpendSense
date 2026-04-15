package com.example.spendsense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.spendsense.ui.components.TransactionItem
import com.example.spendsense.viewmodel.TransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.spendsense.ui.theme.*

@Composable
fun ProfileScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = viewModel()
) {

    val isDark = ThemeManager.isDarkTheme
    val user = FirebaseAuth.getInstance().currentUser
    val transactions by transactionViewModel.transactions.collectAsState()

    // ✅ FIX: Proper reset when user becomes null
    LaunchedEffect(user?.uid) {
        if (user == null) {
            transactionViewModel.isSharedMode = false
            transactionViewModel.clearData()
        } else {
            transactionViewModel.loadAllData()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .appBackground()
            .padding(16.dp)
    ) {

        // ---------------- HEADER ----------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .appGlass()
                .padding(16.dp)
        ) {
            Column {

                Text(
                    text = "👤 Profile",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = user?.email ?: "No email found",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- THEME TOGGLE ----------------
        Button(
            onClick = { ThemeManager.toggleTheme() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "📜 Transaction History",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ---------------- TRANSACTIONS ----------------
        if (transactions.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .appGlass(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No transactions yet 💡",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(transactions) { tx ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .appGlass()
                            .padding(12.dp)
                    ) {
                        TransactionItem(
                            transaction = tx,
                            onDelete = { },
                            onEdit = { }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------------- SIGN OUT (FIXED) ----------------
        Button(
            onClick = {

                FirebaseAuth.getInstance().signOut()

                transactionViewModel.isSharedMode = false
                transactionViewModel.clearData()

                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Sign Out", color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Back", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}