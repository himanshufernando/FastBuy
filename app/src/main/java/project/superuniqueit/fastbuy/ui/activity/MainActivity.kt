package project.superuniqueit.fastbuy.ui.activity


import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Base64.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import lk.payhere.androidsdk.PHConfigs
import lk.payhere.androidsdk.PHConstants
import lk.payhere.androidsdk.PHMainActivity
import lk.payhere.androidsdk.PHResponse
import lk.payhere.androidsdk.model.InitRequest
import lk.payhere.androidsdk.model.Item
import lk.payhere.androidsdk.model.StatusResponse
import org.json.JSONObject
import project.superuniqueit.fastbuy.FastBuy
import project.superuniqueit.fastbuy.R
import project.superuniqueit.fastbuy.data.model.FastBuyResult
import project.superuniqueit.fastbuy.data.model.orderoutput.PastOrder
import project.superuniqueit.fastbuy.services.ErrorLog
import project.superuniqueit.fastbuy.services.listeners.OnBackListener
import project.superuniqueit.fastbuy.services.perfrences.AppPrefs
import project.superuniqueit.fastbuy.viewmodels.home.HomeViewModel
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


@AndroidEntryPoint
class MainActivity : FragmentActivity(), View.OnClickListener {

    lateinit var navController: NavController
    lateinit var navView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    private val viewmodel: HomeViewModel by viewModels()
    var errorLog = ErrorLog()

    lateinit var mGoogleSignInClient: GoogleSignInClient
     var account: GoogleSignInAccount? = null
    lateinit var onBackListener: OnBackListener

    lateinit var facebookObject: JSONObject

    lateinit var socialMediaLoginType: String


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

        facebookObject = JSONObject()
        account = null

