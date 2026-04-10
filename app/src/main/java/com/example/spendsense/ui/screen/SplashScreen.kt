package com.example.spendsense.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spendsense.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DeepSpacePurple,
                        IndigoBase,
                        DarkVoid
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔮 App Title
            Text(
                text = "SpendSense",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Smart Expense Tracker 💜",
                fontSize = 14.sp,
                color = White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 🔵 Loading Indicator (modern look)
            CircularProgressIndicator(
                color = NeonPurple,
                strokeWidth = 3.dp
            )
        }
    }
}