package com.jcxdc.musium.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.jcxdc.musium.db.RemoteAudio
import com.jcxdc.musium.db.RemoteAudioItem
import com.jcxdc.musium.repository.APIRepository
import com.jcxdc.musium.utils.CommonFunction.isNetworkAvailable
import com.jcxdc.musium.utils.RemoteAudioState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.internal.NopCollector.emit

import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class RemoteAudioViewModel @Inject constructor(
    private val repository: APIRepository,
) : ViewModel() {
    var isloaded = false
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _remoteAudios = MutableLiveData<List<RemoteAudioItem>>()
    val remoteAudios: LiveData<List<RemoteAudioItem>> = _remoteAudios

    private val _currentAudioIndex = MutableLiveData<Int>()
    val currentAudioIndex: LiveData<Int> = _currentAudioIndex

    init {
        if (!isloaded) loadRemoteAudios()
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


    fun setCurrentAudioIndex(index: Int) {
        _currentAudioIndex.value = index
    }

    fun nextAudio() {
        val nextIndex = (_currentAudioIndex.value ?: 0) + 1
        if (nextIndex < (_remoteAudios.value?.size ?: 0)) {
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
        return _remoteAudios.value?.getOrNull(_currentAudioIndex.value!!)
    }
}
