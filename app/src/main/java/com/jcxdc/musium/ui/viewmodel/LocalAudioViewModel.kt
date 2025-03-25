package com.jcxdc.musium.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jcxdc.musium.content_provider.SongDataSource
import com.jcxdc.musium.db.AudioItem


class LocalAudioViewModel(
    private val songDataSource: SongDataSource
) : ViewModel() {

    private val _localAudios = MutableLiveData<List<AudioItem>>()
    val localAudios: LiveData<List<AudioItem>> = _localAudios

    private val _currentAudioIndex = MutableLiveData<Int>()
    val currentAudioIndex: LiveData<Int> = _currentAudioIndex
    private val _selectedAudio = MutableLiveData<AudioItem?>()
    val selectedAudio: LiveData<AudioItem?> = _selectedAudio

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
    fun deselectCurrentAudio() {
        _selectedAudio.value?.isSelected = false
    }

    fun getSelectedAudio(): AudioItem? {
        return _localAudios.value?.find { it.isSelected }
    }
    fun loadLocalAudios() {

        _localAudios.value = songDataSource.getAllAudio()
    }

    fun setCurrentAudioIndex(index: Int) {
        _currentAudioIndex.value = index
    }

    fun nextAudio() {
        val nextIndex = (_currentAudioIndex.value ?: 0) + 1
        if (nextIndex < (_localAudios.value?.size ?: 0)) {
            setCurrentAudioIndex(nextIndex)
        }
    }
    fun setLocalAudios(audios: List<AudioItem>) {
        _localAudios.value = audios
    }

    fun previousAudio() {
        val prevIndex = (_currentAudioIndex.value ?: 0) - 1
        if (prevIndex >= 0) {
            setCurrentAudioIndex(prevIndex)
        }
    }

    fun getCurrentAudioItem(): AudioItem? {
        return _localAudios.value?.getOrNull(_currentAudioIndex.value ?: 0)
    }


}
