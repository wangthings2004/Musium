package com.jcxdc.musium.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.jcxdc.musium.R
import com.jcxdc.musium.db.AudioItem
import com.jcxdc.musium.ui.viewmodel.LocalAudioViewModel
import com.jcxdc.musium.ui.viewmodel.RemoteAudioViewModel

class MusicService : Service() {
    private var remoteAudioViewModel: RemoteAudioViewModel? = null
    private var localAudioViewModel : LocalAudioViewModel?=null
    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackIndex = -1
    enum class AudioSource {
        LOCAL, REMOTE, NONE
    }
    private var currentAudioSource: AudioSource = AudioSource.NONE

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnCompletionListener {
            remoteAudioViewModel?.let {
                it.nextAudio()
                playTrack(it)
            } ?: localAudioViewModel?.let {
                it.nextAudio()
                playLocalTrack(it)
            }
        }
    }
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "music_channel",
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
    fun isPlayingLocal(): Boolean {
        return currentAudioSource == AudioSource.LOCAL
    }

    fun playLocalTrack(localAudioViewModel: LocalAudioViewModel) {
        val localItem = localAudioViewModel.getCurrentAudioItem()
        if (localItem != null && (currentTrackIndex != localAudioViewModel.currentAudioIndex.value || mediaPlayer == null)) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(localItem.path)
                prepare()
                start()
            }
            currentTrackIndex = localAudioViewModel.currentAudioIndex.value ?: -1
            currentAudioSource = AudioSource.LOCAL
            showNotification(localAudioItem = localItem, null)

        }
    }
    fun playTrack(remoteAudioViewModel: RemoteAudioViewModel) {
        val audioItem = remoteAudioViewModel.getCurrentAudioItem()
        if (audioItem != null && currentTrackIndex != remoteAudioViewModel.currentAudioIndex.value) {
            mediaPlayer?.reset()
            mediaPlayer?.apply {
                setDataSource(audioItem.path)
                prepare()
                start()
            }
            currentTrackIndex = remoteAudioViewModel.currentAudioIndex.value ?: -1
            currentAudioSource = AudioSource.REMOTE
            showNotification(null, remoteAudioItem = audioItem)
        }
    }
    fun setLocalAudioViewModel(localAudioViewModel: LocalAudioViewModel?) {
        this.localAudioViewModel = localAudioViewModel
        remoteAudioViewModel?.deselectCurrentAudio()

    }
    fun setRemoteAudioViewModel(remoteAudioViewModel: RemoteAudioViewModel?) {
        this.remoteAudioViewModel = remoteAudioViewModel
        localAudioViewModel?.deselectCurrentAudio()

    }
    fun nextRemoteAudioTrack(){
        remoteAudioViewModel?.nextAudio()
    }
    fun previousRemoteAudioTrack(){
        remoteAudioViewModel?.previousAudio()
    }
    fun nextLocalAudioTrack(){
        localAudioViewModel?.nextAudio()
    }
    fun previousLocalAudioTrack(){
        localAudioViewModel?.previousAudio()
    }

    fun pauseTrack() {
        mediaPlayer?.pause()
    }

    fun resumeTrack() {
        mediaPlayer?.start()
    }

    fun stopTrack() {
        mediaPlayer?.stop()
        stopSelf()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    private fun showNotification(localAudioItem: AudioItem?, remoteAudioItem: AudioItem?) {
        val audioItem = localAudioItem ?: remoteAudioItem
        val isLocal = localAudioItem != null

        audioItem?.let {
            val notificationLayout = RemoteViews(packageName, R.layout.notification_layout)
            notificationLayout.setTextViewText(R.id.tv_song_title, it.title)
            notificationLayout.setTextViewText(R.id.tv_artist_name, it.artist)

            val playPauseIcon = if (isPlaying()) R.drawable.pause else R.drawable.play
            notificationLayout.setImageViewResource(R.id.iv_play_pause, playPauseIcon)

            val prevAction = if (isLocal) "ACTION_PREV_LOCAL" else "ACTION_PREV_REMOTE"
            val playPauseAction = if (isLocal) "ACTION_PLAY_PAUSE_LOCAL" else "ACTION_PLAY_PAUSE_REMOTE"
            val nextAction = if (isLocal) "ACTION_NEXT_LOCAL" else "ACTION_NEXT_REMOTE"

            val prevIntent = PendingIntent.getService(
                this, 0, Intent(this, MusicService::class.java).apply { action = prevAction },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val playPauseIntent = PendingIntent.getService(
                this, 1, Intent(this, MusicService::class.java).apply { action = playPauseAction },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val nextIntent = PendingIntent.getService(
                this, 2, Intent(this, MusicService::class.java).apply { action = nextAction },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            notificationLayout.setOnClickPendingIntent(R.id.iv_prev, prevIntent)
            notificationLayout.setOnClickPendingIntent(R.id.iv_play_pause, playPauseIntent)
            notificationLayout.setOnClickPendingIntent(R.id.iv_next, nextIntent)

            val notification = NotificationCompat.Builder(this, "music_channel")
                .setSmallIcon(R.drawable.img_logo)
                .setContent(notificationLayout)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setOnlyAlertOnce(true)
                .build()

            startForeground(1, notification)
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTION_PLAY_PAUSE_LOCAL" -> {
                if (isPlaying()) pauseTrack() else resumeTrack()
                showNotification(localAudioViewModel?.getCurrentAudioItem(), null)
            }
            "ACTION_PREV_LOCAL" -> {
                localAudioViewModel?.previousAudio()
                playLocalTrack(localAudioViewModel!!)
                showNotification(localAudioViewModel?.getCurrentAudioItem(), null)
            }
            "ACTION_NEXT_LOCAL" -> {
                localAudioViewModel?.nextAudio()
                playLocalTrack(localAudioViewModel!!)
                showNotification(localAudioViewModel?.getCurrentAudioItem(), null)
            }
            "ACTION_PLAY_PAUSE_REMOTE" -> {
                if (isPlaying()) pauseTrack() else resumeTrack()
                showNotification(null, remoteAudioViewModel?.getCurrentAudioItem())
            }
            "ACTION_PREV_REMOTE" -> {
                remoteAudioViewModel?.previousAudio()
                playTrack(remoteAudioViewModel!!)
                showNotification(null, remoteAudioViewModel?.getCurrentAudioItem())
            }
            "ACTION_NEXT_REMOTE" -> {
                remoteAudioViewModel?.nextAudio()
                playTrack(remoteAudioViewModel!!)
                showNotification(null, remoteAudioViewModel?.getCurrentAudioItem())
            }
        }
        return START_STICKY
    }

    fun stopMusicService() {
        stopForeground(true) // Xóa notification
        stopSelf() // Dừng service
    }

    override fun onDestroy() {
        stopMusicService()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }
}
