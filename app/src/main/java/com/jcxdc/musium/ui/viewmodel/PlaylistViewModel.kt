package com.jcxdc.musium.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcxdc.musium.db.Playlist
import com.jcxdc.musium.model.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlaylistViewModel(private val repository: PlaylistRepository) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> get() = _playlists

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchPlaylists() {
        viewModelScope.launch {
            try {
                val fetchedPlaylists = withContext(Dispatchers.IO) {
                    repository.getPlaylists()
                }
                _playlists.value = fetchedPlaylists
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch playlists: ${e.message}"
            }
        }
    }

    fun createPlaylist(title: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val newPlaylist = Playlist(id = 0, title = title)
                withContext(Dispatchers.IO) {
                    repository.insertPlaylist(newPlaylist)
                }
                fetchPlaylists() // Refresh playlists after creation
                onResult(true, "Playlist created successfully")
            } catch (e: Exception) {
                onResult(false, "Failed to create playlist: ${e.message}")
            }
        }
    }

    fun deletePlaylist(playlist: Playlist, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.deletePlaylist(playlist)
                }
                fetchPlaylists() // Refresh playlists after deletion
                onResult(true, "Playlist deleted successfully")
            } catch (e: Exception) {
                onResult(false, "Failed to delete playlist: ${e.message}")
            }
        }
    }
}
