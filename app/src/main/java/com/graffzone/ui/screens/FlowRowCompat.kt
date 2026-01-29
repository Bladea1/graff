package com.graffzone.ui.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Простая заглушка вместо FlowRow (чтобы не тянуть дополнительные зависимости).
 * Если чипов много, они просто обрежутся по ширине.
 */
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: androidx.compose.foundation.layout.Arrangement.Horizontal = androidx.compose.foundation.layout.Arrangement.Start,
    content: @Composable () -> Unit
) {
    Row(modifier = modifier.padding(vertical = 2.dp), horizontalArrangement = horizontalArrangement) {
        content()
    }
}
