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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // ⭐ FILTERED SONGS
    val filteredSongs = MutableStateFlow<List<AudioFile>>(emptyList())

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
        player.setOnSongCompleteListener {
            playNext()
        }
    }

    private val _likedSongs = MutableStateFlow<List<AudioFile>>(emptyList())
    val likedSongs: StateFlow<List<AudioFile>> = _likedSongs.asStateFlow()

    private val _playlists = MutableStateFlow<Map<String, List<AudioFile>>>(emptyMap())
    val playlists: StateFlow<Map<String, List<AudioFile>>> = _playlists.asStateFlow()

    // Toggle liked song
    fun toggleLikeSong(song: AudioFile) {
        val current = _likedSongs.value.toMutableList()
        if (current.contains(song)) {
            current.remove(song)
        } else {
            current.add(song)
        }
        _likedSongs.value = current
    }

    // Create a new playlist
    fun createPlaylist(name: String) {
        if (!_playlists.value.containsKey(name)) {
            _playlists.value = _playlists.value.toMutableMap().apply { put(name, emptyList()) }
        }
    }

    // Add song to a playlist
    fun addSongToPlaylist(playlistName: String, song: AudioFile) {
        val current = _playlists.value.toMutableMap()
        val playlist = current[playlistName]?.toMutableList() ?: mutableListOf()
        if (!playlist.contains(song)) {
            playlist.add(song)
            current[playlistName] = playlist
            _playlists.value = current
        }
    }

    private fun observePlayer() {
        viewModelScope.launch {
            player.currentPosition.collectLatest { _progress.value = it }
        }
        viewModelScope.launch {
            player.duration.collectLatest { _duration.value = it }
        }
    }
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applySearch()
    }

    private fun applySearch() {
        val query = _searchQuery.value.lowercase()

        filteredSongs.value =
            if (query.isBlank()) {
                _audioFiles.value
            } else {
                _audioFiles.value.filter { song ->
                    song.title.lowercase().contains(query) ||
                            song.artist.lowercase().contains(query)
                }
            }
    }

    fun loadSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repo.getAllAudioFiles()
            _audioFiles.value = list
            filteredSongs.value = list
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