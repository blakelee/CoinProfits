package net.blakelee.coinprofits.models

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation


class TransactionHoldings {
    @Embedded
    var holdings: Holdings? = null

    @Relation(parentColumn = "id", entityColumn = "id", entity = Transaction::class)
    var transaction: List<Transaction> = emptyList()
}