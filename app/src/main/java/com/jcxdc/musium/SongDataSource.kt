package com.jcxdc.musium

import android.content.ContentResolver
import android.provider.MediaStore
import com.jcxdc.musium.db.RemoteAudioItem

class SongDataSource(private val contentResolver: ContentResolver) {
    fun getAllAudio(): ArrayList<RemoteAudioItem> {
        val tempList = ArrayList<RemoteAudioItem>()
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DURATION
        )

        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val titleIndex = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
                    val idIndex = it.getColumnIndex(MediaStore.Audio.Media._ID)
                    val albumIndex = it.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                    val artistIndex = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                    val pathIndex = it.getColumnIndex(MediaStore.Audio.Media.DATA)
                    val durationIndex = it.getColumnIndex(MediaStore.Audio.Media.DURATION)

                    val title = if (titleIndex != -1) it.getString(titleIndex) else "Unknown Title"
                    val id = if (idIndex != -1) it.getString(idIndex) else "Unknown ID"
                    val album = if (albumIndex != -1) it.getString(albumIndex) else "Unknown Album"
                    val artist = if (artistIndex != -1) it.getString(artistIndex) else "Unknown Artist"
                    val path = if (pathIndex != -1) it.getString(pathIndex) else "Unknown Path"
                    val duration = if (durationIndex != -1) it.getInt(durationIndex) else 0
                    val music = RemoteAudioItem(
                        id = id,
                        title = title,
                        artist = artist,
                        path = path,
                        duration = duration,
                        kind = "music",
                        isSelected = false,
                        img = null
                    )
                    tempList.add(music)
                } while (it.moveToNext())
            }
        }
        return tempList
    }
}