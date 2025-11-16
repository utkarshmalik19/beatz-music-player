package com.utkarsh.beatzmusicplayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.utkarsh.beatzmusicplayer.model.AudioFile
import com.utkarsh.beatzmusicplayer.player.PlayerManager
import com.utkarsh.beatzmusicplayer.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPlayerScreen(
    viewModel: HomeViewModel,
    navController: NavHostController
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val duration by viewModel.duration.collectAsState()

    val song = currentSong ?: return

    // Slider state for smooth dragging
    val sliderPosition = remember { mutableStateOf(progress.toFloat()) }
    val isDragging = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Beatz Music Player") })
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AlbumArtFromMetadata(song.data, modifier = Modifier.size(300.dp).clip(
                    RoundedCornerShape(24.dp)
                ))

                Spacer(modifier = Modifier.height(24.dp))
                Text(song.title)
                Text(song.artist)

                Spacer(modifier = Modifier.height(24.dp))

                // Custom slider
                CustomSlider(
                    value = sliderPosition.value,
                    onValueChange = { sliderPosition.value = it },
                    onValueChangeFinished = { viewModel.seekTo(sliderPosition.value.toLong()) },
                    valueRange = 0f..duration.coerceAtLeast(1L).toFloat(),
                    trackHeight = 6.dp,
                    thumbRadius = 8.dp,
                    horizontalPadding = 16.dp,
                    activeTrackBrush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF00BCD4), MaterialTheme.colorScheme.primary)
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatTime(sliderPosition.value.toLong()))
                    Text(formatTime(duration))
                }
                // Update slider when not dragging
                LaunchedEffect(progress) {
                    if (!isDragging.value) {
                        sliderPosition.value = progress.toFloat()
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row {
                    IconButton(onClick = { viewModel.playPrevious() }) {
                        Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous")
                    }
                    IconButton(onClick = { viewModel.togglePlayPause() }) {
                        Icon(
                            if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "Play/Pause"
                        )
                    }
                    IconButton(onClick = { viewModel.playNext() }) {
                        Icon(Icons.Filled.SkipNext, contentDescription = "Next")
                    }
                }
            }
        }
    }
}

fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}