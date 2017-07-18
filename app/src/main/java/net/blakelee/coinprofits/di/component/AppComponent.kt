package net.blakelee.coinprofits.di.component

import dagger.Component
import net.blakelee.coinprofits.adapters.AutoCompleteCurrencyAdapter
import net.blakelee.coinprofits.adapters.HoldingsAdapter
import net.blakelee.coinprofits.di.modules.*
import net.blakelee.coinprofits.dialogs.HoldingsDialog
import net.blakelee.coinprofits.viewmodels.MainViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, DatabaseModule::class, ImageModule::class,
        NetworkModule::class, SharedPreferencesModule::class))

interface AppComponent {
    fun inject(mainViewModel: MainViewModel)
    fun inject(holdings: HoldingsAdapter.HoldingsViewHolder)
    fun inject(holdingsDialog: HoldingsDialog)
    fun inject(autoCompleteCurrencyAdapter: AutoCompleteCurrencyAdapter)
}