package ccl.exercise.githubsearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ccl.exercise.githubsearch.extension.afterTextChanged
import ccl.exercise.githubsearch.ui.ItemSpacingDecoration
import ccl.exercise.githubsearch.ui.UserSearchAdapter
import ccl.exercise.githubsearch.ui.UserViewModel
import ccl.exercise.githubsearch.utils.getStr
import ccl.exercise.githubsearch.utils.toast
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchingUserActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModel()
    private lateinit var userSearchAdapter: UserSearchAdapter

    companion object {
        private const val LOAD_MORE_THRESHOLD = 15
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        registerLiveData()
    }

    private fun setupView() {
        search.afterTextChanged {
            if (it.isNotEmpty()) {
                viewModel.search(it)
            }
        }
        userSearchAdapter = UserSearchAdapter()
        recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(this@SearchingUserActivity)
            val scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (viewModel.isLoading.value == true) {
                        return
                    }
                    val itemNotViewed =
                        linearLayoutManager.itemCount - linearLayoutManager.findLastVisibleItemPosition()
                    if (itemNotViewed <= LOAD_MORE_THRESHOLD) {
                        viewModel.loadMore()
                    }
                }
            }
            adapter = userSearchAdapter
            layoutManager = linearLayoutManager
            addItemDecoration(ItemSpacingDecoration(R.dimen.large, R.dimen.medium))
            addOnScrollListener(scrollListener)
        }
    }

    private fun registerLiveData() {
        viewModel.apply {
            loadingError.observe(this@SearchingUserActivity, Observer { error ->
                error ?: return@Observer
                toast("${error.message} ${getStr(R.string.something_went_wrong)}")
                loadingError.value = null
            })
            userList.observe(this@SearchingUserActivity, Observer { githubUsers ->
                run {
                    userSearchAdapter.updateUsers(githubUsers)
                    toast(str = "notify user updates size: ${githubUsers.size}")//TODO remove this line
                }
            })
            isLoading.observe(this@SearchingUserActivity, Observer {
                if (isLoading.value == true) {
                    userSearchAdapter.appendLoadingView()
                } else {
                    userSearchAdapter.removeLoadingView()
                }
            })
        }
    }
}
