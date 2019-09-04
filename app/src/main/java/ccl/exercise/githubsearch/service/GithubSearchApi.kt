package ccl.exercise.githubsearch.service

import ccl.exercise.githubsearch.model.SearchResponse
import ccl.exercise.githubsearch.model.User
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubSearchApi {
    @GET("/search/users")
    fun getSearchUsers(@Query("q") term: String, @Query("per_page") pageSize: Int, @Query("page") pageNumber: Int?): Single<SearchResponse<User>>

    @GET("/search/users")
    fun getSearchUsers(@Query("q") term: String, @Query("per_page") pageSize: Int): Single<SearchResponse<User>>
}