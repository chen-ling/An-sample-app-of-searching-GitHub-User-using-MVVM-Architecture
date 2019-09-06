package ccl.exercise.githubsearch.ui

import android.view.ViewGroup
import ccl.exercise.githubsearch.BaseViewHolder
import ccl.exercise.githubsearch.R
import ccl.exercise.githubsearch.model.Item

class LoadingViewHolder(parent: ViewGroup) : BaseViewHolder<Item.LoadingItem>(parent, R.layout.view_holder_loading) {
    override fun onBind(item: Item.LoadingItem) {
    }
}