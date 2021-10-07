package project.ceyloninnovationlabs.fastbuy.ui.fragment.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import project.ceyloninnovationlabs.fastbuy.data.model.product.Product

import project.ceyloninnovationlabs.fastbuy.services.network.api.APIInterface
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

private const val PAGE_INDEX = 1
class SearchPagingSource (private val service: APIInterface,private val searchQuery: String) : PagingSource<Int, Product>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val position = params.key ?: PAGE_INDEX


        return try {


        //    var sortedProlist = service.getRecentProducts(params.loadSize, position)

            var sortedProlist = service.searchProducts(params.loadSize, position, search = searchQuery, instock = true)


          /*  var response = service.searchProducts(params.loadSize, position, search = searchQuery, instock = true)

            var sortedProlist = ArrayList<Product>()
            response.map {
                println("xxxxxxxxxxxxxxxxxxxxxx name "+it.name)

                it.categories.map {cat ->
                    println("xxxxxxxxxxxxxxxxxxxxxx categories name "+it.name)
                    if(cat.name == "Laptops"){
                        sortedProlist.add(it)
                        println("xxxxxxxxxxxxxxxxxxxxxx add product name "+it.name)
                    }

                }


            }*/




            val nextKey = if (sortedProlist.isEmpty()) {
                null
            } else {
                position + (params.loadSize / 5)
            }

            LoadResult.Page(
                data = sortedProlist,
                prevKey = if (position == PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )


        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }catch (exception: NullPointerException) {
            LoadResult.Error(exception)
        }catch (exception : UnknownHostException) {
            LoadResult.Error(exception)
        }

    }
    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
