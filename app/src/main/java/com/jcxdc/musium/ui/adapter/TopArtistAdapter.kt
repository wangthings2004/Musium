package com.jcxdc.musium.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jcxdc.musium.databinding.ItemTopAlbumBinding
import com.jcxdc.musium.databinding.ItemTopArtistBinding
import com.jcxdc.musium.db.RemoteAudioItem
import javax.inject.Inject

class TopArtistAdapter @Inject constructor() :
    ListAdapter<RemoteAudioItem, TopArtistAdapter.TopArtistViewHolder>(AudioDiffCallback) {
    var onItemClick: ((RemoteAudioItem) -> Unit)? = null

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
        fun bind(data: RemoteAudioItem) {
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