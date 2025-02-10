package com.jcxdc.musium.ui.screen.library

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.jcxdc.musium.databinding.FragmentLibraryBinding
import com.jcxdc.musium.R
import com.jcxdc.musium.content_provider.SongDataSource
import com.jcxdc.musium.ui.screen.BottomViewNavigationListener
import com.jcxdc.musium.ui.screen.MainActivity

import com.jcxdc.musium.ui.viewmodel.LocalAudioViewModel


class LibraryFragment : Fragment(), BottomViewNavigationListener {
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
        setupTabLayout()
        return binding.root

    }

    private fun setupTabLayout() {
        val adapter = ViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "Local" else "Remote"
        }.attach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLocalMusicAdapter()
        initView()
    }
    private fun formatTime(ms: Int): String {
        val minutes = ms / 1000 / 60
        val seconds = (ms / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun setupLocalMusicAdapter() {


    }

    private fun initView() {
        val musicList = songDataSource.getAllAudio()
        localMusicAdapter.submitList(musicList)


    }
    override fun navigateToPlayer() {
        val action = LibraryFragmentDirections.actionLibraryFragmentToPlayerFragment(isLocalMusic = true)
        findNavController().navigate(action)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            context.setBottomViewNavigationListener(this)
        }
    }

    override fun onDetach() {
        super.onDetach()
        (requireActivity() as? MainActivity)?.setBottomViewNavigationListener(null)
    }





}