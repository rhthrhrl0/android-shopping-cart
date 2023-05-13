package woowacourse.shopping.feature.main

import woowacourse.shopping.feature.main.product.MainProductItemModel
import woowacourse.shopping.feature.main.recent.RecentProductItemModel
import woowacourse.shopping.model.ProductUiModel

interface MainContract {
    interface View {
        fun showCartScreen()
        fun showProductDetailScreenByProduct(productUiModel: ProductUiModel)
        fun addProducts(products: List<MainProductItemModel>)
        fun updateRecent(recent: List<RecentProductItemModel>)
        fun showProductDetailScreenByRecent(position: Int)
    }

    interface Presenter {
        fun loadProducts()
        fun moveToCart()
        fun loadMoreProduct()
        fun loadRecent()
        fun resetProducts()
    }
}
