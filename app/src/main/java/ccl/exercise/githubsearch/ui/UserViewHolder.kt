package ccl.exercise.githubsearch.ui

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ccl.exercise.githubsearch.ui.base.BaseViewHolder
import ccl.exercise.githubsearch.R
import ccl.exercise.githubsearch.model.Item
import com.bumptech.glide.Glide

class UserViewHolder(parent: ViewGroup) : BaseViewHolder<Item.UserItem>(parent, R.layout.view_holder_user) {

    private val userName: TextView = itemView.findViewById(R.id.userName)
    private val avatar: ImageView = itemView.findViewById(R.id.avatar)

    override fun onBind(item: Item.UserItem) {
        val user = item.user
        userName.text = user.userName
        Glide.with(itemView.context).apply {
            clear(avatar)
            if (user.avatarUrl?.isNotEmpty() == true) {
                load(user.avatarUrl)
                    .placeholder(R.drawable.github)
                    .circleCrop()
                    .into(avatar)
            }
        }

    }
}