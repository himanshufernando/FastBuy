package project.ceyloninnovationlabs.fastbuy.data.model.past

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LineItem(
    val id: Int,
    val name: String,
    val price: Int,
    val product_id: Int,
    val quantity: Int,
    val sku: String,
    val subtotal: String,
    val total: String,
    val total_tax: String
): Parcelable {
    constructor() : this(0,"",0,0,0,"","","",""
    )

}