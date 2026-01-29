package com.graffzone.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.graffzone.data.AppDatabase
import com.graffzone.data.UserEntity
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(db: AppDatabase) {
    val users by db.userDao().observeUsers().collectAsState(initial = emptyList())
    val me = users.firstOrNull()
    var username by remember(me?.username) { mutableStateOf(me?.username ?: "you") }
    var bio by remember(me?.bio) { mutableStateOf(me?.bio ?: "") }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Профиль (локально)", style = MaterialTheme.typography.titleLarge)

        if (me == null) {
            Button(onClick = {
                scope.launch {
                    db.userDao().insert(UserEntity(username = "you", bio = ""))
                }
            }) { Text("Создать локального пользователя") }
            return
        }

        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Ник") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") }, modifier = Modifier.fillMaxWidth())

        Button(
            enabled = username.isNotBlank(),
            onClick = {
                scope.launch {
                    db.userDao().update(me.copy(username = username.trim(), bio = bio.trim()))
                }
            }
        ) { Text("Сохранить") }

        Divider()

        Text("Пользователи в демо:", style = MaterialTheme.typography.titleMedium)
        users.forEach { u -> Text("• ${u.username}") }
    }
}
