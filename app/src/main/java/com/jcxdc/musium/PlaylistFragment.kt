package com.jcxdc.musium

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.jcxdc.musium.databinding.FragmentPlaylistBinding

class PlaylistFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playlist, container, false)


        binding.ivAddPlaylist.setOnClickListener {

            showCreatePlaylistDialog()
        }

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
                // Thực hiện thêm playlist ở đây
                Toast.makeText(context, "Playlist created: $playlistTitle", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
