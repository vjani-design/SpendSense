package com.example.spendsense.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.example.spendsense.ui.theme.*

@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .neonBackground()   // 🌌 upgraded background
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // ---------------- TITLE ----------------
        Text(
            text = "👋 Welcome Back",
            color = White,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(25.dp))

        // ---------------- EMAIL ----------------
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .glass(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedTextColor = White,
                unfocusedTextColor = White,
                cursorColor = NeonPurple,
                focusedIndicatorColor = NeonPink,
                unfocusedIndicatorColor = White.copy(0.3f)
            )
        )

        Spacer(Modifier.height(12.dp))

        // ---------------- PASSWORD ----------------
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation =
                if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .glass(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedTextColor = White,
                unfocusedTextColor = White,
                cursorColor = NeonPurple,
                focusedIndicatorColor = NeonPink,
                unfocusedIndicatorColor = White.copy(0.3f)
            )
        )

        Spacer(Modifier.height(8.dp))

        // ---------------- SHOW PASSWORD TOGGLE ----------------
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = showPassword,
                onCheckedChange = { showPassword = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = PurpleMain
                )
            )
            Text("Show Password", color = White)
        }

        Spacer(Modifier.height(20.dp))

        // ---------------- LOGIN BUTTON ----------------
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
                        isLoading = false
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        val message = e.localizedMessage ?: "Unknown error"
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        Log.e("LoginScreen", "Login failed", e)
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleMain
            )
        ) {
            Text(
                text = if (isLoading) "Logging in..." else "Login",
                color = White
            )
        }

        Spacer(Modifier.height(12.dp))

        // ---------------- SIGNUP LINK ----------------
        TextButton(
            onClick = { navController.navigate("signup") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Don't have an account? Sign up", color = NeonPurple)
        }
    }
}