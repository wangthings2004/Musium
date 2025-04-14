package com.jcxdc.musium.ui.screen.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jcxdc.musium.R
import com.jcxdc.musium.databinding.FragmentPlayerBinding
import com.jcxdc.musium.service.MusicService
import com.jcxdc.musium.ui.screen.MainActivity
import com.jcxdc.musium.ui.viewmodel.LocalAudioViewModel
import com.jcxdc.musium.ui.viewmodel.RemoteAudioViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PlayerFragment : Fragment() {
    private lateinit var binding: FragmentPlayerBinding
    private val remoteAudioViewModel: RemoteAudioViewModel by sharedViewModel()
    private val localAudioViewModel: LocalAudioViewModel by inject()
    private val args: PlayerFragmentArgs by navArgs()
    private var musicService: MusicService? = null
    private var isServiceBound = false
    private val handler = Handler(Looper.getMainLooper())
    private var isTracking = false
    private val img = listOf(R.drawable.img_library2, R.drawable.img_library3, R.drawable.img_library4, R.drawable.img_library5)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            // Thiết lập AudioSource với SourceType dựa trên args.isLocalMusic
            val sourceType = if (args.isLocalMusic) MusicService.SourceType.LOCAL else MusicService.SourceType.REMOTE
            val audioSource = if (args.isLocalMusic) localAudioViewModel else remoteAudioViewModel
            musicService?.setAudioSource(audioSource, sourceType)
            isServiceBound = true
            updateUIFromPlaybackState()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            musicService = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)
        setupPlayerControls()
        handleBackPress()
        setupSeekBar()
        return binding.root
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (requireActivity() as? MainActivity)?.showBottomView()
                findNavController().popBackStack()
            }
        })
        binding.ivBack.setOnClickListener {
            (requireActivity() as? MainActivity)?.showBottomView()
            findNavController().popBackStack()
        }
        binding.ivX.setOnClickListener {
            musicService?.stop()
            if (isServiceBound) {
                requireActivity().unbindService(serviceConnection)
                isServiceBound = false
            }
            (requireActivity() as? MainActivity)?.hideBottomView()
            findNavController().popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(requireContext(), MusicService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(intent)
        } else {
            requireContext().startService(intent)
        }
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isServiceBound) {
            requireActivity().unbindService(serviceConnection)
            isServiceBound = false
        }
    }

    private fun setupPlayerControls() {
        binding.ivPlay.setOnClickListener {
            if (musicService?.getPlaybackState()?.isPlaying == true) {
                musicService?.pause()
                binding.ivPlay.setImageResource(R.drawable.play_arrow)
            } else {
                musicService?.resume()
                binding.ivPlay.setImageResource(R.drawable.pause)
            }
        }
        binding.ivNext.setOnClickListener {
            musicService?.nextTrack()
        }
        binding.ivPrevious.setOnClickListener {
            musicService?.previousTrack()
        }
    }

    private fun setupSeekBar() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    musicService?.seekTo(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isTracking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isTracking = false
                musicService?.seekTo(binding.seekBar.progress.toLong())
            }
        })
    }

    private fun updateUIFromPlaybackState() {
        musicService?.getPlaybackState()?.let { state ->
            // Cập nhật UI từ PlaybackState
            binding.tvTitle.text = state.currentTrack?.title ?: "Unknown"
            binding.tvArtistName.text = state.currentTrack?.artist ?: "Unknown"
            binding.seekBar.max = state.duration.toInt()
            binding.seekBar.progress = state.currentPosition.toInt()
            binding.tvCurrentTime.text = formatTime(state.currentPosition.toInt())
            binding.tvTotalTime.text = formatTime(state.duration.toInt())
            binding.ivPlay.setImageResource(if (state.isPlaying) R.drawable.pause else R.drawable.play_arrow)
            // Cập nhật bottom view của MainActivity
            (activity as? MainActivity)?.updateBottomViewTitle(state.currentTrack?.title ?: "Unknown", formatTime(state.duration.toInt()))
            // Cập nhật hình ảnh dựa trên nguồn nhạc
            if (musicService?.getCurrentSourceType() == MusicService.SourceType.REMOTE) {
                val index = remoteAudioViewModel.currentAudioIndex.value ?: 0
                binding.ivMusicImage.setBackgroundResource(img[index % img.size])
            } else {
                binding.ivMusicImage.setBackgroundResource(R.drawable.img_library1)
            }
        }
        // Cập nhật liên tục mỗi 500ms
        handler.postDelayed({ updateUIFromPlaybackState() }, 500)
    }

    private fun formatTime(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}