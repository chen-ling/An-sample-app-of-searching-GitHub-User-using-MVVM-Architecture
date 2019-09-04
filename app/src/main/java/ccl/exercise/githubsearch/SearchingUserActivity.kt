package ccl.exercise.githubsearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ccl.exercise.githubsearch.extension.afterTextChanged
import ccl.exercise.githubsearch.ui.UserSearchAdapter
import ccl.exercise.githubsearch.ui.UserViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class SearchingUserActivity : AppCompatActivity() {

    val viewModel: UserViewModel by viewModel()
    val searchSubject = PublishSubject.create<String>()
    private lateinit var userSearchAdapter: UserSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        registerSearchSubject()
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

    fun registerSearchSubject() {
        searchSubject
            .debounce(300, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                viewModel.search(it)
            }
    }


}
