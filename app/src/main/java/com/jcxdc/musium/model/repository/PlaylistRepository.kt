package com.jcxdc.musium.model.repository

import  com.jcxdc.musium.db.Playlist
import com.jcxdc.musium.db.PlaylistDao


class PlaylistRepository(
    private val dao: PlaylistDao
) {
    suspend fun insertPlaylist(playlist: Playlist) = dao.insertPlaylist(playlist)
    suspend fun getPlaylists() = dao.getPlaylists()
    suspend fun deletePlaylist(playlist: Playlist) = dao.deletePlaylist(playlist)
}