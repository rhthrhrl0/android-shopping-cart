package woowacourse.shopping.feature.main

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.shopping.model.ProductUiModel

@Parcelize
data class MainProductItemModel(
    val product: ProductUiModel,
    val onClick: (position: Int) -> Unit
) : Parcelable