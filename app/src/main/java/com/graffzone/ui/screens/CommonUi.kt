package com.graffzone.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun TagChips(tags: List<String>, modifier: Modifier = Modifier) {
    FlowRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        tags.filter { it.isNotBlank() }.take(8).forEach { t ->
            AssistChip(onClick = { }, label = { Text("#$t") })
        }
    }
}

@Composable
fun MediaPreview(mediaUri: String?, mediaType: String, modifier: Modifier = Modifier) {
    if (mediaUri.isNullOrBlank() || mediaType == "NONE") return
    val uri = Uri.parse(mediaUri)
    if (mediaType == "IMAGE") {
        AsyncImage(model = uri, contentDescription = null, modifier = modifier.fillMaxWidth())
    } else {
        // Video handled in detail screen to keep feed lightweight
        Card(modifier = modifier.fillMaxWidth()) {
            Box(Modifier.padding(16.dp)) {
                Text("Видео прикреплено (открой пост, чтобы воспроизвести).")
            }
        }
    }
}
