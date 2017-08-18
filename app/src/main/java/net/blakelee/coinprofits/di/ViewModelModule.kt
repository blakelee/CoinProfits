package net.blakelee.coinprofits.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import net.blakelee.coinprofits.viewmodels.AddHoldingsViewModel
import net.blakelee.coinprofits.viewmodels.MainViewModel
import net.blakelee.coinprofits.viewmodels.factory.ViewModelFactory

/**
 * Declare ViewModels here
 */

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddHoldingsViewModel::class)
    abstract fun bindAddHoldingsViewModel(addHoldingsViewModel: AddHoldingsViewModel): ViewModel

    /** This is so we can inject ViewModels with parameters */
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}