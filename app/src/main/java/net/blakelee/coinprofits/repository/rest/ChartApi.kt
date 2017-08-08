package net.blakelee.coinprofits.repository.rest

import io.reactivex.Observable
import net.blakelee.coinprofits.models.ChartData
import retrofit2.http.GET
import retrofit2.http.Path

interface ChartApi {

    companion object {
        val HTTPS_API_CHART_URL: String = "https://graphs.coinmarketcap.com/"
    }

    @GET("currencies/{id}/{start}/{end}")
    fun getChart(
            @Path("id") id: String,
            @Path("start") start: Long,
            @Path("end") end: Long
    ): Observable<ChartData>
}