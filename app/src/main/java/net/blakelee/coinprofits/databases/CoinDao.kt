package net.blakelee.coinprofits.databases

import android.arch.persistence.room.*
import net.blakelee.coinprofits.models.Coin

@Dao
interface CoinDao {

    @Insert
    fun insertCoin(coin: Coin): Long

    @Query("SELECT * FROM coin ORDER BY id")
    fun getCoins(): List<Coin>

    @Query("SELECT * FROM coin WHERE id = :id LIMIT 1")
    fun getCoinById(id: String): Coin

    @Update
    fun updateCoin(coin: Coin)

    @Delete
    fun deleteCoin(coin: Coin)
}