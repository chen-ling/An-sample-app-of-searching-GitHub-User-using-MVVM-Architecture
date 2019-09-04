package ccl.exercise.githubsearch.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ccl.exercise.githubsearch.extension.fromIoToMain
import ccl.exercise.githubsearch.model.User
import ccl.exercise.githubsearch.service.GithubSearchService
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent
import org.koin.core.inject

class UserViewModel : ViewModel(), KoinComponent {
    private val searchService: GithubSearchService by inject()
    private val disposables = CompositeDisposable()

    val userList = MutableLiveData<List<User>>()
    val loadingError = MutableLiveData<Throwable>()

    fun search(term: String) {
        searchService
            .getSearchUserList(term)
            .retry(3)
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