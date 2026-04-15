package com.example.spendsense.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.appBackground(): Modifier {
    return this.background(MaterialTheme.colorScheme.background)
}

@Composable
fun Modifier.appGlass(): Modifier {
    return this
        .clip(RoundedCornerShape(20.dp))
        .background(MaterialTheme.colorScheme.surface)
        .border(
            1.dp,
            MaterialTheme.colorScheme.outline,
            RoundedCornerShape(20.dp)
        )
}