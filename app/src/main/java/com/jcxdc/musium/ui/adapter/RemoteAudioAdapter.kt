package com.jcxdc.musium.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jcxdc.musium.databinding.ItemTopTracksBinding
import com.jcxdc.musium.db.RemoteAudioItem
import javax.inject.Inject

class RemoteAudioAdapter @Inject constructor() :
    ListAdapter<RemoteAudioItem, RemoteAudioAdapter.RemoteAudioViewHolder>(AudioDiffCallback) {
    var onItemClick: ((RemoteAudioItem) -> Unit)? = null
    private val colors = listOf(
        "#FF7777", "#FFFA77", "#4462FF", "#14FF00", "#E231FF", "#00FFFF", "#FB003C", "#F2A5FF"
    )
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemoteAudioViewHolder {
        return RemoteAudioViewHolder(
            ItemTopTracksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RemoteAudioViewHolder, position: Int) {
        val audio = getItem(position)
        holder.bind(audio)
    }

    inner class RemoteAudioViewHolder(private val binding: ItemTopTracksBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RemoteAudioItem) {
            binding.tvTrackTitle.text = data.title
            binding.tvArtistName.text = data.artist
            binding.tvKind.text = data.kind
            val colorIndex = position % colors.size
            binding.bottomView.setBackgroundColor(Color.parseColor(colors[colorIndex]))
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
