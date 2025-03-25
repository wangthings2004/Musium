package com.jcxdc.musium.ui.screen.playlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jcxdc.musium.R
import com.jcxdc.musium.databinding.ItemPlaylistBinding
import com.jcxdc.musium.db.Playlist

class PlaylistAdapter:
    ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder>(PlaylistDiffCallback) {

    var onItemClick: ((Playlist) -> Unit)? = null
    var onItemDeleteClick: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(
            ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = getItem(position)
        holder.bind(playlist)
    }

    inner class PlaylistViewHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(playlist: Playlist) {
            binding.tvPlaylistName.text = playlist.title
            binding.root.setOnClickListener {
                onItemClick?.invoke(playlist)
            }
            binding.ivOptionLocalMusic.setOnClickListener {
                showPopupMenu(it, playlist)
            }
        }
    }
    private fun showPopupMenu(view: View, playlist : Playlist) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_add_playlist_song, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.remove -> {
                    onItemDeleteClick?.invoke(playlist)
                }

            }
            true
        }

        // Hiển thị icon trong menu
        popupMenu.setForceShowIcon(true)
        popupMenu.show()
    }

    companion object PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem == newItem
        }
    }
}
