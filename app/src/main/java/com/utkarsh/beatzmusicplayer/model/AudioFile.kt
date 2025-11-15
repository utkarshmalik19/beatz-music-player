package com.utkarsh.beatzmusicplayer.model

data class AudioFile(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val data: String
)