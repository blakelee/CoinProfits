package net.blakelee.coinprofits.databases

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import net.blakelee.coinprofits.models.Holdings

@Dao
interface HoldingsDao {
    @Insert
    fun insertHoldings(holdings: Holdings): Long

    @Query("SELECT * FROM holdings INNER JOIN coin ON coin.id = holdings.id ORDER BY coin.id")
    fun getHoldingsOrderId(): LiveData<List<Holdings>>

    @Query("SELECT * FROM holdings INNER JOIN coin ON coin.id = holdings.id")
    fun getHoldings(): LiveData<List<Holdings>>

    @Query("SELECT * FROM holdings WHERE id = :id LIMIT 1")
    fun getHoldingsById(id: String): Holdings?

    @Update
    fun updateHoldings(holdings: Holdings)

    @Delete
    fun deleteHoldings(holdings: Holdings)
}