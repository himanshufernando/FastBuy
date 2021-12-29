package project.ceyloninnovationlabs.fastbuy.data.model.product

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    var average_rating: String,
    var backordered: Boolean,
    var backorders: String,
    var backorders_allowed: Boolean,
    var catalog_visibility: String,
    var categories: ArrayList<Category>,
    var date_created: String,
    var date_created_gmt: String,
    var date_modified: String,
    var date_modified_gmt: String,
    var date_on_sale_from: String,
    var date_on_sale_from_gmt: String,
    var date_on_sale_to: String,
    var date_on_sale_to_gmt: String,
    var description: String,
    var dimensions: Dimensions,
    var featured: Boolean,
    var id: Int,
    var images: ArrayList<Image>,
    var low_stock_amount: String,
    var manage_stock: Boolean,
    var name: String,
    var on_sale: Boolean,
    var permalink: String,
    var price: String,
    var purchasable: Boolean,
    var regular_price: String,
    var reviews_allowed: Boolean,
    var sale_price: String,
    var short_description: String,
    var sku: String,
    var slug: String,
    var sold_individually: Boolean,
    var status: String,
    var stock_quantity: Int,
    var tags: ArrayList<Tag>,
    var total_sales: Int,
    var type: String,
    var virtual: Boolean,
    var weight: String,
    var rating_count : Int,
    var isGiftWrapping : Boolean,
    var quantity: Int,
    var shipping_class : String,
    var shipping_class_id : Int
) : Parcelable {

    constructor() : this("", false, "",false,"",ArrayList<Category>(),"","","","",
        "","","","","", Dimensions(),false,0,ArrayList<Image>(),"",
        false,"",false,"","",false,"",false,"","","","",
        false,"",0,ArrayList<Tag>(),0,"",false,"",0,false,0,"",0
    )


}