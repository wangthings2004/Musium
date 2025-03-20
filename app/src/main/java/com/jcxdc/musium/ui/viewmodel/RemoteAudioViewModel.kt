package com.jcxdc.musium.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcxdc.musium.db.AudioItem
import com.jcxdc.musium.model.repository.APIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class RemoteAudioViewModel @Inject constructor(
    private val repository: APIRepository,
) : ViewModel() {
    var isloaded = false
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _remoteAudios = MutableLiveData<List<AudioItem>>()
    val remoteAudios: LiveData<List<AudioItem>> = _remoteAudios

    private val _currentAudioIndex = MutableLiveData<Int>()
    val currentAudioIndex: LiveData<Int> = _currentAudioIndex
    private val _selectedAudio = MutableLiveData<AudioItem?>()
    val selectedAudio: LiveData<AudioItem?> = _selectedAudio
    init {
        if (!isloaded) loadRemoteAudios()
    }
    fun selectAudio(index: Int) {
        _remoteAudios.value = _remoteAudios.value?.mapIndexed { i, audioItem ->
            audioItem.copy(isSelected = i == index)
        }
        setCurrentAudioIndex(index)
        _selectedAudio.value = _remoteAudios.value?.get(index)
    }
    fun deselectCurrentAudio() {
        _selectedAudio.value?.isSelected = false
    }

    private fun loadRemoteAudios() {
        viewModelScope.launch {
            _isLoading.value = true
            val data = repository.getRemoteAudios()
            _remoteAudios.value = data ?: emptyList()
            _isLoading.value = false
            isloaded = true
        }
    }

    fun getSelectedAudio(): AudioItem? {
        return _remoteAudios.value?.find { it.isSelected }
    }

    fun setCurrentAudioIndex(index: Int) {
        _currentAudioIndex.value = index
        _remoteAudios.value = _remoteAudios.value?.mapIndexed { i, audioItem ->
            audioItem.copy(isSelected = i == index) // Update isSelected directly
        }
    }

    fun nextAudio() {
        val nextIndex = (_currentAudioIndex.value ?: 0) + 1
        if (nextIndex < (_remoteAudios.value?.size ?: 0)) {
            setCurrentAudioIndex(nextIndex)
        }
    }

    fun previousAudio() {
        val prevIndex = (_currentAudioIndex.value ?: 0) - 1
        if (prevIndex >= 0) {
            setCurrentAudioIndex(prevIndex)
        }
    }

    fun getCurrentAudioItem(): AudioItem? {
        return _remoteAudios.value?.getOrNull(_currentAudioIndex.value!!)
    }
}
