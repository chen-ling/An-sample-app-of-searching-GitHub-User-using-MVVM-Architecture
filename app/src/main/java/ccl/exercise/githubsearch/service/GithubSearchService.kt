package ccl.exercise.githubsearch.service

import ccl.exercise.githubsearch.model.SearchResponse
import ccl.exercise.githubsearch.model.User
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject

interface GithubSearchService {
    fun getSearchUserList(term: String, pageNumber: Int): Single<SearchResponse<User>>
}

class GithubSearchServiceImpl : GithubSearchService, KoinComponent {

    companion object {
        private const val RETRY_TIMES = 3L
    }

    private val api: GithubSearchApi by inject()

    override fun getSearchUserList(term: String, pageNumber: Int): Single<SearchResponse<User>> =
        api.searchUsers(term, pageNumber).retry(RETRY_TIMES)
}