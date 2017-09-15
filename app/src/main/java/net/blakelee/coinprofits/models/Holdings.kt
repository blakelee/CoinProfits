package net.blakelee.coinprofits.models

import android.arch.persistence.room.*

@Entity(tableName = "holdings", indices = [Index("id", unique = true)])
open class Holdings {

    @ColumnInfo(name = "itemOrder")
    var order = Long.MAX_VALUE

    @PrimaryKey
    lateinit var id: String
}