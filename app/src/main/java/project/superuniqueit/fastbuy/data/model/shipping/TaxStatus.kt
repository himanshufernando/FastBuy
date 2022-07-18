package project.superuniqueit.fastbuy.data.model.shipping

data class TaxStatus(
    val default: String,
    val description: String,
    val id: String,
    val label: String,
    val options: Options,
    val placeholder: String,
    val tip: String,
    val type: String,
    val value: String
)