        val info: PackageInfo
        try {
            info = packageManager.getPackageInfo("project.superuniqueit.fastbuy", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                var md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something: String = String(android.util.Base64.encode(md.digest(), 0))
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something)
                println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx     "+something)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("no such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }


    }

    override fun onResume() {
        super.onResume()
        getShippingMethods()

    }

    override fun onBackPressed() {
        var nav = navController.currentDestination
        when {
            nav?.label?.equals("LastOrder")!! -> {
                onBackListener = FastBuy.getOnBackResponseListener()!!
                onBackListener.onBackListenerResponse(1)
            }
            else -> super.onBackPressed()

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> {
                handleSignInResult(data)
            }
            PAYHERE_REQUEST -> {
                if ((data!!.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) && (data != null)) {
                    val response = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT) as PHResponse<StatusResponse>
                    if (resultCode == Activity.RESULT_OK) {
                        if (response != null) {
                            if (response.isSuccess) {
                                viewmodel.payherePaymentCallBack.value = 5
                            } else {
                                viewmodel.payherePaymentCallBack.value = 10
                                errorAlertDialog("Error", "Payment request not complete,Please try again !!")
                            }

                        } else {
                            viewmodel.payherePaymentCallBack.value = 10
                            errorAlertDialog(
                                "Error",
                                "Payment request not complete,Please try again !!"
                            )
                        }

                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        viewmodel.payherePaymentCallBack.value = 10
                        if (response != null) {
                            errorAlertDialog("Error", "Payment request not complete,Please try again !!")
                        } else {
                            errorAlertDialog(
                                "Error",
                                "Payment request not complete,Please try again !!"
                            )
                        }

                    }
                } else {
                    viewmodel.payherePaymentCallBack.value = 10
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


    private fun performLogin(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->

            facebookObject = `object`!!

            if (`object`.has("email")) {
                var email = `object`.getString("email")
                cl_main.visibility = View.VISIBLE
                checkUser(email)

            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.something_went_wrong),
                    Toast.LENGTH_LONG
                ).show()
            }


        }
        val parameters = Bundle()
        parameters.putString(
            "fields",
            "id, first_name, last_name, email,gender, birthday, location"
        )
        request.parameters = parameters
        request.executeAsync()

    }


    fun facebooklogin() {
        val callbackManager = CallbackManager.Factory.create()
            val loginManager = LoginManager.getInstance()
            loginManager.registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onCancel() {
                        Toast.makeText(this@MainActivity, "Login canceled!", Toast.LENGTH_LONG).show()
                    }

                    override fun onError(error: FacebookException) {
                        Log.e("Login", error.message ?: "Unknown error")
                        Toast.makeText(this@MainActivity, "Login failed with errors!", Toast.LENGTH_LONG).show()
                    }

                    override fun onSuccess(result: LoginResult) {
                        Toast.makeText(this@MainActivity, "Login succeed!", Toast.LENGTH_LONG).show()
                        val accessToken: AccessToken = result.accessToken
                        performLogin(accessToken)
                    }
                })
            LoginManager.getInstance().logIn(this@MainActivity, callbackManager, listOf("email"))

        socialMediaLoginType = "F"
    }

    fun googleSignIn() {
        socialMediaLoginType = "G"
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    private fun getShippingMethods() {
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

    private fun handleSignInResult(data: Intent?) {
        try {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            account = task.result
            checkUser(task.result.email!!)

        } catch (e: ApiException) {
            viewmodel.googleSignProgress.value = false
            errorLog.setError("ApiException "+e.message)
            Toast.makeText(this, "Error in google sign in, Please try again !", Toast.LENGTH_SHORT)
                .show()
        }

    }


/*
    private fun handleSignInResult(data: Intent?) {
        try {
            errorLog.setError("handleSignInResult "+data)
           viewmodel.googleSignProgress.value = true
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        it.result?.let {
                            account = it
                            checkUser(it.email!!)
                        }

                    } else {
                        println("xxxxxxxxxxxxxx "+it.exception?.message)
                        viewmodel.googleSignProgress.value = false
                        Toast.makeText(
                            this,
                            "Error in google sign in, Please try again !",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        } catch (e: ApiException) {
            viewmodel.googleSignProgress.value = false
            errorLog.setError("ApiException "+e.message)
            Toast.makeText(this, "Error in google sign in, Please try again !", Toast.LENGTH_SHORT)
                .show()
        }

    }
*/



    private fun checkUser(email: String) {
        viewmodel.checkCustomer(email, socialMediaLoginType, account, facebookObject)
            .observe(this, Observer {
                when (it) {
                    is FastBuyResult.Success -> {
                        viewmodel.googleSignProgress.value = false
                        viewmodel.googleSign.value = it.data
                        cl_main.visibility = View.GONE
                    }
                    is FastBuyResult.ExceptionError.ExError -> {
                        viewmodel.googleSignProgress.value = false
                        errorAlertDialog("Error", it.exception.toString())
                    }
                    is FastBuyResult.LogicalError.LogError -> {
                        viewmodel.googleSignProgress.value = false
                        errorAlertDialog("Error", it.exception.toString())
                    }

                }
            })
    }


    private fun errorAlertDialog(title: String, message: String) {
        val alertInfoDialog: AlertDialog = this.let {
            val alertInfobuilder: AlertDialog.Builder = AlertDialog.Builder(it)
            alertInfobuilder.apply {
                setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User clicked OK button
                    })
            }
            alertInfobuilder.setMessage(message).setTitle(title)
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

    fun payhereCall(order: PastOrder) {

        val req = InitRequest()
        req.merchantId = getMerchant()
        req.merchantSecret = getMerchantSecret()
        req.currency = "LKR"
        req.amount = order.total.toDouble()
        req.orderId = order.id.toString()



        var itemsDescription = ""
        for (item in order.line_items) {
            itemsDescription = itemsDescription + item.name + "_"
            req.items.add(Item(null, item.name, item.quantity, item.total.toDouble()))
        }
        req.itemsDescription = itemsDescription

        req.custom1 = order.customer_note
        req.customer.firstName = order.billing.first_name
        req.customer.lastName = order.billing.last_name
        req.customer.email = order.billing.email
        req.customer.phone = order.billing.phone
        req.customer.address.address = order.billing.address_1 + " " + order.billing.address_2
        req.customer.address.city = order.billing.city
        req.customer.address.country = "Sri Lanka"

        req.customer.deliveryAddress.address =
            order.shipping.address_1 + " " + order.shipping.address_2
        req.customer.deliveryAddress.city =
            order.shipping.address_1 + " " + order.shipping.address_2
        req.customer.deliveryAddress.country = "Sri Lanka"


        val intent = Intent(this, PHMainActivity::class.java)
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req)
        PHConfigs.setBaseUrl(PHConfigs.LIVE_URL)
        startActivityForResult(intent, PAYHERE_REQUEST)

    }

    external fun getMerchant(): String
    external fun getMerchantSecret(): String

}