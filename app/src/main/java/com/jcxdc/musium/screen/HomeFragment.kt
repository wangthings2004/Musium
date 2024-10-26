package com.jcxdc.musium.screen

import com.jcxdc.musium.viewmodel.RemoteAudioViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcxdc.musium.R

import com.jcxdc.musium.adapter.RemoteAudioAdapter
import com.jcxdc.musium.databinding.FragmentHomeBinding
import com.jcxdc.musium.utils.Constants.API_KEY

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

        remoteAudioViewModel.remoteAudios.observe(viewLifecycleOwner) { audios ->
            audios?.let {
                remoteAudioAdapter.submitList(it)

            }
        }

    }

    private fun setupRecyclerView() {
        binding.rvTopTracks.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = remoteAudioAdapter
            remoteAudioAdapter.onItemClick = {
                findNavController().navigate(
                    R.id.action_homeFragment_to_playerFragment,
                    bundleOf(API_KEY to it)
                )
            }
        }
    }
}
