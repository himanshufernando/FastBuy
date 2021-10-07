package project.ceyloninnovationlabs.fastbuy.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import project.ceyloninnovationlabs.fastbuy.data.model.coupon.Coupon
import project.ceyloninnovationlabs.fastbuy.data.model.product.Product

@Parcelize
data class Order (
    var id : Int,
    var subtotal : Double,
    var total : Double,
    var coupon : Coupon,
    var product : ArrayList<Product>,
    var error: Boolean,
    var message: String,
    var data: String?,
    var code:Int


): Parcelable {
    constructor() : this(0, 0.0, 0.0,Coupon(),ArrayList<Product>(),false,"","",0
    )

}
