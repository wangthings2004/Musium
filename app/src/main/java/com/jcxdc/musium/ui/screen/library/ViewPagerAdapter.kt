package com.jcxdc.musium.ui.screen.library

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2 // Two tabs: Local and Remote

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LocalLibraryFragment()

            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}