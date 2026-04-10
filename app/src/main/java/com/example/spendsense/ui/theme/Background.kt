package com.example.spendsense.ui.theme

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun NeonBackground(modifier: Modifier = Modifier): Modifier {
    return modifier.background(
        brush = Brush.linearGradient(
            colors = listOf(
                DeepSpacePurple,
                IndigoBase,
                Color(0xFF120A2A)
            )
        )
    )
}