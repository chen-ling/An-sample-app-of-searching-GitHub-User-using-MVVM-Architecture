package ccl.exercise.githubsearch.ui

import android.view.ViewGroup
import ccl.exercise.githubsearch.ui.base.BaseRecyclerViewAdapter
import ccl.exercise.githubsearch.ui.base.BaseViewHolder

class UserSearchAdapter : BaseRecyclerViewAdapter<Item>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Item> {
        val holder = when (Item.Type.values()[viewType]) {
            Item.Type.User -> UserViewHolder(parent)
            Item.Type.LOADING -> LoadingViewHolder(parent)
        }
        return holder as BaseViewHolder<Item>
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
    }

    fun updateUsers(gitHubUsers: List<Item>?) {
        val users = gitHubUsers ?: return
        users.let {
            if (it.isEmpty()) {
                clearAll()
            } else {
                if (items.isEmpty()) {
                    setItems(it)
                } else {
                    appendItems(it.subList(items.size, it.size))
                }
            }
        }
    }

    fun appendLoadingView() {
        items.add(Item.LoadingItem())
        notifyItemInserted(itemCount - 1)
    }

    fun removeLoadingView() {
        if (items.isEmpty()) return

        val lastItem = items.last()
        if (lastItem.type == Item.Type.LOADING) {
            items.remove(lastItem)
            notifyDataSetChanged()
        }
    }
}
