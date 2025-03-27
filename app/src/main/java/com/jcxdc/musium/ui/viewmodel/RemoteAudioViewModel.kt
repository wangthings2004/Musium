package com.jcxdc.musium.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcxdc.musium.db.AudioItem
import com.jcxdc.musium.model.repository.APIRepository
import com.jcxdc.musium.service.AudioSource
import kotlinx.coroutines.launch

class RemoteAudioViewModel(
    private val repository: APIRepository
) : ViewModel(), AudioSource {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _remoteAudios = MutableLiveData<List<AudioItem>>(emptyList())
    val remoteAudios: LiveData<List<AudioItem>> = _remoteAudios

    private val _currentAudioIndex = MutableLiveData<Int>(-1)
    val currentAudioIndex: LiveData<Int> = _currentAudioIndex

    private var isLoaded = false

    init {
        if (!isLoaded) loadRemoteAudios()
    }

    // Triển khai AudioSource
    override fun getCurrentAudioItem(): AudioItem? {
        val index = _currentAudioIndex.value ?: -1
        return if (index >= 0 && index < (_remoteAudios.value?.size ?: 0)) {
            _remoteAudios.value?.get(index)
        } else null
    }

    override fun nextAudio() {
        val currentIndex = _currentAudioIndex.value ?: -1
        val nextIndex = currentIndex + 1
        if (nextIndex < (_remoteAudios.value?.size ?: 0)) {
            setCurrentAudioIndex(nextIndex)
        }
    }

    override fun previousAudio() {
        val currentIndex = _currentAudioIndex.value ?: -1
        val prevIndex = currentIndex - 1
        if (prevIndex >= 0) {
            setCurrentAudioIndex(prevIndex)
        }
    }

    // Load dữ liệu từ repository
    private fun loadRemoteAudios() {
        viewModelScope.launch {
            _isLoading.value = true
            val data = repository.getRemoteAudios() ?: emptyList()
            _remoteAudios.value = data
            if (data.isNotEmpty() && _currentAudioIndex.value == -1) {
                setCurrentAudioIndex(0) // Chọn bài đầu tiên mặc định
            }
            _isLoading.value = false
            isLoaded = true
        }
    }

    // Chọn bài hát theo index
    fun selectAudio(index: Int) {
        if (index >= 0 && index < (_remoteAudios.value?.size ?: 0)) {
            setCurrentAudioIndex(index)
        }
    }

    // Đặt index hiện tại và cập nhật trạng thái
    private fun setCurrentAudioIndex(index: Int) {
        _currentAudioIndex.value = index
    }

    // Làm mới danh sách nếu cần
    fun refreshRemoteAudios() {
        isLoaded = false
        loadRemoteAudios()
    }
}