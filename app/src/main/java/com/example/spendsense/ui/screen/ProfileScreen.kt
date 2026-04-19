package com.example.spendsense.ui.screens

import androidx.compose.foundation.clickable
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
    var showGroupDialog by remember { mutableStateOf(false) }

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .appBackground()
                .padding(padding)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 120.dp)
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
                                        transactionViewModel.setSharedMode(true, groupId.ifEmpty { null })
                                    } else {
                                        transactionViewModel.setSharedMode(false)
                                    }

                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (enabled) "Family Mode ON" else "Personal Mode ON"
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
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Group created 🎉")
                                                }
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
                                            transactionViewModel.joinGroup(groupCode) {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Join attempt done")
                                                }
                                            }
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

                    item {
                        Button(
                            onClick = { showGroupDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Select Group")
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
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Invite sent 📩")
                                        }
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

                items(transactions) { tx ->
                    TransactionItem(tx, {}, {})
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Out")
                }

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back")
                }
            }
        }

        // 🔥 GROUP DIALOG
        if (showGroupDialog) {

            val uid = user?.uid ?: ""

            AlertDialog(
                onDismissRequest = { showGroupDialog = false },
                title = { Text("Select Group") },
                text = {
                    LazyColumn {
                        items(groups) { g ->

                            val isOwner = g.createdBy == uid
                            val isMember = g.members.containsKey(uid)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = isOwner || isMember) {
                                        transactionViewModel.setSharedMode(true, g.id)
                                        showGroupDialog = false

                                        scope.launch {
                                            snackbarHostState.showSnackbar("Group activated ✅")
                                        }
                                    }
                                    .padding(12.dp)
                            ) {

                                Text(g.name, fontWeight = FontWeight.Bold)
                                Text("Code: ${g.code}")

                                Text(
                                    when {
                                        isOwner -> "👑 Owner"
                                        isMember -> "👤 Member"
                                        else -> "Guest"
                                    },
                                    fontSize = 11.sp
                                )

                                if (g.invitedEmails.isNotEmpty()) {
                                    Text("Invited:")
                                    g.invitedEmails.keys.forEach {
                                        val email = it.replace(",", ".")
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(email)

                                            if (isOwner) {
                                                Text(
                                                    "Remove",
                                                    color = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.clickable {
                                                        transactionViewModel.removeInvite(g.id, it)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                if (isOwner) {
                                    Button(
                                        onClick = {},
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Edit Group")
                                    }
                                }
                            }

                            Divider()
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showGroupDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}