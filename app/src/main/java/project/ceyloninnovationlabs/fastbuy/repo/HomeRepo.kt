package project.ceyloninnovationlabs.fastbuy.repo


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import project.ceyloninnovationlabs.fastbuy.FastBuy
import project.ceyloninnovationlabs.fastbuy.R
import project.ceyloninnovationlabs.fastbuy.data.model.product.Product
import project.ceyloninnovationlabs.fastbuy.data.model.SliderImage
import project.ceyloninnovationlabs.fastbuy.data.model.coupon.Coupon
import project.ceyloninnovationlabs.fastbuy.data.model.coupon.CouponBase
import project.ceyloninnovationlabs.fastbuy.data.model.page.Page
import project.ceyloninnovationlabs.fastbuy.services.network.api.APIInterface
import project.ceyloninnovationlabs.fastbuy.ui.fragment.home.featured.FeaturedPagingSource
import project.ceyloninnovationlabs.fastbuy.ui.fragment.home.onsale.OnSalePagingSource
import project.ceyloninnovationlabs.fastbuy.ui.fragment.home.recent.RecentPagingSource
import project.ceyloninnovationlabs.fastbuy.ui.fragment.search.MorePagingSource
import project.ceyloninnovationlabs.fastbuy.ui.fragment.search.SearchPagingSource


class HomeRepo(private var client: APIInterface) {


    private val APPCONTEX = FastBuy.applicationContext()
    val NETWORK_PAGE_SIZE = 5



    suspend fun getPage(_pageid : Int): Page {
        return client.getPage(pageid =_pageid)
    }


    suspend fun couponsValidate(_code : String): CouponBase {
        var baseApiModal = CouponBase()
        baseApiModal.error = true
        return when {
            !InternetConnection.checkInternetConnection() -> {
                baseApiModal.message = FastBuy.applicationContext().resources.getString(R.string.no_internet)
                baseApiModal
            }
            _code.isNullOrEmpty() -> {
                baseApiModal.message = "Please enter a coupon code"
                baseApiModal
            }
            else -> {
                var listResult = client.couponsValidate(code = _code)
                if(listResult.isEmpty()){
                    baseApiModal.message = "Coupon $_code does not exist!"
                    baseApiModal
                }else{
                    baseApiModal.error = false
                    baseApiModal.data = listResult
                    baseApiModal
                }
            }
        }
    }


    fun moreProducts(productCat: Int): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { MorePagingSource(client,productCat) }
        ).flow
    }


   fun searchProducts(searchQuery: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { SearchPagingSource(client,searchQuery) }
        ).flow
    }

     fun getRecentProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { RecentPagingSource(client) }
        ).flow
    }

    suspend fun getTopSellingProducts(): ArrayList<Product> {
        return client.getTopSellingProducts(onSale = true, featured = true,order = "asc",orderby = "popularity")
    }

     fun getFeaturedProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { FeaturedPagingSource(client) }
        ).flow
    }


     fun getOnSaleProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { OnSalePagingSource(client) }
        ).flow
    }


     fun getSlider(): ArrayList<SliderImage> {
        var listItems = java.util.ArrayList<SliderImage>()
        var item = SliderImage().apply {
            id = "1"
            productid = 1
            image =
                "https://www.lenovo.com/medias/lenovo-outlet-homepage-pricing-gone-lower-desktop.jpg?context=bWFzdGVyfHJvb3R8NzQwMjZ8aW1hZ2UvanBlZ3xoNTkvaDNkLzk5NTMwMTMyMzU3NDIuanBnfDBmODM5MmZjYTNiYWY3YTgzYmYxOTU5YmMwZGQ2NjFmMTZjN2ZlOTNmZWE5MzE3MDJjMWM3YjNiZmVlNDRiOGI&w=480"
        }

        var item2 = SliderImage().apply {
            id = "2"
            productid = 2
            image =
                "https://www.transcend-info.com/dist/images/event/dashcam/main2.png"
        }
        var item3 = SliderImage().apply {
            id = "3"
            productid = 3
            image = "https://i.pinimg.com/originals/cd/1c/7c/cd1c7cbd61e5f596d2d59ae2ea7b3d9c.jpg"
        }
        listItems.add(item)
        listItems.add(item2)
        listItems.add(item3)

        return listItems
    }


}
