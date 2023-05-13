package woowacourse.shopping.feature.main.recent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemRecentProductBinding

class RecentAdapter(
    items: List<RecentProductItemModel>
) : RecyclerView.Adapter<RecentViewHolder>() {

    private val _items = items.toMutableList()
    val items: List<RecentProductItemModel>
        get() = _items.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        return RecentViewHolder.create(parent)
    }

    override fun getItemCount(): Int = _items.size

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.bind(_items[position])
    }

    fun setItems(items: List<RecentProductItemModel>) {
        _items.clear()
        _items.addAll(items)
        notifyDataSetChanged()
    }
}
