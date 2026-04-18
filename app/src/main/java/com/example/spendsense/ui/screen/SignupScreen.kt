package com.example.spendsense.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.spendsense.ui.theme.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spendsense.viewmodel.TransactionViewModel
import com.example.spendsense.R

@Composable
fun SignupScreen(navController: NavController) {

    val transactionViewModel: TransactionViewModel = viewModel()

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
            .appBackground()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),

        // 🔥 FIX 1: match login (REMOVE center)
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 🔥 FIX 2: same top spacing as login
        Spacer(Modifier.height(16.dp))
        // 🔥 FIX 3: logo size same as login style
        Image(
            painter = painterResource(id = R.drawable.signup_logo),
            contentDescription = "Signup Logo",
            modifier = Modifier.size(200.dp)        )

        // 🔥 FIX 4: tight spacing like login
        Spacer(Modifier.height(4.dp))
        Text(
            "Create Account",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(4.dp))
        Text(
            "Enter your details to begin your journey",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        // 🔥 FIX 5: same spacing as login before card
        Spacer(Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp), // 🔥 match login
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {

            Column(Modifier.padding(18.dp)) {

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = customTextFieldColors()
                )

                Spacer(Modifier.height(14.dp))

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = customTextFieldColors()
                )

                Spacer(Modifier.height(14.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation =
                        if (showPassword) VisualTransformation.None
                        else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword)
                                    Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = customTextFieldColors()
                )

                Spacer(Modifier.height(16.dp)) // 🔥 match login

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

                                transactionViewModel.setPersonalMode()
                                transactionViewModel.clearData()
                                transactionViewModel.loadAllData()

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
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(30.dp)) // 🔥 match login
                            .background(
                                Brush.horizontalGradient(
                                    listOf(GradientStart, GradientEnd)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (isLoading) "Creating Account..." else "Sign Up",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text(
                buildAnnotatedString {
                    append("Already have account? ")
                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("Login")
                    }
                },
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            "Secure signup powered by Firebase",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(20.dp))
    }
}