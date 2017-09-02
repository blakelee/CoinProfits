package net.blakelee.coinprofits.models

import android.arch.persistence.room.Relation

class HoldingsTransactions : Holdings() {
    @Relation(parentColumn = "id", entityColumn = "id", entity = Transaction::class)
    var transaction: List<Transaction> = emptyList()

    lateinit var name: String
    lateinit var symbol: String
}