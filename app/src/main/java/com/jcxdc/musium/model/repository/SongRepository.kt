package com.jcxdc.musium.model.repository

import com.jcxdc.musium.db.AudioItem

import com.jcxdc.musium.db.SongDao


class SongRepository(private val dao: SongDao) {
    suspend fun insertSong(song: AudioItem) = dao.insertSong(song)
    suspend fun getSongsByPlaylistId(playlistId: Int) = dao.getSongByPlaylistId(playlistId)
    suspend fun deleteSong(song: AudioItem) = dao.deleteSong(song)
}
