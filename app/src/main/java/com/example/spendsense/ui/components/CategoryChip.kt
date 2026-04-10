package com.example.spendsense.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun CategoryChip(text: String, selected: Boolean, onClick: () -> Unit) {

    Surface(
        color = if (selected) Color(0xFF1976D2) else Color.Gray,
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp),
            color = Color.White
        )
    }
}