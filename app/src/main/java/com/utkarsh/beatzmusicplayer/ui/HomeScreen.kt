package com.utkarsh.beatzmusicplayer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utkarsh.beatzmusicplayer.model.AudioFile
import com.utkarsh.beatzmusicplayer.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val songs by viewModel.audioFiles.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSongs()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Beatz Music Player") })
        }
    ) { padding ->

        // IMPORTANT: Wrap everything in a Box
        Box(modifier = Modifier.padding(padding)) {

            LazyColumn {
                items(songs) { audio ->
                    SongItem(
                        song = audio,
                        onClick = {
                            viewModel.playSong(audio)  // <-- FIXED to pass AudioFile, not path
                        }
                    )
                }
            }

            // MINI PLAYER AT THE BOTTOM
            MiniPlayer(
                currentSong = currentSong,
                isPlaying = isPlaying,
                onPlayPause = { viewModel.togglePlayPause() },
                onNext = { viewModel.playNext() },
                onPrevious = { viewModel.playPrevious() },
                modifier = Modifier.align(Alignment.BottomCenter),
                onOpenPlayer = { /* TODO: navigate to full player */ }
            )
        }
    }
}

@Composable
fun SongItem(song: AudioFile, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(song.title, fontSize = 18.sp)
        Text(song.artist, color = Color.Gray)
    }
}