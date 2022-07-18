package project.superuniqueit.fastbuy.ui.fragment.home.featured

import androidx.paging.PagingSource
import androidx.paging.PagingState
import project.superuniqueit.fastbuy.data.model.product.Product

import project.superuniqueit.fastbuy.services.network.api.APIInterface
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

private const val PAGE_INDEX = 1
class FeaturedPagingSource (private val service: APIInterface) : PagingSource<Int, Product>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val position = params.key ?: PAGE_INDEX


        return try {
            var response = service.getFeaturedProducts(params.loadSize, position, featured = true)

            val nextKey = if (response.isEmpty()) {
                null
            } else {
                position + (params.loadSize / 5)
            }

            LoadResult.Page(
                data = response,
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
