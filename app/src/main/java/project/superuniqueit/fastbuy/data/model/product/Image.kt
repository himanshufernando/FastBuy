package project.superuniqueit.fastbuy.data.model.product

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Image(
    var alt: String,
    var date_created: String,
    var date_created_gmt: String,
    var date_modified: String,
    var date_modified_gmt: String,
    var id: Int,
    var name: String,
    var src: String,
    var isSelect : Boolean
): Parcelable {
    constructor() : this("", "",
        "","","",0,"","",false
    )
}