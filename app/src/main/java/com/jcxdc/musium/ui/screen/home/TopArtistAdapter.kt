package com.jcxdc.musium.ui.screen.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jcxdc.musium.databinding.ItemTopArtistBinding
import com.jcxdc.musium.db.AudioItem
import javax.inject.Inject

class TopArtistAdapter @Inject constructor() :
    ListAdapter<AudioItem, TopArtistAdapter.TopArtistViewHolder>(AudioDiffCallback) {
    var onItemClick: ((AudioItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopArtistViewHolder {
        return TopArtistViewHolder(
            ItemTopArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: TopArtistViewHolder, position: Int) {
        val audio = getItem(position)
        holder.bind(audio)
    }

    inner class TopArtistViewHolder(private val binding: ItemTopArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AudioItem) {
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