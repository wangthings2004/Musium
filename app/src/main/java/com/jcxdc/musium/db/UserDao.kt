package com.jcxdc.musium.db
import androidx.room.Dao
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
