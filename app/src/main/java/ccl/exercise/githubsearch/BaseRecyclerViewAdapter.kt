package ccl.exercise.githubsearch

import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T> : RecyclerView.Adapter<BaseViewHolder<T>>() {
    protected val items = mutableListOf<T>()

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun getItemCount(): Int = items.size

    fun setItems(items: List<T>) {
        if (items.isNotEmpty()) {
            appendItems(items)
        } else {
            this.items.clear()
            this.items.addAll(items)
            notifyDataSetChanged()
        }
    }

    private fun appendItems(items: List<T>) {
        val startPosition = this.items.size
        this.items.addAll(items)
        notifyItemInserted(startPosition)
    }

    fun getItem(position: Int): T {
        return items[position]
    }

    fun clearAll() {
        items.clear()
        notifyDataSetChanged()
    }
}