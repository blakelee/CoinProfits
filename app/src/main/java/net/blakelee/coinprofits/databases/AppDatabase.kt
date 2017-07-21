package net.blakelee.coinprofits.databases

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import net.blakelee.coinprofits.models.Coin
import net.blakelee.coinprofits.models.Holdings
import android.arch.persistence.db.SupportSQLiteDatabase



@Database(entities = arrayOf(Coin::class, Holdings::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coinModel(): CoinDao
    abstract fun holdingsModel(): HoldingsDao
}