package net.blakelee.coinprofits.di.modules

import android.app.Application
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Module
class ImageModule {

    companion object {
        val IMAGE_URL = "https://files.coinmarketcap.com/static/img/coins/64x64/"
        private val DISK_CACHE_SIZE: Long = 5 * 1024 * 1024

        private fun createHttpClient(app: Application): OkHttpClient.Builder {
            val cacheDir = File(app.cacheDir, "http")
            val cache = Cache(cacheDir, DISK_CACHE_SIZE)

            return OkHttpClient.Builder()
                    .cache(cache)
        }
    }

    @Provides @Singleton
    fun provideOkHttpClient(app: Application):OkHttpClient = createHttpClient(app).build()

    @Provides @Singleton
    fun providePicasso(client: OkHttpClient, app: Application): Picasso =
            Picasso.Builder(app)
                    .downloader(OkHttp3Downloader(client))
                    .build()
}