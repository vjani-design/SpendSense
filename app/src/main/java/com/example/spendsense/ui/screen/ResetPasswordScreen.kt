package com.example.spendsense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spendsense.data.repository.AuthRepository

@Composable
fun ResetPasswordScreen(
    docId: String,
    navController: NavController
) {

    val repo = AuthRepository()

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Create New Password", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("New Password") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                when {
                    password.isEmpty() -> message = "Enter password"
                    password.length < 6 -> message = "Min 6 characters"
                    password != confirmPassword -> message = "Passwords do not match"
                    else -> {
                        repo.updatePasswordInFirestore(docId, password) { success, msg ->
                            message = msg ?: ""
                            if (success) navController.popBackStack()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Password")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(message)
    }
}