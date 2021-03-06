package project.superuniqueit.fastbuy.repo


import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import project.superuniqueit.fastbuy.FastBuy
import project.superuniqueit.fastbuy.R
import project.superuniqueit.fastbuy.data.db.UserDao
import project.superuniqueit.fastbuy.data.model.product.Product
import project.superuniqueit.fastbuy.data.model.SliderImage
import project.superuniqueit.fastbuy.data.model.user.User
import project.superuniqueit.fastbuy.data.model.coupon.CouponBase
import project.superuniqueit.fastbuy.data.model.login.AuthRespons
import project.superuniqueit.fastbuy.data.model.orderoutput.PastOrder
import project.superuniqueit.fastbuy.data.model.page.Page
import project.superuniqueit.fastbuy.data.model.past.Orders
import project.superuniqueit.fastbuy.data.model.shipping.ShippingMethods
import project.superuniqueit.fastbuy.services.network.api.APIInterface
import project.superuniqueit.fastbuy.services.perfrences.AppPrefs
import project.superuniqueit.fastbuy.ui.fragment.home.featured.FeaturedPagingSource
import project.superuniqueit.fastbuy.ui.fragment.home.onsale.OnSalePagingSource
import project.superuniqueit.fastbuy.ui.fragment.home.recent.RecentPagingSource
import project.superuniqueit.fastbuy.ui.fragment.past.PastPagingSource
import project.superuniqueit.fastbuy.ui.fragment.search.MorePagingSource
import project.superuniqueit.fastbuy.ui.fragment.search.SearchPagingSource
import java.lang.Exception

import java.util.regex.Pattern


class HomeRepo(private var client: APIInterface, private var userDao: UserDao) {


    companion object {
        val appPrefs = AppPrefs
        val NETWORK_PAGE_SIZE = 5
    }


