package net.blakelee.coinprofits.repository.rest

import io.reactivex.Observable
import net.blakelee.coinprofits.models.ERC20
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ERC20Api {

    companion object {
        val HTTPS_API_ETHPLORER_URL: String = "https://api.ethplorer.io/"
        val API_KEY: String = "freekey"
    }

    @GET("getAddressInfo/{address}")
    fun getAddressInfo(
            @Path("address") address: String,
            @Query("apiKey") key: String = API_KEY
    ): Observable<ERC20>
}