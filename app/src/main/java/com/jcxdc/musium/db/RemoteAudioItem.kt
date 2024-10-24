package com.jcxdc.musium.db

data class RemoteAudioItem(
    val artist: String,
    val duration: Int,
    val kind: String,
    val path: String,
    val title: String
)