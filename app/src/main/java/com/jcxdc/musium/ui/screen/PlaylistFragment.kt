package com.jcxdc.musium.ui.screen

import PlaylistViewModelFactory
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import com.jcxdc.musium.R
import com.jcxdc.musium.databinding.FragmentPlaylistBinding

import com.jcxdc.musium.db.PlaylistDao

import com.jcxdc.musium.model.repository.PlaylistRepository
import com.jcxdc.musium.ui.adapter.PlaylistAdapter
import com.jcxdc.musium.ui.viewmodel.PlaylistViewModel


class PlaylistFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var viewModel: PlaylistViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false)
        val playlistDao: PlaylistDao = UserDatabase.getInstance(requireContext()).playlistDao()
        val repository = PlaylistRepository(playlistDao)
        val viewModelFactory = PlaylistViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PlaylistViewModel::class.java)

        val playlistAdapter = PlaylistAdapter()
        binding.rvMyplaylist.adapter = playlistAdapter
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.submitList(playlists)
            binding.llAdd.visibility = if (playlists.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivAddPlaylist.setOnClickListener {
            showCreatePlaylistDialog()
        }

        viewModel.fetchPlaylists(userId = 1)

        return binding.root
    }

    private fun showCreatePlaylistDialog() {
        val dialog = Dialog(requireContext())
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_new_playlist, null)
        dialog.setContentView(dialogView)
        val etPlaylistTitle = dialogView.findViewById<EditText>(R.id.etPlaylistTitle)
        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)
        val btnCreate = dialogView.findViewById<TextView>(R.id.btnCreate)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnCreate.setOnClickListener {
            val playlistTitle = etPlaylistTitle.text.toString()
            if (playlistTitle.isNotEmpty()) {
                viewModel.createPlaylist(userId = 1, title = playlistTitle) { success, message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    if (success) {
                        dialog.dismiss()
                    }
                }
            } else {
                Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
