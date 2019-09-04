package ccl.exercise.githubsearch

import android.app.Application
import ccl.exercise.githubsearch.service.GithubSearchServiceImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class App : Application() {
    companion object {
        private const val TIME_OUT_SEC = 30L
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        val okhttp =
            OkHttpClient.Builder()
                .writeTimeout(TIME_OUT_SEC, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT_SEC, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Accept", "application/vnd.github.v3.text-match+json").build()
                    chain.proceed(request)
                }

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okhttp.addInterceptor(loggingInterceptor)
        }
        GithubSearchServiceImpl.init(okhttp)

    }


}