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

    val transactions by transactionViewModel.transactions.collectAsState()
    val user = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .neonBackground()
            .padding(16.dp)
    ) {

        // ---------------- HEADER ----------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glass()
                .padding(16.dp)
        ) {
            Column {

                Text(
                    text = "👤 Profile",
                    color = White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = user?.email ?: "No email found",
                    color = White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- HISTORY TITLE ----------------
        Text(
            text = "📜 Transaction History",
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ---------------- TRANSACTIONS LIST ----------------
        if (transactions.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .glass(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No transactions yet 💡",
                    color = White.copy(alpha = 0.7f)
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
                            .glass()
                            .padding(12.dp)
                    ) {
                        TransactionItem(transaction = tx)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------------- SIGN OUT ----------------
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleMain
            )
        ) {
            Text("Sign Out", color = White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------------- BACK BUTTON ----------------
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleMain
            )
        ) {
            Text("Back", color = White)
        }
    }
}