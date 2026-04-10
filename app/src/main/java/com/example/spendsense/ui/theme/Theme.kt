package com.example.spendsense.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = PurpleMain,
    background = DeepBlack,
    surface = BlackSoft,
    onBackground = White,
    onSurface = White,
    onPrimary = White
)

@Composable
fun SpendSenseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography,
        content = content
    )
}