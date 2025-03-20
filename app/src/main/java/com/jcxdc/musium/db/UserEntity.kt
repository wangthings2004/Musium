package com.jcxdc.musium.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.jcxdc.musium.utils.Constants.USER_TABLE

@Entity(tableName = USER_TABLE)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var username: String,
    var passwordHash: String,
    var email : String?,

)

@Entity(tableName = "playlist_table",)
data class Playlist(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var title: String,

)





