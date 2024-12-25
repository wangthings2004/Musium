package com.jcxdc.musium.ui.screen

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import com.jcxdc.musium.ui.viewmodel.RemoteAudioViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcxdc.musium.R

import com.jcxdc.musium.ui.adapter.RemoteAudioAdapter
import com.jcxdc.musium.databinding.FragmentHomeBinding
import com.jcxdc.musium.service.MusicService
import com.jcxdc.musium.ui.adapter.TopAlbumAdapter
import com.jcxdc.musium.ui.adapter.TopArtistAdapter
import com.jcxdc.musium.utils.Constants.API_KEY
import com.jcxdc.musium.utils.RemoteAudioState

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(), BottomViewNavigationListener {

    private lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var remoteAudioAdapter: RemoteAudioAdapter
    @Inject
    lateinit var topAlbumAdapter: TopAlbumAdapter
    @Inject
    lateinit var topArtistAdapter: TopArtistAdapter
    private val remoteAudioViewModel: RemoteAudioViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        Log.d("MainActivity Check", "MainActivity found: aaaaa")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        remoteAudioViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE

        }
        remoteAudioViewModel.remoteAudios.observe(viewLifecycleOwner) { audios ->
            audios?.let {
                remoteAudioAdapter.submitList(it)
                topAlbumAdapter.submitLimitedList(it)
                topArtistAdapter.submitList(it)
            }
        }
//

    }

    private fun formatTime(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun setupRecyclerView() {
        binding.rvTopTracks.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = remoteAudioAdapter
            remoteAudioAdapter.onItemClick = { audioItem ->
                val index = remoteAudioViewModel.remoteAudios.value?.indexOf(audioItem) ?: 0
                remoteAudioViewModel.selectAudio(index)
                remoteAudioViewModel.setCurrentAudioIndex(index)

                (requireActivity() as? MainActivity)?.let { activity ->
                    val musicService = activity.musicService
                    if (musicService != null) {
                        musicService.setRemoteAudioViewModel(remoteAudioViewModel)
                        musicService.playTrack(remoteAudioViewModel)
                        activity.showBottomView()
                        activity.updateBottomViewTitle(audioItem.title,formatTime(audioItem.duration))
                    }
                }
            }
        }
        binding.rvTopAlbum.apply {
            layoutManager = GridLayoutManager(requireContext(),2)
            adapter = topAlbumAdapter
            topAlbumAdapter.onItemClick = { audioItem ->
                val index = remoteAudioViewModel.remoteAudios.value?.indexOf(audioItem) ?: 0
                remoteAudioViewModel.setCurrentAudioIndex(index)
                (requireActivity() as? MainActivity)?.let { activity ->
                    val musicService = activity.musicService
                    if (musicService != null) {
                        musicService.setRemoteAudioViewModel(remoteAudioViewModel)
                        musicService.playTrack(remoteAudioViewModel)
                        activity.showBottomView()
                        activity.updateBottomViewTitle(audioItem.title,formatTime(audioItem.duration))
                    }
                }
            }
        }
        binding.rvTopArtists.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = topArtistAdapter
            topAlbumAdapter.onItemClick = { audioItem ->
                val index = remoteAudioViewModel.remoteAudios.value?.indexOf(audioItem) ?: 0
                remoteAudioViewModel.setCurrentAudioIndex(index)
                (requireActivity() as? MainActivity)?.let { activity ->
                    val musicService = activity.musicService
                    if (musicService != null) {
                        musicService.setRemoteAudioViewModel(remoteAudioViewModel)
                        musicService.playTrack(remoteAudioViewModel)
                        activity.showBottomView()
                        activity.updateBottomViewTitle(audioItem.title,formatTime(audioItem.duration))
                    }
                }
            }

        }
    }

    override fun navigateToPlayer() {
        val action = HomeFragmentDirections.actionHomeFragmentToPlayerFragment(false)
        findNavController().navigate(action)
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
