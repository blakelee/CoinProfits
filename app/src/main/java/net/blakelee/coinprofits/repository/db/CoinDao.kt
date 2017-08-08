package net.blakelee.coinprofits.repository.db

import android.arch.persistence.room.*
import io.reactivex.Flowable
import net.blakelee.coinprofits.models.Coin

@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoin(coin: Coin): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoins(vararg coins: Coin)

    @Query("SELECT * FROM coin ORDER BY id")
    fun getCoins(): Flowable<List<Coin>>

    @Query("SELECT * FROM coin WHERE id = :id LIMIT 1")
    fun getCoinById(id: String): Coin

    @Update
    fun updateCoin(coin: Coin)

    @Update
    fun updateCoins(vararg coins: Coin)

    @Delete
    fun deleteCoin(coin: Coin)

    @Query("DELETE FROM coin")
    fun deleteAllCoins()
}