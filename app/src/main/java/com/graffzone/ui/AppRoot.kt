package com.graffzone.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.graffzone.data.AppDatabase
import com.graffzone.data.SeedData
import com.graffzone.ui.screens.*

private sealed class Dest(val route: String) {
    data object Feed : Dest("feed")
    data object Explore : Dest("explore")
    data object Map : Dest("map")
    data object Events : Dest("events")
    data object Chats : Dest("chats")
    data object Profile : Dest("profile")
    data object AddPost : Dest("add_post")
    data object Post : Dest("post/{postId}") {
        fun route(postId: Long) = "post/$postId"
    }
    data object Chat : Dest("chat/{conversationId}") {
        fun route(conversationId: Long) = "chat/$conversationId"
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }

    // Seed sample content once
    LaunchedEffect(Unit) {
        SeedData.ensureSeeded(db)
    }

    val nav = rememberNavController()
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* ignore */ }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate(Dest.AddPost.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Add post")
            }
        },
        bottomBar = { BottomBar(nav) }
    ) { inner ->
        AppNavHost(
            nav = nav,
            db = db,
            inner = inner,
            onRequestLocation = {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        )
    }
}

@Composable
private fun BottomBar(nav: NavHostController) {
    val current by nav.currentBackStackEntryAsState()
    val route = current?.destination?.route ?: ""

    data class Item(val label: String, val icon: @Composable () -> Unit, val dest: Dest)

    val items = listOf(
        Item("Лента", { Icon(Icons.Default.Home, contentDescription = null) }, Dest.Feed),
        Item("Поиск", { Icon(Icons.Default.Search, contentDescription = null) }, Dest.Explore),
        Item("Карта", { Icon(Icons.Default.Map, contentDescription = null) }, Dest.Map),
        Item("Ивенты", { Icon(Icons.Default.Event, contentDescription = null) }, Dest.Events),
        Item("Чаты", { Icon(Icons.Default.ChatBubble, contentDescription = null) }, Dest.Chats),
        Item("Профиль", { Icon(Icons.Default.Person, contentDescription = null) }, Dest.Profile),
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = route.startsWith(item.dest.route),
                onClick = {
                    nav.navigate(item.dest.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(nav.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                },
                icon = item.icon,
                label = { Text(item.label) }
            )
        }
    }
}

@Composable
private fun AppNavHost(
    nav: NavHostController,
    db: AppDatabase,
    inner: PaddingValues,
    onRequestLocation: () -> Unit
) {
    NavHost(
        navController = nav,
        startDestination = Dest.Feed.route,
        modifier = Modifier.padding(inner)
    ) {
        composable(Dest.Feed.route) {
            FeedScreen(db = db, onOpenPost = { nav.navigate(Dest.Post.route(it)) })
        }
        composable(Dest.Explore.route) {
            ExploreScreen(db = db, onOpenPost = { nav.navigate(Dest.Post.route(it)) })
        }
        composable(Dest.Map.route) {
            MapScreen(
                db = db,
                onOpenPost = { nav.navigate(Dest.Post.route(it)) },
                onRequestLocation = onRequestLocation
            )
        }
        composable(Dest.Events.route) {
            EventsScreen(db = db, onRequestLocation = onRequestLocation)
        }
        composable(Dest.Chats.route) {
            ChatsScreen(db = db, onOpenChat = { nav.navigate(Dest.Chat.route(it)) })
        }
        composable(Dest.Profile.route) {
            ProfileScreen(db = db)
        }
        composable(Dest.AddPost.route) {
            AddPostScreen(
                db = db,
                onDone = { nav.popBackStack() },
                onRequestLocation = onRequestLocation
            )
        }
        composable(Dest.Post.route) { backStack ->
            val postId = backStack.arguments?.getString("postId")?.toLongOrNull() ?: 0L
            PostDetailScreen(db = db, postId = postId, onBack = { nav.popBackStack() })
        }
        composable(Dest.Chat.route) { backStack ->
            val conversationId = backStack.arguments?.getString("conversationId")?.toLongOrNull() ?: 0L
            ChatScreen(db = db, conversationId = conversationId, onBack = { nav.popBackStack() })
        }
    }
}
