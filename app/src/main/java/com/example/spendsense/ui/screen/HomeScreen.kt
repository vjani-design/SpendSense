package com.example.spendsense.ui.screen

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.spendsense.ui.theme.*
import com.example.spendsense.ui.components.*
import com.example.spendsense.viewmodel.TransactionViewModel
import com.example.spendsense.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = viewModel()
) {

    val context = LocalContext.current
    val activity = context as Activity
    val user = FirebaseAuth.getInstance().currentUser

    val isDark = ThemeManager.isDarkTheme

    var selectedChart by remember { mutableStateOf("") }
    var showAnalytics by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("U") }

    val transactions by transactionViewModel.transactions.collectAsState()
    val income by transactionViewModel.income.collectAsState()
    val expense by transactionViewModel.expense.collectAsState()
    val balance by transactionViewModel.balance.collectAsState()
    val budget by transactionViewModel.budget.collectAsState()
    val budgetAlert by transactionViewModel.budgetAlert.collectAsState()
    val budgetPercent by transactionViewModel.budgetUsedPercent.collectAsState()
    val budgetAlertEvent by transactionViewModel.budgetAlertEvent.collectAsState()

    val sortedList = remember(transactions) {
        transactions.sortedByDescending { it.timestamp }
    }

    // Load transactions
    LaunchedEffect(user?.uid) {
        if (user != null) {
            transactionViewModel.loadTransactions()
        } else {
            transactionViewModel.clearData()
        }
    }

    // Budget alert notification
    LaunchedEffect(budgetAlertEvent) {
        if (budgetAlertEvent) {
            NotificationHelper.showBudgetAlert(
                context,
                "⚠ Budget limit reached!"
            )
            transactionViewModel.resetBudgetAlert()
        }
    }

    // Permission request
    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
    }

    // Fetch username
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener {
                    userName = it.getString("name") ?: "User"
                }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .appBackground()
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // TOP BAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .height(64.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "SpendSense",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                            .clickable { navController.navigate("profile") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.firstOrNull()?.uppercase() ?: "U",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // BALANCE SECTION
            Column(Modifier.padding(16.dp)) {

                Box(
                    Modifier
                        .fillMaxWidth()
                        .appGlass()
                        .padding(12.dp)
                ) {
                    Text(
                        "Balance: ₹$balance",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row {

                    Box(
                        Modifier
                            .weight(1f)
                            .appGlass()
                            .padding(12.dp)
                    ) {
                        Text("Income: ₹$income", color = Color(0xFF00C853))
                    }

                    Spacer(Modifier.width(10.dp))

                    Box(
                        Modifier
                            .weight(1f)
                            .appGlass()
                            .padding(12.dp)
                    ) {
                        Text("Expense: ₹$expense", color = Color(0xFFD50000))
                    }
                }
            }

            // STATUS
            Text(
                text = when {
                    expense > income -> "⚠ Overspending!"
                    expense > income * 0.8 -> "⚠ High spending"
                    else -> "✅ Healthy finances"
                },
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(16.dp)
            )

            val mostSpent = transactionViewModel.getMostSpentCategory()

            Text(
                text = "Top Category: $mostSpent",
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            // BUDGET
            Column(Modifier.padding(horizontal = 16.dp)) {

                Text("Budget: ₹$budget", color = MaterialTheme.colorScheme.onBackground)

                LinearProgressIndicator(
                    progress = if (budget > 0)
                        (budgetPercent.coerceIn(0.0, 100.0) / 100).toFloat()
                    else 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )

                Text(
                    "Used: ${"%.1f".format(budgetPercent)}%",
                    color = if (budgetAlert) Color.Red else MaterialTheme.colorScheme.onBackground
                )

                if (budgetAlert) {
                    Text("⚠ Budget almost full!", color = Color.Red)
                }
            }

            // ANALYTICS BUTTON
            Button(
                onClick = { showAnalytics = !showAnalytics },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(if (showAnalytics) "Hide Analytics" else "Show Analytics")
            }

            if (showAnalytics) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { selectedChart = "PIE" }) {
                            Text("Pie Chart")
                        }

                        Button(onClick = { selectedChart = "BAR" }) {
                            Text("Bar Chart")
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    if (selectedChart == "PIE") {
                        IncomeExpensePieChart(
                            income = income.toFloat(),
                            expense = expense.toFloat()
                        )
                    }

                    if (selectedChart == "BAR") {
                        MonthlyBarChart(transactions)
                    }

                    if (selectedChart.isEmpty()) {
                        Text(
                            "Select a chart to view analytics",
                            color = Color.Gray
                        )
                    }
                }
            }

            // LIST
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                items(sortedList, key = { it.id }) { item ->
                    SwipeToDeleteItem(
                        onDelete = { transactionViewModel.delete(item.id) }
                    ) {
                        TransactionItem(
                            transaction = item,
                            onDelete = { transactionViewModel.delete(item.id) },
                            onEdit = {
                                navController.navigate("editTransaction/${item.id}")
                            }
                        )
                    }
                }
            }
        }

        FabMenu(navController)
    }
}