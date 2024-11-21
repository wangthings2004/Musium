package com.jcxdc.musium.db
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RemoteAudioItem(
    val artist: String,
    val duration: Int,
    val kind: String,
    val path: String,
    val title: String,
    var isSelected: Boolean = false,
    var img: Int?,
    var id: String?
) : Parcelable