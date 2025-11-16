package com.utkarsh.beatzmusicplayer.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayerManager(context: Context) {

    private val exoPlayer = ExoPlayer.Builder(context).build()

    // Expose current position as StateFlow
    val currentPosition: StateFlow<Long> = flow {
        while (true) {
            emit(exoPlayer.currentPosition)
            delay(100)
        }
    }.stateIn(CoroutineScope(Dispatchers.Main), SharingStarted.Eagerly, 0L)

    // Expose duration as StateFlow
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    fun playSong(path: String) {
        exoPlayer.setMediaItem(MediaItem.fromUri(path))
        exoPlayer.prepare()
        exoPlayer.play()

        // Update duration once
        CoroutineScope(Dispatchers.Main).launch {
            delay(100) // small delay to ensure ExoPlayer has duration
            _duration.value = exoPlayer.duration.coerceAtLeast(0L)
        }
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun play() = exoPlayer.play()
    fun pause() = exoPlayer.pause()
    fun isPlaying(): Boolean = exoPlayer.isPlaying

    fun release() = exoPlayer.release()
}