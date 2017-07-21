package net.blakelee.coinprofits.databases

import android.arch.persistence.room.*
import net.blakelee.coinprofits.models.Coin

@Dao
interface CoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCoin(coin: Coin): Long

    @Query("SELECT * FROM coin ORDER BY id")
    fun getCoins(): List<Coin>

    @Query("SELECT * FROM coin WHERE id = :id LIMIT 1")
    fun getCoinById(id: String): Coin

    @Query("SELECT COUNT(id) FROM coin")
    fun getCoinCount(): Int

    @Update
    fun updateCoin(coin: Coin)

    @Delete
    fun deleteCoin(coin: Coin)

    @Query("DELETE FROM coin")
    fun deleteAllCoins()
}