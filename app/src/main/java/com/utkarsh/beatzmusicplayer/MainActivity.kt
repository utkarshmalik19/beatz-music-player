package com.utkarsh.beatzmusicplayer

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.utkarsh.beatzmusicplayer.repository.AudioRepository
import com.utkarsh.beatzmusicplayer.ui.HomeScreen
import com.utkarsh.beatzmusicplayer.ui.theme.BeatzMusicPlayerTheme
import com.utkarsh.beatzmusicplayer.viewmodel.HomeViewModel
import com.utkarsh.beatzmusicplayer.viewmodel.HomeViewModelFactory

class MainActivity : ComponentActivity() {
    private val permission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_AUDIO
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()

        val repo = AudioRepository(this)

        setContent {
            BeatzMusicPlayerTheme {

                // Check if permission is granted
                val hasPermission = ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    // Now it's safe to load ViewModel and fetch songs
                    val viewModel: HomeViewModel = viewModel(
                        factory = HomeViewModelFactory(repo, this)
                    )
                    HomeScreen(viewModel)

                } else {
                    // UI shown before permission is granted
                    PermissionScreen {
                        requestPermission()
                    }
                }
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission),
            100
        )
    }
}

@Composable
fun PermissionScreen(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Please allow storage permission to load music.")
        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
    }
}