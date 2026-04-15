package com.example.spendsense.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object ThemeManager {
    var isDarkTheme by mutableStateOf(true)

    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }
}