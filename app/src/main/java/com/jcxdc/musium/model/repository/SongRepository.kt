package com.jcxdc.musium.model.repository

import com.jcxdc.musium.db.Song
import com.jcxdc.musium.db.SongDao

class SongRepository(private val dao: SongDao) {
    suspend fun insertSong(song: Song) = dao.insertSong(song)
    suspend fun getSongsByPlaylistId(playlistId: Int) = dao.getSongByPlaylistId(playlistId)
}
