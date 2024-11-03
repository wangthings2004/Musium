package com.jcxdc.musium.screen

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jcxdc.musium.R
import com.jcxdc.musium.databinding.FragmentPlayerBinding
import com.jcxdc.musium.db.RemoteAudioItem
import com.jcxdc.musium.utils.Constants.API_KEY
import com.jcxdc.musium.viewmodel.RemoteAudioViewModel

class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isTracking = false

    private val remoteAudioViewModel: RemoteAudioViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)
        val currentAudioIndex = arguments?.getInt(API_KEY) ?: -1
        if (currentAudioIndex != -1) {
            remoteAudioViewModel.setCurrentAudioIndex(currentAudioIndex)
        }
        observeCurrentAudio()
        setupPlayerControls()


        return binding.root
    }

    private fun observeCurrentAudio() {
        Toast.makeText(requireContext(),"${remoteAudioViewModel.currentAudioIndex.value}",Toast.LENGTH_SHORT).show()
        remoteAudioViewModel.currentAudioIndex.observe(viewLifecycleOwner) {
            val audioItem = remoteAudioViewModel.getCurrentAudioItem()
            setupMediaPlayer(audioItem)
        }

    }

    private fun setupPlayerControls() {
        binding.ivPlay.setOnClickListener {
            togglePlayPause()
        }

        binding.ivNext.setOnClickListener {
            remoteAudioViewModel.nextAudio()
        }

        binding.ivPrevious.setOnClickListener {
            remoteAudioViewModel.previousAudio()
        }

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun togglePlayPause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            binding.ivPlay.setImageResource(R.drawable.play)
        } else {
            mediaPlayer?.start()
            binding.ivPlay.setImageResource(R.drawable.pause)
        }
    }

    private fun setupMediaPlayer(audioItem: RemoteAudioItem?) {
        mediaPlayer?.release()
        mediaPlayer = null

        audioItem?.let { item ->
            binding.tvTitle.text = item.title
            binding.tvArtistName.text = item.artist

            mediaPlayer = MediaPlayer().apply {
                setDataSource(item.path)
                prepareAsync()
                setOnPreparedListener {
                    binding.seekBar.max = duration
                    start()
                    binding.ivPlay.setImageResource(R.drawable.pause)
                    updateSeekBar()
                }
            }
        }

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

    private fun updateSeekBar() {
        mediaPlayer?.let { player ->
            if (!isTracking) {
                binding.seekBar.progress = player.currentPosition
            }
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
