package ccl.exercise.githubsearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(parent: ViewGroup, @LayoutRes layoutId: Int) :
    RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false)) {

    abstract fun onBind(item: T)
}
