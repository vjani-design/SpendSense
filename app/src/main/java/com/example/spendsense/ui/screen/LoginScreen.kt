package com.example.spendsense.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.example.spendsense.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendsense.viewmodel.TransactionViewModel

@Composable
fun LoginScreen(navController: NavController) {

    val isDark = ThemeManager.isDarkTheme   // ✅ FIXED

    val transactionViewModel: TransactionViewModel = viewModel()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .appBackground()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "👋 Welcome Back",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(25.dp))

        // EMAIL
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .appGlass(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        )

        Spacer(Modifier.height(12.dp))

        // PASSWORD
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation =
                if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .appGlass(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        )

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {

            Checkbox(
                checked = showPassword,
                onCheckedChange = { showPassword = it }
            )

            Text("Show Password", color = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {

                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Email and Password required", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {

                        val uid = FirebaseAuth.getInstance().currentUser?.uid

                        if (uid != null) {
                            transactionViewModel.clearData()
                        }

                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
                        Log.e("LoginScreen", "Login failed", e)
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (isLoading) "Logging in..." else "Login",
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(Modifier.height(12.dp))

        TextButton(
            onClick = { navController.navigate("signup") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                "Don't have an account? Sign up",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}