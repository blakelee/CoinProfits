package net.blakelee.coinprofits.di

import android.app.Application
import android.arch.persistence.room.Room
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import net.blakelee.coinprofits.repository.*
import net.blakelee.coinprofits.repository.db.AppDatabase
import net.blakelee.coinprofits.repository.db.CoinDao
import net.blakelee.coinprofits.repository.db.HoldingsDao
import net.blakelee.coinprofits.repository.rest.ChartApi
import net.blakelee.coinprofits.repository.rest.CoinApi
import net.blakelee.coinprofits.repository.rest.ERC20Api
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Singleton

@Module
class AppModule{

    companion object {
        private const val NAME = "app.db"
        private const val DISK_CACHE_SIZE: Long = 5 * 1024 * 1024
        const val IMAGE_URL = "https://files.coinmarketcap.com/static/img/coins/64x64/"

        private fun createHttpClient(app: Application): OkHttpClient.Builder {
            val cacheDir = File(app.cacheDir, "http")
            val cache = Cache(cacheDir, DISK_CACHE_SIZE)

            return OkHttpClient.Builder()
                    .cache(cache)
        }
    }

    /** DATABASE COMPONENTS */
    @Singleton
    @Provides
    fun providePersistentDatabase(app: Application): AppDatabase =
            Room.databaseBuilder(app, AppDatabase::class.java, NAME)
                    .allowMainThreadQueries()
                    .build()

    @Provides
    @Singleton
    fun provideCoinDao(db: AppDatabase) = db.coinModel()

    @Provides
    @Singleton
    fun provideHoldingsDao(db: AppDatabase) = db.holdingsModel()


    /** IMAGE COMPONENTS*/
    @Provides
    @Singleton
    fun provideOkHttpClient(app: Application):OkHttpClient = createHttpClient(app).build()

    @Provides
    @Singleton
    fun providePicasso(client: OkHttpClient, app: Application): Picasso =
            Picasso.Builder(app)
                    .downloader(OkHttp3Downloader(client))
                    .build()


    /** NETWORK COMPONENTS */
    @Provides
    @Singleton
    fun provideCoinMarketCapApi(): CoinApi =
         Retrofit.Builder()
                .baseUrl(CoinApi.HTTPS_API_COINMARKETCAP_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(CoinApi::class.java)

    @Provides
    @Singleton
    fun provideChartApi(): ChartApi =
            Retrofit.Builder()
                    .baseUrl(ChartApi.HTTPS_API_CHART_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(ChartApi::class.java)

    @Provides
    @Singleton
    fun provideERC20Api(): ERC20Api =
            Retrofit.Builder()
                    .baseUrl(ERC20Api.HTTPS_API_ETHPLORER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
                    .create(ERC20Api::class.java)


    /** SHARED PREFERENCES COMPONENTS */
    @Provides
    @Singleton
    fun provideSharedPreferences(app: Application): SharedPreferences
        = PreferenceManager.getDefaultSharedPreferences(app)


    /** REPOSITORIES */
    @Provides
    @Singleton
    fun provideChartRepository(api: ChartApi) = ChartRepository(api)

    @Provides
    @Singleton
    fun provideCoinRepository(dao: CoinDao, api: CoinApi) = CoinRepository(dao, api)

    @Provides
    @Singleton
    fun provideHoldingsRepository(hdao: HoldingsDao, cdao: CoinDao, api: CoinApi) = HoldingsRepository(hdao, cdao, api)

    @Provides
    @Singleton
    fun providePreferencesRepository(prefs: SharedPreferences) = PreferencesRepository(prefs)

    @Provides
    @Singleton
    fun provideERC20Repository(api: ERC20Api) = ERC20Repository(api)
}