package com.jcxdc.musium.ui.viewmodel

import android.content.ContentResolver
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jcxdc.musium.SongDataSource
import com.jcxdc.musium.db.RemoteAudioItem
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinApplication.Companion.init
import javax.inject.Inject

@HiltViewModel
class LocalAudioViewModel @Inject constructor(
    private val songDataSource: SongDataSource
) : ViewModel() {

    private val _localAudios = MutableLiveData<List<RemoteAudioItem>>()
    val localAudios: LiveData<List<RemoteAudioItem>> = _localAudios

    private val _currentAudioIndex = MutableLiveData<Int>()
    val currentAudioIndex: LiveData<Int> = _currentAudioIndex
    private val _selectedAudio = MutableLiveData<RemoteAudioItem?>()
    val selectedAudio: LiveData<RemoteAudioItem?> = _selectedAudio

    fun selectAudio(index: Int) {
        _localAudios.value = _localAudios.value?.mapIndexed { i, audioItem ->
            audioItem.copy(isSelected = i == index)
        }
        setCurrentAudioIndex(index)
        _selectedAudio.value = _localAudios.value?.get(index)
    }
    init {
        loadLocalAudios()
    }
    
    private fun loadLocalAudios() {

        _localAudios.value = songDataSource.getAllAudio()
    }

    fun setCurrentAudioIndex(index: Int) {
        _currentAudioIndex.value = index
    }

    fun nextAudio() {
        val nextIndex = (_currentAudioIndex.value ?: 0) + 1
        if (nextIndex < (_localAudios.value?.size ?: 0)) {
            _currentAudioIndex.value = nextIndex
        }
    }

    fun previousAudio() {
        val prevIndex = (_currentAudioIndex.value ?: 0) - 1
        if (prevIndex >= 0) {
            _currentAudioIndex.value = prevIndex
        }
    }

    fun getCurrentAudioItem(): RemoteAudioItem? {
        return _localAudios.value?.getOrNull(_currentAudioIndex.value ?: 0)
    }


}
