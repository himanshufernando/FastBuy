package project.superuniqueit.fastbuy.data.model.user

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class MetaData(
    val id: Int,
    val key: String,
    var value: @RawValue Any
): Parcelable {
    constructor() : this(0,
    "", Any()
    )
}