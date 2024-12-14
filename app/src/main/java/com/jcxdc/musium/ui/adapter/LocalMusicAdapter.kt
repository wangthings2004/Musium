package com.jcxdc.musium.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jcxdc.musium.R
import com.jcxdc.musium.databinding.ItemLocalMusicBinding

import com.jcxdc.musium.db.RemoteAudioItem

class LocalMusicAdapter : ListAdapter<RemoteAudioItem, LocalMusicAdapter.LocalMusicViewHolder>(MusicDiffCallback) {

    var onItemClick: ((RemoteAudioItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalMusicViewHolder {
        val binding = ItemLocalMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocalMusicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocalMusicViewHolder, position: Int) {
        val music = getItem(position)
        holder.bind(music)
    }
    private fun formatTime(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    inner class LocalMusicViewHolder(private val binding: ItemLocalMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(music: RemoteAudioItem) {
            binding.tvSongName.text = music.title
            binding.tvArtistName.text = music.artist
            binding.tvDuration.text = formatTime(music.duration)
            if (music.isSelected) {
                binding.ctlLocalBG.setBackgroundResource(R.color.selected_bg)
            } else {
                binding.ctlLocalBG.setBackgroundResource(R.color.black_bg)
            }
            binding.root.setOnClickListener {
                onItemClick?.invoke(music)
            }
            binding.ivOptionLocalMusic.setOnClickListener {
                showPopupMenu(it, music)
            }
        }
    }
    private fun showPopupMenu(view: View, music: RemoteAudioItem) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_local_music_options, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_add_to_playlist -> {
                    // Thêm logic add vào playlist
                }
                R.id.menu_share -> {
                    // Thêm logic chia sẻ
                }
            }
            true
        }

        // Hiển thị icon trong menu
        popupMenu.setForceShowIcon(true)
        popupMenu.show()
    }

    companion object MusicDiffCallback : DiffUtil.ItemCallback<RemoteAudioItem>() {
        override fun areItemsTheSame(oldItem: RemoteAudioItem, newItem: RemoteAudioItem): Boolean {
            return oldItem.title == newItem.title && oldItem.artist == newItem.artist
        }

        override fun areContentsTheSame(oldItem: RemoteAudioItem, newItem: RemoteAudioItem): Boolean {
            return oldItem == newItem
        }
    }
}
