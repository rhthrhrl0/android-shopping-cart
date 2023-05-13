package woowacourse.shopping.feature.main

import com.example.domain.cache.ProductLocalCache
import com.example.domain.model.Price
import com.example.domain.model.Product
import com.example.domain.model.RecentProduct
import com.example.domain.repository.ProductRepository
import com.example.domain.repository.RecentProductRepository
import io.mockk.*
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.feature.main.product.MainProductItemModel
import woowacourse.shopping.feature.main.recent.RecentProductItemModel
import woowacourse.shopping.mapper.toDomain
import java.time.LocalDateTime

internal class MainPresenterTest {
    private lateinit var view: MainContract.View
    private lateinit var presenter: MainContract.Presenter
    private lateinit var productRepository: ProductRepository
    private lateinit var recentProductRepository: RecentProductRepository

    @Before
    fun init() {
        view = mockk()
        productRepository = mockk()
        recentProductRepository = mockk()
        presenter = MainPresenter(view, productRepository, recentProductRepository)
        ProductLocalCache.clear()
    }

    @Test
    fun `처음에 상품 목록을 제대로 불러와서 상품을 화면에 띄운다`() {
        every { productRepository.getFirstProducts() } returns mockProducts
        val slot = slot<List<MainProductItemModel>>()
        every { view.addProducts(capture(slot)) } just Runs

        presenter.loadProducts()

        val actual = slot.captured.map { it.product.toDomain() }
        val expected = mockProducts.toList()
        assert(actual == expected)
        verify { view.addProducts(any()) }
        assert(ProductLocalCache.productList == expected)
    }

    @Test
    fun `캐쉬로부터 복구받은 상품록을 화면에 띄운다`() {
        ProductLocalCache.addProducts(mockProducts)
        val slot = slot<List<MainProductItemModel>>()
        every { view.addProducts(capture(slot)) } just Runs

        presenter.loadProductsFromCache()

        val actual = slot.captured.map { it.product.toDomain() }
        val expected = mockProducts.toList()

        assert(actual == expected)
        verify { view.addProducts(any()) }
    }

    @Test
    fun `장바구니 화면으로 이동한다`() {
        every { view.showCartScreen() } just Runs

        presenter.moveToCart()

        verify { view.showCartScreen() }
    }

    @Test
    fun `상품 목록을 이어서 더 불러와서 화면에 추가로 띄운다`() {
        val lastProductId = 10L
        every { productRepository.getNextProducts(lastProductId) } returns mockProducts.subList(
            10,
            15
        )
        val slot = slot<List<MainProductItemModel>>()
        every { view.addProducts(capture(slot)) } just Runs

        presenter.loadMoreProduct(lastProductId)

        val actual = slot.captured.map { it.product.toDomain() }
        val expected = mockProducts.subList(10, 15)

        assert(actual == expected)
        verify { view.addProducts(any()) }
        assert(ProductLocalCache.productList == expected)
    }

    @Test
    fun `최근 본 상품 목록을 가져와서 화면에 띄운다`() {
        every { recentProductRepository.getAll() } returns mockRecentProducts
        val slot = slot<List<RecentProductItemModel>>()
        every { view.updateRecent(capture(slot)) } just Runs

        presenter.loadRecent()

        val actual = slot.captured.map { it.recentProduct.productUiModel.toDomain() }
        val expected = mockRecentProducts.map { it.product }
        assert(actual == expected)
        verify { view.updateRecent(any()) }
    }

    private val mockProducts = listOf<Product>(
        Product(
            1,
            "쿨피스 프리미엄 복숭아맛",
            "https://product-image.kurly.com/product/image/0a8fe9ec-2ee0-495e-a6fc-b25de98e2d09.jpg",
            Price(2000)
        ),
        Product(
            2,
            "지수 머스크메론 2종",
            "https://product-image.kurly.com/product/image/91e97eee-1d8a-4194-84de-19f6a90e69a2.jpg",
            Price(13000)
        ),
        Product(
            3,
            "국산 블루베리 200g",
            "https://product-image.kurly.com/product/image/ee17bd9e-1561-46af-a7bb-d9731361a243.jpg",
            Price(9000)
        ),
        Product(
            4,
            "DOLE 실속 바나나 1kg",
            "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/165303902534l0.jpg",
            Price(4000)
        ),
        Product(
            5,
            "유명산지 고당도사과 1.5kg",
            "https://product-image.kurly.com/cdn-cgi/image/quality=85,width=676/product/image/b573ba85-9bfa-433b-bafc-3356b081440b.jpg",
            Price(13000)
        ),
        Product(
            6,
            "성주 참외 1.5kg(4~7입)",
            "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/1653038449592l0.jpeg",
            Price(12900)
        ),
        Product(
            7,
            "감자 1kg",
            "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/1637154387515l0.jpg",
            Price(6500)
        ),
        Product(
            8,
            "다다기오이 3입",
            "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/1656895312504l0.jpg",
            Price(4000)
        ),
        Product(
            9,
            "호박고구마 800g",
            "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/1637154415267l0.jpg",
            Price(16900)
        ),
        Product(
            10,
            "한통 양배추",
            "https://img-cf.kurly.com/cdn-cgi/image/quality=85,width=676/shop/data/goods/1653038077839l0.jpeg",
            Price(4200)
        ),
        Product(
            11,
            "워셔블 이중쿠션 푹신한 체크 거실 3종",
            "https://product-image.kurly.com/product/image/e05bcba7-577a-4ca2-978e-9cc048251428.jpg",
            Price(10900)
        ),
        Product(
            12,
            "양면 발각질제거",
            "https://img-cf.kurly.com/shop/data/goods/1654168928940l0.jpg",
            Price(10900)
        ),
        Product(
            13,
            "릴리 베게커버 2종",
            "https://product-image.kurly.com/product/image/a2ec521c-ee47-428e-bc6b-8ea1de973558.jpg",
            Price(32000)
        ),
        Product(
            14,
            "크루아르 미니건조기",
            "https://3p-image.kurly.com/product-image/ae3651a9-6b2f-42ca-b7a2-2bccaca5cd67/2672da62-f670-4ce7-9979-cb1222222b03.jpg",
            Price(219000)
        ),
        Product(
            15,
            "맥주효모 샴푸 300g",
            "https://img-cf.kurly.com/shop/data/goods/1648539965147l0.jpg",
            Price(14900)
        ),
        Product(
            16,
            "린스 473ml",
            "https://product-image.kurly.com/product/image/548169b8-fcb5-4f67-921e-42094815ebe1.jpg",
            Price(10500)
        )
    )

    private val mockRecentProducts = List(16) {
        RecentProduct(
            mockProducts[it],
            LocalDateTime.now().plusMinutes(it.toLong())
        )
    }
}
