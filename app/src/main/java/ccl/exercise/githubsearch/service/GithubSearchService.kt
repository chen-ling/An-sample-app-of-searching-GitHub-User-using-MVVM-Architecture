package ccl.exercise.githubsearch.service

import ccl.exercise.githubsearch.model.SearchResponse
import ccl.exercise.githubsearch.model.User
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

interface GithubSearchService {
    fun getSearchUserList(term: String): Single<SearchResponse<User>>
    fun getSearchUserListNextPage(term: String, pageNumber: Int): Single<SearchResponse<User>>
}

object GithubSearchServiceImpl : GithubSearchService {

    private const val API_ENDPOINT = "https://api.github.com"
    private const val PAGE_SIZE = 10

    private lateinit var builder: OkHttpClient.Builder


    private val api: GithubSearchApi by lazy {
        Retrofit.Builder()
            .client(builder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(API_ENDPOINT)
            .build()
            .create(GithubSearchApi::class.java)
    }

    fun init(builder: OkHttpClient.Builder) {
        this.builder = builder
    }

    override fun getSearchUserList(term: String): Single<SearchResponse<User>> =
        api.getSearchUsers(term, PAGE_SIZE)


    override fun getSearchUserListNextPage(term: String, pageNumber: Int): Single<SearchResponse<User>> =
        api.getSearchUsers(term, PAGE_SIZE, pageNumber)

}