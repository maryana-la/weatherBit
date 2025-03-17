package com.mobilepiscine42.advanced_weather_app.pageviewer.helpers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobilepiscine42.advanced_weather_app.pageviewer.Currently
import com.mobilepiscine42.advanced_weather_app.pageviewer.Today
import com.mobilepiscine42.advanced_weather_app.pageviewer.Weekly

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Currently()
            1 -> Today()
            2 -> Weekly()
            else -> Currently()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}