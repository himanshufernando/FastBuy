package project.ceyloninnovationlabs.fastbuy.data.model.login

data class Data(
    var errorCode: Int=0,
    var jwt: String="",
    var message: String=""
)