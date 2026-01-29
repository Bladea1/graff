package com.graffzone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.graffzone.ui.AppRoot
import com.graffzone.ui.theme.GraffZoneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GraffZoneTheme {
                AppRoot()
            }
        }
    }
}
