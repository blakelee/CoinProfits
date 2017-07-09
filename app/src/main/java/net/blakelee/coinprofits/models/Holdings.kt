package net.blakelee.coinprofits.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "holdings")
class Holdings : Coin() {
    var amount: Double = 0.0
    var buyin: Double = 0.0
}