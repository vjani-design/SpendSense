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
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = viewModel()
) {

    val currentGroupName by transactionViewModel.currentGroupName.collectAsState()
    val currentGroupCode by transactionViewModel.currentGroupCode.collectAsState()
    val isDark = ThemeManager.isDarkTheme
    val user = FirebaseAuth.getInstance().currentUser

    val transactions by transactionViewModel.transactions.collectAsState()
    val isShared by transactionViewModel.isSharedMode.collectAsState()
    val groups by transactionViewModel.userGroups.collectAsState()

    var groupName by remember { mutableStateOf("") }
    var groupCode by remember { mutableStateOf("") }
    var inviteEmail by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(user?.uid) {
        if (user != null) {
            transactionViewModel.loadUserGroups()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .appBackground()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Box(Modifier.fillMaxWidth().appGlass().padding(16.dp)) {
                    Column {
                        Text("👤 Profile", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text(user?.email ?: "No email found")
                    }
                }
            }

            item {
                Button(
                    onClick = { ThemeManager.toggleTheme() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isDark) "Switch to Light Mode" else "Switch to Dark Mode")
                }
            }

            item {
                Box(Modifier.fillMaxWidth().appGlass().padding(16.dp)) {
                    Column {
                        Text("👨‍👩‍👧 Family Mode", fontWeight = FontWeight.Bold)

                        Switch(
                            checked = isShared,
                            onCheckedChange = { enabled ->

                                val groupId = transactionViewModel.currentGroupId

                                if (enabled) {
                                    if (groupId.isNotEmpty()) {
                                        transactionViewModel.setSharedMode(true, groupId)
                                    } else {
                                        transactionViewModel.setSharedMode(true, null)
                                    }
                                } else {
                                    transactionViewModel.setSharedMode(false)
                                }

                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (enabled) "Family Mode ON"
                                        else "Personal Mode ON"
                                    )
                                }
                            }
                        )
                    }
                }
            }

            if (isShared) {

                item {
                    Box(Modifier.fillMaxWidth().appGlass().padding(12.dp)) {
                        Column {
                            Text("Create Group", fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = groupName,
                                onValueChange = { groupName = it },
                                label = { Text("Group Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    if (groupName.isNotBlank()) {
                                        transactionViewModel.createGroup(groupName) {
                                            groupName = ""
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Create Group")
                            }
                        }
                    }
                }

                item {
                    Box(Modifier.fillMaxWidth().appGlass().padding(12.dp)) {
                        Column {
                            Text("Join Group", fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = groupCode,
                                onValueChange = { groupCode = it },
                                label = { Text("Group Code") },
                                modifier = Modifier.fillMaxWidth()
                            )

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
                        }
                    }
                }
            }

            val uid = user?.uid ?: ""
            val safeEmail = user?.email?.trim()?.lowercase()?.replace(".", ",") ?: ""

            items(groups) { g ->

                val canUse =
                    g.createdBy == uid ||
                            (g.members.containsKey(uid) &&
                                    g.invitedEmails.containsKey(safeEmail))

                Box(Modifier.fillMaxWidth().appGlass().padding(12.dp)) {
                    Column {
                        Text("Name: ${g.name}", fontWeight = FontWeight.Bold)
                        Text("Code: ${g.code}")

                        if (canUse) {
                            Button(
                                onClick = {
                                    transactionViewModel.setSharedMode(true, g.id)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Use This Group")
                            }
                        }
                    }
                }
            }

            item {
                Box(Modifier.fillMaxWidth().appGlass().padding(12.dp)) {
                    Column {
                        Text("Invite Member", fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = inviteEmail,
                            onValueChange = { inviteEmail = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (inviteEmail.isNotBlank()) {
                                    transactionViewModel.inviteUser(inviteEmail)
                                    inviteEmail = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Send Invite")
                        }
                    }
                }
            }

            item {
                Box(Modifier.fillMaxWidth().appGlass().padding(12.dp)) {
                    Column {
                        Text("👥 Active Group", fontWeight = FontWeight.Bold)
                        Text("Name: ${currentGroupName.ifEmpty { "None" }}")

                        Card {
                            Text(
                                currentGroupCode.ifEmpty { "No Code" },
                                modifier = Modifier.padding(12.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                Text("📜 Transaction History", fontWeight = FontWeight.Bold)
            }

            items(transactions) { tx ->
                TransactionItem(tx, {}, {})
            }

            item {
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        transactionViewModel.setSharedMode(false)
                        transactionViewModel.clearData()

                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Out")
                }
            }

            item {
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