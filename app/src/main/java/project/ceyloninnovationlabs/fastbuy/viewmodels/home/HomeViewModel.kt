package project.ceyloninnovationlabs.fastbuy.viewmodels.home

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import project.ceyloninnovationlabs.fastbuy.data.model.BaseApiModal
import project.ceyloninnovationlabs.fastbuy.data.model.FastBuyResult
import project.ceyloninnovationlabs.fastbuy.data.model.product.Product
import project.ceyloninnovationlabs.fastbuy.repo.HomeRepo

import java.net.UnknownHostException
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class HomeViewModel
@Inject
constructor( private val homeRepo: HomeRepo) : ViewModel() {


    val searchQuery: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val moreProductStatus: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }


    val itemQty: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val itemValue: MutableLiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    val cartCount: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }



    init {
        itemQty.value = 0
        itemValue.value = 0.0
        moreProductStatus.value = 0

    }

    private var productCatValue: Int? = null
    private var moreProductsResult: Flow<PagingData<Product>>? = null



    fun couponsValidate(_code: String) = liveData(Dispatchers.IO) {
        try {
            var respond = homeRepo.couponsValidate(_code)
            println("cccccccccccccccccccccccccccc  respond: "+respond)
            if(respond.error){
                var baseApiModal = BaseApiModal().apply {
                    error = respond.error
                    message = respond.message
                }
                emit(FastBuyResult.LogicalError.LogError(baseApiModal))
            }else{
                emit(FastBuyResult.Success(respond))
            }

        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        }catch (un : UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }


    fun moreProducts(productCat: Int): Flow<PagingData<Product>> {
        val lastResult = moreProductsResult

        return if ((productCat == productCatValue) && (lastResult != null)) {
            lastResult
        } else {
            productCatValue = productCat
            val newResult: Flow<PagingData<Product>> = homeRepo.moreProducts(productCat).cachedIn(viewModelScope)
            moreProductsResult = newResult
            newResult
        }

    }



    private var searchQueryValue: String? = null
    private var searchProductsResult: Flow<PagingData<Product>>? = null

    fun searchProducts(searchQuery: String): Flow<PagingData<Product>> {
        val lastResult = searchProductsResult
        return if ((searchQuery == searchQueryValue) && (lastResult != null)) {
            lastResult
        } else {
            searchQueryValue = searchQuery
            val newResult: Flow<PagingData<Product>> = homeRepo.searchProducts(searchQuery).cachedIn(viewModelScope)
            searchProductsResult = newResult
            newResult
        }
    }


    private var onRecentProductsResult: Flow<PagingData<Product>>? = null
    fun getRecentProducts(): Flow<PagingData<Product>> {
        val lastResult = onRecentProductsResult
        return if (lastResult != null) {
            lastResult
        } else {
            val newResult: Flow<PagingData<Product>> =homeRepo.getRecentProducts().cachedIn(viewModelScope)
            onRecentProductsResult = newResult
            newResult
        }
    }


    fun getTopSellingProducts() = liveData(Dispatchers.IO) {
        try {
            var respond = homeRepo.getTopSellingProducts()
            emit(FastBuyResult.Success(respond))
        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        }catch (un : UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }


    private var onFeaturedProductsResult: Flow<PagingData<Product>>? = null
    fun getFeaturedProducts(): Flow<PagingData<Product>> {
        val lastResult = onFeaturedProductsResult
        return if (lastResult != null) {
            lastResult
        } else {
            val newResult: Flow<PagingData<Product>> =homeRepo.getFeaturedProducts().cachedIn(viewModelScope)
            onFeaturedProductsResult = newResult
            newResult
        }
    }


    private var onSaleProductsResult: Flow<PagingData<Product>>? = null
    fun getOnSaleProducts(): Flow<PagingData<Product>> {
        val lastResult = onSaleProductsResult
        return if (lastResult != null) {
            lastResult
        } else {
            val newResult: Flow<PagingData<Product>> =homeRepo.getOnSaleProducts().cachedIn(viewModelScope)
            onSaleProductsResult = newResult
            newResult
        }
    }


    fun getSlider() = liveData(Dispatchers.IO) {
        try {
            var respond = homeRepo.getSlider()
            emit(FastBuyResult.Success(respond))
        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        }catch (un : UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }


}


