package com.example.spendsense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spendsense.ui.theme.*

@Composable
fun AnalyticsScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .neonBackground()   // 🌌 upgraded background
            .padding(16.dp)
    ) {

        // ---------------- TITLE ----------------
        Text(
            text = "📊 Analytics",
            color = White,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------- CATEGORY CARD ----------------
        Text(
            text = "Category-wise Spending",
            color = White,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .glass(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📊 Pie Chart", color = White)
                Spacer(Modifier.height(6.dp))
                Text("Coming Soon", color = White.copy(0.7f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------- MONTHLY TREND ----------------
        Text(
            text = "Monthly Trend",
            color = White,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .glass(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📈 Bar Chart", color = White)
                Spacer(Modifier.height(6.dp))
                Text("Coming Soon", color = White.copy(0.7f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------- BACK BUTTON ----------------
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleMain
            )
        ) {
            Text("Back", color = White)
        }
    }
}