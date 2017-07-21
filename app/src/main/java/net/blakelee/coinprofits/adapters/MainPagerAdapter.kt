package net.blakelee.coinprofits.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import net.blakelee.coinprofits.fragments.MainFragment
import net.blakelee.coinprofits.fragments.OverviewFragment

class MainPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment? {
        return when(position) {
            0 -> OverviewFragment()
            1 -> MainFragment()
            else -> null
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> "Overview"
            1 -> "Holdings"
            else -> null
        }
    }
}