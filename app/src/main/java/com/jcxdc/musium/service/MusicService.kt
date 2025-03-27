package com.jcxdc.musium.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.jcxdc.musium.db.AudioItem

interface AudioSource {
    fun getCurrentAudioItem(): AudioItem?
    fun nextAudio()
    fun previousAudio()
}

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val currentTrack: AudioItem? = null
)

class MusicService : Service() {
    private var currentSourceType: SourceType = SourceType.NONE
    private val binder = MusicBinder()
    private var exoPlayer: ExoPlayer? = null
    private var audioSource: AudioSource? = null
    enum class SourceType {
        LOCAL, REMOTE, NONE
    }

    private var playbackState = PlaybackState()

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        nextTrack()
                    }
                    updatePlaybackState()
                }
            })
        }

    }

    fun setAudioSource(source: AudioSource,type : SourceType) {
        this.currentSourceType = type
        this.audioSource = source
        playCurrentTrack()
    }
    fun getCurrentSourceType(): SourceType = currentSourceType
    private fun playCurrentTrack() {
        audioSource?.getCurrentAudioItem()?.let { audioItem ->
            exoPlayer?.apply {
                stop()
                clearMediaItems()
                val mediaItem = MediaItem.fromUri(audioItem.path)
                setMediaItem(mediaItem)
                prepare()
                play()
            }
            playbackState = playbackState.copy(currentTrack = audioItem)

        }
    }

    fun nextTrack() {
        audioSource?.nextAudio()
        playCurrentTrack()
    }

    fun previousTrack() {
        audioSource?.previousAudio()
        playCurrentTrack()
    }

    fun pause() {
        exoPlayer?.pause()
        updatePlaybackState()

    }

    fun resume() {
        exoPlayer?.play()
        updatePlaybackState()

    }

    fun stop() {
        exoPlayer?.stop()
        stopForeground(true)
        stopSelf()
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
        updatePlaybackState()
    }

    fun getPlaybackState(): PlaybackState = playbackState

    private fun updatePlaybackState() {
        playbackState = PlaybackState(
            isPlaying = exoPlayer?.isPlaying ?: false,
            currentPosition = exoPlayer?.currentPosition ?: 0,
            duration = exoPlayer?.duration ?: 0,
            currentTrack = playbackState.currentTrack
        )
    }




    override fun onDestroy() {

        exoPlayer?.release()
        exoPlayer = null
        super.onDestroy()
    }
}
