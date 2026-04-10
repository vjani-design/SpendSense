package com.example.spendsense.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun SummaryCard(
    title: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title)
            Text(
                amount,
                color = color,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}