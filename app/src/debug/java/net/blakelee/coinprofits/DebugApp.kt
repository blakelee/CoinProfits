package net.blakelee.coinprofits

import android.app.Application
import com.facebook.stetho.Stetho

class DebugApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}