package project.superuniqueit.fastbuy.data.model.orderoutput

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Shipping(
    @ColumnInfo(name = "shipping_address_1")
    var address_1: String,
    @ColumnInfo(name = "shipping_address_2")
    var address_2: String,
    @ColumnInfo(name = "shipping_city")
    var city: String,
    @ColumnInfo(name = "shipping_company")
    var company: String,
    @ColumnInfo(name = "shipping_country")
    var country: String,
    @ColumnInfo(name = "shipping_first_name")
    var first_name: String,
    @ColumnInfo(name = "shipping_last_name")
    var last_name: String,
    @ColumnInfo(name = "shipping_phone")
    var phone: String,
    @ColumnInfo(name = "shipping_postcode")
    var postcode: String,
    @ColumnInfo(name = "shipping_state")
    var state: String
): Parcelable {
    constructor() : this("","","","","","","","","",""
    )

}