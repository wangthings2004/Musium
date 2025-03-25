package com.jcxdc.musium.ui.screen.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
            Log.d("TAG", "${args.isLocalMusic}")
            if (args.isLocalMusic) {
                musicService?.setLocalAudioViewModel(localAudioViewModel)
                val initialIndex = localAudioViewModel.currentAudioIndex.value ?: 0
                Toast.makeText(requireContext(),initialIndex.toString(),Toast.LENGTH_SHORT).show()
                localAudioViewModel.selectAudio(initialIndex)
                observeLocalAudio()
            } else {
                musicService?.setRemoteAudioViewModel(remoteAudioViewModel)
                observeRemoteAudio()
            }
            isServiceBound = true
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
        togglePlayPause()
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
        binding.ivX.setOnClickListener{
            musicService?.stopTrack()

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

    private fun observeLocalAudio() {
        localAudioViewModel.currentAudioIndex.observe(viewLifecycleOwner) { index ->
            val audioItem = localAudioViewModel.localAudios.value?.getOrNull(index)
            if (audioItem != null) {
                binding.tvTitle.text = audioItem.title
                binding.tvArtistName.text = audioItem.artist
                binding.ivMusicImage.setBackgroundResource(R.drawable.img_library1)
                (activity as? MainActivity)?.updateBottomViewTitle(audioItem.title, formatTime(audioItem.duration))

                musicService?.playLocalTrack(localAudioViewModel)
                updateSeekBar()
            }
        }
    }
    private fun observeRemoteAudio() {
        remoteAudioViewModel.currentAudioIndex.observe(viewLifecycleOwner) {
            val audioItem = remoteAudioViewModel.remoteAudios.value?.getOrNull(it)
            if (audioItem != null) {
                binding.tvTitle.text = audioItem.title
                binding.tvArtistName.text = audioItem.artist
                binding.ivMusicImage.setBackgroundResource(img[remoteAudioViewModel.currentAudioIndex.value!! % img.size])
                (activity as? MainActivity)?.updateBottomViewTitle(audioItem.title, formatTime(audioItem.duration))
                musicService?.playTrack(remoteAudioViewModel)
                updateSeekBar()
            } else {
                Log.e("PlayerFragment", "No audio item available")
            }
        }
    }

    private fun setupPlayerControls() {
        binding.ivPlay.setOnClickListener { togglePlayPause() }
        binding.ivNext.setOnClickListener {
            if (args.isLocalMusic) musicService?.nextLocalAudioTrack() else musicService?.nextRemoteAudioTrack()
        }
        binding.ivPrevious.setOnClickListener {
            if (args.isLocalMusic) musicService?.previousLocalAudioTrack() else musicService?.previousRemoteAudioTrack()}
        binding.ivBack.setOnClickListener { requireActivity().onBackPressed() }
    }

    private fun togglePlayPause() {
        if (musicService?.isPlaying() == true) {
            musicService?.pauseTrack()
            binding.ivPlay.setImageResource(R.drawable.play_arrow)
        } else {
            musicService?.resumeTrack()
            binding.ivPlay.setImageResource(R.drawable.pause)
        }
    }

    private fun setupSeekBar() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isTracking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isTracking = false
                musicService?.seekTo(binding.seekBar.progress)
            }
        })
    }

    private fun updateSeekBar() {
        musicService?.let { service ->
            if (!isTracking) {
                binding.seekBar.progress = service.getCurrentPosition()
            }
            binding.seekBar.max = service.getDuration()
            binding.tvCurrentTime.text = formatTime(service.getCurrentPosition())
            binding.tvTotalTime.text = formatTime(service.getDuration())

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
        handler.removeCallbacksAndMessages(null)
    }
}
