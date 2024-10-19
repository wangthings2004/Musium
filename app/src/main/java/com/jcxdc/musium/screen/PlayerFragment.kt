package com.jcxdc.musium.screen

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.jcxdc.musium.R
import com.jcxdc.musium.databinding.FragmentPlayerBinding

class PlayerFragment : Fragment() {
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout using DataBindingUtil
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)

        // Khởi tạo MediaPlayer với file MP3 trong res/raw
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.jack)

        // Sự kiện khi nhấn vào nút play/pause
        binding.ivPlay.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            } else {
                mediaPlayer.pause()
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}