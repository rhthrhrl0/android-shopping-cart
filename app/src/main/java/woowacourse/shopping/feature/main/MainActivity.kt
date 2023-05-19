package woowacourse.shopping.feature.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.local.CartRepositoryImpl
import woowacourse.shopping.data.repository.local.ProductMockRepositoryImpl
import woowacourse.shopping.data.repository.local.RecentProductRepositoryImpl
import woowacourse.shopping.data.repository.remote.MockRemoteProductRepositoryImpl
import woowacourse.shopping.data.service.MockProductRemoteService
import woowacourse.shopping.data.sql.cart.CartDao
import woowacourse.shopping.data.sql.recent.RecentDao
import woowacourse.shopping.databinding.ActivityMainBinding
import woowacourse.shopping.feature.cart.CartActivity
import woowacourse.shopping.feature.detail.DetailActivity
import woowacourse.shopping.feature.main.load.LoadAdapter
import woowacourse.shopping.feature.main.product.MainProductAdapter
import woowacourse.shopping.feature.main.product.ProductClickListener
import woowacourse.shopping.feature.main.recent.RecentAdapter
import woowacourse.shopping.feature.main.recent.RecentProductClickListener
import woowacourse.shopping.feature.main.recent.RecentWrapperAdapter
import woowacourse.shopping.model.ProductUiModel
import woowacourse.shopping.model.RecentProductUiModel

class MainActivity : AppCompatActivity(), MainContract.View {
    lateinit var binding: ActivityMainBinding
    private lateinit var presenter: MainContract.Presenter
    private lateinit var mainProductAdapter: MainProductAdapter
    private lateinit var recentAdapter: RecentAdapter
    private lateinit var recentWrapperAdapter: RecentWrapperAdapter
    private lateinit var loadAdapter: LoadAdapter

    private var cartCountBadge: TextView? = null

    private val concatAdapter: ConcatAdapter by lazy {
        val config = ConcatAdapter.Config.Builder().apply {
            setIsolateViewTypes(false)
        }.build()
        ConcatAdapter(config, recentWrapperAdapter, mainProductAdapter, loadAdapter)
    }

    private val recentProductClickListener: RecentProductClickListener =
        object : RecentProductClickListener {
            override fun onClick(productId: Long) {
                presenter.showRecentProductDetail(productId)
            }
        }

    private val productClickListener: ProductClickListener = object : ProductClickListener {
        override fun onClick(productId: Long) {
            presenter.showProductDetail(productId)
        }

        override fun onCartCountChanged(productId: Long, count: Int) {
            presenter.changeProductCartCount(productId, count)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initAdapters()
        initLayoutManager()
        binding.productRecyclerView.adapter = concatAdapter

        initPresenter()
    }

    private fun initPresenter() {
        presenter = MainPresenter(
            this,
            MockRemoteProductRepositoryImpl(MockProductRemoteService()),
            CartRepositoryImpl(CartDao(this)),
            RecentProductRepositoryImpl(RecentDao(this))
        )
        presenter.loadProducts()
        presenter.loadRecent()
    }

    override fun onRestart() {
        super.onRestart()
        presenter.loadProducts()
    }

    private fun initAdapters() {
        mainProductAdapter = MainProductAdapter(productClickListener)
        recentAdapter = RecentAdapter(recentProductClickListener)
        recentWrapperAdapter = RecentWrapperAdapter(recentAdapter)
        loadAdapter = LoadAdapter { presenter.loadMoreProduct() }
    }

    private fun initLayoutManager() {
        val layoutManager = GridLayoutManager(this, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (concatAdapter.getItemViewType(position)) {
                    LoadAdapter.VIEW_TYPE, RecentWrapperAdapter.VIEW_TYPE -> 2
                    MainProductAdapter.VIEW_TYPE -> 1
                    else -> 2
                }
            }
        }
        binding.productRecyclerView.layoutManager = layoutManager
    }

    override fun showCartScreen() {
        startActivity(CartActivity.getIntent(this))
    }

    override fun showProductDetailScreen(
        productUiModel: ProductUiModel,
        recentProductUiModel: RecentProductUiModel?
    ) {
        startActivity(DetailActivity.getIntent(this, productUiModel, recentProductUiModel))
    }

    override fun setProducts(products: List<ProductUiModel>) {
        runOnUiThread { mainProductAdapter.setItems(products) }
    }

    override fun updateRecent(recent: List<RecentProductUiModel>) {
        runOnUiThread { recentAdapter.setItems(recent) }
    }

    override fun showCartCountBadge() {
        runOnUiThread { cartCountBadge?.visibility = View.VISIBLE }
    }

    override fun hideCartCountBadge() {
        runOnUiThread { cartCountBadge?.visibility = View.GONE }
    }

    override fun updateCartCount(count: Int) {
        runOnUiThread { cartCountBadge?.text = "$count" }
    }

    override fun hideLoadMore() {
        runOnUiThread {
            Toast.makeText(this, getString(R.string.load_more_end), Toast.LENGTH_SHORT).show()
            loadAdapter.hide()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)

        cartCountBadge =
            menu.findItem(R.id.cart_count_badge).actionView?.findViewById(R.id.badge)

        presenter.loadCartCountSize()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.cart_action -> {
                presenter.moveToCart()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        recentWrapperAdapter.onSaveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        recentWrapperAdapter.onRestoreState(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            presenter.resetProducts()
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
