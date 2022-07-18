package project.superuniqueit.fastbuy.data.model.orderoutput

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MetaDataX(
    val display_key: String,
    val display_value: String,
    val id: Int,
    val key: String,
    val value: String
): Parcelable {
    constructor() : this("","",0,"",""
    )

}