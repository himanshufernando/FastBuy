package project.ceyloninnovationlabs.fastbuy.data.model.orderoutput

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import project.ceyloninnovationlabs.fastbuy.data.model.coupon.Coupon
import project.ceyloninnovationlabs.fastbuy.data.model.coupon.CouponBase
import project.ceyloninnovationlabs.fastbuy.data.model.product.Product


@Parcelize
data class PastOrder(

    var billing: Billing,
    val created_via: String,
    val currency: String,
    val currency_symbol: String,
    val customer_id: Int,
    val customer_note: String,
    val date_completed: String,
    val date_created: String,
    val date_modified: String,
    val date_paid: String,
    val discount_total: String,
    var id: Int,
    val line_items: ArrayList<LineItem>,
    val number: String,
    val order_key: String,
    var payment_method: String,
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
    var coupon_lines: ArrayList<CouponBase>,
    var shippingType : String,
    var coupon : Coupon,
    var shippingCost: Double,
    var subtotal : Double,
    var paymentGatewayValue : Double,
    var cashOnDeliveryValue : Double,
    var finaltotal : Double
): Parcelable {
    constructor() : this(Billing(),"","","",0,"","","","","","",
        0, ArrayList<LineItem>(),"","","","", Shipping(), ArrayList<ShippingLine>(),"","","","",false,
        "",false,"",ArrayList<Product>(),ArrayList<CouponBase>(),"",Coupon(),
        0.0,0.0,0.0,0.0,0.0
    )
}
