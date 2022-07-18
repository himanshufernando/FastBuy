package project.superuniqueit.fastbuy.data.model.orderoutput

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Billing(
    @ColumnInfo(name = "billing_address_1")
    var address_1: String,
    @ColumnInfo(name = "billing_address_2")
    var address_2: String,
    @ColumnInfo(name = "billing_city")
    var city: String,
    @ColumnInfo(name = "billing_company")
    var company: String,
    @ColumnInfo(name = "billing_country")
    var country: String,
    @ColumnInfo(name = "billing_email")
    var email: String,
    @ColumnInfo(name = "billing_first_name")
    var first_name: String,
    @ColumnInfo(name = "billing_last_name")
    var last_name: String,
    @ColumnInfo(name = "billing_phone")
    var phone: String,
    @ColumnInfo(name = "billing_postcode")
    var postcode: String,
    @ColumnInfo(name = "billing_state")
    var state: String
): Parcelable {
    constructor() : this(
        "", "", "", "", "", "", "", "", "", "", ""
    )
}