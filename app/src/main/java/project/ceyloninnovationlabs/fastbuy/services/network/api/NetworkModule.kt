package project.ceyloninnovationlabs.fastbuy.services.network.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import project.ceyloninnovationlabs.fastbuy.BuildConfig
import project.ceyloninnovationlabs.fastbuy.FastBuy
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRecipeService():APIInterface{
        val oauth1Woocommerce = OAuthInterceptor.Builder()
            .consumerKey(BuildConfig.COSTUMER_KEY)
            .consumerSecret(BuildConfig.COSTUMER_SECRET)
            .build()

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        var cacheSize = 10 * 1024 * 1024
        val httpCacheDirectory = File(FastBuy.applicationContext().cacheDir, "offlineCache")
        var cache = Cache(httpCacheDirectory, cacheSize.toLong())

        var client = OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .cache(cache)
            .addInterceptor(oauth1Woocommerce)
            .addInterceptor(logging)
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                val maxAge =
                    0 // read from cache for 60 seconds even if there is internet connection
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .removeHeader("Pragma")
                    .build()
            }
            .addInterceptor { chain ->
                var request = chain.request()
                if (!InternetConnection.checkInternetConnection()) {
                    val maxStale = 60 * 60 * 24 * 30 // Offline cache available for 30 days
                    request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                        .removeHeader("Pragma")
                        .build()
                }
                chain.proceed(request)
            }.build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL.toHttpUrlOrNull()!!)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIInterface::class.java)

    }



}