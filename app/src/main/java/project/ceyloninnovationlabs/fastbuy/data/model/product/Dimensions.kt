package project.ceyloninnovationlabs.fastbuy.data.model.product

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Dimensions(
    var height: String,
    var length: String,
    var width: String
) : Parcelable {
    constructor() : this("", "",
        ""
    )
}