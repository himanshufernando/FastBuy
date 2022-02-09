package project.ceyloninnovationlabs.fastbuy.ui.activity


import android.R.attr
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import project.ceyloninnovationlabs.fastbuy.R
import project.ceyloninnovationlabs.fastbuy.services.listeners.OnNavigationListener
import project.ceyloninnovationlabs.fastbuy.viewmodels.home.HomeViewModel
import com.google.android.gms.common.api.ApiException
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import project.ceyloninnovationlabs.fastbuy.data.model.FastBuyResult
import project.ceyloninnovationlabs.fastbuy.data.model.user.User
import project.ceyloninnovationlabs.fastbuy.services.perfrences.AppPrefs
import android.content.pm.PackageManager

import android.content.pm.PackageInfo
import android.util.Base64.*
import android.util.Log
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import retrofit2.HttpException
import java.lang.Exception
import java.net.SocketTimeoutException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import com.facebook.AccessToken
import androidx.annotation.NonNull
import com.facebook.GraphResponse

import org.json.JSONObject

import com.facebook.GraphRequest
import com.facebook.GraphRequest.GraphJSONObjectCallback
import com.google.gson.JsonObject
import lk.payhere.androidsdk.model.InitRequest
import org.json.JSONException
import project.ceyloninnovationlabs.fastbuy.data.model.orderoutput.PastOrder
import java.net.MalformedURLException
import java.net.URL
import androidx.core.app.ActivityCompat.startActivityForResult

import lk.payhere.androidsdk.PHConfigs

import lk.payhere.androidsdk.PHConstants

import lk.payhere.androidsdk.PHMainActivity
import lk.payhere.androidsdk.model.Item
import android.app.Activity

import android.R.attr.data

import lk.payhere.androidsdk.model.StatusResponse

import lk.payhere.androidsdk.PHResponse
import android.R.attr.data








@AndroidEntryPoint
class MainActivity : FragmentActivity(), View.OnClickListener {

    lateinit var navController: NavController
    lateinit var navView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    private val viewmodel: HomeViewModel by viewModels()



    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var account: GoogleSignInAccount

    lateinit var callbackManager: CallbackManager
    lateinit var loginManager: LoginManager
    lateinit var facebookObject: JSONObject

    lateinit var socialMediaLoginType : String



    companion object {
        const val RC_SIGN_IN = 100
         val appPrefs = AppPrefs
        const val PAYHERE_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        System.loadLibrary("keys")

        trasperat()

        cl_about.setOnClickListener(this)
        cl_contact.setOnClickListener(this)
        cl_terms.setOnClickListener(this)
        cl_return.setOnClickListener(this)
        cl_warranty.setOnClickListener(this)
        cl_home.setOnClickListener(this)
        cl_past.setOnClickListener(this)

        navView = nav_view
        drawerLayout = drawer_layout



        navController = Navigation.findNavController(this, R.id.nav_host_fragment)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        LoginManager.getInstance().logOut()
        initFacebookSDK()


        try {
            val info = packageManager.getPackageInfo(
                applicationContext.packageName,
                PackageManager.GET_SIGNATURES
            )

            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(android.util.Base64.encode(md.digest(), 0))
                println("xxxxxxxxxxxxxxxxxc printHashKey "+hashKey)

            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("Name not found", e.message, e)
        } catch (e: NoSuchAlgorithmException) {
            Log.d("Error", e.message, e)
        }


    }

    override fun onResume() {
        super.onResume()
        getShippingMethods()

        println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx appPrefs "+appPrefs.getUserPrefs())


    }

