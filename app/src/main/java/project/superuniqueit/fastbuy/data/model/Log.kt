package project.superuniqueit.fastbuy.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Log (
    var crash: String

): Parcelable {
    constructor() : this("")
}
