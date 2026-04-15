package com.example.spendsense.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SwipeToDeleteItem(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX < -200f) {
                            onDelete()
                        }
                        offsetX = 0f
                    }
                ) { _, dragAmount ->
                    offsetX += dragAmount
                }
            }
    ) {

        // RED BACKGROUND
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.Red)
        )

        // FRONT ITEM
        Box(
            modifier = Modifier
                .offset(x = offsetX.dp)
        ) {
            content()
        }
    }
}