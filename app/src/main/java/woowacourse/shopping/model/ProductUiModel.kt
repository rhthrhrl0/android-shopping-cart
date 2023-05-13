package woowacourse.shopping.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.feature.main.product.MainProductItemModel
import java.text.DecimalFormat

@Parcelize
data class ProductUiModel(
    val id: Long,
    val name: String,
    val imgUrl: String,
    val price: Int
) : Parcelable {
    fun toPriceFormat(): String {
        return DecimalFormat("#,###").format(price)
    }

    fun toItemModel(onClick: (productId: Long) -> Unit): MainProductItemModel {
        return MainProductItemModel(this, onClick)
    }
}
