package ccl.exercise.githubsearch.di

import ccl.exercise.githubsearch.BuildConfig
import ccl.exercise.githubsearch.service.GithubSearchApi
import ccl.exercise.githubsearch.service.GithubSearchService
import ccl.exercise.githubsearch.service.GithubSearchServiceImpl
import ccl.exercise.githubsearch.ui.UserViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {
    single {
        val timeoutSeconds = 15L

        val okhttp =
            OkHttpClient.Builder()
                .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Accept", "application/vnd.github.v3+json").build()
                    chain.proceed(request)
                }

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okhttp.addInterceptor(loggingInterceptor)
        }

        okhttp.build()
    }

    single<Retrofit> {
        Retrofit.Builder()
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(GithubSearchApi.API_ENDPOINT)
            .build()
    }

    single<GithubSearchApi> { get<Retrofit>().create(GithubSearchApi::class.java) }

    single<GithubSearchService> { GithubSearchServiceImpl() }

    viewModel { UserViewModel() }
}