package com.jcxdc.musium.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jcxdc.musium.databinding.ItemTopAlbumBinding
import com.jcxdc.musium.databinding.ItemTopTracksBinding
import com.jcxdc.musium.db.RemoteAudioItem
import javax.inject.Inject

class TopAlbumAdapter @Inject constructor() :
    ListAdapter<RemoteAudioItem, TopAlbumAdapter.TopAlbumViewHolder>(AudioDiffCallback) {
    var onItemClick: ((RemoteAudioItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopAlbumViewHolder {
        return TopAlbumViewHolder(
            ItemTopAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun submitLimitedList(list: List<RemoteAudioItem>) {

        val limitedList = if (list.size > 4) list.take(4) else list
        submitList(limitedList)
    }
    override fun onBindViewHolder(holder: TopAlbumViewHolder, position: Int) {
        val audio = getItem(position)
        holder.bind(audio)
    }

    inner class TopAlbumViewHolder(private val binding: ItemTopAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RemoteAudioItem) {
            binding.tvSongName.text = data.title
            binding.tvArtistName.text = data.artist

            binding.root.setOnClickListener {
                onItemClick?.invoke(data)
            }
        }


    }

    companion object AudioDiffCallback : DiffUtil.ItemCallback<RemoteAudioItem>() {
        override fun areItemsTheSame(oldItem: RemoteAudioItem, newItem: RemoteAudioItem): Boolean {
            return oldItem.title == newItem.title && oldItem.artist == newItem.artist
        }

        override fun areContentsTheSame(oldItem: RemoteAudioItem, newItem: RemoteAudioItem): Boolean {
            return oldItem == newItem
        }
    }
}