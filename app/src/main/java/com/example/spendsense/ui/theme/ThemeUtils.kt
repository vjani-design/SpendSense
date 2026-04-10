package com.example.spendsense.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// 🌌 BACKGROUND GRADIENT
fun Modifier.neonBackground(): Modifier {
    return this.background(
        brush = Brush.verticalGradient(
            colors = listOf(
                DeepSpacePurple,
                IndigoBase,
                DarkVoid
            )
        )
    )
}

// 💎 GLASSMORPHISM EFFECT
fun Modifier.glass(): Modifier {
    return this
        .clip(RoundedCornerShape(20.dp))
        .background(Color.White.copy(alpha = 0.08f))
        .border(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.15f),
            shape = RoundedCornerShape(20.dp)
        )
}