package net.blakelee.coinprofits.models

import android.arch.persistence.room.*
import net.blakelee.coinprofits.tools.TransactionConverter

@Entity(tableName = "holdings", indices = [(Index("id", unique = true))])
class Holdings : Coin(){
    @PrimaryKey(autoGenerate = true)
    var itemOrder: Long? = null

}