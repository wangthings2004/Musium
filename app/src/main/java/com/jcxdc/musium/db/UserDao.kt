package com.jcxdc.musium.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user_table WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?
}

@Dao
interface PlaylistDao {

    @Insert
    suspend fun insertPlaylist(playlist: Playlist)

    @Query("SELECT * FROM playlist_table")
    suspend fun getPlaylists(): List<Playlist>

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

}

@Dao
interface SongDao {
    // Insert a new song
    @Insert
    suspend fun insertSong(song: AudioItem)

    // Fetch all songs by playlist ID
    @Query("SELECT * FROM song_table WHERE playlistId = :playlistId")
    suspend fun getSongByPlaylistId(playlistId: Int): List<AudioItem>

    // Delete a song
    @Delete
    suspend fun deleteSong(song: AudioItem)

}
