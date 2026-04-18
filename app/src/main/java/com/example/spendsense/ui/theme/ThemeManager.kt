package com.example.spendsense.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object ThemeManager {
    private val _isDarkTheme = mutableStateOf(false)
    var isDarkTheme: Boolean
        get() = _isDarkTheme.value
        set(value) { _isDarkTheme.value = value }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }
}