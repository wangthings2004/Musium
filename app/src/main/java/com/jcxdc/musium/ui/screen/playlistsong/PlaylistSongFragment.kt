package com.jcxdc.musium.ui.screen.playlistsong

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jcxdc.musium.ui.screen.MainActivity
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcxdc.musium.R
import com.jcxdc.musium.databinding.FragmentPlaylistSongBinding
import com.jcxdc.musium.ui.screen.BottomViewNavigationListener
import com.jcxdc.musium.ui.screen.home.HomeFragmentDirections
import com.jcxdc.musium.ui.viewmodel.LocalAudioViewModel
import com.jcxdc.musium.ui.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistSongFragment : Fragment(), BottomViewNavigationListener {

    private lateinit var binding: FragmentPlaylistSongBinding
    private lateinit var playlistSongAdapter: PlaylistSongAdapter
    private val localAudioViewModel: LocalAudioViewModel by viewModels({ requireActivity() })
    private val songViewModel: SongViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_playlist_song, container, false
        )
        setupListener()
        setupRecyclerView()
        setupViewModel()
        observeViewModel()
        return binding.root
    }

    private fun setupListener() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        playlistSongAdapter = PlaylistSongAdapter().apply {
            onItemClick = { song ->
                val index = localAudioViewModel.localAudios.value?.indexOf(song) ?: 0
                localAudioViewModel.setCurrentAudioIndex(index)
                (requireActivity() as? MainActivity)?.let { activity ->
                    val musicService = activity.musicService
                    if (musicService != null) {
                        musicService.setLocalAudioViewModel(localAudioViewModel)
                        musicService.playLocalTrack(localAudioViewModel)
                        activity.showBottomView()
                        activity.updateBottomViewTitle(
                            song.title,
                            formatTime(song.duration)
                        )
                    }
                }
            }
            onItemDeleteClick = { song ->
                songViewModel.deleteSong(song) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.rvMyPlaylistSong.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistSongAdapter
        }
    }

    private fun formatTime(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    private fun setupViewModel() {
        val playlistId = arguments?.getInt("playlistId") ?: -1
        songViewModel.fetchSongsByPlaylistId(playlistId)
    }

    private fun observeViewModel() {
        songViewModel.songs.observe(viewLifecycleOwner) { songs ->
            playlistSongAdapter.submitList(songs)
            localAudioViewModel.setLocalAudios(songs)
        }
        songViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun navigateToPlayer() {
        (requireActivity() as? MainActivity)?.let { activity ->
            val musicService = activity.musicService
            val isLocal = musicService?.isPlayingLocal() ?: false
            val action =
                PlaylistSongFragmentDirections.actionPlaylistSongFragmentToPlayerFragment(isLocal)
            findNavController().navigate(action)
        }

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            context.setBottomViewNavigationListener(this)
        }
    }

    override fun onDetach() {
        super.onDetach()
        (requireActivity() as? MainActivity)?.setBottomViewNavigationListener(null)
    }
}
