package com.utkarsh.beatzmusicplayer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utkarsh.beatzmusicplayer.model.AudioFile
import com.utkarsh.beatzmusicplayer.player.PlayerManager
import com.utkarsh.beatzmusicplayer.repository.AudioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: AudioRepository,
    private val context: Context
) : ViewModel() {

    private val player = PlayerManager(context)

    private val _audioFiles = MutableStateFlow(emptyList<AudioFile>())
    val audioFiles = _audioFiles.asStateFlow()

    private val _currentSong = MutableStateFlow<AudioFile?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

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
