package com.utkarsh.beatzmusicplayer.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.utkarsh.beatzmusicplayer.ui.HomeScreen
import com.utkarsh.beatzmusicplayer.ui.FullPlayerScreen
import com.utkarsh.beatzmusicplayer.ui.LibraryScreen
import com.utkarsh.beatzmusicplayer.ui.LikedSongsScreen
import com.utkarsh.beatzmusicplayer.ui.MiniPlayer
import com.utkarsh.beatzmusicplayer.ui.PlaceholderScreen
import com.utkarsh.beatzmusicplayer.viewmodel.HomeViewModel

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home)
    object Explore : BottomNavItem("explore", "Explore", Icons.Filled.Explore)
    object Library : BottomNavItem("library", "Library", Icons.Filled.LibraryMusic)
    object Profile : BottomNavItem("profile", "Profile", Icons.Filled.Person)
}

@Composable
fun AppNavGraph(viewModel: HomeViewModel) {
    val navController = rememberNavController()
    val likedSongs by viewModel.likedSongs.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val duration by viewModel.duration.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Explore,
                    BottomNavItem.Library,
                    BottomNavItem.Profile
                ).forEach { item ->
                    NavigationBarItem(
                        selected = navController.currentDestination?.route == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                restoreState = true
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(item.icon, item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {

            // SCREEN CONTENT
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.padding(padding)
            ) {

                composable(BottomNavItem.Home.route) {
                    HomeScreen(viewModel, navController)
                }

                composable(BottomNavItem.Explore.route) {
                    PlaceholderScreen("Explore")
                }

                composable(BottomNavItem.Library.route) {
                    LibraryScreen(
                        likedSongs = likedSongs,
                        playlists = playlists,
                        onLikedSongsClick = { navController.navigate("liked_songs") },
                        onPlaylistClick = {},
                        onSongClick = { viewModel.playSong(it) }
                    )
                }

                composable("liked_songs") {
                    LikedSongsScreen(
                        likedSongs = likedSongs,
                        onSongClick = { viewModel.playSong(it) },
                        onToggleLike = { viewModel.toggleLikeSong(it) }
                    )
                }

                composable("full_player") {
                    FullPlayerScreen(viewModel, navController)
                }

                composable(BottomNavItem.Profile.route) {
                    PlaceholderScreen("Profile")
                }
            }


            if (currentRoute != "full_player") {
                MiniPlayer(
                    currentSong = currentSong,
                    isPlaying = isPlaying,
                    progress = progress,
                    duration = duration,
                    onPlayPause = { viewModel.togglePlayPause() },
                    onNext = { viewModel.playNext() },
                    onPrevious = { viewModel.playPrevious() },
                    onSeek = { viewModel.seekTo(it) },
                    onOpenPlayer = { navController.navigate("full_player") },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 68.dp) // so it stays above bottom nav
                )
            }
        }
    }
}