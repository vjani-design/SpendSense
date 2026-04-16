package com.example.spendsense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = viewModel()
) {

    val isDark = ThemeManager.isDarkTheme
    val user = FirebaseAuth.getInstance().currentUser
    val transactions by transactionViewModel.transactions.collectAsState()

    var isShared by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var groupCode by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(user?.uid) {
        if (user == null) {
            transactionViewModel.isSharedMode = false
            transactionViewModel.clearData()
        } else {
            transactionViewModel.setUserSession(user.uid)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .appBackground()
                .padding(padding)
        ) {

            // ✅ SCROLLABLE CONTENT AREA (TOP SECTION)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
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

                Spacer(modifier = Modifier.height(16.dp))

                // ---------------- THEME ----------------
                Button(
                    onClick = { ThemeManager.toggleTheme() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isDark) "Switch to Light Mode" else "Switch to Dark Mode")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ---------------- FAMILY MODE ----------------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .appGlass()
                        .padding(16.dp)
                ) {
                    Column {

                        Text(
                            text = "👨‍👩‍👧 Family Mode",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isShared)
                                "Sharing expenses with your group"
                            else
                                "Track expenses privately",
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Switch(
                            checked = isShared,
                            onCheckedChange = {
                                isShared = it
                                transactionViewModel.isSharedMode = it
                                transactionViewModel.loadAllData()

                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (it) "Shared Mode Enabled"
                                        else "Personal Mode Enabled"
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ---------------- GROUP ----------------
                if (isShared) {

                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Group Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (groupName.isNotBlank()) {
                                transactionViewModel.createGroup(groupName)
                                groupName = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Group")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = groupCode,
                        onValueChange = { groupCode = it },
                        label = { Text("Group Code") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (groupCode.isNotBlank()) {
                                transactionViewModel.joinGroup(groupCode)
                                groupCode = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Join Group")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ---------------- TITLE ----------------
                Text(
                    text = "📜 Transaction History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // ---------------- TRANSACTIONS LIST (FIXED AREA) ----------------
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {

                if (transactions.isEmpty()) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transactions yet 💡")
                    }

                } else {

                    LazyColumn {
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
            }

            // ---------------- BOTTOM BUTTONS ----------------
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        transactionViewModel.isSharedMode = false
                        transactionViewModel.clearData()

                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Out")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back")
                }
            }
        }
    }
}