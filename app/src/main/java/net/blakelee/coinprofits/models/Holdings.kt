package net.blakelee.coinprofits.models

import android.arch.persistence.room.*

@Entity(tableName = "holdings", indices = [Index("id", unique = true)])
open class Holdings {
    @PrimaryKey(autoGenerate = true)
    var itemOrder: Long? = null

    lateinit var id: String
}