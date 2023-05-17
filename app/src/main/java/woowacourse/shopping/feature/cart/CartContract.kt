package woowacourse.shopping.feature.cart

import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.PageNationUiModel

interface CartContract {
    interface View {
        fun updateCartProducts(newItems: List<CartProductUiModel>)
        fun setPreviousButtonState(enabled: Boolean)
        fun setNextButtonState(enabled: Boolean)
        fun setPageCount(count: Int)
        fun setOrderButtonState(enabled: Boolean, orderCount: Int)
        fun updateMoney(money: Int)
        fun exitCartScreen()
    }

    interface Presenter {
        val page: PageNationUiModel
        fun loadInitCartProduct()
        fun handleDeleteCartProductClick(cartId: Long)
        fun handleCartProductCartCountChange(cartId: Long, count: Int)
        fun handlePurchaseSelectedCheckedChange(cartId: Long, checked: Boolean)
        fun handleAllSelectedCheckedChange(checked: Boolean)
        fun processOrderClick()
        fun loadPreviousPage()
        fun loadNextPage()
        fun setPage(restorePage: Int)
        fun exit()
    }
}
