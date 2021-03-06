package project.superuniqueit.fastbuy.viewmodels.home

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import project.superuniqueit.fastbuy.data.model.BaseApiModal
import project.superuniqueit.fastbuy.data.model.FastBuyResult
import project.superuniqueit.fastbuy.data.model.user.User
import project.superuniqueit.fastbuy.data.model.orderoutput.PastOrder
import project.superuniqueit.fastbuy.data.model.past.Orders
import project.superuniqueit.fastbuy.data.model.product.Product
import project.superuniqueit.fastbuy.data.model.shipping.ShippingMethods
import project.superuniqueit.fastbuy.repo.HomeRepo

import java.net.UnknownHostException
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class HomeViewModel
@Inject
constructor(private val homeRepo: HomeRepo) : ViewModel() {


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


    val termsAndCondition: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val shippingMethods: MutableLiveData<ShippingMethods> by lazy {
        MutableLiveData<ShippingMethods>()
    }

    val pastOrder: MutableLiveData<Orders> by lazy {
        MutableLiveData<Orders>()
    }

    val lastOrder: MutableLiveData<PastOrder> by lazy {
        MutableLiveData<PastOrder>()
    }


    val googleSign: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }


    val googleSignProgress: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }


    val payherePaymentCallBack: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }


    init {
        itemQty.value = 0
        itemValue.value = 0.0
        moreProductStatus.value = 0
        termsAndCondition.value = ""

    }

    fun getCustomersOrders(userid: Int): Flow<PagingData<Orders>> {
        return homeRepo.customersOrders(userid).cachedIn(viewModelScope)
    }

    fun deleteUser() = liveData(Dispatchers.IO) {
        try {
            emit(FastBuyResult.Success(homeRepo.deleteUser()))
        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        } catch (un: UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }


    fun updateCustomer(user: User) = liveData(Dispatchers.IO) {
        try {
            var respons = homeRepo.updateCustomer(user)
            homeRepo.insertUser(respons)
            emit(FastBuyResult.Success(respons))
        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        } catch (un: UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }

    fun getUser() = liveData(Dispatchers.IO) {
        try {
            var respons = homeRepo.getUser()
            emit(FastBuyResult.Success(respons))
        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        } catch (un: UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }

    fun checkCustomer(
        email: String,
        socialMediaLoginType: String,
        account: GoogleSignInAccount?,
        facebookObject: JSONObject
    ) = liveData(Dispatchers.IO) {
        try {
            var respons =
                homeRepo.checkCustomer(email, socialMediaLoginType, account, facebookObject)
            emit(FastBuyResult.Success(respons))
        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        } catch (un: UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }

    fun getCustomer(email: String) = liveData(Dispatchers.IO) {
        try {
            var respons = homeRepo.getCustomer(email)
            emit(FastBuyResult.Success(respons))
        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        } catch (un: UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }


    fun addCustomer(user: User) = liveData(Dispatchers.IO) {
        try {
            var respons = homeRepo.addCustomer(user)
            emit(FastBuyResult.Success(respons))
        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        } catch (un: UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }


    fun newOrder(order: PastOrder) = liveData(Dispatchers.IO) {
        try {
            var respons = homeRepo.newOrder(order)
            if (respons.errorStatus) {
                var baseApiModal = BaseApiModal().apply {
                    code = 100
                    message = respons.errorMessage
                }
                emit(FastBuyResult.LogicalError.LogError(baseApiModal))
            } else {
                emit(FastBuyResult.Success(respons))
            }

        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        } catch (un: UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }


    fun updateOrder(orderid: Int, status: Int) = liveData(Dispatchers.IO) {
        try {
            var respons = homeRepo.updateOrder(orderid, status)
            emit(FastBuyResult.Success(respons))
        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        } catch (un: UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }

    fun getPages(pageid: Int) = liveData(Dispatchers.IO) {
        try {
            var respond = homeRepo.getPage(pageid)
            emit(FastBuyResult.Success(respond))
        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        } catch (un: UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }


    private var productCatValue: Int? = null
    private var moreProductsResult: Flow<PagingData<Product>>? = null


    fun couponsValidate(_code: String) = liveData(Dispatchers.IO) {
        try {
            var respond = homeRepo.couponsValidate(_code)
            if (respond.error) {
                var baseApiModal = BaseApiModal().apply {
                    error = respond.error
                    message = respond.message
                }
                emit(FastBuyResult.LogicalError.LogError(baseApiModal))
            } else {
                emit(FastBuyResult.Success(respond))
            }

        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        } catch (un: UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }


    fun moreProducts(productCat: Int): Flow<PagingData<Product>> {
        val lastResult = moreProductsResult

        return if ((productCat == productCatValue) && (lastResult != null)) {
            lastResult
        } else {
            productCatValue = productCat
            val newResult: Flow<PagingData<Product>> =
                homeRepo.moreProducts(productCat).cachedIn(viewModelScope)
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
            val newResult: Flow<PagingData<Product>> =
                homeRepo.searchProducts(searchQuery).cachedIn(viewModelScope)
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
            val newResult: Flow<PagingData<Product>> = homeRepo.getRecentProducts().cachedIn(viewModelScope)
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
        } catch (un: UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }


    private var onFeaturedProductsResult: Flow<PagingData<Product>>? = null
    fun getFeaturedProducts(): Flow<PagingData<Product>> {
        val lastResult = onFeaturedProductsResult
        return if (lastResult != null) {
            lastResult
        } else {
            val newResult: Flow<PagingData<Product>> =
                homeRepo.getFeaturedProducts().cachedIn(viewModelScope)
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
            val newResult: Flow<PagingData<Product>> =
                homeRepo.getOnSaleProducts().cachedIn(viewModelScope)
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
        } catch (un: UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }


    fun getShippingMethods() = liveData(Dispatchers.IO) {
        try {
            var respond = homeRepo.getShippingMethods()
            emit(FastBuyResult.Success(respond))
        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        } catch (un: UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }


    fun userLogin(user: User) = liveData(Dispatchers.IO) {
        try {
            var respond = homeRepo.loginUser(user)
            emit(FastBuyResult.Success(respond))
        } catch (exception: Exception) {
            emit(FastBuyResult.ExceptionError.ExError(exception))
        } catch (un: UnknownHostException) {
            emit(FastBuyResult.ExceptionError.ExError(un))
        }
    }

    fun saveUserDataFromLastOrder(_user: User) = liveData(Dispatchers.IO) {
        if (_user.facebook_id.isNotEmpty() || _user.google_id.isNotEmpty()) {
            lastOrder.value?.let {
                _user.shipping = it.shipping
                _user.billing = it.billing
            }
        } else {
            lastOrder.value?.let {
                _user.email = it.billing.email
                _user.first_name = it.billing.first_name
                _user.last_name = it.billing.last_name
                _user.shipping = it.shipping
                _user.billing = it.billing
            }
        }
        homeRepo.insertUser(_user)
        emit(_user)
    }


}


