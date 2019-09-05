package ccl.exercise.githubsearch.ui

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ccl.exercise.githubsearch.BaseViewHolder
import ccl.exercise.githubsearch.R
import ccl.exercise.githubsearch.model.User
import com.bumptech.glide.Glide

class UserViewHolder(parent: ViewGroup) : BaseViewHolder<User>(parent, R.layout.view_holder_user) {

    private val userName: TextView = itemView.findViewById(R.id.userName)
    private val avatar: ImageView = itemView.findViewById(R.id.avatar)

    override fun onBind(item: User) {
        userName.text = item.userName
        Glide.with(itemView.context).apply {
            clear(avatar)
            if (item.avatarUrl?.isNotEmpty() == true) {
                load(item.avatarUrl)
                    .placeholder(R.drawable.github)
                    .circleCrop()
                    .into(avatar)
            }
        }

    }
}