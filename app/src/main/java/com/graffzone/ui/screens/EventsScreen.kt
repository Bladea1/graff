package com.graffzone.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.graffzone.data.AppDatabase
import com.graffzone.data.EventEntity
import kotlinx.coroutines.launch

@Composable
fun EventsScreen(db: AppDatabase, onRequestLocation: () -> Unit) {
    val events by db.eventDao().observeEvents().collectAsState(initial = emptyList())
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Ивенты (локально)", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Название") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Описание") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onRequestLocation) { Text("Гео") }
            Button(
                enabled = title.isNotBlank(),
                onClick = {
                    scope.launch {
                        db.eventDao().insert(
                            EventEntity(
                                creatorId = 1,
                                title = title.trim(),
                                description = desc.trim(),
                                startTime = System.currentTimeMillis() + 1000L*60*60*24
                            )
                        )
                        title = ""; desc = ""
                    }
                }
            ) { Text("Добавить") }
        }

        Divider()

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(events, key = { it.id }) { e ->
                Card {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(e.title, style = MaterialTheme.typography.titleMedium)
                        Text(e.description)
                    }
                }
            }
        }
    }
}
