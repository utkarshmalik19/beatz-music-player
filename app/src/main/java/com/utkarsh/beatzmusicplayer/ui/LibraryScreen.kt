package com.utkarsh.beatzmusicplayer.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.utkarsh.beatzmusicplayer.model.AudioFile

@Composable
fun LibraryScreen(
    likedSongs: List<AudioFile>,
    playlists: Map<String, List<AudioFile>>,
    onPlaylistClick: (String) -> Unit = {},
    onSongClick: (AudioFile) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text(
            text = "Your Playlists",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(playlists.keys.toList()) { playlist ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onPlaylistClick(playlist) }
                ) {
                    Text(
                        text = playlist,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Liked Songs",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(likedSongs) { song ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onSongClick(song) }
                ) {
                    Text(
                        text = song.title,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}