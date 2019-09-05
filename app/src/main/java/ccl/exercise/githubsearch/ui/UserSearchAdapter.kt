package ccl.exercise.githubsearch.ui

import android.view.ViewGroup
import ccl.exercise.githubsearch.BaseRecyclerViewAdapter
import ccl.exercise.githubsearch.model.User

class UserSearchAdapter : BaseRecyclerViewAdapter<User, UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder = UserViewHolder(parent)

    fun updateUsers(gitHubUsers: List<User>?) {
        val users = gitHubUsers ?: return
        users.let {
            if (it.isEmpty()) {
                clearAll()
            } else {
                setItems(users)
            }
        }
    }
}