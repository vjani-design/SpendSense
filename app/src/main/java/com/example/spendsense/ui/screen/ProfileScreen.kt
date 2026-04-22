package com.example.spendsense.ui.screens

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

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("user_session", 0)
    val savedEmail = sharedPref.getString("email", null)

    val transactions by transactionViewModel.transactions.collectAsState()
    val isShared by transactionViewModel.isSharedMode.collectAsState()
    val groupId by transactionViewModel.currentGroupId.collectAsState()
    val currentGroupId by transactionViewModel.currentGroupId.collectAsState()
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
                            Text(user?.email ?: savedEmail ?: "No email found")                        }
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

                                    if (enabled) {

                                        if (currentGroupId.isNotEmpty()) {
                                            transactionViewModel.setSharedMode(true, currentGroupId)

                                            scope.launch {
                                                snackbarHostState.showSnackbar("Family Mode ON")
                                            }

                                        } else {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Please select a group first")
                                            }
                                        }

                                    } else {
                                        transactionViewModel.setSharedMode(false)

                                        scope.launch {
                                            snackbarHostState.showSnackbar("Personal Mode ON")
                                        }
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF3B82F6), // blue

                                    uncheckedThumbColor = if (isDark) Color.Black else Color.White,
                                    uncheckedTrackColor = if (isDark) Color.White else Color.Black
                                )
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
                    Text(
                        "Transaction History",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 12.dp, bottom = 6.dp)
                    )
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
                        val sharedPref = context.getSharedPreferences("user_session", 0)
                        sharedPref.edit().clear().apply()
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
                            val isInvited = g.invitedEmails.containsKey(
                                (user?.email ?: "").replace(".", ",")
                            )
                            Text(
                                when {
                                    isOwner -> "👑 Owner"
                                    isMember -> "👤 Member"
                                    else -> "📩 Invited"
                                },
                                fontSize = 11.sp
                            )

                            val hasAccess = isOwner || isMember || isInvited

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {

                                if (hasAccess) {

                                    Text(g.name, fontWeight = FontWeight.Bold)
                                    Text("Code: ${g.code}")
                                    Text("Owner: ${g.createdBy}")

                                    Text(
                                        when {
                                            isOwner -> "👑 Owner"
                                            isMember -> "👤 Member"
                                            else -> "📩 Invited"
                                        },
                                        fontSize = 11.sp
                                    )
                                    if (isInvited && !isMember) {
                                        Spacer(Modifier.height(6.dp))

                                        Button(
                                            onClick = {
                                                transactionViewModel.joinGroup(g.code) {
                                                    showGroupDialog = false
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Joined successfully ✅")
                                                    }
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Join Group")
                                        }
                                    }

                                    Spacer(Modifier.height(6.dp))

                                    // MEMBERS
                                    if (g.members.isNotEmpty()) {
                                        Text("Members:")
                                        g.members.keys.forEach {
                                            Text("• $it", fontSize = 12.sp)
                                        }
                                    }

                                    Spacer(Modifier.height(6.dp))

                                    // INVITED
                                    if (g.invitedEmails.isNotEmpty()) {
                                        Text("Invited:")

                                        g.invitedEmails.keys.forEach { safeEmail ->
                                            val email = safeEmail.replace(",", ".")

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(email)

                                                if (isOwner) {
                                                    Text(
                                                        "Remove",
                                                        color = MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.clickable {
                                                            transactionViewModel.removeInvite(g.id, safeEmail)
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {

                                        Button(
                                            onClick = {
                                                transactionViewModel.setSharedMode(true, g.id)
                                                showGroupDialog = false

                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Group activated ✅")
                                                }
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Activate")
                                        }

                                        Spacer(Modifier.width(8.dp))

                                        Button(
                                            onClick = {
                                                transactionViewModel.setSharedMode(false)
                                                showGroupDialog = false

                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Group deactivated ❌")
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Text("Deactivate")
                                        }
                                    }

                                } else {
                                    Text("No Access", color = Color.Gray)
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