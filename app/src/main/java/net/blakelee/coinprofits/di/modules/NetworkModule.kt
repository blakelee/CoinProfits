package net.blakelee.coinprofits.di.modules

import dagger.Module
import dagger.Provides
import net.blakelee.coinprofits.service.repository.CoinMarketCapApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides @Singleton
    fun provideCoinMarketCapApi(): CoinMarketCapApi {
        return Retrofit.Builder()
                .baseUrl(CoinMarketCapApi.HTTPS_API_COINMARKETCAP_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CoinMarketCapApi::class.java)
    }
}