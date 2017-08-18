package net.blakelee.coinprofits.di

import android.app.Application
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import net.blakelee.coinprofits.adapters.AutoCompleteCurrencyAdapter

@Module
class AddHoldingsModule {

    @Provides
    @ActivityScope
    fun provideAutoCompleteCurrencyAdapter(app: Application, picasso: Picasso) =
        AutoCompleteCurrencyAdapter(app, picasso)
}