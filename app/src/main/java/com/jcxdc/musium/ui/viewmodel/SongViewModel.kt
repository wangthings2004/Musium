package com.jcxdc.musium.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcxdc.musium.db.AudioItem
import com.jcxdc.musium.model.repository.SongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SongViewModel (private val repository: SongRepository) : ViewModel() {

    private val _songs = MutableLiveData<List<AudioItem>>()
    val songs: LiveData<List<AudioItem>> get() = _songs

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchSongsByPlaylistId(playlistId: Int) {
        viewModelScope.launch {
            try {
                val fetchedSongs = withContext(Dispatchers.IO) {
                    repository.getSongsByPlaylistId(playlistId)
                }
                _songs.value = fetchedSongs
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch songs: ${e.message}"
            }
        }
    }

    fun insertSong(song: AudioItem, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.insertSong(song)
                }
                fetchSongsByPlaylistId(song.playlistId) // Refresh songs after insertion
                onResult(true, "Song added successfully")
            } catch (e: Exception) {
                onResult(false, "Failed to add song: ${e.message}")
            }
        }
    }

    fun deleteSong(song: AudioItem, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.deleteSong(song)
                }
                fetchSongsByPlaylistId(song.playlistId) // Refresh songs after deletion
                onResult(true, "Song deleted successfully")
            } catch (e: Exception) {
                onResult(false, "Failed to delete song: ${e.message}")
            }
        }
    }
}
