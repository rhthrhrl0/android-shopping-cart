package woowacourse.shopping.feature.main

import com.example.domain.ProductCache
import com.example.domain.model.RecentProduct
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
import woowacourse.shopping.mapper.toPresentation
import java.time.LocalDateTime

class MainPresenter(
    private val view: MainContract.View,
    private val productRepository: ProductRepository,
    private val recentProductRepository: RecentProductRepository
) : MainContract.Presenter {

    override fun loadProducts() {
        val firstProducts = productRepository.getFirstProducts()
        val productItems = firstProducts.map { product ->
            product.toPresentation().toItemModel { position ->
                addRecentProduct(RecentProduct(product, LocalDateTime.now()))
                view.showProductDetailScreenByProduct(position)
                loadRecent()
            }
        }
        view.addProducts(productItems)
        ProductCache.addProducts(firstProducts)
    }

    override fun loadProductsFromCache() {
        val cacheProducts = ProductCache.productList
        val cacheItems = cacheProducts.map { product ->
            product.toPresentation().toItemModel { position ->
                addRecentProduct(RecentProduct(product, LocalDateTime.now()))
                view.showProductDetailScreenByProduct(position)
                loadRecent()
            }
        }
        view.addProducts(cacheItems)
    }

    override fun moveToCart() {
        view.showCartScreen()
    }

    override fun loadMoreProduct(lastProductId: Long) {
        val nextProducts = productRepository.getNextProducts(lastProductId)
        val nextProductItems = nextProducts.map { product ->
            product.toPresentation().toItemModel { position ->
                addRecentProduct(RecentProduct(product, LocalDateTime.now()))
                view.showProductDetailScreenByProduct(position)
                loadRecent()
            }
        }
        view.addProducts(nextProductItems)
        ProductCache.addProducts(nextProducts)
    }

    override fun loadRecent() {
        val recent = recentProductRepository.getAll().map {
            it.toPresentation().toItemModel { position ->
                addRecentProduct(it)
                view.showProductDetailScreenByRecent(position)
                loadRecent()
            }
        }
        view.updateRecent(recent)
    }

    private fun addRecentProduct(recentProduct: RecentProduct) {
        recentProductRepository.addRecentProduct(recentProduct.copy(dateTime = LocalDateTime.now()))
    }
}
