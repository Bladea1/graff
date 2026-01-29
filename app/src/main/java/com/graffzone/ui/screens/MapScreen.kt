package com.graffzone.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.graffzone.data.AppDatabase
import com.graffzone.util.LatLon
import com.graffzone.util.haversineMeters
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

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
fun MapScreen(
    db: AppDatabase,
    onOpenPost: (Long) -> Unit,
    onRequestLocation: () -> Unit
) {
    val context = LocalContext.current
    val posts by db.postDao().observeWithLocation().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onRequestLocation) { Text("Разрешить гео") }
            Text("Постов на карте: ${posts.size}", modifier = Modifier.padding(top = 10.dp))
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                MapView(it).apply {
                    setMultiTouchControls(true)
                    controller.setZoom(12.0)
                    controller.setCenter(GeoPoint(52.3676, 4.9041)) // Amsterdam fallback
                }
            },
            update = { map ->
                map.overlays.removeAll { it is Marker }
                posts.forEach { p ->
                    val gp = GeoPoint(p.lat!!, p.lon!!)
                    val m = Marker(map).apply {
                        position = gp
                        title = p.caption.take(40)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        setOnMarkerClickListener { _, _ ->
                            onOpenPost(p.id)
                            true
                        }
                    }
                    map.overlays.add(m)
                }
                map.invalidate()
            }
        )
    }
}
