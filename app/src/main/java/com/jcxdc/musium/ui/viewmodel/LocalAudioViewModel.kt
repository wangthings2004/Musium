package com.jcxdc.musium.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jcxdc.musium.content_provider.SongDataSource
import com.jcxdc.musium.db.AudioItem
import com.jcxdc.musium.service.AudioSource

class LocalAudioViewModel(
    private val songDataSource: SongDataSource
) : ViewModel(), AudioSource {

    private val _localAudios = MutableLiveData<List<AudioItem>>(emptyList())
    val localAudios: LiveData<List<AudioItem>> = _localAudios

    private val _currentAudioIndex = MutableLiveData<Int>(-1)
    val currentAudioIndex: LiveData<Int> = _currentAudioIndex

    init {
        loadLocalAudios()
    }

    // Load danh sách bài hát local
    private fun loadLocalAudios() {
        _localAudios.value = songDataSource.getAllAudio()
        if (_localAudios.value?.isNotEmpty() == true && _currentAudioIndex.value == -1) {
            setCurrentAudioIndex(0) // Chọn bài đầu tiên mặc định
        }
    }

    // Chọn bài hát theo index
    fun selectAudio(index: Int) {
        if (index >= 0 && index < (_localAudios.value?.size ?: 0)) {
            setCurrentAudioIndex(index)
        }
    }

    // Đặt index hiện tại
    private fun setCurrentAudioIndex(index: Int) {
        _currentAudioIndex.value = index
    }

    // Làm mới danh sách bài hát
    fun refreshLocalAudios() {
        loadLocalAudios()
    }

    // Triển khai AudioSource
    override fun getCurrentAudioItem(): AudioItem? {
        val index = _currentAudioIndex.value ?: -1
        return if (index >= 0 && index < (_localAudios.value?.size ?: 0)) {
            _localAudios.value?.get(index)
        } else null
    }

    override fun nextAudio() {
        val currentIndex = _currentAudioIndex.value ?: -1
        val nextIndex = currentIndex + 1
        if (nextIndex < (_localAudios.value?.size ?: 0)) {
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
}