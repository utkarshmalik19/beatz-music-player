package com.utkarsh.beatzmusicplayer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utkarsh.beatzmusicplayer.model.AudioFile
import com.utkarsh.beatzmusicplayer.player.PlayerManager
import com.utkarsh.beatzmusicplayer.repository.AudioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: AudioRepository,
    context: Context
) : ViewModel() {

    private val player = PlayerManager(context)

    private val _audioFiles = MutableStateFlow<List<AudioFile>>(emptyList())
    val audioFiles: StateFlow<List<AudioFile>> = _audioFiles

    private val _currentSong = MutableStateFlow<AudioFile?>(null)
    val currentSong: StateFlow<AudioFile?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _progress = MutableStateFlow(0L)
    val progress: StateFlow<Long> = _progress

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    init {
        observePlayer()
    }

    private fun observePlayer() {
        viewModelScope.launch {
            player.currentPosition.collectLatest { _progress.value = it }
        }
        viewModelScope.launch {
            player.duration.collectLatest { _duration.value = it }
        }
    }

    fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            _audioFiles.value = repo.getAllAudioFiles()
        }
    }

    fun playSong(file: AudioFile) {
        _currentSong.value = file
        player.playSong(file.data)
        _isPlaying.value = true
    }

    fun togglePlayPause() {
        if (player.isPlaying()) {
            player.pause()
            _isPlaying.value = false
        } else {
            player.play()
            _isPlaying.value = true
        }
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
    }

    fun playNext() {
        val list = _audioFiles.value
        val current = _currentSong.value ?: return
        val index = list.indexOf(current)
        if (index == -1) return
        val nextIndex = (index + 1) % list.size
        playSong(list[nextIndex])
    }

    fun playPrevious() {
        val list = _audioFiles.value
        val current = _currentSong.value ?: return
        val index = list.indexOf(current)
        if (index == -1) return
        val prevIndex = if (index - 1 < 0) list.size - 1 else index - 1
        playSong(list[prevIndex])
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}