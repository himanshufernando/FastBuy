package project.ceyloninnovationlabs.fastbuy.services.perfrences


import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import project.ceyloninnovationlabs.fastbuy.FastBuy
import project.ceyloninnovationlabs.fastbuy.data.model.Order
import project.ceyloninnovationlabs.fastbuy.data.model.product.Product
import java.util.*
import java.util.regex.Pattern


object AppPrefs {


    const val PREFNAME = "FastBuy"
    const val LAYOUT_HOME = 301
    const val KEY_CART_ITEM = "CRAT_ITEM"

    var mContext = FastBuy.applicationContext()




    fun setCartItemPrefs(value: Order) {
        val sharedPref = mContext.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(KEY_CART_ITEM, Gson().toJson(value))
            commit()
        }
    }



    fun getCartItemPrefs(): Order {
        val sharedPref = mContext.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE)
        var userList = Order()

        return if (sharedPref.getString(KEY_CART_ITEM, null) == null) {
            userList
        } else {
            Gson().fromJson(
                sharedPref.getString(KEY_CART_ITEM, null),
                object : TypeToken<Order>() {}.type
            )
        }
    }


    fun checkValidString(st: String): Boolean {
        if ((st.isNullOrEmpty()) || (st == "null")) {
            return true
        }
        return false
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







    fun genarateUniqCode(): Long {
        val c: Calendar = Calendar.getInstance()

        var numberFromTime =
            c.get(Calendar.MONTH).toString() +
                    c.get(Calendar.DATE).toString() +
                    c.get(Calendar.YEAR).toString() +
                    c.get(Calendar.HOUR).toString() +
                    c.get(Calendar.MINUTE).toString() +
                    c.get(Calendar.SECOND).toString() +
                    c.get(Calendar.MILLISECOND).toString() + ((1..10000).random()).toString()

        return numberFromTime.toLong()
    }


    fun manipulateMobileNumber(number: String):String{
        return if(number.first().equals("0")){
            var a = number.removeRange(0,1)
            "+94$a"

        }else{
            "+94$number"
        }

    }


}
