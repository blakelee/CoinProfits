package net.blakelee.coinprofits.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import net.blakelee.coinprofits.activities.AddHoldingsActivity
import net.blakelee.coinprofits.activities.MainActivity
import net.blakelee.coinprofits.activities.SettingsActivity

/**
 * Taken from link: https://medium.com/@iammert/new-android-injector-with-dagger-2-part-1-8baa60152abe
 * also: https://github.com/googlesamples/android-architecture-components
 *
 * Declare activities here
 */

@Module
internal abstract class ActivityBuildersModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [AddHoldingsModule::class])
    internal abstract fun  contributeAddHoldingsActivity(): AddHoldingsActivity

    @ActivityScope
    @ContributesAndroidInjector
    internal abstract fun contributeMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector
    internal abstract fun contributeSettingsActivity(): SettingsActivity
}