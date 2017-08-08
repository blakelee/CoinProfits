package net.blakelee.coinprofits.repository.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import net.blakelee.coinprofits.models.Coin
import net.blakelee.coinprofits.models.Holdings

@Database(entities = [Coin::class, Holdings::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coinModel(): CoinDao
    abstract fun holdingsModel(): HoldingsDao
}