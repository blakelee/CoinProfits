package net.blakelee.coinprofits.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.blakelee.coinprofits.activities.SettingsActivity
import net.blakelee.coinprofits.fragments.MainFragment
import net.blakelee.coinprofits.fragments.OverviewFragment

/**
 * Declare fragments here
 */

@Module
internal abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeOverviewFragment(): OverviewFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsActivity.SettingsFragment
}