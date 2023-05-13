package woowacourse.shopping.feature.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ItemCartProductBinding

class CartProductAdapter(
    items: List<CartProductItemModel>
) : RecyclerView.Adapter<CartProductViewHolder>() {
    private val _items = items.toMutableList()
    val items: List<CartProductItemModel>
        get() = _items.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
        return CartProductViewHolder.create(parent)
    }

    override fun getItemCount(): Int = _items.size

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        holder.bind(_items[position])
    }

    fun setItems(newItems: List<CartProductItemModel>) {
        _items.clear()
        _items.addAll(newItems)
        notifyDataSetChanged()
    }
}
