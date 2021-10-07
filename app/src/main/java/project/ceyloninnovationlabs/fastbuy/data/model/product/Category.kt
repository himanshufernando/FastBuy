package project.ceyloninnovationlabs.fastbuy.data.model.product

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize


@Parcelize
data class Category(
    var id: Int,
    var name: String,
    var slug: String
) : Parcelable {
    constructor() : this(0, "",
        ""
    )
}