package net.blakelee.coinprofits.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "transaction")
class Transaction {
    @PrimaryKey
    var id: String = ""
    var amount: Double = 0.0
    var price: Double = 0.0
    var publicKey: String? = null
}