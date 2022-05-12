package project.ceyloninnovationlabs.fastbuy.data.model.user

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize
import project.ceyloninnovationlabs.fastbuy.data.model.orderoutput.Billing
import project.ceyloninnovationlabs.fastbuy.data.model.orderoutput.Shipping

@Entity(tableName = "user_table")
@Parcelize
data class User(
    @ColumnInfo(name = "user_email")
    var email: String,
    @ColumnInfo(name = "user_facebook_id")
    var facebook_id: String,
    @ColumnInfo(name = "user_facebook_token")
    var firebase_token: String,
    @ColumnInfo(name = "user_first_name")
    var first_name: String,
    @ColumnInfo(name = "user_google_id")
    var google_id: String,
    @PrimaryKey
    @ColumnInfo(name = "user_id",defaultValue = "0")
    var id: Int,
    @ColumnInfo(name = "user_last_name")
    var last_name: String,
    @ColumnInfo(name = "user_mobile_Number")
    var mobileNumber: String,
    @ColumnInfo(name = "user_picture")
    var picture: String,
    @ColumnInfo(name = "user_referral_id")
    var referral_id: String,
    @ColumnInfo(name = "user_username")
    var username: String,
    @Embedded var billing: Billing,
    @Embedded var shipping: Shipping,
    @ColumnInfo(name = "user_role")
    var role: String,
    @ColumnInfo(name = "user_code")
    var code: String,
    @ColumnInfo(name = "user_message")
    var message: String,
    @Ignore var meta_data: ArrayList<MetaData>,
    @ColumnInfo(name = "user_status")
    var status: Boolean,
    var password : String

    ): Parcelable {
    constructor() : this("",
        "","",
        "", "",0,"","",
        "","","",Billing(),Shipping(),"","","",ArrayList<MetaData>(),false,""
    )
}