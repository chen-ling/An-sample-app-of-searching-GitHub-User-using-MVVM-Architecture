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

    private val api: GithubSearchApi by inject()

    // preparing local cache here if we need

    override fun getSearchUserList(term: String, pageNumber: Int): Single<SearchResponse<User>> =
        api.searchUsers(term, pageNumber)
}