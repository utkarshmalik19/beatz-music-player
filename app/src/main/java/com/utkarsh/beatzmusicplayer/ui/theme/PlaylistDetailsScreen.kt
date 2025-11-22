package com.utkarsh.beatzmusicplayer.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.utkarsh.beatzmusicplayer.model.AudioFile
import com.utkarsh.beatzmusicplayer.ui.SongItem

@Composable
fun PlaylistDetailsScreen(
    playlistName: String,
    songs: List<AudioFile>,
    onSongClick: (AudioFile) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {

        Text(
            playlistName,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (songs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), Alignment.Center) {
                Text("No songs yet", color = Color.Gray)
            }
        } else {
            LazyColumn {
                items(songs) { song ->
                    SongItem(
                        song = song,
                        isLiked = false,
                        onClick = { onSongClick(song) },
                        onToggleLike = {},
                        onAddToPlaylist = {}
                    )
                }
            }
        }
    }
}