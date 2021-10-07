package project.ceyloninnovationlabs.fastbuy.data.model.coupon

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CouponBase (
    var error: Boolean,
    var message: String,
    var data: ArrayList<Coupon>,
    var code:Int
): Parcelable {
    constructor() : this(false,"",ArrayList<Coupon>(),0)
}