    fun customersOrders(userid: Int): Flow<PagingData<Orders>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { PastPagingSource(client, userid) }
        ).flow
    }


    suspend fun getPage(_pageid: Int): Page {
        return client.getPage(pageid = _pageid)
    }

    suspend fun getCustomer(email: String): User {
        val result = client.checkCustomer(email)
        return if (result.isEmpty()) {
            User().apply {
                status = false
                message = "No User found please try again!"
            }
        } else {
            var existingUser = result.first()
            insertUser(existingUser)
            existingUser
        }
    }


    suspend fun checkCustomer(
        email: String,
        socialMediaLoginType: String,
        account: GoogleSignInAccount?,
        facebookObject: JSONObject
    ): User {
        val checkCus = client.checkCustomer(email)

        println("sssssssssssssssss socialMediaLoginType : "+socialMediaLoginType)

        if (checkCus.isEmpty()) {
            var newUser = User()
            if (socialMediaLoginType == "G") {
                account.let {
                    newUser.email = account?.email!!
                    newUser.first_name = account?.givenName!!
                    newUser.last_name = account?.familyName!!
                    newUser.google_id = account?.id!!
                    if (account?.photoUrl != null) {
                        newUser.picture = account?.photoUrl.toString()
                    }
                }
            } else {
                newUser.email = facebookObject.getString("email")
                newUser.first_name = facebookObject.getString("first_name")
                newUser.last_name = facebookObject.getString("last_name")
                newUser.facebook_id = facebookObject.getString("id")
                newUser.picture =
                    "https://graph.facebook.com/" + facebookObject.getString("id") + "/picture?width=200&height=150"
            }
            val user = client.addCustomer(orderInfo = getNewUserJsonData(newUser))
            if (socialMediaLoginType == "G") {
                user.google_id = account?.id!!
            } else if (socialMediaLoginType == "F") {
                user.facebook_id = facebookObject.getString("id")
            }

            insertUser(user)
            return user
        } else {
            var existingUser = checkCus.first()
            for (item in existingUser.meta_data) {
                if (socialMediaLoginType == "G") {
                    if (item.key == "_wc_social_login_google_identifier") {
                        existingUser.google_id = item.value.toString()
                    }
                } else if (socialMediaLoginType == "F") {
                    if (item.key == "_wc_social_login_facebook_identifier") {
                        existingUser.facebook_id = item.value.toString()
                    }
                }
            }

            insertUser(existingUser)
            return existingUser
        }

    }

    private fun getNewUserJsonData(user: User): JsonObject {
        var orderJson = JsonObject()
        orderJson.addProperty("email", user.email)
        orderJson.addProperty("first_name", user.first_name)
        orderJson.addProperty("last_name", user.last_name)
        orderJson.addProperty("username", user.email)

        if (user.google_id.isNotEmpty()) {
            orderJson.addProperty("password", user.google_id)
        } else {
            orderJson.addProperty("password", user.facebook_id)
        }

        var billingJson = JsonObject()

        if (user.billing.first_name.isNullOrEmpty()) {

            billingJson.addProperty("first_name", user.first_name)
            billingJson.addProperty("last_name", user.last_name)
            billingJson.addProperty("company", user.billing.company)
            billingJson.addProperty("address_1", user.billing.address_1)
            billingJson.addProperty("address_2", user.billing.address_2)
            billingJson.addProperty("city", user.billing.city)
            billingJson.addProperty("state", user.billing.state)
            billingJson.addProperty("postcode", user.billing.postcode)
            billingJson.addProperty("country", "LK")
            billingJson.addProperty("email", user.email)
            billingJson.addProperty("phone", user.email)


        } else {
            billingJson.addProperty("first_name", user.billing.first_name)
            billingJson.addProperty("last_name", user.billing.last_name)
            billingJson.addProperty("company", user.billing.company)
            billingJson.addProperty("address_1", user.billing.address_1)
            billingJson.addProperty("address_2", user.billing.address_2)
            billingJson.addProperty("city", user.billing.city)
            billingJson.addProperty("state", user.billing.state)
            billingJson.addProperty("postcode", user.billing.postcode)
            billingJson.addProperty("country", "LK")
            billingJson.addProperty("email", user.email)
            billingJson.addProperty("phone", user.billing.phone)

        }




        orderJson.add("billing", billingJson)


        var shippingJson = JsonObject()
        shippingJson.addProperty("first_name", user.shipping.first_name)
        shippingJson.addProperty("last_name", user.shipping.last_name)
        shippingJson.addProperty("company", user.shipping.company)
        shippingJson.addProperty("address_1", user.shipping.address_1)
        shippingJson.addProperty("address_2", user.shipping.address_2)
        shippingJson.addProperty("city", user.shipping.city)
        shippingJson.addProperty("state", user.shipping.state)
        shippingJson.addProperty("postcode", user.shipping.postcode)
        shippingJson.addProperty("country", user.shipping.country)
        shippingJson.addProperty("phone", user.shipping.phone)

        orderJson.add("shipping", shippingJson)

        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()

        val meteDataJsonArr = JsonArray()

        var pointsbalanceJson = JsonObject()
        pointsbalanceJson.addProperty("key", "wc_points_balance")
        pointsbalanceJson.addProperty("value", "101")
        meteDataJsonArr.add(pointsbalanceJson)

        var wcemailverifiedcodeJson = JsonObject()
        wcemailverifiedcodeJson.addProperty("key", "wcemailverifiedcode")
        wcemailverifiedcodeJson.addProperty("value", "0587add09364a270c04de59a60c5cd5f")
        meteDataJsonArr.add(wcemailverifiedcodeJson)

        var profileupdatedJson = JsonObject()
        profileupdatedJson.addProperty("key", "_yoast_wpseo_profile_updated")
        profileupdatedJson.addProperty("value", ts)
        meteDataJsonArr.add(profileupdatedJson)


        var wcemailverifiedJson = JsonObject()
        wcemailverifiedJson.addProperty("key", "wcemailverifiedJson")
        wcemailverifiedJson.addProperty("value", "true")
        meteDataJsonArr.add(wcemailverifiedJson)

        if (user.google_id.isNotEmpty()) {

            var socialloginJson = JsonObject()

            socialloginJson.addProperty("key", "_wc_social_login_google_profile")
            var socialloginValueJson = JsonObject()

            socialloginValueJson.addProperty("identifier", user.google_id)
            socialloginValueJson.addProperty("photo_url", user.picture)
            socialloginValueJson.addProperty("display_name", user.first_name + " " + user.last_name)
            socialloginValueJson.addProperty("first_name", user.first_name)
            socialloginValueJson.addProperty("last_name", user.last_name)
            socialloginValueJson.addProperty("language", "en")
            socialloginValueJson.addProperty("email", user.email)
            socialloginValueJson.addProperty("email_verified", user.email)


            socialloginJson.add("value", socialloginValueJson)
            meteDataJsonArr.add(socialloginJson)


            var _wc_social_login_google_identifier = JsonObject()
            _wc_social_login_google_identifier.addProperty(
                "key",
                "_wc_social_login_google_identifier"
            )
            _wc_social_login_google_identifier.addProperty("value", user.google_id)
            meteDataJsonArr.add(_wc_social_login_google_identifier)

            var _wc_social_login_google_profile_image = JsonObject()
            _wc_social_login_google_profile_image.addProperty(
                "key",
                "_wc_social_login_google_profile_image"
            )
            _wc_social_login_google_profile_image.addProperty("value", user.picture)
            meteDataJsonArr.add(_wc_social_login_google_profile_image)


            var _wc_social_login_profile_image = JsonObject()
            _wc_social_login_profile_image.addProperty("key", "_wc_social_login_profile_image")
            _wc_social_login_profile_image.addProperty("value", user.picture)
            meteDataJsonArr.add(_wc_social_login_profile_image)

            var _wc_social_login_google_login_timestamp = JsonObject()
            _wc_social_login_google_login_timestamp.addProperty(
                "key",
                "_wc_social_login_google_login_timestamp"
            )
            _wc_social_login_google_login_timestamp.addProperty("value", ts)
            meteDataJsonArr.add(_wc_social_login_google_login_timestamp)


            var _wc_social_login_google_login_timestamp_gmt = JsonObject()
            _wc_social_login_google_login_timestamp_gmt.addProperty(
                "key",
                "_wc_social_login_google_login_timestamp_gmt"
            )
            _wc_social_login_google_login_timestamp_gmt.addProperty("value", ts)
            meteDataJsonArr.add(_wc_social_login_google_login_timestamp_gmt)


        } else {

            var socialloginJson = JsonObject()

            socialloginJson.addProperty("key", "_wc_social_login_facebook_profile")
            var socialloginValueJson = JsonObject()

            socialloginValueJson.addProperty("identifier", user.facebook_id)
            socialloginValueJson.addProperty("photo_url", user.picture)
            socialloginValueJson.addProperty("display_name", user.first_name + " " + user.last_name)
            socialloginValueJson.addProperty("first_name", user.first_name)
            socialloginValueJson.addProperty("last_name", user.last_name)
            socialloginValueJson.addProperty("language", "en")
            socialloginValueJson.addProperty("email", user.email)
            socialloginValueJson.addProperty("email_verified", user.email)


            socialloginJson.add("value", socialloginValueJson)
            meteDataJsonArr.add(socialloginJson)


            var _wc_social_login_google_identifier = JsonObject()
            _wc_social_login_google_identifier.addProperty(
                "key",
                "_wc_social_login_facebook_identifier"
            )
            _wc_social_login_google_identifier.addProperty("value", user.facebook_id)
            meteDataJsonArr.add(_wc_social_login_google_identifier)

            var _wc_social_login_google_profile_image = JsonObject()
            _wc_social_login_google_profile_image.addProperty(
                "key",
                "_wc_social_login_facebook_profile_image"
            )
            _wc_social_login_google_profile_image.addProperty("value", user.picture)
            meteDataJsonArr.add(_wc_social_login_google_profile_image)


            var _wc_social_login_profile_image = JsonObject()
            _wc_social_login_profile_image.addProperty("key", "_wc_social_login_profile_image")
            _wc_social_login_profile_image.addProperty("value", user.picture)
            meteDataJsonArr.add(_wc_social_login_profile_image)

            var _wc_social_login_google_login_timestamp = JsonObject()
            _wc_social_login_google_login_timestamp.addProperty(
                "key",
                "_wc_social_login_facebook_login_timestamp"
            )
            _wc_social_login_google_login_timestamp.addProperty("value", ts)
            meteDataJsonArr.add(_wc_social_login_google_login_timestamp)


            var _wc_social_login_google_login_timestamp_gmt = JsonObject()
            _wc_social_login_google_login_timestamp_gmt.addProperty(
                "key",
                "_wc_social_login_facebook_login_timestamp_gmt"
            )
            _wc_social_login_google_login_timestamp_gmt.addProperty("value", ts)
            meteDataJsonArr.add(_wc_social_login_google_login_timestamp_gmt)


        }

        var wc_last_activeJson = JsonObject()
        wc_last_activeJson.addProperty("key", "wc_last_activeJson")
        wc_last_activeJson.addProperty("value", ts)
        meteDataJsonArr.add(wc_last_activeJson)

        orderJson.add("meta_data", meteDataJsonArr)

        return orderJson
    }


    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun getUser(): User {
        return userDao.getUser()
    }

    suspend fun updateCustomer(user: User): User {


        var updateUser = User()

        if (user.first_name.isNullOrEmpty()) {
            updateUser.message = "First name can not be empty"
            updateUser.status = true
            return updateUser
        }

        if (user.last_name.isNullOrEmpty()) {
            updateUser.message = "Last name can not be empty"
            updateUser.status = true
            return updateUser
        }


        var orderJson = JsonObject()

        orderJson.addProperty("first_name", user.first_name)
        orderJson.addProperty("last_name", user.last_name)

        var billingJson = JsonObject()

        billingJson.addProperty("first_name", user.first_name)
        billingJson.addProperty("last_name", user.last_name)
        billingJson.addProperty("company", user.billing.company)
        billingJson.addProperty("address_1", user.billing.address_1)
        billingJson.addProperty("address_2", user.billing.address_2)
        billingJson.addProperty("city", user.billing.city)
        billingJson.addProperty("state", user.billing.state)
        billingJson.addProperty("postcode", user.billing.postcode)
        billingJson.addProperty("country", "LK")
        billingJson.addProperty("email", user.billing.email)
        billingJson.addProperty("phone", user.billing.phone)

        orderJson.add("billing", billingJson)


        var shippingJson = JsonObject()
        shippingJson.addProperty("first_name", user.shipping.first_name)
        shippingJson.addProperty("last_name", user.shipping.last_name)
        shippingJson.addProperty("company", user.shipping.company)
        shippingJson.addProperty("address_1", user.shipping.address_1)
        shippingJson.addProperty("address_2", user.shipping.address_2)
        shippingJson.addProperty("city", user.shipping.city)
        shippingJson.addProperty("state", user.shipping.state)
        shippingJson.addProperty("postcode", user.shipping.postcode)
        shippingJson.addProperty("country", user.shipping.country)
        shippingJson.addProperty("phone", user.shipping.phone)

        orderJson.add("shipping", shippingJson)
        var respons = client.updateCustomer(orderInfo = orderJson, user.id)
        try {
            if(!respons.status){
                var values = respons.meta_data[8].value
                var jsonVlau = Gson().toJson(values)

                val moshi = Moshi.Builder().build()
                val adapter = moshi.adapter<Map<String, Any>>(
                    Types.newParameterizedType(
                        Map::class.java, String::class.java,
                        Object::class.java
                    )
                )
                val yourMap = adapter.fromJson(jsonVlau)
                respons.google_id = yourMap?.get("identifier").toString()
            }

        }catch (ex : Exception){

        }


        return client.updateCustomer(orderInfo = orderJson, user.id)

    }

    suspend fun deleteUser(): Boolean {
        userDao.deleteUser()
        return true
    }

    suspend fun addCustomer(user: User): User {
        var orderJson = JsonObject()

        orderJson.addProperty("email", user.email)
        orderJson.addProperty("first_name", user.first_name)
        orderJson.addProperty("last_name", user.last_name)
        orderJson.addProperty("username", user.email)

        if (user.google_id.isNotEmpty()) {
            orderJson.addProperty("password", user.google_id)
        } else {
            orderJson.addProperty("password", user.facebook_id)
        }


        var billingJson = JsonObject()

        if (user.billing.first_name.isNullOrEmpty()) {

            billingJson.addProperty("first_name", user.first_name)
            billingJson.addProperty("last_name", user.last_name)
            billingJson.addProperty("company", user.billing.company)
            billingJson.addProperty("address_1", user.billing.address_1)
            billingJson.addProperty("address_2", user.billing.address_2)
            billingJson.addProperty("city", user.billing.city)
            billingJson.addProperty("state", user.billing.state)
            billingJson.addProperty("postcode", user.billing.postcode)
            billingJson.addProperty("country", "LK")
            billingJson.addProperty("email", user.email)
            billingJson.addProperty("phone", user.email)


        } else {
            billingJson.addProperty("first_name", user.billing.first_name)
            billingJson.addProperty("last_name", user.billing.last_name)
            billingJson.addProperty("company", user.billing.company)
            billingJson.addProperty("address_1", user.billing.address_1)
            billingJson.addProperty("address_2", user.billing.address_2)
            billingJson.addProperty("city", user.billing.city)
            billingJson.addProperty("state", user.billing.state)
            billingJson.addProperty("postcode", user.billing.postcode)
            billingJson.addProperty("country", "LK")
            billingJson.addProperty("email", user.email)
            billingJson.addProperty("phone", user.billing.phone)

        }




        orderJson.add("billing", billingJson)


        var shippingJson = JsonObject()
        shippingJson.addProperty("first_name", user.shipping.first_name)
        shippingJson.addProperty("last_name", user.shipping.last_name)
        shippingJson.addProperty("company", user.shipping.company)
        shippingJson.addProperty("address_1", user.shipping.address_1)
        shippingJson.addProperty("address_2", user.shipping.address_2)
        shippingJson.addProperty("city", user.shipping.city)
        shippingJson.addProperty("state", user.shipping.state)
        shippingJson.addProperty("postcode", user.shipping.postcode)
        shippingJson.addProperty("country", user.shipping.country)
        shippingJson.addProperty("phone", user.shipping.phone)

        orderJson.add("shipping", shippingJson)

        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()

        val meteDataJsonArr = JsonArray()

        var pointsbalanceJson = JsonObject()
        pointsbalanceJson.addProperty("key", "wc_points_balance")
        pointsbalanceJson.addProperty("value", "101")
        meteDataJsonArr.add(pointsbalanceJson)

        var wcemailverifiedcodeJson = JsonObject()
        wcemailverifiedcodeJson.addProperty("key", "wcemailverifiedcode")
        wcemailverifiedcodeJson.addProperty("value", "0587add09364a270c04de59a60c5cd5f")
        meteDataJsonArr.add(wcemailverifiedcodeJson)

        var profileupdatedJson = JsonObject()
        profileupdatedJson.addProperty("key", "_yoast_wpseo_profile_updated")
        profileupdatedJson.addProperty("value", ts)
        meteDataJsonArr.add(profileupdatedJson)


        var wcemailverifiedJson = JsonObject()
        wcemailverifiedJson.addProperty("key", "wcemailverifiedJson")
        wcemailverifiedJson.addProperty("value", "true")
        meteDataJsonArr.add(wcemailverifiedJson)

        if (user.google_id.isNotEmpty()) {

            var socialloginJson = JsonObject()

            socialloginJson.addProperty("key", "_wc_social_login_google_profile")
            var socialloginValueJson = JsonObject()

            socialloginValueJson.addProperty("identifier", user.google_id)
            socialloginValueJson.addProperty("photo_url", user.picture)
            socialloginValueJson.addProperty("display_name", user.first_name + " " + user.last_name)
            socialloginValueJson.addProperty("first_name", user.first_name)
            socialloginValueJson.addProperty("last_name", user.last_name)
            socialloginValueJson.addProperty("language", "en")
            socialloginValueJson.addProperty("email", user.email)
            socialloginValueJson.addProperty("email_verified", user.email)


            socialloginJson.add("value", socialloginValueJson)
            meteDataJsonArr.add(socialloginJson)


            var _wc_social_login_google_identifier = JsonObject()
            _wc_social_login_google_identifier.addProperty(
                "key",
                "_wc_social_login_google_identifier"
            )
            _wc_social_login_google_identifier.addProperty("value", user.google_id)
            meteDataJsonArr.add(_wc_social_login_google_identifier)

            var _wc_social_login_google_profile_image = JsonObject()
            _wc_social_login_google_profile_image.addProperty(
                "key",
                "_wc_social_login_google_profile_image"
            )
            _wc_social_login_google_profile_image.addProperty("value", user.picture)
            meteDataJsonArr.add(_wc_social_login_google_profile_image)


            var _wc_social_login_profile_image = JsonObject()
            _wc_social_login_profile_image.addProperty("key", "_wc_social_login_profile_image")
            _wc_social_login_profile_image.addProperty("value", user.picture)
            meteDataJsonArr.add(_wc_social_login_profile_image)

            var _wc_social_login_google_login_timestamp = JsonObject()
            _wc_social_login_google_login_timestamp.addProperty(
                "key",
                "_wc_social_login_google_login_timestamp"
            )
            _wc_social_login_google_login_timestamp.addProperty("value", ts)
            meteDataJsonArr.add(_wc_social_login_google_login_timestamp)


            var _wc_social_login_google_login_timestamp_gmt = JsonObject()
            _wc_social_login_google_login_timestamp_gmt.addProperty(
                "key",
                "_wc_social_login_google_login_timestamp_gmt"
            )
            _wc_social_login_google_login_timestamp_gmt.addProperty("value", ts)
            meteDataJsonArr.add(_wc_social_login_google_login_timestamp_gmt)


        } else {

            var socialloginJson = JsonObject()

            socialloginJson.addProperty("key", "_wc_social_login_facebook_profile")
            var socialloginValueJson = JsonObject()

            socialloginValueJson.addProperty("identifier", user.facebook_id)
            socialloginValueJson.addProperty("photo_url", user.picture)
            socialloginValueJson.addProperty("display_name", user.first_name + " " + user.last_name)
            socialloginValueJson.addProperty("first_name", user.first_name)
            socialloginValueJson.addProperty("last_name", user.last_name)
            socialloginValueJson.addProperty("language", "en")
            socialloginValueJson.addProperty("email", user.email)
            socialloginValueJson.addProperty("email_verified", user.email)


            socialloginJson.add("value", socialloginValueJson)
            meteDataJsonArr.add(socialloginJson)


            var _wc_social_login_google_identifier = JsonObject()
            _wc_social_login_google_identifier.addProperty(
                "key",
                "_wc_social_login_facebook_identifier"
            )
            _wc_social_login_google_identifier.addProperty("value", user.facebook_id)
            meteDataJsonArr.add(_wc_social_login_google_identifier)

            var _wc_social_login_google_profile_image = JsonObject()
            _wc_social_login_google_profile_image.addProperty(
                "key",
                "_wc_social_login_facebook_profile_image"
            )
            _wc_social_login_google_profile_image.addProperty("value", user.picture)
            meteDataJsonArr.add(_wc_social_login_google_profile_image)


            var _wc_social_login_profile_image = JsonObject()
            _wc_social_login_profile_image.addProperty("key", "_wc_social_login_profile_image")
            _wc_social_login_profile_image.addProperty("value", user.picture)
            meteDataJsonArr.add(_wc_social_login_profile_image)

            var _wc_social_login_google_login_timestamp = JsonObject()
            _wc_social_login_google_login_timestamp.addProperty(
                "key",
                "_wc_social_login_facebook_login_timestamp"
            )
            _wc_social_login_google_login_timestamp.addProperty("value", ts)
            meteDataJsonArr.add(_wc_social_login_google_login_timestamp)


            var _wc_social_login_google_login_timestamp_gmt = JsonObject()
            _wc_social_login_google_login_timestamp_gmt.addProperty(
                "key",
                "_wc_social_login_facebook_login_timestamp_gmt"
            )
            _wc_social_login_google_login_timestamp_gmt.addProperty("value", ts)
            meteDataJsonArr.add(_wc_social_login_google_login_timestamp_gmt)


        }


        var wc_last_activeJson = JsonObject()
        wc_last_activeJson.addProperty("key", "wc_last_activeJson")
        wc_last_activeJson.addProperty("value", ts)
        meteDataJsonArr.add(wc_last_activeJson)

        orderJson.add("meta_data", meteDataJsonArr)

        return client.addCustomer(orderInfo = orderJson)

    }


    suspend fun newOrder(order: PastOrder): PastOrder {

        if (!order.isAgreedToTerms) {
            order.errorMessage =
                "Please read and accept the terms and conditions to proceed with your order."
            order.errorStatus = true
            return order
        }


        if (!InternetConnection.checkInternetConnection()) {
            order.errorMessage =
                "Failed to connect to the server. please check your internet connection and try again"
            order.errorStatus = true
            return order
        }


        if (order.billing.first_name.isNullOrEmpty()) {
            order.errorMessage = "Billing first name can not be empty"
            order.errorStatus = true
            return order
        }

        if (order.billing.address_1.isNullOrEmpty()) {
            order.errorMessage = "Billing address line one can not be empty"
            order.errorStatus = true
            return order
        }

        if (order.billing.city.isNullOrEmpty()) {
            order.errorMessage = "Billing city one can not be empty"
            order.errorStatus = true
            return order
        }

        if (order.billing.postcode.isNullOrEmpty()) {
            order.errorMessage = "Billing post code can not be empty"
            order.errorStatus = true
            return order
        }


        if (order.billing.phone.isNullOrEmpty()) {
            order.errorMessage = "Billing phone number not valid"
            order.errorStatus = true
            return order
        }


        if (!validatePhoneNumber(order.billing.phone)) {
            order.errorMessage = "Billing phone number not valid"
            order.errorStatus = true
            return order
        }



        if (order.billing.email.isNullOrEmpty()) {
            order.errorMessage = "Enter valid email address."
            order.errorStatus = true
            return order
        }


        if (!validateEmailAddress(order.billing.email)) {
            order.errorMessage = "Enter valid email address."
            order.errorStatus = true
            return order
        }


        if (order.shipping.first_name.isNullOrEmpty()) {
            order.errorMessage = "Shipping first name can not be empty"
            order.errorStatus = true
            return order
        }

        if (order.shipping.address_1.isNullOrEmpty()) {
            order.errorMessage = "Shipping address line one can not be empty"
            order.errorStatus = true
            return order
        }

        if (order.shipping.city.isNullOrEmpty()) {
            order.errorMessage = "Shipping city one can not be empty"
            order.errorStatus = true
            return order
        }

        if (order.shipping.postcode.isNullOrEmpty()) {
            order.errorMessage = "Shipping post code can not be empty"
            order.errorStatus = true
            return order
        }




        if (order.product.isEmpty()) {
            order.errorMessage = "No item added to order"
            order.errorStatus = true
            return order
        }


        if (order.paymentType.isNullOrEmpty()) {
            order.errorMessage = "Select the payment type "
            order.errorStatus = true
            return order
        }


        var orderJson = JsonObject()

        when (order.paymentType) {
            "bacs" -> {
                orderJson.addProperty("payment_method", "bacs")
                orderJson.addProperty("payment_method_title", "Direct bank transfer")
            }
            "cod" -> {
                orderJson.addProperty("payment_method", "cod")
                orderJson.addProperty("payment_method_title", "Cash on delivery")

            }
            "cop" -> {
                orderJson.addProperty("payment_method", "cop")
                orderJson.addProperty("payment_method_title", "Pay on Showroom pickup")

            }

            "payhere" -> {
                orderJson.addProperty("payment_method", "payhere")
                orderJson.addProperty("payment_method_title", "PayHere")
                order.payment_method = "payhere"

            }

        }


        var _user = userDao.getUser()

        if (_user != null) {
            if (_user.id != 0)
                orderJson.addProperty("customer_id", _user.id)
        }

        var billingJson = JsonObject()
        billingJson.addProperty("first_name", order.billing.first_name)
        billingJson.addProperty("last_name", order.billing.last_name)
        billingJson.addProperty("company", order.billing.company)
        billingJson.addProperty("address_1", order.billing.address_1)
        billingJson.addProperty("address_2", order.billing.address_2)
        billingJson.addProperty("city", order.billing.city)
        billingJson.addProperty("state", order.billing.state)
        billingJson.addProperty("postcode", order.billing.postcode)
        billingJson.addProperty("country", order.billing.country)
        billingJson.addProperty("email", order.billing.email)
        billingJson.addProperty("phone", order.billing.phone)

        orderJson.add("billing", billingJson)


        var shippingJson = JsonObject()
        shippingJson.addProperty("first_name", order.shipping.first_name)
        shippingJson.addProperty("last_name", order.shipping.last_name)
        shippingJson.addProperty("company", order.shipping.company)
        shippingJson.addProperty("address_1", order.shipping.address_1)
        shippingJson.addProperty("address_2", order.shipping.address_2)
        shippingJson.addProperty("city", order.shipping.city)
        shippingJson.addProperty("state", order.shipping.state)
        shippingJson.addProperty("postcode", order.shipping.postcode)
        shippingJson.addProperty("country", order.shipping.country)
        shippingJson.addProperty("phone", order.shipping.phone)

        orderJson.add("shipping", shippingJson)

        if (!order.customer_note.isNullOrBlank()) {
            orderJson.addProperty("customer_note", order.customer_note)
        }


        val itemsJsonArr = JsonArray()

        order.product.map {
            var productJson = JsonObject()
            productJson.addProperty("product_id", it.id)
            productJson.addProperty("quantity", it.quantity)

            if (it.isGiftWrapping) {
                var tot = it.price.toDouble() + 200.00
                productJson.addProperty("total", tot.toString())
            }


            itemsJsonArr.add(productJson)

            val itemsMetadataJsonArr = JsonArray()

            if (it.isGiftWrapping) {
                var giftObjectJson = JsonObject()
                giftObjectJson.addProperty("key", "Gift Wrapping for All Products (Rs.200.00)")
                giftObjectJson.addProperty("value", "Add gift wrapping")
                giftObjectJson.addProperty(
                    "display_key",
                    "Gift Wrapping for All Products (Rs.200.00)"
                )
                giftObjectJson.addProperty("display_value", "Add gift wrapping")
                itemsMetadataJsonArr.add(giftObjectJson)
            }

            var stokeObjectJson = JsonObject()
            stokeObjectJson.addProperty("key", "_reduced_stock")
            stokeObjectJson.addProperty("value", it.quantity.toString())
            stokeObjectJson.addProperty("display_key", "_reduced_stock")
            stokeObjectJson.addProperty("display_value", it.quantity.toString())

            itemsMetadataJsonArr.add(stokeObjectJson)

            productJson.add("meta_data", itemsMetadataJsonArr)

        }

        orderJson.add("line_items", itemsJsonArr)


        if (order.coupon_lines.isNotEmpty()) {
            if (order.coupon_lines[0].code != 0) {
                val couponArr = JsonArray()
                var couponJson = JsonObject()
                couponJson.addProperty("code", order.coupon_lines[0].code)
                couponArr.add(couponJson)
                orderJson.add("coupon_lines", couponArr)
            }
        }


        if ((order.cashOnDeliveryValue != 0.0) && (order.paymentType == "cod")) {

            val feeLines = JsonArray()
            var feeLinesJson = JsonObject()

            feeLinesJson.addProperty("name", "Cash on delivery:")
            feeLinesJson.addProperty("tax_class", "0")
            feeLinesJson.addProperty("tax_status", "taxable")
            feeLinesJson.addProperty("amount", "399")
            feeLinesJson.addProperty("total", "399.00")

            val feeLinesMete = JsonArray()
            var feeLinesMeteJson = JsonObject()

            feeLinesMeteJson.addProperty("key", "_added_by")
            feeLinesMeteJson.addProperty("value", "woocommerce_additional_fees")
            feeLinesMeteJson.addProperty("display_key", "_added_by")
            feeLinesMeteJson.addProperty("display_value", "woocommerce_additional_fees")

            feeLinesMete.add(feeLinesMeteJson)
            feeLinesJson.add("meta_data", feeLinesMete)

            feeLines.add(feeLinesJson)

            orderJson.add("fee_lines", feeLines)

        }

        if ((order.paymentGatewayValue != 0.0) && (order.paymentType == "payhere")) {

            val feeLines = JsonArray()
            var feeLinesJson = JsonObject()

            feeLinesJson.addProperty("name", "Fee for PayHere:")
            feeLinesJson.addProperty("tax_class", "0")
            feeLinesJson.addProperty("tax_status", "taxable")
            feeLinesJson.addProperty("amount", order.paymentGatewayValue.toString())
            feeLinesJson.addProperty("total", order.paymentGatewayValue.toString())

            val feeLinesMete = JsonArray()
            var feeLinesMeteJson = JsonObject()

            feeLinesMeteJson.addProperty("key", "_added_by")
            feeLinesMeteJson.addProperty("value", "woocommerce_additional_fees")
            feeLinesMeteJson.addProperty("display_key", "_added_by")
            feeLinesMeteJson.addProperty("display_value", "woocommerce_additional_fees")

            feeLinesMete.add(feeLinesMeteJson)
            feeLinesJson.add("meta_data", feeLinesMete)

            feeLines.add(feeLinesJson)

            orderJson.add("fee_lines", feeLines)

        }


        var _method_id = ""
        var _method_title = ""

        when (order.shippingType) {
            "Flat rate" -> {
                _method_id = "flat_rate"
                _method_title = "Flat rate"
                orderJson.addProperty("status", "on-hold")
            }

            "Local pickup" -> {
                _method_id = "local_pickup"
                _method_title = "Local pickup"
                orderJson.addProperty("status", "on-hold")

            }

            "Free shipping" -> {
                _method_id = "free_shipping"
                _method_title = "Free shipping"
                orderJson.addProperty("status", "on-hold")
            }
        }


        val shippingLineJarray = JsonArray()
        var shippingLineJson = JsonObject()
        shippingLineJson.addProperty("method_id", _method_id)
        shippingLineJson.addProperty("method_title", _method_title)
        shippingLineJson.addProperty("total", order.shippingCost.toString())

        shippingLineJarray.add(shippingLineJson)
        orderJson.add("shipping_lines", shippingLineJarray)

        Log.i("HOME_REPO", orderJson.toString())

        return client.addOrder(orderInfo = orderJson)

    }


    suspend fun loginUser(user: User): AuthRespons {
        var respons = AuthRespons()

        if (user.email.isNullOrEmpty()) {
            respons.success = false
            respons.data.message = "Username or Email is required."
            return respons
        }
        if (user.password.isNullOrEmpty()) {
            respons.success = false
            respons.data.message = "The password field is empty"
            return respons
        }


        respons = if (validateEmailAddress(user.email)) {
            return client.authenticationEmail(email = user.email, password = user.password)
        } else {
            return client.authenticationUserName(username = user.email, password = user.password)
        }



        return respons
    }


    suspend fun updateOrder(orderid: Int, status: Int): PastOrder {

        var orderJson = JsonObject()
        when (status) {
            5 -> {
                orderJson.addProperty("status", "processing")
            }
            10 -> {
                orderJson.addProperty("status", "failed")
            }
            else -> {
                orderJson.addProperty("status", "failed")
            }
        }

        return client.updateOrders(orderJson, orderid)
    }


    suspend fun couponsValidate(_code: String): CouponBase {
        var baseApiModal = CouponBase()
        baseApiModal.error = true
        return when {
            !InternetConnection.checkInternetConnection() -> {
                baseApiModal.message =
                    FastBuy.applicationContext().resources.getString(R.string.no_internet)
                baseApiModal
            }
            _code.isNullOrEmpty() -> {
                baseApiModal.message = "Please enter a coupon code"
                baseApiModal
            }
            else -> {
                var listResult = client.couponsValidate(code = _code)
                if (listResult.isEmpty()) {
                    baseApiModal.message = "Coupon $_code does not exist!"
                    baseApiModal
                } else {
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
            pagingSourceFactory = { MorePagingSource(client, productCat) }
        ).flow
    }


    fun searchProducts(searchQuery: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { SearchPagingSource(client, searchQuery) }
        ).flow
    }

    fun getRecentProducts(): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { RecentPagingSource(client) }
        ).flow
    }

    suspend fun getTopSellingProducts(): ArrayList<Product> {
        return client.getTopSellingProducts(
            onSale = true,
            featured = true,
            order = "asc",
            orderby = "popularity"
        ).shuffled() as ArrayList<Product>
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


    suspend fun getShippingMethods(): ArrayList<ShippingMethods> {
        return client.getShippingMethods()
    }


    suspend fun getSlider(): ArrayList<SliderImage> {
        return client.getsider()


        /* var listItems = java.util.ArrayList<SliderImage>()
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

         return listItems*/
    }

    fun validateEmailAddress(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }


    fun validatePhoneNumber(phone: String): Boolean {
        if (phone.length != 10) {
            return false
        }

        return phone.matches("-?\\d+(\\.\\d+)?".toRegex())


    }


}
