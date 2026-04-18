package com.example.spendsense.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

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
                        if (offsetX > 200f) {
                            onDelete()
                        }
                        offsetX = 0f
                    }
                ) { _, dragAmount ->

                    offsetX += dragAmount

                    // allow only left → right
                    if (offsetX < 0f) offsetX = 0f

                    // limit
                    if (offsetX > 400f) offsetX = 400f
                }
            }
    ) {

        // 🔴 Background
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

        // 🟢 Foreground (FIXED OFFSET)
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(offsetX.roundToInt(), 0)
                }
        ) {
            content()
        }
    }
}