package com.utkarsh.beatzmusicplayer.utils

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import androidx.compose.ui.graphics.Color

@SuppressLint("ContextCastToActivity")
@Composable
fun SetSystemBarsDarkTheme(darkTheme: Boolean = true) {
    val view = LocalView.current
    val activity = LocalContext.current as? Activity ?: return

    SideEffect {
        val window = activity.window
        window.statusBarColor = Color.Transparent.toArgb() // or set a specific dark color
        window.navigationBarColor = if (darkTheme) 0xFF121212.toInt() else 0xFFFFFFFF.toInt()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
    }
}