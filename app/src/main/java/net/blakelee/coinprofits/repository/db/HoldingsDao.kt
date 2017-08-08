package net.blakelee.coinprofits.repository.db

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import net.blakelee.coinprofits.models.Holdings

@Dao
interface HoldingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHoldings(holdings: Holdings)

    @Query("SELECT * FROM holdings INNER JOIN coin ON coin.id = holdings.id ORDER BY coin.id")
    fun getHoldingsOrderId(): Flowable<List<Holdings>>

    @Query("SELECT * FROM holdings INNER JOIN coin ON coin.id = holdings.id")
    fun getHoldings(): Flowable<List<Holdings>>

    @Query("SELECT COUNT(id) FROM holdings")
    fun getHoldingsCount(): Flowable<Int>

    @Query("SELECT * FROM holdings WHERE id = :id LIMIT 1")
    fun getHoldingsById(id: String): Maybe<Holdings>

    @Update
    fun updateHoldings(holdings: Holdings)

    @Delete
    fun deleteHoldings(holdings: Holdings)
}