package ccl.exercise.githubsearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ccl.exercise.githubsearch.extension.afterTextChanged
import ccl.exercise.githubsearch.service.GithubSearchServiceImpl
import ccl.exercise.githubsearch.ui.UserSearchAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class SearchingUserActivity : AppCompatActivity() {

    private lateinit var userSearchAdapter: UserSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        callApi()
    }

    private fun setupView() {
        search.afterTextChanged {
            if (it.isNotEmpty()) {
                searchSubject.onNext(it)
            }
        }
        userSearchAdapter = UserSearchAdapter()
        recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(this@SearchingUserActivity)
            adapter = userSearchAdapter
            layoutManager = linearLayoutManager

        }
    }

    fun callApi() {
        //viewmodel
        searchSubject
            .debounce(300, TimeUnit.MILLISECONDS)
            .flatMap { GithubSearchServiceImpl.getSearchUserList(it).toObservable() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                userSearchAdapter.setItems(it.item)
            })
    }

    //TODO move subject to viewmodel
    private val searchSubject = PublishSubject.create<String>()


}
