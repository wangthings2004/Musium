package com.jcxdc.musium.ui.screen.playlist

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.jcxdc.musium.R
import com.jcxdc.musium.databinding.FragmentPlaylistBinding
import com.jcxdc.musium.ui.viewmodel.PlaylistViewModel
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.LinearLayoutManager
import com.jcxdc.musium.ui.screen.BottomViewNavigationListener
import com.jcxdc.musium.ui.screen.MainActivity
import com.jcxdc.musium.ui.screen.home.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistFragment : Fragment(), BottomViewNavigationListener {
    private lateinit var binding: FragmentPlaylistBinding
    private val playlistViewModel: PlaylistViewModel by viewModels()
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false)
        setupRecyclerView()
        binding.ivAddPlaylist.setOnClickListener {
            showCreatePlaylistDialog()
        }
        binding.ivAddPlaylist1.setOnClickListener {
            showCreatePlaylistDialog()
        }
        observeViewModel()
        return binding.root
    }

    private fun setupRecyclerView() {
        playlistAdapter = PlaylistAdapter().apply {
            onItemClick = { playlist ->
                var action = PlaylistFragmentDirections.actionPlaylistFragmentToPlaylistSongFragment(playlist.id)
                findNavController().navigate(action)
            }
            onItemDeleteClick = { playlist ->
                playlistViewModel.deletePlaylist(playlist) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.rvMyplaylist.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistAdapter
        }
    }

    private fun observeViewModel() {
        playlistViewModel.fetchPlaylists()
        playlistViewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.submitList(playlists)
            Toast.makeText(context, playlists.toString(), Toast.LENGTH_SHORT).show()
            if (playlists.isNotEmpty()) {
                binding.llAdd.visibility = View.GONE
                binding.ivAddPlaylist1.visibility = View.VISIBLE
            } else {
                binding.llAdd.visibility = View.VISIBLE
                binding.ivAddPlaylist1.visibility = View.GONE
            }
        }
        playlistViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showCreatePlaylistDialog() {
        val dialog = Dialog(requireContext())
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_new_playlist, null)
        dialog.setContentView(dialogView)
        val etPlaylistTitle = dialogView.findViewById<EditText>(R.id.etPlaylistTitle)
        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)
        val btnCreate = dialogView.findViewById<TextView>(R.id.btnCreate)
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnCreate.setOnClickListener {
            val playlistTitle = etPlaylistTitle.text.toString()
            if (playlistTitle.isNotEmpty()) {
                playlistViewModel.createPlaylist( title = playlistTitle) { success, message ->
                    Log.d("PlaylistFragment", message)
                }
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    override fun navigateToPlayer() {
        (requireActivity() as? MainActivity)?.let { activity ->
            val musicService = activity.musicService
            val isLocal = musicService?.isPlayingLocal() ?: false
            val action = PlaylistFragmentDirections.actionPlaylistFragmentToPlayerFragment(isLocal)
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