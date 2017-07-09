package net.blakelee.coinprofits.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "coin")
open class Coin {
    @PrimaryKey
    var id: String = ""                    //ethereum
    var name: String = ""                  //Ethereum
    var symbol: String = ""                //ETH
    var image: ByteArray? = null           //Contains the image
    var currency: String = "USD"          //Currency set from settings
    var price: Double? = 0.0              //Last updated price
    var price_btc: Double? = 0.0
    var volume_24h: Double? = 0.0
    var market_cap: Double? = 0.0
    var total_supply: Double? = 0.0
    var percent_change_1h: Double? = 0.0
    var percent_change_24h: Double? = 0.0
    var percent_change_7d: Double? = 0.0
}