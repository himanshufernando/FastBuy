package project.superuniqueit.fastbuy.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BaseApiModal (
    var error: Boolean,
    var message: String,
    var data: String?,
    var code:Int


): Parcelable {
    constructor() : this(false, "",
        "",0
    )

}
