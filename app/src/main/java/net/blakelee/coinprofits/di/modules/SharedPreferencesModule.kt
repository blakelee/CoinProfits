package net.blakelee.coinprofits.di.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager

@Module
class SharedPreferencesModule {

    @Provides @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(app)
    }
}