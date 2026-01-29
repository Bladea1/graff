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
import com.graffzone.data.ConversationEntity
import kotlinx.coroutines.launch

@Composable
fun ChatsScreen(db: AppDatabase, onOpenChat: (Long) -> Unit) {
    val chats by db.conversationDao().observeConversations().collectAsState(initial = emptyList())
    var newChatName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Чаты (локально)", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = newChatName,
                onValueChange = { newChatName = it },
                label = { Text("Ник собеседника") },
                modifier = Modifier.weight(1f)
            )
            Button(
                enabled = newChatName.isNotBlank(),
                onClick = {
                    scope.launch {
                        val id = db.conversationDao().insert(ConversationEntity(title = newChatName.trim()))
                        newChatName = ""
                        onOpenChat(id)
                    }
                }
            ) { Text("Создать") }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(chats, key = { it.id }) { c ->
                Card(modifier = Modifier.fillMaxWidth().clickable { onOpenChat(c.id) }) {
                    Column(Modifier.padding(12.dp)) {
                        Text(c.title, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