    override fun onStop() {
        super.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
            PAYHERE_REQUEST ->{
                if((data!!.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) && (data != null)){
                    val response = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT) as PHResponse<StatusResponse>
                    println("qqqqqqqqqqqqqq response : "+response)
                    println("qqqqqqqqqqqqqq resultCode : "+resultCode)

                   /* if (resultCode == Activity.RESULT_OK) {
                        if (response != null){

                            if(response.isSuccess){
                                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()

                            }else{
                                errorAlertDialog("Error", response.toString())
                            }



                        }else{
                            errorAlertDialog("Error", "Payment request not complete,Please try again !!")
                        }

                    }else if(resultCode == Activity.RESULT_CANCELED){

                        if (response != null){
                            errorAlertDialog("Error", response.toString())
                        }else{
                            errorAlertDialog("Error", "Payment request not complete,Please try again !!")
                        }

                    }*/

                }else{
                    errorAlertDialog("Error", "Payment request not complete,Please try again !!")
                    //not done correctly

                }
            }

        }

    }


    override fun onClick(v: View) {
        var nav = navController.currentDestination
        when (v.id) {
            R.id.cl_home -> {
                closeDrawer()
                if (!nav?.label?.equals("Home")!!) {
                    navController.navigate(R.id.fragment_activity_to_home)
                }
            }
            R.id.cl_past -> {
                closeDrawer()
                if (!nav?.label?.equals("Past")!!) {
                    navController.navigate(R.id.fragment_activity_to_past)
                }
            }

            R.id.cl_about -> {
                closeDrawer()
                if (!nav?.label?.equals("About")!!) {
                    navController.navigate(R.id.fragment_activity_to_about)
                }
            }
            R.id.cl_contact -> {
                closeDrawer()
                if (!nav?.label?.equals("Contact")!!) {
                    navController.navigate(R.id.fragment_activity_to_contact)
                }
            }
            R.id.cl_terms -> {
                closeDrawer()
                if (!nav?.label?.equals("Terms")!!) {
                    navController.navigate(R.id.fragment_activity_to_terms)
                }
            }
            R.id.cl_return -> {
                closeDrawer()
                if (!nav?.label?.equals("Returns")!!) {
                    navController.navigate(R.id.fragment_returns_to_terms)
                }
            }
            R.id.cl_warranty -> {
                closeDrawer()
                if (!nav?.label?.equals("Warranty")!!) {
                    navController.navigate(R.id.fragment_warranty_to_terms)
                }
            }
        }
    }

    fun initFacebookSDK() {
        //facebook sdk login callback register
        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance()
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult?) {

                val accessToken: AccessToken = loginResult!!.accessToken
                performLogin(accessToken)


            }
            override fun onCancel() { Toast.makeText(applicationContext, getString(R.string.facebook_login_cancel), Toast.LENGTH_LONG).show() }
            override fun onError(error: FacebookException) {
                when (error.cause) {
                    is HttpException -> Toast.makeText(applicationContext, getString(R.string.network_failed), Toast.LENGTH_LONG).show()
                    is SocketTimeoutException ->Toast.makeText(applicationContext, getString(R.string.timeout), Toast.LENGTH_LONG).show()
                    else -> Toast.makeText(applicationContext, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()

                }

            }
        })
    }


    private fun performLogin(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->

            facebookObject = `object`

            if(`object`.has("email")){
                var email = `object`.getString("email")
                cl_main.visibility = View.VISIBLE
                checkUser(email)

            }else{
                Toast.makeText(applicationContext, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
            }


        }
        val parameters = Bundle()
        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location")
        request.parameters = parameters
        request.executeAsync()

    }




    fun facebooklogin(){
        loginManager.logInWithReadPermissions(this, listOf("email"))
        socialMediaLoginType = "F"
    }
     fun googleSignIn(){
         socialMediaLoginType = "G"
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    private fun getShippingMethods(){
        viewmodel.getShippingMethods().observe(this, Observer {
                when (it) {
                    is FastBuyResult.Success -> {
                       viewmodel.shippingMethods.value = it.data.last()
                    }
                    is FastBuyResult.ExceptionError.ExError -> {

                    }

                }
            })

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            account = completedTask.getResult(ApiException::class.java)!!
            account.email.let {
                cl_main.visibility = View.VISIBLE
                checkUser(it)
            }

        } catch (e: ApiException) {
            Toast.makeText(this, "Error in google sign in, Please try again !", Toast.LENGTH_SHORT).show()
        }
    }

   private fun checkUser(email: String){

       viewmodel.checkCustomer(email).observe(this, Observer {
           when (it) {
               is FastBuyResult.Success -> {
                   if(it.data.isNullOrEmpty()){
                       addCustomer()
                   }else{
                       cl_main.visibility = View.GONE

                       var _user =it.data.first()

                       var values = _user.meta_data[8].value
                       var jsonVlau =  Gson().toJson(values)

                       val moshi = Moshi.Builder().build()
                       val adapter = moshi.adapter<Map<String, Any>>(
                           Types.newParameterizedType(Map::class.java, String::class.java,
                               Object::class.java)
                       )
                       val yourMap =  adapter.fromJson(jsonVlau)
                       val identifier = yourMap?.get("identifier").toString()

                       if(socialMediaLoginType == "G"){
                           _user.google_id =identifier
                       }else {
                           _user.facebook_id =identifier
                       }



                       appPrefs.setUserPrefs(_user)

                       viewmodel.googleSign.value = _user
                       viewmodel.googleSignTest.value = 5

                   }
               }
               is FastBuyResult.ExceptionError.ExError -> {
                   cl_main.visibility = View.GONE
                   errorAlertDialog("Error", it.exception.toString())
               }
               is FastBuyResult.LogicalError.LogError -> {
                   cl_main.visibility = View.GONE
                   errorAlertDialog("Error", it.exception.toString())
               }

           }

       })

   }


    private fun addCustomer(){
        var userDetails = appPrefs.getUserPrefs()
        if(socialMediaLoginType == "G"){
            userDetails.email = account.email
            userDetails.first_name = account.givenName
            userDetails.last_name = account.familyName
            userDetails.google_id =  account.id
            if(account.photoUrl != null){ userDetails.picture = account.photoUrl.toString() }
        }else{
            userDetails.email =facebookObject.getString("email")
            userDetails.first_name = facebookObject.getString("first_name")
            userDetails.last_name = facebookObject.getString("last_name")
            userDetails.facebook_id =  facebookObject.getString("id")
            userDetails.picture = "https://graph.facebook.com/" + facebookObject.getString("id") + "/picture?width=200&height=150"
        }
        viewmodel.addCustomer(userDetails).observe(this, Observer {
            when (it) {
                is FastBuyResult.Success -> {
                    cl_main.visibility = View.GONE
                    var _user =it.data

                    if(socialMediaLoginType == "G"){
                        _user.google_id = account.id
                    }else{
                        _user.facebook_id = facebookObject.getString("id")
                    }
                    appPrefs.setUserPrefs(_user)
                    viewmodel.googleSign.value = _user
                }
                is FastBuyResult.ExceptionError.ExError -> {
                    cl_main.visibility = View.GONE
                    errorAlertDialog("Error", it.exception.toString())
                }
                is FastBuyResult.LogicalError.LogError -> {
                    cl_main.visibility = View.GONE
                    errorAlertDialog("Error", it.exception.toString())
                }

            }

        })

    }


    private fun errorAlertDialog(title: String,message: String){
        val alertInfoDialog : AlertDialog = this?.let {
            val alertInfobuilder:AlertDialog.Builder = AlertDialog.Builder(it)
            alertInfobuilder.apply {
                setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User clicked OK button
                    })
            }
            alertInfobuilder?.setMessage(message).setTitle(title)
            alertInfobuilder.create()
        }
        alertInfoDialog.show()

    }


    private fun trasperat() {

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.BLACK

    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = this.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    fun lockDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun unLockDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    fun openDrawer() {
        hideKeyboard()
        drawerLayout.openDrawer(GravityCompat.START)
    }


    fun closeDrawer() {
        hideKeyboard()
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

     fun payhereCall(order: PastOrder){
         val req = InitRequest()
         req.merchantId = "214383"
         req.merchantSecret ="MS42dmRoNWhqNWp5"
         req.currency = "LKR"
         req.amount = order.total.toDouble()
         req.orderId = order.id.toString()

         var itemsDescription =""
         for(item in order.line_items){
             itemsDescription=itemsDescription+item.name+"_"
             req.items.add(Item(null, item.name, item.quantity, item.total.toDouble()))
         }
         req.itemsDescription = itemsDescription

         req.custom1 = order.customer_note
         req.customer.firstName = order.billing.first_name
         req.customer.lastName = order.billing.last_name
         req.customer.email = order.billing.email
         req.customer.phone = order.billing.phone
         req.customer.address.address = order.billing.address_1+" "+order.billing.address_2
         req.customer.address.city = order.billing.city
         req.customer.address.country = "Sri Lanka"

         req.customer.deliveryAddress.address = order.shipping.address_1+" "+order.shipping.address_2
         req.customer.deliveryAddress.city = order.shipping.address_1+" "+order.shipping.address_2
         req.customer.deliveryAddress.country = "Sri Lanka"




         val intent = Intent(this, PHMainActivity::class.java)
         intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req)
         PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL)
         startActivityForResult(intent, PAYHERE_REQUEST)

    }

    external fun getMerchant(): String
    external fun getMerchantSecret(): String

}