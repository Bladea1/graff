package com.graffzone.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.graffzone.data.AppDatabase
import com.graffzone.data.PostEntity

@Composable
fun ExploreScreen(db: AppDatabase, onOpenPost: (Long) -> Unit) {
    var q by remember { mutableStateOf("") }
    val posts by db.postDao().observeSearch(q.ifBlank { "" }).collectAsState(initial = emptyList())

    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = q,
            onValueChange = { q = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Поиск по тегу/тексту") }
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(posts, key = { it.id }) { post ->
                Card(onClick = { onOpenPost(post.id) }) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(post.caption, style = MaterialTheme.typography.titleMedium)
                        TagChips(tags = post.tags.split(",").map { it.trim() })
                    }
                }
            }
        }
    }
}
