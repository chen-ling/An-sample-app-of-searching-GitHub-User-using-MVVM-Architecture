package ccl.exercise.githubsearch

import android.app.Application
import ccl.exercise.githubsearch.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        init()
    }

    private fun init() {
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }

}