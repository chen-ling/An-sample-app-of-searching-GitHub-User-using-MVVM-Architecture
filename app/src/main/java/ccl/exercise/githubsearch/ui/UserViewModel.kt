package ccl.exercise.githubsearch.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ccl.exercise.githubsearch.extension.fromIoToMain
import ccl.exercise.githubsearch.model.User
import ccl.exercise.githubsearch.service.GithubSearchService
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.concurrent.TimeUnit

class UserViewModel : ViewModel(), KoinComponent {
    companion object {
        private const val DEBOUNCE_TIME = 300L
    }

    private val searchService: GithubSearchService by inject()

    private val searchSubject = PublishSubject.create<String>()
    private val disposables = CompositeDisposable()
    private var pageNumber: Int = 1
    private var query: String? = null

    val userList = MutableLiveData<List<User>>()
    val loadingError = MutableLiveData<Throwable>()
    val isLoading = MutableLiveData<Boolean>()

    init {
        searchSubject
            .debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
            .flatMap {
                searchService.getSearchUserList(it, pageNumber).toObservable()
            }
            .fromIoToMain()
            .doAfterTerminate { isLoading.value = false }
            .subscribe({
                userList.value = it.item
                pageNumber++
            }, {
                loadingError.value = it
            })
            .let(disposables::add)
    }

    fun search(term: String) {
        if (!isSearchTermSame(term)) {
            clearHistory()
            query = term
        }
        searchSubject.onNext(term)
    }

    private fun clearHistory() {
        userList.value = listOf()
        pageNumber = 1
    }

    private fun isSearchTermSame(term: String): Boolean {
        return query == term
    }

    fun loadMore() {
        val queryTerm = query ?: return
        isLoading.value = true
        searchService.getSearchUserList(queryTerm, pageNumber)
            .fromIoToMain()
            .doAfterTerminate { isLoading.value = false }
            .subscribe({
                userList.value = it.item
                pageNumber++
            }, {
                loadingError.value = it
            })
            .let(disposables::add)
    }

    override fun onCleared() {
        disposables.clear()
    }
}