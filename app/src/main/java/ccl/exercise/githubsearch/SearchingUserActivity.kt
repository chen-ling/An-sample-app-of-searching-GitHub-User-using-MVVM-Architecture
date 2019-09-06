package ccl.exercise.githubsearch

import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ccl.exercise.githubsearch.extension.afterTextChanged
import ccl.exercise.githubsearch.ui.UserSearchAdapter
import ccl.exercise.githubsearch.ui.UserViewModel
import ccl.exercise.githubsearch.ui.base.ItemSpacingDecoration
import ccl.exercise.githubsearch.utils.getStr
import ccl.exercise.githubsearch.utils.hideKeyboard
import ccl.exercise.githubsearch.utils.toast
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchingUserActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModel()
    private lateinit var userSearchAdapter: UserSearchAdapter
    private val handler = Handler()

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
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        userSearchAdapter = UserSearchAdapter()
        recyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(this@SearchingUserActivity)
            val scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (viewModel.isLoading.value == true || viewModel.noMoreItem.value == true) {
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
            setOnTouchListener(recyclerViewTouchListener)
        }
    }

    private val recyclerViewTouchListener = View.OnTouchListener { _, _ ->
        hideKeyboard(this@SearchingUserActivity)
        false
    }

    private fun registerLiveData() {
        viewModel.apply {
            loadingError.observe(this@SearchingUserActivity, Observer { error ->
                error ?: return@Observer
                val errorMsg = error.message
                loadingError.value = null
                toast(if (errorMsg.isNullOrEmpty()) getStr(R.string.something_went_wrong) else errorMsg)
            })
            userList.observe(this@SearchingUserActivity, Observer { githubUsers ->
                run {
                    userSearchAdapter.updateUsers(githubUsers)
                    if (githubUsers.isEmpty() && isLoading.value == false) {
                        toast(R.string.no_result)
                    }
                }
            })
            isLoading.observe(this@SearchingUserActivity, Observer {
                if (isLoading.value == true) {
                    handler.post { userSearchAdapter.appendLoadingView() }
                } else {
                    userSearchAdapter.removeLoadingView()
                }
            })
        }
    }
}
