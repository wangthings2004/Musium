package com.jcxdc.musium.ui.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcxdc.musium.SongDataSource
import com.jcxdc.musium.databinding.FragmentLocalLibraryBinding
import com.jcxdc.musium.ui.adapter.LocalMusicAdapter
import com.jcxdc.musium.ui.viewmodel.LocalAudioViewModel

class LocalLibraryFragment : Fragment(){
    private lateinit var binding: FragmentLocalLibraryBinding
    private lateinit var localMusicAdapter: LocalMusicAdapter
    private lateinit var songDataSource: SongDataSource
    private val localAudioViewModel: LocalAudioViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocalLibraryBinding.inflate(inflater, container, false)
        songDataSource = SongDataSource(requireContext().contentResolver)
        setupRecyclerView()
        loadLocalMusic()
        return binding.root
    }
    private fun formatTime(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    private fun setupRecyclerView() {
        localMusicAdapter = LocalMusicAdapter()
        binding.rvLocalLibrary.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = localMusicAdapter
            localMusicAdapter.onItemClick = { audioItem ->
                val index = localAudioViewModel.localAudios.value?.indexOf(audioItem) ?: 0
                localAudioViewModel.setCurrentAudioIndex(index)
                localAudioViewModel.selectAudio(index)
                (requireActivity() as? MainActivity)?.let { activity ->
                    val musicService = activity.musicService
                    if (musicService != null) {
                        musicService.setLocalAudioViewModel(localAudioViewModel)
                        musicService.playLocalTrack(localAudioViewModel)
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

    private fun loadLocalMusic() {
        localAudioViewModel.localAudios.observe(viewLifecycleOwner){
            localMusicAdapter.submitList(it)
        }
    }
}
