package com.utkarsh.beatzmusicplayer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.utkarsh.beatzmusicplayer.ui.FullPlayerScreen
import com.utkarsh.beatzmusicplayer.ui.HomeScreen
import com.utkarsh.beatzmusicplayer.viewmodel.HomeViewModel

@Composable
fun AppNavGraph(viewModel: HomeViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(viewModel = viewModel, navController = navController)
        }
        composable("full_player") {
            FullPlayerScreen(viewModel = viewModel, navController = navController)
        }
    }
}