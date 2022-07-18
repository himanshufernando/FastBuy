package project.superuniqueit.fastbuy.data.model.past

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import project.superuniqueit.fastbuy.data.model.coupon.Coupon
import project.superuniqueit.fastbuy.data.model.orderoutput.Billing
import project.superuniqueit.fastbuy.data.model.orderoutput.Shipping
import project.superuniqueit.fastbuy.data.model.product.Product


@Parcelize
data class Orders(

    var billing: Billing,
    val customer_id: Int,
    val customer_note: String,
    val date_completed: String,
    val date_created: String,
    val date_modified: String,
    val date_paid: String,
    val discount_total: String,
    val id: Int,
    val line_items: ArrayList<LineItem>,
    val number: String,
    val payment_method: String,
    val payment_method_title: String,
    var shipping: Shipping,
    val shipping_lines: ArrayList<ShippingLine>,
    val shipping_total: String,
    val status: String,
    var total: String,
    val transaction_id: String,
    var errorStatus: Boolean,
    var errorMessage: String,
    var isAgreedToTerms : Boolean,
    var paymentType: String,
    var product : ArrayList<Product>,
    var coupon_lines: ArrayList<Coupon>,
    var shippingType : String,
    var shippingCost: Double,
    var subtotal : Double
): Parcelable {
    constructor() : this(Billing(),0,"","","","","","",
        0, ArrayList<LineItem>(),"","","",Shipping(), ArrayList<ShippingLine>(),"","","","",false,
        "",false,"",ArrayList<Product>(),ArrayList<Coupon>(),"",0.0,0.0
    )
}
