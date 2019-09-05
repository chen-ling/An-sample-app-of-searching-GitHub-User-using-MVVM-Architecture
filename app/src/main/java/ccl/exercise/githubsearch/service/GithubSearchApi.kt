package ccl.exercise.githubsearch.service

import ccl.exercise.githubsearch.model.SearchResponse
import ccl.exercise.githubsearch.model.User
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubSearchApi {
    companion object {
        const val API_ENDPOINT = "https://api.github.com"
        private const val PAGE_SIZE = 30
    }

    @GET("/search/users")
    fun searchUsers(@Query("q") term: String, @Query("page") pageNumber: Int = 1, @Query("per_page") pageSize: Int = PAGE_SIZE): Single<SearchResponse<User>>
}