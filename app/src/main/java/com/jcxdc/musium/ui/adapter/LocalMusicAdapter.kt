package com.jcxdc.musium.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jcxdc.musium.databinding.ItemLocalMusicBinding

import com.jcxdc.musium.db.RemoteAudioItem

class LocalMusicAdapter : ListAdapter<RemoteAudioItem, LocalMusicAdapter.LocalMusicViewHolder>(MusicDiffCallback) {

    var onItemClick: ((RemoteAudioItem,Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalMusicViewHolder {
        val binding = ItemLocalMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocalMusicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocalMusicViewHolder, position: Int) {
        val music = getItem(position)
        holder.bind(music,position)
    }
    private fun formatTime(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    inner class LocalMusicViewHolder(private val binding: ItemLocalMusicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(music: RemoteAudioItem,position: Int) {
            binding.tvSongName.text = music.title
            binding.tvArtistName.text = music.artist
            binding.tvDuration.text = formatTime(music.duration)
            binding.root.setOnClickListener {
                onItemClick?.invoke(music,position)
            }
        }
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
