package net.blakelee.coinprofits.models

import android.arch.persistence.room.ColumnInfo

class HoldingsOverview (
    val id: String,     //ethereum
    val name: String,   //Ethereum
    val symbol: String, //ETH

    @ColumnInfo(name = "itemOrder")
    val order: Long
) {
    override fun toString(): String = name
}