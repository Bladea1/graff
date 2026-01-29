package com.graffzone.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.graffzone.data.AppDatabase
import com.graffzone.data.PostEntity
import kotlinx.coroutines.launch

@Composable
fun FeedScreen(db: AppDatabase, onOpenPost: (Long) -> Unit) {
    val posts by db.postDao().observeFeed().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    LazyColumn(contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(posts, key = { it.id }) { post ->
            PostCard(
                post = post,
                onOpen = { onOpenPost(post.id) },
                onLike = {
                    scope.launch { db.postDao().update(post.copy(likes = post.likes + 1)) }
                }
            )
        }
    }
}

@Composable
private fun PostCard(post: PostEntity, onOpen: () -> Unit, onLike: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onOpen() }) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(post.caption, style = MaterialTheme.typography.titleMedium)
            TagChips(tags = post.tags.split(",").map { it.trim() })
            MediaPreview(mediaUri = post.mediaUri, mediaType = post.mediaType)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("❤ ${post.likes}")
                TextButton(onClick = onLike) { Text("Лайк") }
                if (post.lat != null && post.lon != null) Text("📍")
            }
        }
    }
}
