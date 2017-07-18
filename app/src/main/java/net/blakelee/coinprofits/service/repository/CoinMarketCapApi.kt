package net.blakelee.coinprofits.service.repository

import net.blakelee.coinprofits.service.model.ticker
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface CoinMarketCapApi {

    companion object {
        val HTTPS_API_COINMARKETCAP_URL: String = "https://api.coinmarketcap.com/v1/"
    }

    @GET("ticker/")
    fun getTicker(
            @Query("limit") limit: Int? = null,
            @Query("convert") convert: String? = null
    ): Call<MutableList<ticker>>

    @GET("ticker/{id}/")
    fun getCoin(
            @Path("id") id: String,
            @Query("convert") convert: String? = null
    ): Call<List<ticker>>
}