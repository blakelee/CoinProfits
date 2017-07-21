package net.blakelee.coinprofits.service

import net.blakelee.coinprofits.service.repository.CoinMarketCapApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestClient {

    private val api_url = "https://api.coinmarketcap.com/v1/"

    private val retrofit = Retrofit.Builder()
            .baseUrl(api_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val currency_app = retrofit.create(CoinMarketCapApi::class.java)

    fun getService(): CoinMarketCapApi = currency_app
}