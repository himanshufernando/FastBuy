package project.superuniqueit.fastbuy.data.model.shipping

data class Type(
    val default: String,
    val description: String,
    val id: String,
    val label: String,
    val options: OptionsX,
    val placeholder: String,
    val tip: String,
    val type: String,
    val value: String
)