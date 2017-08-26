package net.blakelee.coinprofits.repository.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import net.blakelee.coinprofits.models.Coin
import net.blakelee.coinprofits.models.Holdings
import net.blakelee.coinprofits.models.Transaction

@Database(entities = [Coin::class, Holdings::class, Transaction::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coinModel(): CoinDao
    abstract fun holdingsModel(): HoldingsDao
    abstract fun transactionModel(): TransactionDao
}