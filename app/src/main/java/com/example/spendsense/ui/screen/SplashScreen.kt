package com.example.spendsense.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spendsense.R
import com.example.spendsense.ui.theme.*

@Composable
fun SplashScreen(navController: NavController) {

    val isDark = ThemeManager.isDarkTheme

    // 🔒 Prevent multiple navigation calls
    var hasNavigated by remember { mutableStateOf(false) }

    // 🔥 Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val gradient = if (isDark) {
        listOf(
            DarkBackground,
            DarkSurface,
            DarkSurfaceVariant
        )
    } else {
        listOf(
            Color(0xFF8FB8FF),
            Color(0xFFD6E4FF),
            Color(0xFFFFFFFF)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradient)),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔷 LOGO CARD (click → login)
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFFF1F5F9))
                    .clickable {
                        if (!hasNavigated) {
                            hasNavigated = true
                            navController.navigate("login") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.spendsense_logo),
                    contentDescription = null,
                    modifier = Modifier.size(95.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Spend")
                    }
                    append("Sense")
                },
                fontSize = 34.sp,
                color = Color(0xFF334E7D)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Smart Expense Tracker",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)

            )
        }
    }
}