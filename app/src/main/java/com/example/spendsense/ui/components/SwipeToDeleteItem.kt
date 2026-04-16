package com.example.spendsense.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Delete
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
                        // 👉 LEFT → RIGHT swipe delete (positive offset)
                        if (offsetX > 200f) {
                            onDelete()
                        }
                        offsetX = 0f
                    }
                ) { _, dragAmount ->

                    offsetX += dragAmount

                    // ❗ allow ONLY left → right swipe
                    if (offsetX < 0f) offsetX = 0f

                    // limit swipe distance
                    if (offsetX > 400f) offsetX = 400f
                }
            }
    ) {

        // 🔴 CLEAN BACKGROUND (NO ICON, NO TEXT)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(
                    Color.Red.copy(
                        alpha = (offsetX / 400f).coerceIn(0f, 1f)
                    )
                )
        )

        // 🟢 YOUR ITEM (UNCHANGED)
        Box(
            modifier = Modifier
                .offset(x = offsetX.dp)
        ) {
            content()
        }
    }
}