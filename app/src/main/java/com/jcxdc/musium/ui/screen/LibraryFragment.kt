package com.jcxdc.musium.ui.screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcxdc.musium.databinding.FragmentLibraryBinding
import com.jcxdc.musium.R
import com.jcxdc.musium.SongDataSource
import com.jcxdc.musium.ui.adapter.LocalMusicAdapter

import com.jcxdc.musium.ui.viewmodel.LocalAudioViewModel


class LibraryFragment : Fragment() {
    lateinit var binding : FragmentLibraryBinding
    lateinit var songDataSource: SongDataSource
    lateinit var localMusicAdapter: LocalMusicAdapter
    private val localAudioViewModel: LocalAudioViewModel by viewModels({ requireActivity() })
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_library, container, false)
        songDataSource = SongDataSource(requireContext().contentResolver)
        localMusicAdapter = LocalMusicAdapter()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLocalMusicAdapter()
        initView()
    }

    private fun setupLocalMusicAdapter() {

        binding.rvLocalMusic.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = localMusicAdapter
            localMusicAdapter.onItemClick = { music,position ->
                Log.d("TAG", "Local audio item: ${position}")
                localAudioViewModel.setCurrentAudioIndex(position)
                Log.d("TAG", "Local audio item: ${localAudioViewModel.currentAudioIndex.value}")

                findNavController().navigate(
                    LibraryFragmentDirections.actionLibraryFragmentToPlayerFragment(true,position,0)
                )

            }
        }
    }

    private fun initView() {
        val musicList = songDataSource.getAllAudio()
        localMusicAdapter.submitList(musicList)


    }


}