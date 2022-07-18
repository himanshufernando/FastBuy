package project.superuniqueit.fastbuy.data.model.product

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tag(
    var id: Int,
    var name: String,
    var slug: String
): Parcelable {
    constructor() : this(0, "",
        ""
    )
}