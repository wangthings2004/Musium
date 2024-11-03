package com.jcxdc.musium.service

import com.jcxdc.musium.R


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.os.Binder
import android.os.Build
import androidx.core.app.NotificationCompat
import com.jcxdc.musium.db.RemoteAudioItem

class MusicService : Service() {

    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var currentAudio: RemoteAudioItem? = null

    companion object {
        const val CHANNEL_ID = "MusicChannel"
        const val NOTIFICATION_ID = 1
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaâ",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }


    fun playAudio(audioItem: RemoteAudioItem) {
        currentAudio = audioItem
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioItem.path)
            prepare()
            start()
            showNotification()
        }
    }

    fun pauseAudio() {
        mediaPlayer?.pause()
        showNotification()
    }

    fun resumeAudio() {
        mediaPlayer?.start()
        showNotification()
    }

    private fun showNotification() {
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(currentAudio?.title)
            .setContentText(currentAudio?.artist)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(mediaPlayer?.isPlaying == true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
