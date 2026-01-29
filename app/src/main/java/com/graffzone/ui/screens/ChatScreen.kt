@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.graffzone.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.graffzone.data.AppDatabase
import com.graffzone.data.MessageEntity
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(db: AppDatabase, conversationId: Long, onBack: () -> Unit) {
    val msgs by db.messageDao().observeMessages(conversationId).collectAsState(initial = emptyList())
    var text by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Чат") },
            navigationIcon = { TextButton(onClick = onBack) { Text("Назад") } }
        )
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(msgs, key = { it.id }) { m ->
                Surface(tonalElevation = 1.dp, shape = MaterialTheme.shapes.medium) {
                    Column(Modifier.padding(10.dp)) {
                        Text(m.sender, style = MaterialTheme.typography.labelMedium)
                        Text(m.text)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                label = { Text("Сообщение") }
            )
            Button(
                enabled = text.isNotBlank(),
                onClick = {
                    scope.launch {
                        db.messageDao().insert(
                            MessageEntity(
                                conversationId = conversationId,
                                sender = "you",
                                text = text.trim()
                            )
                        )
                        text = ""
                    }
                }
            ) { Text("Отпр.") }
        }
    }
}
