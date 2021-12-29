package project.ceyloninnovationlabs.fastbuy.ui.activity


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


@AndroidEntryPoint
class MainActivity : FragmentActivity(), View.OnClickListener {

    lateinit var navController: NavController
    lateinit var navView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    private val viewmodel: HomeViewModel by viewModels()

    lateinit var onNavigationListener: OnNavigationListener

    private var mLastClickTime: Long = 0

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var account: GoogleSignInAccount
    companion object {
        const val RC_SIGN_IN = 100
         val appPrefs = AppPrefs
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        mGoogleSignInClient.signOut()

      //  googleSignIn()



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
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }

        }

    }


    override fun onClick(v: View) {
        if ((SystemClock.elapsedRealtime() - mLastClickTime < 1500)) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
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
                    println("ccccccccccccccccccccc cl_past")
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



    public fun googleSignIn(){
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


          //  appPrefs.setUserPrefs(userDetails)
         //   viewmodel.googleSign.value = userDetails



        } catch (e: ApiException) {
            Toast.makeText(this, "Error in google sign in, Please try again !", Toast.LENGTH_SHORT).show()
        }
    }

   private fun checkUser(email: String){
       viewmodel.checkCustomer(email).observe(this, Observer {
           when (it) {
               is FastBuyResult.Success -> {
                   if(it.data.isNullOrEmpty()){
                       var userDetails = appPrefs.getUserPrefs()
                       userDetails.email = account.email
                       userDetails.first_name = account.givenName
                       userDetails.last_name = account.familyName
                       userDetails.google_id =  account.id

                       if(account.photoUrl != null){
                           userDetails.picture = account.photoUrl.toString()
                       }

                       addCustomer(userDetails)
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

                       _user.google_id =identifier

                       appPrefs.setUserPrefs(_user)

                       println("cccccccccccccccccccccccccc checkCustomer "+_user)

                       println("cccccccccccccccccccccccccc activity zzzzzzzzzzzzzzzzzzzzz " )
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


    private fun addCustomer(user: User){
        viewmodel.addCustomer(user).observe(this, Observer {
            when (it) {
                is FastBuyResult.Success -> {
                    cl_main.visibility = View.GONE

                    var _user =it.data
                    _user.google_id = account.id

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


}