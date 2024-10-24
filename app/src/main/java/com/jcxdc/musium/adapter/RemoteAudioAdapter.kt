package com.jcxdc.musium.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jcxdc.musium.databinding.ItemTopTracksBinding
import com.jcxdc.musium.db.RemoteAudio
import com.jcxdc.musium.db.RemoteAudioItem
import javax.inject.Inject

class RemoteAudioAdapter @Inject constructor() :
    ListAdapter<RemoteAudioItem, RemoteAudioAdapter.RemoteAudioViewHolder>(AudioDiffCallback) {
    var onItemClick: ((RemoteAudioItem) -> Unit)? = null

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


            binding.root.setOnClickListener {
                onItemClick?.invoke(data)
            }
        }

        private fun formatDuration(duration: String): String {
            // Assuming duration is in milliseconds, format it to mm:ss
            val seconds = duration.toLong() / 1000
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return String.format("%d:%02d", minutes, remainingSeconds)
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
