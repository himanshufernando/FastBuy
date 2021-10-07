package project.ceyloninnovationlabs.fastbuy.data.model.page

data class Page(
    val content: Content,
    val excerpt: Excerpt,
    val id: Int,
    val link: String,
    val title: Title
)