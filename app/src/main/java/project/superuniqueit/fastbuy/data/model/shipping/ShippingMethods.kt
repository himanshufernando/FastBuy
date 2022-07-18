package project.superuniqueit.fastbuy.data.model.shipping

data class ShippingMethods(
    val _links: Links,
    val enabled: Boolean,
    val id: Int,
    val instance_id: Int,
    val method_description: String,
    val method_id: String,
    val method_title: String,
    val order: Int,
    val settings: Settings,
    val title: String
)