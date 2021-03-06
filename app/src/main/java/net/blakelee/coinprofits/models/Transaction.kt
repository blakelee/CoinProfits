package net.blakelee.coinprofits.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "transactions", foreignKeys = [ForeignKey(entity = Holdings::class,
        parentColumns = ["id"],
        childColumns = ["id"],
        onDelete = ForeignKey.CASCADE
        )],
        indices = [Index("id")])
class Transaction {
    @PrimaryKey(autoGenerate = true)
    var rowId: Long? = null

    lateinit var id: String
    var amount: Double = 0.0
    var price: Double = 0.0
    var publicKey: String? = null
}