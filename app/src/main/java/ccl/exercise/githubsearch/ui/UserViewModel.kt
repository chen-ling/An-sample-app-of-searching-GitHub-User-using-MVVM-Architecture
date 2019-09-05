package ccl.exercise.githubsearch.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ccl.exercise.githubsearch.extension.fromIoToMain
import ccl.exercise.githubsearch.model.User
import ccl.exercise.githubsearch.service.GithubSearchService
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.concurrent.TimeUnit

class UserViewModel : ViewModel(), KoinComponent {
    companion object {
        private const val RETRY_TIMES = 3L
        private const val DEBOUNCE_TIME = 300L
    }

    private val searchService: GithubSearchService by inject()
    private val disposables = CompositeDisposable()

    private val searchTerm = MutableLiveData<String>()
    val userList = MutableLiveData<List<User>>()
    val loadingError = MutableLiveData<Throwable>()

    fun search(term: String) {
        if (!isSearchTermSame(term)) {
            clearHistory()
            searchTerm.value = term
        }
        fetchSearchResult(term)
    }

    private fun clearHistory() {
        userList.value = listOf()
    }

    private fun isSearchTermSame(term: String): Boolean {
        return searchTerm.value == term
    }

    private fun fetchSearchResult(term: String) {
        disposables.clear()
        Observable.just(term)
            .debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
            .flatMap {
                searchService.getSearchUserList(it).toObservable()
            }.retry(RETRY_TIMES)
            .fromIoToMain()
            .subscribe({
                userList.value = it.item
            }, {
                loadingError.value = it
            })
            .let(disposables::add)
    }

    override fun onCleared() {
        disposables.clear()
    }
}