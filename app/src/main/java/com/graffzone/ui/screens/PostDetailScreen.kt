@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.graffzone.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.graffzone.data.AppDatabase
import com.graffzone.data.CommentEntity
import kotlinx.coroutines.launch

@Composable
fun PostDetailScreen(db: AppDatabase, postId: Long, onBack: () -> Unit) {
    var post by remember { mutableStateOf<com.graffzone.data.PostEntity?>(null) }
    val comments by db.commentDao().observeForPost(postId).collectAsState(initial = emptyList())
    var text by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(postId) {
        post = db.postDao().getById(postId)
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Пост") },
            navigationIcon = { TextButton(onClick = onBack) { Text("Назад") } }
        )

        if (post == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Не найдено")
            }
            return
        }

        val p = post!!

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(p.caption, style = MaterialTheme.typography.titleLarge)
                        TagChips(tags = p.tags.split(",").map { it.trim() })
                        if (!p.mediaUri.isNullOrBlank() && p.mediaType == "IMAGE") {
                            AsyncImage(model = Uri.parse(p.mediaUri), contentDescription = null, modifier = Modifier.fillMaxWidth())
                        }
                        if (!p.mediaUri.isNullOrBlank() && p.mediaType == "VIDEO") {
                            VideoPlayer(uri = p.mediaUri)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("❤ ${p.likes}")
                            TextButton(onClick = {
                                scope.launch {
                                    db.postDao().update(p.copy(likes = p.likes + 1))
                                    post = db.postDao().getById(postId)
                                }
                            }) { Text("Лайк") }
                        }
                        if (p.lat != null && p.lon != null) {
                            Text("📍 ${"%.5f".format(p.lat)}, ${"%.5f".format(p.lon)}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            item { Text("Комментарии", style = MaterialTheme.typography.titleMedium) }

            items(comments, key = { it.id }) { c ->
                Surface(tonalElevation = 1.dp) {
                    Column(Modifier.padding(10.dp)) {
                        Text("user#${c.authorId}", style = MaterialTheme.typography.labelMedium)
                        Text(c.text)
                    }
                }
            }
        }

        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = text, onValueChange = { text = it }, modifier = Modifier.weight(1f), label = { Text("Комментарий") })
            Button(
                enabled = text.isNotBlank(),
                onClick = {
                    scope.launch {
                        db.commentDao().insert(CommentEntity(postId = postId, authorId = 1, text = text.trim()))
                        text = ""
                    }
                }
            ) { Text("Ок") }
        }
    }
}

@Composable
private fun VideoPlayer(uri: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val exoPlayer = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
            prepare()
            playWhenReady = false
        }
    }
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
    AndroidView(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
            }
        }
    )
}
