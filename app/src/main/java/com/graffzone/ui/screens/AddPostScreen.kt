package com.graffzone.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.graffzone.data.AppDatabase
import com.graffzone.data.PostEntity
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
private fun getLastKnownLocation(context: Context): Location? {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val providers = lm.getProviders(true)
    for (p in providers.reversed()) {
        val l = lm.getLastKnownLocation(p)
        if (l != null) return l
    }
    return null
}

@Composable
fun AddPostScreen(
    db: AppDatabase,
    onDone: () -> Unit,
    onRequestLocation: () -> Unit
) {
    val context = LocalContext.current
    var caption by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("ru,graff") }
    var mediaUri by remember { mutableStateOf<String?>(null) }
    var mediaType by remember { mutableStateOf("NONE") }
    var attachLocation by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            mediaUri = uri.toString()
            mediaType = "IMAGE"
        }
    }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            mediaUri = uri.toString()
            mediaType = "VIDEO"
        }
    }

    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Новый пост", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = caption, onValueChange = { caption = it }, label = { Text("Текст") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Теги (через запятую)") }, modifier = Modifier.fillMaxWidth())

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { imagePicker.launch("image/*") }) { Text("Фото") }
            Button(onClick = { videoPicker.launch("video/*") }) { Text("Видео") }
            TextButton(onClick = { mediaUri = null; mediaType = "NONE" }) { Text("Убрать") }
        }

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Switch(checked = attachLocation, onCheckedChange = { attachLocation = it })
            Text("Прикрепить локацию")
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onRequestLocation) { Text("Разрешить") }
        }

        Button(
            enabled = caption.isNotBlank(),
            onClick = {
                scope.launch {
                    val loc = if (attachLocation) getLastKnownLocation(context) else null
                    db.postDao().insert(
                        PostEntity(
                            authorId = 1,
                            caption = caption.trim(),
                            tags = tags,
                            mediaUri = mediaUri,
                            mediaType = mediaType,
                            lat = loc?.latitude,
                            lon = loc?.longitude
                        )
                    )
                    onDone()
                }
            }
        ) { Text("Опубликовать") }
    }
}
