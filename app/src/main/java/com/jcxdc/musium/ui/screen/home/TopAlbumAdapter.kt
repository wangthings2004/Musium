package com.jcxdc.musium.ui.screen.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jcxdc.musium.databinding.ItemTopAlbumBinding
import com.jcxdc.musium.db.AudioItem
import javax.inject.Inject

class TopAlbumAdapter @Inject constructor() :
    ListAdapter<AudioItem, TopAlbumAdapter.TopAlbumViewHolder>(AudioDiffCallback) {
    var onItemClick: ((AudioItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopAlbumViewHolder {
        return TopAlbumViewHolder(
            ItemTopAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun submitLimitedList(list: List<AudioItem>) {

        val limitedList = if (list.size > 4) list.take(4) else list
        submitList(limitedList)
    }
    override fun onBindViewHolder(holder: TopAlbumViewHolder, position: Int) {
        val audio = getItem(position)
        holder.bind(audio)
    }

    inner class TopAlbumViewHolder(private val binding: ItemTopAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AudioItem) {
            binding.tvSongName.text = data.title
            binding.tvArtistName.text = data.artist

            binding.root.setOnClickListener {
                onItemClick?.invoke(data)
            }
        }


    }

    companion object AudioDiffCallback : DiffUtil.ItemCallback<AudioItem>() {
        override fun areItemsTheSame(oldItem: AudioItem, newItem: AudioItem): Boolean {
            return oldItem.title == newItem.title && oldItem.artist == newItem.artist
        }

        override fun areContentsTheSame(oldItem: AudioItem, newItem: AudioItem): Boolean {
            return oldItem == newItem
        }
    }
}