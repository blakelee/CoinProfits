package net.blakelee.coinprofits.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import net.blakelee.coinprofits.App
import javax.inject.Singleton

@Singleton
@Component(modules = [
        AndroidInjectionModule::class, //This is a dagger built-in class
        ActivityBuildersModule::class, //This is the class containing all of our activities
        FragmentBuildersModule::class, //Contains all of our fragments
        ViewModelModule::class,        //Contains all of our viewmodels
        AppModule::class //Contains prefs, db, REST, image
        ])

interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {
        @BindsInstance fun application(application: Application): Builder
        fun build(): AppComponent
    }

    override fun inject(app: App)
}