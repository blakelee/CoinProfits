package net.blakelee.coinprofits

import android.app.Application
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import net.blakelee.coinprofits.di.component.AppComponent
import net.blakelee.coinprofits.di.component.DaggerAppComponent
import net.blakelee.coinprofits.di.modules.*

class App : Application() {

    companion object {
        lateinit var app: App
        val component: AppComponent by lazy {
            DaggerAppComponent
                    .builder()
                    .appModule(AppModule(app))
                    .databaseModule(DatabaseModule())
                    .networkModule(NetworkModule())
                    .imageModule(ImageModule())
                    .sharedPreferencesModule(SharedPreferencesModule())
                    .build()
        }
    }

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)

        if (LeakCanary.isInAnalyzerProcess(this))
            return

        LeakCanary.install(this)
        app = this
    }
}