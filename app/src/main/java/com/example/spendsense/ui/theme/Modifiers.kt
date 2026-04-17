package com.example.spendsense.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.appBackground(): Modifier {

    val colors = MaterialTheme.colorScheme
    val isDark = ThemeManager.isDarkTheme

    return this.background(
        brush = Brush.verticalGradient(
            colors = if (isDark) {
                listOf(
                    Color(0xFF22325A),   // 🔽 slightly softer top blue
                    colors.background,
                    colors.background
                )
            } else {
                listOf(
                    Color(0xFF8FB8FF),
                    Color(0xFFD6E4FF),
                    Color(0xFFFFFFFF)
                )
            }
        )
    ).background(
        brush = Brush.radialGradient(
            colors = if (isDark) {
                listOf(
                    Color(0xFF3B82F6).copy(alpha = 0.18f), // 🔽 reduced glow
                    Color.Transparent
                )
            } else {
                listOf(
                    Color(0xFF3B82F6).copy(alpha = 0.35f),
                    Color.Transparent
                )
            },
            radius = 600f,
            center = Offset(0f, 0f)
        )
    )
}

@Composable
fun Modifier.appGlass(): Modifier {

    val colors = MaterialTheme.colorScheme

    return this
        .clip(RoundedCornerShape(20.dp))
        .background(
            colors.surface.copy(alpha = if (ThemeManager.isDarkTheme) 0.88f else 0.95f)
        )
        .border(
            1.dp,
            colors.outline.copy(alpha = if (ThemeManager.isDarkTheme) 0.25f else 0.5f),
            RoundedCornerShape(20.dp)
        )
}