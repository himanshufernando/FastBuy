package project.ceyloninnovationlabs.fastbuy.data.model




sealed  class FastBuyResult <out T : Any>{
     class Success<out T : Any>(val data: T) : FastBuyResult<T>()


     object InProgress : FastBuyResult<Nothing>()
     sealed class ExceptionError(val exception: Exception) : FastBuyResult<Nothing>() {
          class ExError(exception: Exception) : ExceptionError(exception)
     }
     sealed class LogicalError(val exception: BaseApiModal) : FastBuyResult<Nothing>() {
          class LogError(exception: BaseApiModal) : LogicalError(exception)
     }
}