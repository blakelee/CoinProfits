package net.blakelee.coinprofits

import android.app.Activity
import android.app.Application
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import net.blakelee.coinprofits.di.DaggerAppComponent
import javax.inject.Inject

class App : Application(), HasActivityInjector {

    @Inject lateinit var activityDispatchingInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity>
            = activityDispatchingInjector

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)

        if (LeakCanary.isInAnalyzerProcess(this))
            return

        LeakCanary.install(this)
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)
    }
}