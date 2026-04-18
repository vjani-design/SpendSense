package com.example.spendsense.ui.screen

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.spendsense.R
import com.example.spendsense.ui.components.*
import com.example.spendsense.ui.theme.*
import com.example.spendsense.utils.NotificationHelper
import com.example.spendsense.viewmodel.TransactionViewModel
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

    var selectedChart by remember { mutableStateOf("") }
    var showAnalytics by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("U") }

    val transactions by transactionViewModel.transactions.collectAsState()
    val income by transactionViewModel.income.collectAsState()
    val expense by transactionViewModel.expense.collectAsState()
    val balance by transactionViewModel.balance.collectAsState()
    val budget by transactionViewModel.budget.collectAsState()
    val budgetAlert by transactionViewModel.budgetAlert.collectAsState()
    val budgetAlertEvent by transactionViewModel.budgetAlertEvent.collectAsState()
    val budgetPercent by transactionViewModel.budgetUsedPercent.collectAsState()

    val sortedList = remember(transactions) {
        transactions.sortedByDescending { it.timestamp }
    }

    val textColor = MaterialTheme.colorScheme.onBackground

    LaunchedEffect(Unit) {
        FirebaseAuth.getInstance().currentUser?.let {
            transactionViewModel.loadTransactions()
        }
    }

    LaunchedEffect(budgetAlertEvent) {
        if (budgetAlertEvent) {
            NotificationHelper.showBudgetAlert(context, "⚠ Budget limit reached!")
            transactionViewModel.resetBudgetAlert()
        }
    }

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

            // 🔹 HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        "SpendSense",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Spacer(Modifier.width(8.dp))

                    Image(
                        painter = painterResource(id = R.drawable.updated_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(26.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(textColor.copy(alpha = 0.1f))
                        .clickable { navController.navigate("profile") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(userName.firstOrNull()?.uppercase() ?: "U", color = textColor)
                }
            }

            Spacer(Modifier.height(16.dp))

            // 🔹 BALANCE
            Column(Modifier.padding(16.dp)) {

                Box(Modifier.fillMaxWidth().appGlass().padding(12.dp)) {
                    Text(
                        "Balance: ₹$balance",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row {
                    Box(Modifier.weight(1f).appGlass().padding(12.dp)) {
                        Text("Income: ₹$income", color = Color(0xFF00C853), fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(10.dp))
                    Box(Modifier.weight(1f).appGlass().padding(12.dp)) {
                        Text("Expense: ₹$expense", color = Color(0xFFD50000), fontWeight = FontWeight.Bold)
                    }
                }
            }


            // 🔹 BUDGET
            Column(Modifier.padding(horizontal = 16.dp)) {

                val statusColor =
                    if (budgetAlert) Color(0xFFD50000) else Color(0xFF00C853)

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (MaterialTheme.colorScheme.background.luminance() < 0.5f)
                        MaterialTheme.colorScheme.surfaceVariant else Color.White
                ) {
                    Row(Modifier.padding(10.dp)) {
                        Text(if (budgetAlert) "⚠" else "✔", color = statusColor)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (budgetAlert) "OVERSPENDING" else "HEALTHY FINANCES",
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Set Budget", color = textColor, fontWeight = FontWeight.Bold)
                    Text("₹$budget", color = textColor, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(10.dp))

                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Monthly Budget", color = textColor, fontWeight = FontWeight.Bold)
                    Text("${"%.0f".format(budgetPercent)}%", color = textColor, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = (budgetPercent / 100).toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = if (budgetAlert) Color.Red else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            }

            // 🔷 ANALYTICS BUTTON
            Button(
                onClick = { showAnalytics = !showAnalytics },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(if (showAnalytics) "Hide Analytics" else "Show Analytics", fontWeight = FontWeight.Bold)
            }

            // 🔷 ANALYTICS
            if (showAnalytics) {

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Text(
                        "Pie Chart",
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            selectedChart = if (selectedChart == "PIE") "" else "PIE"
                        }
                    )

                    Text(
                        "Bar Chart",
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            selectedChart = if (selectedChart == "BAR") "" else "BAR"
                        }
                    )
                }

                Box(Modifier.padding(16.dp)) {
                    when (selectedChart) {
                        "PIE" -> IncomeExpensePieChart(income.toFloat(), expense.toFloat())
                        "BAR" -> MonthlyBarChart(transactions)
                        else -> Text("Select a chart", color = textColor.copy(0.7f), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 🔷 HISTORY
            Text(
                "Recent Activity",
                Modifier.padding(16.dp),
                color = textColor,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(sortedList) { item ->

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