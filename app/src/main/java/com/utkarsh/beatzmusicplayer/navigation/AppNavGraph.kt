package com.utkarsh.beatzmusicplayer.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.utkarsh.beatzmusicplayer.ui.HomeScreen
import com.utkarsh.beatzmusicplayer.ui.FullPlayerScreen
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
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Explore,
        BottomNavItem.Library,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            var currentRoute by remember { mutableStateOf(BottomNavItem.Home.route) }

            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            currentRoute = item.route
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(item.label) },
                        icon = { Icon(item.icon, contentDescription = item.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(viewModel = viewModel, navController = navController)
            }
            composable(BottomNavItem.Explore.route) {
                PlaceholderScreen("Explore")
            }
            composable(BottomNavItem.Library.route) {
                PlaceholderScreen("Library")
            }
            composable(BottomNavItem.Profile.route) {
                PlaceholderScreen("Profile")
            }
            composable("full_player") {
                FullPlayerScreen(viewModel = viewModel, navController = navController)
            }
        }
    }
}