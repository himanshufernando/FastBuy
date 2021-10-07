package project.ceyloninnovationlabs.fastbuy.services.network.api


import project.ceyloninnovationlabs.fastbuy.data.model.coupon.Coupon
import project.ceyloninnovationlabs.fastbuy.data.model.page.Page
import project.ceyloninnovationlabs.fastbuy.data.model.product.Product
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


interface APIInterface {



    @GET("wc/v3/products")
    suspend fun searchProducts(@Query("per_page") pageSize: Int, @Query("page") page: Int,
                               @Query("search") search: String,@Query("instock") instock: Boolean): ArrayList<Product>


    @GET("wc/v3/products")
    suspend fun getRecentProducts(@Query("per_page") pageSize: Int, @Query("page") page: Int): ArrayList<Product>



    @GET("wc/v3/products")
    suspend fun getTopSellingProducts(@Query("on_sale") onSale: Boolean,@Query("featured") featured: Boolean,
                                      @Query("order") order: String,@Query("orderby") orderby: String): ArrayList<Product>


    @GET("wc/v3/products")
    suspend fun getFeaturedProducts(@Query("per_page") pageSize: Int, @Query("page") page: Int, @Query("featured") featured: Boolean): ArrayList<Product>


    @GET("wc/v3/products")
    suspend fun getOnSaleProducts(@Query("per_page") pageSize: Int, @Query("page") page: Int,
                                  @Query("on_sale") onSale: Boolean,@Query("featured") featured: Boolean): ArrayList<Product>


    @GET("wc/v3/coupons")
    suspend fun couponsValidate(@Query("code") code: String): ArrayList<Coupon>

    @GET("wp/v2/pages/{pageid}")
    suspend fun getPage( @Path("pageid") pageid: Int): Page




}
