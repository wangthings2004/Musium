package com.jcxdc.musium.screen

import android.app.Dialog
import com.jcxdc.musium.viewmodel.RemoteAudioViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcxdc.musium.R

import com.jcxdc.musium.adapter.RemoteAudioAdapter
import com.jcxdc.musium.databinding.FragmentHomeBinding
import com.jcxdc.musium.utils.Constants.API_KEY
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
        remoteAudioViewModel.fetchRemoteAudios()
        remoteAudioViewModel.remoteAudioState.observe(viewLifecycleOwner) { state ->
            when(state){
                is RemoteAudioState.Loading ->{
                    binding.ctlMain.visibility = View.GONE
                    binding.pbLoading.visibility = View.VISIBLE
                } is RemoteAudioState.Success ->{
                    binding.pbLoading.visibility = View.GONE
                    binding.ctlMain.visibility = View.VISIBLE

                    remoteAudioAdapter.submitList(state.data)
                } is RemoteAudioState.Error ->{

                }
            }
        }

    }



    private fun setupRecyclerView() {
        binding.rvTopTracks.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = remoteAudioAdapter
            remoteAudioAdapter.onItemClick = {
                it.isSelected = true
                findNavController().navigate(
                    R.id.action_homeFragment_to_playerFragment,
                    bundleOf(API_KEY to it)
                )
            }
        }
    }
}
