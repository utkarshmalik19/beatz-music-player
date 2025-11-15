package com.utkarsh.beatzmusicplayer.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class PlayerManager(context: Context) {

    private val exoPlayer = ExoPlayer.Builder(context).build()

    fun playSong(path: String) {
        exoPlayer.setMediaItem(MediaItem.fromUri(path))
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun play() {
        exoPlayer.play()
    }

    fun isPlaying(): Boolean = exoPlayer.isPlaying

    fun release() {
        exoPlayer.release()
    }
}