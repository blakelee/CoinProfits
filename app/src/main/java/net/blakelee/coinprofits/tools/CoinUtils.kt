package net.blakelee.coinprofits.tools

import com.google.gson.JsonObject
import net.blakelee.coinprofits.models.Coin

fun JsonObject.toCoin(convert: String): Coin {
    val coin = Coin()

    if (this.has("error"))
        return coin

    coin.id = this.retrieveString("id")
    coin.name = this.retrieveString("name")
    coin.symbol = this.retrieveString("symbol")
    coin.price = this.retrieveDouble("price_" + convert)
    coin.price_btc = this.retrieveDouble("price_btc")
    coin.volume_24h = this.retrieveDouble("24h_volume_" + convert)
    coin.market_cap = this.retrieveDouble("market_cap_" + convert)
    coin.available_supply = this.retrieveDouble("available_supply")
    coin.total_supply = this.retrieveDouble("total_supply")
    coin.percent_change_1h = this.retrieveDouble("percent_change_1h")
    coin.percent_change_24h = this.retrieveDouble("percent_change_24h")
    coin.percent_change_7d = this.retrieveDouble("percent_change_7d")

    return coin
}

fun JsonObject.retrieveString(memberName: String): String {
    try {
        return this.get(memberName).asString
    } catch (e: Exception) {
        return ""
    }
}

fun JsonObject.retrieveDouble(memberName: String): Double {
    try {
        return this[memberName].asDouble
    } catch (e : Exception) {
        return 0.0
    }
}