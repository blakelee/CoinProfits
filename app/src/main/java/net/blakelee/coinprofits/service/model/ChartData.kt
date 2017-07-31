package net.blakelee.coinprofits.service.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ChartData {

    @SerializedName("market_cap_by_available_supply")
    @Expose
    var marketCapByAvailableSupply: List<List<Long>>? = null

    @SerializedName("price_btc")
    @Expose
    var priceBtc: List<List<Long>>? = null

    @SerializedName("price_usd")
    @Expose
    var priceUsd: List<List<Float>>? = null

}