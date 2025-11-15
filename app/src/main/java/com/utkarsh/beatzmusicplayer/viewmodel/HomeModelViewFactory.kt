package com.utkarsh.beatzmusicplayer.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.utkarsh.beatzmusicplayer.repository.AudioRepository

class HomeViewModelFactory(
    private val repo: AudioRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo, context) as T
    }
}