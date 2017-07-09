package net.blakelee.coinprofits.rest

import net.blakelee.coinprofits.rest.service.CoinApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestClient {

    private val api_url = "https://api.coinmarketcap.com/v1/"

    private val retrofit = Retrofit.Builder()
            .baseUrl(api_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val currency_app = retrofit.create(CoinApi::class.java)

    fun getService(): CoinApi = currency_app
}