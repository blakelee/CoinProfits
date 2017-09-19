package net.blakelee.coinprofits.repository.db

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import net.blakelee.coinprofits.models.*

@Dao
interface HoldingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHoldings(holdings: Holdings)

    @Query("SELECT COUNT(id) FROM holdings")
    fun getHoldingsCount(): Flowable<Int>

    @Query("SELECT * FROM holdings INNER JOIN coin on coin.id = holdings.id WHERE holdings.id = :id LIMIT 1")
    fun getHoldingsTransactions(id: String): Maybe<HoldingsTransactions>

    @Query("SELECT *, (price / (SELECT price FROM coin WHERE id = 'ethereum')) AS price_eth " +
            "FROM holdings " +
            "INNER JOIN coin " +
            "   ON coin.id = holdings.id " +
            "ORDER BY holdings.itemOrder")
    fun getHoldingsCombined(): Flowable<List<HoldingsCombined>>

    @Query("SELECT coin.id, coin.name, coin.symbol, holdings.itemOrder FROM holdings INNER JOIN coin on coin.id = holdings.id ORDER BY holdings.itemOrder")
    fun getHoldingsOverview(): Flowable<List<HoldingsOverview>>

    @Query("SELECT * FROM holdings ORDER BY itemOrder")
    fun getHoldings(): Flowable<List<Holdings>>

    @Update
    fun updateHoldings(vararg holdings: Holdings)

    @Delete
    fun deleteHoldings(holdings: Holdings)
}