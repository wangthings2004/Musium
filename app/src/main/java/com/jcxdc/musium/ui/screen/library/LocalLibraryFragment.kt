package com.jcxdc.musium.ui.screen.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcxdc.musium.databinding.FragmentLocalLibraryBinding
import com.jcxdc.musium.ui.viewmodel.LocalAudioViewModel
import com.jcxdc.musium.ui.viewmodel.PlaylistViewModel


import android.app.Dialog


import android.util.Log

import android.widget.Toast

import androidx.recyclerview.widget.RecyclerView
import com.jcxdc.musium.R
import com.jcxdc.musium.db.AudioItem
import com.jcxdc.musium.ui.screen.MainActivity
import com.jcxdc.musium.ui.viewmodel.SongViewModel
import org.koin.android.ext.android.inject


class LocalLibraryFragment : Fragment() {
    private lateinit var binding: FragmentLocalLibraryBinding
    private val localMusicAdapter: LocalMusicAdapter by inject()
    private val addPlaylistAdapter: AddPlaylistAdapter by inject()
    private val localAudioViewModel: LocalAudioViewModel by inject()
    private val playlistViewModel: PlaylistViewModel by inject()
    private val songViewModel: SongViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocalLibraryBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModels()
        loadLocalMusic()

        return binding.root
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun setupRecyclerView() {
        localAudioViewModel.loadLocalAudios()

        binding.rvLocalLibrary.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = localMusicAdapter
            localMusicAdapter.onAddToPlaylistClick = {
                showAddPlaylistDialog(it)
            }
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

        private fun showAddPlaylistDialog(song: AudioItem) {
            val dialog = Dialog(requireContext())
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_playlist, null)
            dialog.setContentView(dialogView)

            val rvAddPlaylist = dialogView.findViewById<RecyclerView>(R.id.rvAddPlaylist)


            addPlaylistAdapter.onItemClick = { playlist ->
                song.playlistId = playlist.id
                songViewModel.insertSong(song) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }

            rvAddPlaylist.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = addPlaylistAdapter
            }
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.show()
        }

    private fun loadLocalMusic() {
        localAudioViewModel.localAudios.observe(viewLifecycleOwner) {
            localMusicAdapter.submitList(it)
        }
        playlistViewModel.fetchPlaylists()
        playlistViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isNotEmpty()) {
                addPlaylistAdapter.submitList(playlists)
            } else {
                Toast.makeText(context, "No playlists found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModels() {
        playlistViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Log.d("LocalLibraryFragment", "Error fetching playlists: $it")
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }


}
