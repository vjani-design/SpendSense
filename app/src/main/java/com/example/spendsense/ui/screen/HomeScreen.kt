package com.example.spendsense.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.spendsense.ui.theme.*
import com.example.spendsense.viewmodel.TransactionViewModel
import com.example.spendsense.ui.components.IncomeExpensePieChart
import com.example.spendsense.ui.components.TransactionItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = viewModel()
) {

    val transactions by transactionViewModel.transactions.collectAsState()

    val income = transactions
        .filter { it.type.equals("income", true) }
        .sumOf { it.amount }

    val expense = transactions
        .filter { it.type.equals("expense", true) }
        .sumOf { it.amount }

    val balance = income - expense

    var showAnalytics by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("U") }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener {
                    userName = it.getString("name") ?: "U"
                }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .neonBackground()
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepSpacePurple)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "SpendSense 💜",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable { navController.navigate("profile") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.uppercase() ?: "U",
                        color = White
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // BALANCE SECTION
            Column(Modifier.padding(16.dp)) {

                Box(
                    Modifier
                        .fillMaxWidth()
                        .glass()
                        .padding(16.dp)
                ) {
                    Text(
                        "Balance: ₹$balance",
                        color = White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row {

                    Box(
                        Modifier
                            .weight(1f)
                            .glass()
                            .padding(12.dp)
                    ) {
                        Text("Income: ₹$income", color = Green)
                    }

                    Spacer(Modifier.width(10.dp))

                    Box(
                        Modifier
                            .weight(1f)
                            .glass()
                            .padding(12.dp)
                    ) {
                        Text("Expense: ₹$expense", color = Red)
                    }
                }
            }

            Text(
                text = when {
                    expense > income -> "⚠ Overspending!"
                    expense > income * 0.8 -> "⚠ High spending"
                    else -> "✅ Healthy finances"
                },
                color = White,
                modifier = Modifier.padding(16.dp)
            )

            Button(
                onClick = { showAnalytics = !showAnalytics },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    if (showAnalytics) "Hide Analytics" else "Show Analytics"
                )
            }

            if (showAnalytics) {
                IncomeExpensePieChart(
                    income = income.toFloat(),
                    expense = expense.toFloat()
                )
            }

            // TRANSACTIONS LIST
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                items(transactions) { item ->
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                            .glass()
                            .padding(12.dp)
                    ) {
                        TransactionItem(item)
                    }
                }
            }

            // ACTION BUTTONS
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.navigate("addExpense") },
                    modifier = Modifier.weight(1f)
                ) { Text("Add Expense") }

                Spacer(Modifier.width(10.dp))

                Button(
                    onClick = { navController.navigate("addIncome") },
                    modifier = Modifier.weight(1f)
                ) { Text("Add Income") }
            }
        }
    }
}