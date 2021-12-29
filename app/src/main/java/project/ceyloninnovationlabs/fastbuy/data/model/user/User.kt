package project.ceyloninnovationlabs.fastbuy.data.model.user

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import project.ceyloninnovationlabs.fastbuy.data.model.orderoutput.Billing
import project.ceyloninnovationlabs.fastbuy.data.model.orderoutput.Shipping

@Parcelize
data class User(
    var email: String,
    var facebook_id: String,
    var firebase_token: String,
    var first_name: String,
    var google_id: String,
    var id: Int,
    var last_name: String,
    var mobileNumber: String,
    var picture: String,
    var referral_id: String,
    var username: String,
    var billing: Billing,
    var shipping: Shipping,
    var role: String,
    var code: String,
    var message: String,
    var meta_data: ArrayList<MetaData>,
    var status: Boolean

    ): Parcelable {
    constructor() : this("",
        "","",
        "", "",0,"","",
        "","","",Billing(),Shipping(),"","","",ArrayList<MetaData>(),false
    )
}