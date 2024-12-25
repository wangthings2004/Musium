package com.jcxdc.musium.db
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface
UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM USER_TABLE WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?
}

@Dao
interface PlaylistDao {
    @Insert
    suspend fun insertPlaylist(playlist: Playlist)

    @Query("SELECT * FROM playlist_table WHERE userId = :userId")
    suspend fun getPlaylistsByUserId(userId: Int): List<Playlist>

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)
}
@Dao
interface SongDao {
    @Insert
    suspend fun insertSong(song: Song)
    @Query("SELECT * FROM song_table WHERE playlistId = :playlistId")
    suspend fun getSongByPlaylistId(playlistId: Int): List<Song>
    @Delete
    suspend fun deleteSong(song: Song)


}
