package com.jcxdc.musium.screen

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.jcxdc.musium.R
import com.jcxdc.musium.databinding.FragmentPlayerBinding
import com.jcxdc.musium.db.RemoteAudioItem
import com.jcxdc.musium.utils.Constants.API_KEY

class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isTracking = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout using DataBindingUtil
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)

        // Retrieve RemoteAudioItem from arguments
        val audioItem = arguments?.getParcelable<RemoteAudioItem>(API_KEY)
        setupMediaPlayer(audioItem)

        // Play/Pause button logic
        binding.ivPlay.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                binding.ivPlay.setImageResource(R.drawable.play) // Change icon to play
            } else {
                mediaPlayer?.start()
                binding.ivPlay.setImageResource(R.drawable.pause) // Change icon to pause
            }
        }

        // Back button
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }

    private fun setupMediaPlayer(audioItem: RemoteAudioItem?) {
        audioItem?.let { item ->
            binding.tvTitle.text = item.title
            binding.tvArtistName.text = item.artist

            mediaPlayer = MediaPlayer().apply {
                setDataSource(item.path)  // Set the path of the remote or local audio file
                prepareAsync()
                setOnPreparedListener {
                    binding.seekBar.max = mediaPlayer?.duration ?: 0
                    start()
                    binding.ivPlay.setImageResource(R.drawable.pause)
                    updateSeekBar()
                }
            }
        }

        // SeekBar change listener
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isTracking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isTracking = false
                mediaPlayer?.seekTo(binding.seekBar.progress)
            }
        })
    }

    // Update SeekBar periodically to reflect MediaPlayer's progress
    private fun updateSeekBar() {
        mediaPlayer?.let { player ->
            binding.seekBar.progress = player.currentPosition
            binding.tvCurrentTime.text = formatTime(player.currentPosition)
            binding.tvTotalTime.text = formatTime(player.duration)

                handler.postDelayed({ updateSeekBar() }, 500)

        }
    }

    private fun formatTime(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null)
    }
}
