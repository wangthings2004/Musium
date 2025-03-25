package com.jcxdc.musium.ui.screen.home

import android.annotation.SuppressLint
import android.content.Context
import com.jcxdc.musium.ui.viewmodel.RemoteAudioViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcxdc.musium.databinding.FragmentHomeBinding
import com.jcxdc.musium.ui.screen.BottomViewNavigationListener
import com.jcxdc.musium.ui.screen.MainActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class HomeFragment : Fragment(), BottomViewNavigationListener {
    private lateinit var binding: FragmentHomeBinding
    private val remoteAudioAdapter: RemoteAudioAdapter by inject()
    private val topAlbumAdapter: TopAlbumAdapter by inject()
    private val remoteAudioViewModel: RemoteAudioViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    isEnabled = false
                    requireActivity().finish()
                }
            })
        setupRecyclerView()
        remoteAudioViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE

        }
        remoteAudioViewModel.remoteAudios.observe(viewLifecycleOwner) { audios ->
            audios?.let {
                remoteAudioAdapter.submitList(it)
                topAlbumAdapter.submitLimitedList(it)

            }
        }
//

    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun setupRecyclerView() {
        binding.rvTopTracks.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
                        activity.updateBottomViewTitle(
                            audioItem.title,
                            formatTime(audioItem.duration)
                        )
                    }
                }
            }
        }
        binding.rvTopAlbum.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
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
                        activity.updateBottomViewTitle(
                            audioItem.title,
                            formatTime(audioItem.duration)
                        )
                    }
                }
            }
        }


    }

    override fun navigateToPlayer() {
        (requireActivity() as? MainActivity)?.let { activity ->
            val musicService = activity.musicService
            val isLocal = musicService?.isPlayingLocal() ?: false
            val action = HomeFragmentDirections.actionHomeFragmentToPlayerFragment(isLocal)
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
