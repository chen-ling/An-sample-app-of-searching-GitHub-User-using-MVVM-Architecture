package ccl.exercise.githubsearch.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ccl.exercise.githubsearch.extension.fromIoToMain
import ccl.exercise.githubsearch.extension.toMain
import ccl.exercise.githubsearch.model.Item
import ccl.exercise.githubsearch.model.SearchResponse
import ccl.exercise.githubsearch.model.User
import ccl.exercise.githubsearch.service.GithubSearchService
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class UserViewModel : ViewModel(), KoinComponent {
    companion object {
        private const val DEBOUNCE_TIME = 300L
        private const val RETRY_TIMES = 3L
    }

    private val searchService: GithubSearchService by inject()

    private val searchSubject = PublishSubject.create<String>()
    private val disposables = CompositeDisposable()
    private var loadDisposable: Disposable? = null
    private val githubUserList = mutableListOf<Item.UserItem>()
    private var pageNumber: Int = 1
    private var query: String? = null

    val userList = MutableLiveData<List<Item.UserItem>>()
    val loadingError = MutableLiveData<Throwable>()
    val isLoading = MutableLiveData<Boolean>()
    val noMoreItem = MutableLiveData<Boolean>()

    init {
        subscribeToSearchSubject()
    }

    fun search(term: String) {
        if (!isSearchTermSame(term)) {
            clearHistory()
            query = term
            searchSubject.onNext(term)
        }
    }

    private fun clearHistory() {
        userList.value = listOf()
        githubUserList.clear()
        noMoreItem.value = false
        pageNumber = 1
    }

    private fun isSearchTermSame(term: String): Boolean {
        return query == term
    }

    private fun subscribeToSearchSubject() {
        searchSubject
            .debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
            .toMain()
            .subscribe {
                isLoading.value = true
                searchService.getSearchUserList(it, pageNumber).toObservable()
                    .retry(RETRY_TIMES)
                    .let(this::loadUserFromObservable)
            }.let(disposables::add)

    }

    fun loadMore() {
        if (noMoreItem.value == true || isLoading.value == true) return
        val queryTerm = query ?: return
        isLoading.value = true
        searchService.getSearchUserList(queryTerm, pageNumber)
            .retry(RETRY_TIMES)
            .toObservable()
            .let(this::loadUserFromObservable)
    }

    private fun loadUserFromObservable(observable: Observable<SearchResponse<User>>) {
        loadDisposable?.dispose()
        observable
            .fromIoToMain()
            .doAfterTerminate { }
            .subscribe({
                isLoading.value = false
                pageNumber++
                onMoreUsersLoaded(it.item)
            }, {
                isLoading.value = false
                if (it is HttpException) {
                    val errorBody = it.response()?.errorBody()?.string()
                    loadingError.value = Throwable(errorBody)
                } else {
                    loadingError.value = it
                }
            })
            .let {
                disposables.add(it)
                loadDisposable = it
            }
    }

    private fun onMoreUsersLoaded(users: List<User>) {
        val itemList = mutableListOf<Item.UserItem>()
        users.forEach { itemList.add(Item.UserItem(it)) }
        // append items in order to restore correct data after configuration changed
        githubUserList.addAll(itemList)
        userList.value = githubUserList
        if (users.isEmpty()) {
            noMoreItem.value = true
        }
    }

    override fun onCleared() {
        disposables.clear()
    }
}