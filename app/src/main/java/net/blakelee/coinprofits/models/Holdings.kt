package net.blakelee.coinprofits.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import net.blakelee.coinprofits.tools.TransactionConverter

@Entity(tableName = "holdings")
class Holdings : Coin() {
    @PrimaryKey(autoGenerate = true)
    var order: Long? = null

    @Ignore
    var transaction: List<Transaction> = emptyList()

    //Average buy-in price of all the transactions
    fun getAveragePrice(): Double = transaction.sumByDouble { it.amount * it.price } / transaction.size

    fun getTotalBalance(): Double = transaction.sumByDouble { it.price }
}