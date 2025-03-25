package com.jcxdc.musium.ui.screen.playlistsong

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jcxdc.musium.R
import com.jcxdc.musium.databinding.ItemLocalMusicBinding
import com.jcxdc.musium.db.AudioItem


class PlaylistSongAdapter:
    ListAdapter<AudioItem, PlaylistSongAdapter.PlaylistSongViewHolder>(PlaylistDiffCallback) {

    var onItemClick: ((AudioItem) -> Unit)? = null
    var onItemDeleteClick: ((AudioItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistSongViewHolder {
        return PlaylistSongViewHolder(
            ItemLocalMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlaylistSongViewHolder, position: Int) {
        val playlist = getItem(position)
        holder.bind(playlist)
    }

    inner class PlaylistSongViewHolder(private val binding: ItemLocalMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: AudioItem) {
            binding.tvSongName.text = song.title
            binding.tvArtistName.text = song.artist
            binding.tvDuration.text = formatTime(song.duration)
            binding.root.setOnClickListener {
                onItemClick?.invoke(song)

            }
            binding.ivOptionLocalMusic.setOnClickListener {
                showPopupMenu(it, song)
            }
        }
    }
    @SuppressLint("NewApi")
    private fun showPopupMenu(view: View, music: AudioItem) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_add_playlist_song, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.remove -> {
                    onItemDeleteClick?.invoke(music)
                }
                R.id.menu_share -> {

                }
            }
            true
        }

        // Hiển thị icon trong menu
        popupMenu.setForceShowIcon(true)
        popupMenu.show()
    }
    @SuppressLint("DefaultLocale")
    private fun formatTime(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    companion object PlaylistDiffCallback : DiffUtil.ItemCallback<AudioItem>() {
        override fun areItemsTheSame(oldItem: AudioItem, newItem: AudioItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AudioItem, newItem: AudioItem): Boolean {
            return oldItem == newItem
        }
    }
}
