package com.onourem.android.activity.ui.counselling.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class DynamicFragmentPagerAdapter(private val fragmentList: List<Fragment>, fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        // Directly return the Fragment instance from the list
        return fragmentList[position]
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }
}
