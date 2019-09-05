package ccl.exercise.githubsearch

import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T, R : BaseViewHolder<T>> : RecyclerView.Adapter<R>() {
    private val items = mutableListOf<T>()

    override fun onBindViewHolder(holder: R, position: Int) {
        holder.onBind(items[position])
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

    fun clearAll() {
        items.clear()
        notifyDataSetChanged()
    }
}