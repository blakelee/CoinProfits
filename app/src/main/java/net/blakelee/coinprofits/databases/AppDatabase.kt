package net.blakelee.coinprofits.databases

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import net.blakelee.coinprofits.models.Coin
import net.blakelee.coinprofits.models.Holdings
import android.arch.persistence.db.SupportSQLiteDatabase



@Database(entities = arrayOf(Coin::class, Holdings::class), version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coinModel(): CoinDao
    abstract fun holdingsModel(): HoldingsDao

    companion object {
        //TODO: Remove this before going live. This is just to test how to add migrations
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE coin ADD COLUMN price_eth REAL DEFAULT 0.0")
                database.execSQL("ALTER TABLE holdings ADD COLUMN price_eth REAL")
            }
        }
    }
}