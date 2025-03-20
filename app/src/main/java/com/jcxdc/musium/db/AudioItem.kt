package com.jcxdc.musium.db
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "song_table")
data class AudioItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    val artist: String,
    val duration: Int,
    val kind: String,
    val path: String,
    val title: String,
    var isSelected: Boolean = false,
    var img: Int?,
    var playlistId: Int
) : Parcelable