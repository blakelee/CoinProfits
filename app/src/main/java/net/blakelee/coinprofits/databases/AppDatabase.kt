package net.blakelee.coinprofits.databases

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import net.blakelee.coinprofits.models.Coin
import net.blakelee.coinprofits.models.Holdings

@Database(entities = arrayOf(Coin::class, Holdings::class), version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coinModel(): CoinDao
    abstract fun holdingsModel(): HoldingsDao

    companion object {
        private const val NAME = "app.db"
        private var instance: AppDatabase? = null

        fun createInMemoryDatabase(context: Context) : AppDatabase {
            if (instance == null)
                instance = Room.inMemoryDatabaseBuilder(context.applicationContext, AppDatabase::class.java)
                        .allowMainThreadQueries()
                        .build()

            return instance!!
        }

        fun createPersistentDatabase(context: Context) : AppDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, NAME)
                        .allowMainThreadQueries()
                        .build()

            return instance!!
        }

        fun destroyInstance() { instance = null }
    }
}