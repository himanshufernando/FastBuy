package project.ceyloninnovationlabs.fastbuy.data.model.coupon

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp


@Parcelize
data class Coupon(
    var amount: String,
    var code: String,
    var date_created: String,
    var date_created_gmt: String,
    var date_expires: String,
    var date_expires_gmt: String,
    var date_modified: String,
    var date_modified_gmt: String,
    var description: String,
    var discount_type: String,
    var exclude_sale_items: Boolean,
    var excluded_product_categories: List<Int>,
    var excluded_product_ids: List<Int>,
    var free_shipping: Boolean,
    var id: Int,
    var individual_use: Boolean,
    var maximum_amount: String,
    var minimum_amount: String,
    var product_categories: List<Int>,
    var product_ids: List<Int>,
    var usage_count: Int
    
): Parcelable {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        false,
        emptyList(),
        emptyList(),
        false,
        0,
        false,
        "",
        "",
        emptyList(),
        emptyList(),
        0
    )
}