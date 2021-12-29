package project.ceyloninnovationlabs.fastbuy.data.model.orderoutput

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShippingLine(
    val id: Int,
    val instance_id: String,
    val meta_data: ArrayList<MetaDataX>,
    val method_id: String,
    val method_title: String,
    val total: String
): Parcelable {
    constructor() : this(0,"", ArrayList<MetaDataX>(),"","",""
    )

}