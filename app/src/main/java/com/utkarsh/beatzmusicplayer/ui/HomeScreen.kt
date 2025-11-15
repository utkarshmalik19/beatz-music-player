package com.utkarsh.beatzmusicplayer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.utkarsh.beatzmusicplayer.R
import com.utkarsh.beatzmusicplayer.model.AudioFile
import com.utkarsh.beatzmusicplayer.utils.getAlbumArt
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
    val context = LocalContext.current
    val bitmap = getAlbumArt(song.data)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) Color(0xFF1F1F1F) else Color(0xFFF5F5F5)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable(
                onClick = onClick,
            ),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art
            Image(
                painter = rememberAsyncImagePainter(
                    model = bitmap ?: R.drawable.ic_launcher_background
                ),
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Song info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = song.artist,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 3-dot menu
            IconButton(onClick = { /* TODO: menu actions */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}