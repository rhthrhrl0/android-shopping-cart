package woowacourse.shopping.feature.cart

import com.example.domain.model.CartProducts
import com.example.domain.model.PageNation
import com.example.domain.repository.CartRepository
import woowacourse.shopping.mapper.toDomain
import woowacourse.shopping.mapper.toPresentation
import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.model.PageNationUiModel

class CartPresenter(
    val view: CartContract.View,
    private val cartRepository: CartRepository,
) : CartContract.Presenter {
    override lateinit var page: PageNationUiModel
        private set

    override fun loadInitCartProduct() {
        page = PageNation(CartProducts(cartRepository.getAll()), 1).toPresentation()
        val initCartProductUiModels = page.currentPageCartProducts

        changePageState(initCartProductUiModels)
    }

    override fun loadPreviousPage() {
        if (page.hasPreviousPage.not()) return
        page = page.toDomain().previousPage().toPresentation()

        changePageState(page.currentPageCartProducts)
    }

    override fun loadNextPage() {
        if (page.hasNextPage.not()) return
        page = page.toDomain().nextPage().toPresentation()

        changePageState(page.currentPageCartProducts)
    }

    override fun handleDeleteCartProductClick(cartId: Long) {
        val cartProduct = page.currentPageCartProducts.find { it.cartId == cartId } ?: return
        cartRepository.deleteProduct(cartProduct.toDomain())
        page = page.toDomain().remove(cartId).toPresentation()
        changePageState(page.currentPageCartProducts)
    }

    override fun handleCartProductCartCountChange(cartId: Long, count: Int) {
        val findCartProduct =
            page.currentPageCartProducts.find { it.cartId == cartId } ?: return

        page = page.toDomain().changeCountState(cartId, count).toPresentation()

        cartRepository.changeCartProductCount(findCartProduct.productUiModel.toDomain(), count)

        changePageState(page.currentPageCartProducts)
    }

    override fun handlePurchaseSelectedCheckedChange(cartId: Long, checked: Boolean) {
        val findCartProduct =
            page.currentPageCartProducts.find { it.cartId == cartId } ?: return

        page = page.toDomain().changeChecked(findCartProduct.cartId, checked).toPresentation()

        cartRepository.changeCartProductCheckedState(
            findCartProduct.productUiModel.toDomain(),
            checked
        )

        changePageState(page.currentPageCartProducts)
    }

    override fun handleAllSelectedCheckedChange(checked: Boolean) {
        page = page.toDomain().setAllChecked(checked).toPresentation()
        cartRepository.changeAllCheckedState(checked)
        changePageState(page.currentPageCartProducts)
    }

    override fun processOrderClick() {
        if (page.isAnyChecked.not()) return
        cartRepository.deleteAllCheckedCartProduct()
        page = page.toDomain().removeAllChecked().toPresentation()
        changePageState(page.currentPageCartProducts)
    }

    override fun setPage(restorePage: Int) {
        page = PageNation(CartProducts(cartRepository.getAll()), restorePage).toPresentation()
        changePageState(page.currentPageCartProducts)
    }

    override fun exit() {
        view.exitCartScreen()
    }

    private fun changePageState(cartProductUiModels: List<CartProductUiModel>) {
        view.updateCartProducts(cartProductUiModels)
        view.setPreviousButtonState(page.hasPreviousPage)
        view.setNextButtonState(page.hasNextPage)
        view.setPageCount(page.currentPage)
    }
}
