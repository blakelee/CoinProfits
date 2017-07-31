package net.blakelee.coinprofits.service.repository

import com.google.gson.JsonArray
import io.reactivex.Maybe
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinApi {

    companion object {
        val HTTPS_API_COINMARKETCAP_URL: String = "https://api.coinmarketcap.com/v1/"
    }

    @GET("ticker/")
    fun getCoins(
            @Query("limit") limit: Int? = null,
            @Query("convert") convert: String? = null
    ): Observable<JsonArray>

    @GET("ticker/{id}")
    fun getCoinById(
            @Path("id") id: String,
            @Query("convert") convert: String? = null
    ): Maybe<JsonArray>
}