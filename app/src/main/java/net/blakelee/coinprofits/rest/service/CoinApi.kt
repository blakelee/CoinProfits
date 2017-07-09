package net.blakelee.coinprofits.rest.service

import net.blakelee.coinprofits.rest.model.ticker
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface CoinApi {

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