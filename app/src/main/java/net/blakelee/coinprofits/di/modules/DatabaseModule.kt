package net.blakelee.coinprofits.di.modules

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import net.blakelee.coinprofits.databases.AppDatabase
import javax.inject.Singleton

@Module
class DatabaseModule {

    companion object {
        private const val NAME = "app.db"
    }

    @Singleton @Provides
    fun providePersistentDatabase(app: Application): AppDatabase =
            Room.databaseBuilder(app, AppDatabase::class.java, NAME)
                    .addMigrations(AppDatabase.MIGRATION_1_2)
                    .allowMainThreadQueries()
                    .build()

    @Singleton @Provides
    fun provideCoinDao(db: AppDatabase) = db.coinModel()

    @Singleton @Provides
    fun provideHoldingsDao(db: AppDatabase) = db.holdingsModel()
}