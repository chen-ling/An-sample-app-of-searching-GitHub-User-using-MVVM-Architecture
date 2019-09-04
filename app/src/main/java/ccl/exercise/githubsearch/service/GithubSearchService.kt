package ccl.exercise.githubsearch.service

import ccl.exercise.githubsearch.model.SearchResponse
import ccl.exercise.githubsearch.model.User
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject

interface GithubSearchService {
    fun getSearchUserList(term: String): Single<SearchResponse<User>>
    fun getSearchUserListNextPage(term: String, pageNumber: Int): Single<SearchResponse<User>>
}

class GithubSearchServiceImpl : GithubSearchService, KoinComponent {

    companion object {
        const val API_ENDPOINT = "https://api.github.com"
        private const val PAGE_SIZE = 10
    }

    val api: GithubSearchApi by inject()

    override fun getSearchUserList(term: String): Single<SearchResponse<User>> =
        api.getSearchUsers(term, PAGE_SIZE)


    override fun getSearchUserListNextPage(term: String, pageNumber: Int): Single<SearchResponse<User>> =
        api.getSearchUsers(term, PAGE_SIZE, pageNumber)

}