package project.superuniqueit.fastbuy.services.network.api


import com.google.gson.JsonObject
import project.superuniqueit.fastbuy.data.model.SliderImage
import project.superuniqueit.fastbuy.data.model.user.User
import project.superuniqueit.fastbuy.data.model.coupon.Coupon
import project.superuniqueit.fastbuy.data.model.login.AuthRespons
import project.superuniqueit.fastbuy.data.model.orderoutput.PastOrder
import project.superuniqueit.fastbuy.data.model.page.Page
import project.superuniqueit.fastbuy.data.model.past.Orders
import project.superuniqueit.fastbuy.data.model.product.Product
import project.superuniqueit.fastbuy.data.model.shipping.ShippingMethods
import retrofit2.http.*


interface APIInterface {



    @GET("wp-json/wc/v3/products")
    suspend fun searchProducts(@Query("per_page") pageSize: Int, @Query("page") page: Int,
                               @Query("search") search: String,@Query("instock") instock: Boolean): ArrayList<Product>


    @GET("wp-json/wc/v3/products")
    suspend fun getRecentProducts(@Query("per_page") pageSize: Int, @Query("page") page: Int): ArrayList<Product>



    @GET("wp-json/wc/v3/products")
    suspend fun getTopSellingProducts(@Query("on_sale") onSale: Boolean,@Query("featured") featured: Boolean,
                                      @Query("order") order: String,@Query("orderby") orderby: String): ArrayList<Product>


    @GET("wp-json/wc/v3/products")
    suspend fun getFeaturedProducts(@Query("per_page") pageSize: Int, @Query("page") page: Int, @Query("featured") featured: Boolean): ArrayList<Product>


    @GET("wp-json/wc/v3/products")
    suspend fun getOnSaleProducts(@Query("per_page") pageSize: Int, @Query("page") page: Int,
                                  @Query("on_sale") onSale: Boolean,@Query("featured") featured: Boolean): ArrayList<Product>


    @GET("wp-json/wc/v3/coupons")
    suspend fun couponsValidate(@Query("code") code: String): ArrayList<Coupon>

    @GET("wp-json/wp/v2/pages/{pageid}")
    suspend fun getPage( @Path("pageid") pageid: Int): Page

    @GET("wp-json/wc/v3/shipping/zones/2/methods")
    suspend fun getShippingMethods(): ArrayList<ShippingMethods>



    @GET("app-slider/fastbuymobilesider.php")
    suspend fun getsider(): ArrayList<SliderImage>

    @POST("wp-json/wc/v3/orders")
    suspend fun addOrder(@Body orderInfo: JsonObject): PastOrder

    @POST("wp-json/wc/v3/customers")
    suspend fun addCustomer(@Body orderInfo: JsonObject): User

    @GET("wp-json/wc/v3/customers")
    suspend fun checkCustomer(@Query("email") email: String): ArrayList<User>

    @PUT("wp-json/wc/v3/customers/{cusid}")
    suspend fun updateCustomer(@Body orderInfo: JsonObject,@Path("cusid") customerid: Int): User

    @GET("wp-json/wc/v3/orders")
    suspend fun customersOrders(@Query("per_page") pageSize: Int, @Query("page") page: Int,
                                @Query("customer") customer_id: Int): ArrayList<Orders>

    @PUT("wp-json/wc/v3/orders/{id}")
    suspend fun updateOrders(@Body orderInfo: JsonObject,@Path("id") orderid: Int): PastOrder

    @POST("?rest_route=/simple-jwt-login/v1/auth")
    suspend fun authenticationEmail(@Query("email") email: String, @Query("password") password: String): AuthRespons

    @POST("?rest_route=/simple-jwt-login/v1/auth")
    suspend fun authenticationUserName(@Query("username") username: String, @Query("password") password: String): AuthRespons


}
