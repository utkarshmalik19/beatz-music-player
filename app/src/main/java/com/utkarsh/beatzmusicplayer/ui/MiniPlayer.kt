package com.utkarsh.beatzmusicplayer.ui

import android.graphics.Bitmap
import android.view.WindowInsets
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.utkarsh.beatzmusicplayer.R
import com.utkarsh.beatzmusicplayer.model.AudioFile
import com.utkarsh.beatzmusicplayer.player.PlayerManager
import com.utkarsh.beatzmusicplayer.utils.getAlbumArt

@Composable
fun MiniPlayer(
    currentSong: AudioFile?,
    isPlaying: Boolean,
    progress: Long,
    duration: Long,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onOpenPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (currentSong == null) return

    val albumArtBitmap = remember(currentSong.data) { mutableStateOf(getAlbumArt(currentSong.data)) }
    val painter = rememberAsyncImagePainter(model = albumArtBitmap.value ?: R.drawable.album_placeholder)

    val sliderPosition = remember { mutableStateOf(progress.toFloat()) }

// Reset slider when song changes
    LaunchedEffect(currentSong) {
        sliderPosition.value = 0f
    }

// Update slider as song progresses
    LaunchedEffect(progress) {
        sliderPosition.value = progress.toFloat()
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onOpenPlayer() },
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        color = if (isSystemInDarkTheme()) Color(0xFF1F1F1F) else Color(0xFFF5F5F5)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Image(
                    painter = painter,
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = currentSong.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (currentSong.artist.isNotEmpty()) {
                        Text(
                            text = currentSong.artist,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onPrevious) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous")
                }
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Next")
                }
            }

            if (duration > 0L) {
                CustomSlider(
                    value = sliderPosition.value,
                    onValueChange = { sliderPosition.value = it },
                    onValueChangeFinished = { onSeek(sliderPosition.value.toLong()) },
                    valueRange = 0f..duration.toFloat(),
                    trackHeight = 4.dp,
                    thumbRadius = 4.dp,
                    horizontalPadding = 16.dp,
                    activeTrackColor = MaterialTheme.colorScheme.primary,


                )


            }
        }
    }
}