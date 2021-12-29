package project.ceyloninnovationlabs.fastbuy.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SliderImage(
    var id: String,
    var productid: Int,
    var name: String
) : Parcelable {
    constructor() : this("", 0, ""
    )

}