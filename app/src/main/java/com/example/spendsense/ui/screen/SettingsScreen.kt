package com.example.spendsense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spendsense.ui.theme.*

@Composable
fun SettingsScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .neonBackground()   // 🌌 FIX: use gradient background
            .padding(16.dp)
    ) {

        // ---------------- TITLE ----------------
        Text(
            text = "⚙️ Settings",
            color = White,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------- NAME FIELD ----------------
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

        Spacer(modifier = Modifier.height(12.dp))

        // ---------------- CURRENCY FIELD ----------------
        TextField(
            value = currency,
            onValueChange = { currency = it },
            label = { Text("Currency (₹ / $ / €)") },
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

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------- SAVE BUTTON ----------------
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleMain
            )
        ) {
            Text("Save Changes", color = White)
        }
    }
}