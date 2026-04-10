package com.example.spendsense.ui.screen

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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.spendsense.ui.theme.*

@Composable
fun SignupScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = Firebase.firestore

    Column(
        modifier = Modifier
            .fillMaxSize()
            .neonBackground()   // 🌌 upgraded background
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        // ---------------- TITLE ----------------
        Text(
            text = "💜 Create Account",
            color = White,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(20.dp))

        // ---------------- NAME ----------------
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
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

        // ---------------- SHOW PASSWORD ----------------
        Row(verticalAlignment = Alignment.CenterVertically) {
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

        // ---------------- SIGNUP BUTTON ----------------
        Button(
            onClick = {

                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "All fields required", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->

                        val uid = authResult.user?.uid ?: ""

                        db.collection("users").document(uid).set(
                            hashMapOf(
                                "uid" to uid,
                                "name" to name,
                                "email" to email
                            )
                        )

                        isLoading = false

                        Toast.makeText(context, "Signup Successful 💜", Toast.LENGTH_SHORT).show()

                        navController.navigate("home") {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
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
                text = if (isLoading) "Creating Account..." else "Signup",
                color = White
            )
        }

        Spacer(Modifier.height(10.dp))

        // ---------------- LOGIN NAV ----------------
        TextButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Already have an account? Login", color = NeonPurple)
        }
    }
}