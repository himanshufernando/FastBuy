package project.ceyloninnovationlabs.fastbuy.data.model.orderoutput

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Shipping(
    var address_1: String,
    var address_2: String,
    var city: String,
    var company: String,
    var country: String,
    var first_name: String,
    var last_name: String,
    var phone: String,
    var postcode: String,
    var state: String
): Parcelable {
    constructor() : this("","","","","","","","","",""
    )

}