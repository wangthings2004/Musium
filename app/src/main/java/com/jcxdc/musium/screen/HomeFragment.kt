package com.jcxdc.musium.screen

import RemoteAudioViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.jcxdc.musium.adapter.RemoteAudioAdapter
import com.jcxdc.musium.databinding.FragmentHomeBinding
import com.jcxdc.musium.utils.RemoteAudioState

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    @Inject
    lateinit var remoteAudioAdapter: RemoteAudioAdapter

    // ViewModel for fetching remote audio
    private val remoteAudioViewModel: RemoteAudioViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        remoteAudioViewModel.responseRemoteAudio.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is RemoteAudioState.Loading -> {

                }
                is RemoteAudioState.Success -> {

                    remoteAudioAdapter.submitList(state.data)
                }
                is RemoteAudioState.Error -> {

                }

            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvTopTracks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = remoteAudioAdapter
        }
    }
}
