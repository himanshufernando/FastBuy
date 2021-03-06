package project.superuniqueit.fastbuy.ui.fragment.account

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.appCompatImageView4
import kotlinx.android.synthetic.main.fragment_account.cl_account
import kotlinx.android.synthetic.main.fragment_account.cl_google
import kotlinx.android.synthetic.main.fragment_account.edit_text_product_search
import kotlinx.android.synthetic.main.fragment_account.edt_apartment_shipping
import kotlinx.android.synthetic.main.fragment_account.edt_city_shipping
import kotlinx.android.synthetic.main.fragment_account.edt_company_shipping
import kotlinx.android.synthetic.main.fragment_account.edt_fname
import kotlinx.android.synthetic.main.fragment_account.edt_fname_shipping
import kotlinx.android.synthetic.main.fragment_account.edt_house_number_shipping
import kotlinx.android.synthetic.main.fragment_account.edt_lname
import kotlinx.android.synthetic.main.fragment_account.edt_lname_shipping
import kotlinx.android.synthetic.main.fragment_account.edt_pcode_shipping
import kotlinx.android.synthetic.main.fragment_account.ic_search
import kotlinx.android.synthetic.main.fragment_account.img_navigation
import kotlinx.android.synthetic.main.fragment_account.txt_cart_count
import project.superuniqueit.fastbuy.R
import project.superuniqueit.fastbuy.data.model.FastBuyResult
import project.superuniqueit.fastbuy.data.model.orderoutput.Billing
import project.superuniqueit.fastbuy.data.model.orderoutput.Shipping
import project.superuniqueit.fastbuy.data.model.user.User
import project.superuniqueit.fastbuy.services.perfrences.AppPrefs
import project.superuniqueit.fastbuy.ui.activity.MainActivity
import project.superuniqueit.fastbuy.viewmodels.home.HomeViewModel
import retrofit2.HttpException
import java.net.SocketTimeoutException


class AccountFragment : Fragment(), View.OnClickListener {

    private val viewmodel: HomeViewModel by activityViewModels()
    lateinit var mainActivity: MainActivity
    var appPrefs = AppPrefs

    private var mLastClickTime: Long = 0
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mainActivity = (activity as MainActivity)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ic_search.setOnClickListener(this)
        cl_google.setOnClickListener(this)
        btn_update.setOnClickListener(this)
        img_navigation.setOnClickListener(this)
        cl_account_cart.setOnClickListener(this)
        cl_facebook.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        btn_user_logout.setOnClickListener(this)

        setupSearchBar()
        googleSignObserver()
        googleSignProgressObserver()


    }

    override fun onResume() {
        super.onResume()
        updateCart()
        getUser()

    }

    private fun deleteUser() {
        viewmodel.deleteUser().observe(viewLifecycleOwner, Observer {
            when (it) {
                is FastBuyResult.Success -> {
                    getUser()
                }
                is FastBuyResult.ExceptionError -> {
                    showToastError(
                        "Network Error",
                        resources.getString(R.string.something_went_wrong),
                        R.color.app_text_red
                    )
                }
            }

        })

    }


    private fun getUser() {
        viewmodel.getUser().observe(viewLifecycleOwner, Observer {
            when (it) {
                is FastBuyResult.Success -> {
                    if(it.data.email.isNullOrEmpty()){
                        cl_login.visibility = View.VISIBLE
                        cl_account.visibility = View.GONE
                    }else{
                        user = it.data
                        cl_login.visibility = View.GONE
                        cl_account.visibility = View.VISIBLE
                        setAccountDetails(it.data)
                    }
                }
                is FastBuyResult.ExceptionError -> {
                    cl_login.visibility = View.VISIBLE
                    cl_account.visibility = View.GONE
                }
            }

        })

    }


    override fun onStop() {
        super.onStop()
    }


    override fun onClick(v: View) {
        if ((SystemClock.elapsedRealtime() - mLastClickTime < 1000) || cl_account_progress.isVisible) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (v.id) {
            R.id.ic_search -> searchProducts()
            R.id.cl_google -> mainActivity.googleSignIn()
            R.id.btn_update -> update()
            R.id.img_navigation -> mainActivity.onBackPressed()
            R.id.cl_account_cart -> goToCart()
            R.id.cl_facebook -> mainActivity.facebooklogin()
            R.id.btn_login -> userLogin()
            R.id.btn_user_logout -> deleteUser()
        }
    }

    private fun userLogin() {
        cl_account_progress.visibility = View.VISIBLE
        var user = User().apply {
            email = edt_username.text.toString()
            password = edt_password.text.toString()
        }

        viewmodel.userLogin(user).observe(viewLifecycleOwner, Observer {
            when (it) {
                is FastBuyResult.Success -> {
                    if (it.data.success) {
                        getCustomer(user.email)
                    } else {
                        showToastError("Error", it.data.data.message, R.color.app_text_red)
                    }
                }
                is FastBuyResult.ExceptionError.ExError -> {
                    cl_account_progress.visibility = View.GONE
                    when (it.exception) {
                        is HttpException -> {
                            showToastError(
                                "Error",
                                " Wrong user credentials",
                                R.color.app_text_red
                            )
                        }
                        is SocketTimeoutException -> {
                            showToastError(
                                "Network Error",
                                resources.getString(R.string.timeout),
                                R.color.app_text_red
                            )
                        }
                        else -> {
                            showToastError(
                                "Network Error",
                                resources.getString(R.string.something_went_wrong),
                                R.color.app_text_red
                            )
                        }
                    }
                }
                is FastBuyResult.LogicalError.LogError -> {
                    cl_account_progress.visibility = View.GONE
                    showToastError("Error", it.exception.message, R.color.app_text_red)
                }
            }
        })
    }

    private fun getCustomer(email: String) {
        viewmodel.getCustomer(email).observe(viewLifecycleOwner, Observer {
            when (it) {
                is FastBuyResult.Success -> {
                    cl_account_progress.visibility = View.GONE
                    user = it.data
                    cl_login.visibility = View.GONE
                    cl_account.visibility = View.VISIBLE
                    setAccountDetails(it.data)
                }
                is FastBuyResult.ExceptionError.ExError -> {
                    cl_account_progress.visibility = View.GONE
                    when (it.exception) {
                        is HttpException -> {
                            showToastError(
                                "Error",
                                " Wrong user credentials",
                                R.color.app_text_red
                            )
                        }
                        is SocketTimeoutException -> {
                            showToastError(
                                "Network Error",
                                resources.getString(R.string.timeout),
                                R.color.app_text_red
                            )
                        }
                        else -> {
                            showToastError(
                                "Network Error",
                                resources.getString(R.string.something_went_wrong),
                                R.color.app_text_red
                            )
                        }
                    }
                }
                is FastBuyResult.LogicalError.LogError -> {
                    cl_account_progress.visibility = View.GONE
                    showToastError("Error", it.exception.message, R.color.app_text_red)
                }
            }
        })
    }


    private fun goToCart() {
        var cart = appPrefs.getCartItemPrefs()
        if (cart.product.isEmpty()) {
            Toast.makeText(requireContext(), "Your cart is currently empty !!", Toast.LENGTH_SHORT)
                .show()
        } else {
            NavHostFragment.findNavController(requireParentFragment())
                .navigate(R.id.fragment_account_to_cart)
        }
    }

    private fun googleSignProgressObserver() {
        viewmodel.googleSignProgress.observe(viewLifecycleOwner, Observer {
            cl_account_progress.isVisible = it
        })
    }

    private fun googleSignObserver() {

        viewmodel.googleSign.observe(viewLifecycleOwner, Observer {

            if (it.facebook_id.isNotEmpty() || it.google_id.isNotEmpty()) {
                user = it
                cl_login.visibility = View.GONE
                cl_account.visibility = View.VISIBLE
                setAccountDetails(it)
            } else {
                cl_login.visibility = View.VISIBLE
                cl_account.visibility = View.GONE
            }

        })

    }


    private fun update() {

        cl_account_progress.visibility = View.VISIBLE

        var _billing = Billing().apply {
            first_name = edt_fname_billing.text.toString()
            last_name = edt_lname_billing.text.toString()
            company = edt_company_billing.text.toString()
            address_1 = edt_house_number_billing.text.toString()
            address_2 = edt_apartment_billing.text.toString()
            city = edt_city_billing.text.toString()
            postcode = edt_pcode_billing.text.toString()
            email = edt_email_billing.text.toString()
            phone = edt_phone_billing.text.toString()
        }

        var _shipping = Shipping().apply {
            first_name = edt_fname_shipping.text.toString()
            last_name = edt_lname_shipping.text.toString()
            company = edt_company_shipping.text.toString()
            address_1 = edt_house_number_shipping.text.toString()
            address_2 = edt_apartment_shipping.text.toString()
            city = edt_city_shipping.text.toString()
            postcode = edt_pcode_shipping.text.toString()
        }

        var updateUser = User().apply {
            id = user.id
            first_name = edt_fname.text.toString()
            last_name = edt_lname.text.toString()
            billing = _billing
            shipping = _shipping
        }



        viewmodel.updateCustomer(updateUser).observe(viewLifecycleOwner, Observer {
            when (it) {
                is FastBuyResult.Success -> {
                    cl_account_progress.visibility = View.GONE
                    if (it.data.status) {
                        println("xxxxxxxxxxxxxxxx 01 : "+ it.data.message)
                        showToastError("Error", it.data.message, R.color.app_text_red)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Your account update successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is FastBuyResult.ExceptionError.ExError -> {
                    cl_account_progress.visibility = View.GONE
                    println("xxxxxxxxxxxxxxxx 02 : "+it.exception)
                    when (it.exception) {
                        is HttpException -> {
                            showToastError(
                                "Network Error",
                                resources.getString(R.string.network_failed),
                                R.color.app_text_red
                            )
                        }
                        is SocketTimeoutException -> {
                            showToastError(
                                "Network Error",
                                resources.getString(R.string.timeout),
                                R.color.app_text_red
                            )
                        }
                        else -> {
                            showToastError(
                                "Network Error",
                                resources.getString(R.string.something_went_wrong),
                                R.color.app_text_red
                            )
                        }
                    }
                }
                is FastBuyResult.LogicalError.LogError -> {
                    cl_account_progress.visibility = View.GONE
                    println("xxxxxxxxxxxxxxxx 03 : "+it.exception)
                    showToastError(
                        "Error",
                        it.exception.message,
                        R.color.app_text_red
                    )
                }
            }


        })


    }


    private fun showToastError(title: String, message: String, typeColor: Int) {

        val alertInfoDialog: AlertDialog = requireContext()?.let { alert ->
            val alertInfobuilder: AlertDialog.Builder = AlertDialog.Builder(alert)
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

    private fun setAccountDetails(_user: User) {

        edt_fname.setText(_user.first_name)
        edt_lname.setText(_user.last_name)
        edt_email_address.setText(_user.email)

        if (_user.google_id.isNotEmpty()) {
            cl_social_google.visibility = View.VISIBLE
            cl_social_facebook.visibility = View.GONE
        }

        if (_user.facebook_id.isNotEmpty()) {
            cl_social_google.visibility = View.GONE
            cl_social_facebook.visibility = View.VISIBLE

        }

        edt_fname_billing.setText(_user.billing.first_name)
        edt_lname_billing.setText(_user.billing.last_name)
        edt_email_billing.setText(_user.billing.email)
        edt_company_billing.setText(_user.billing.company)
        edt_house_number_billing.setText(_user.billing.address_1)
        edt_apartment_billing.setText(_user.billing.address_2)
        edt_city_billing.setText(_user.billing.city)
        edt_pcode_billing.setText(_user.billing.postcode)
        edt_phone_billing.setText(_user.billing.phone)


        edt_fname_shipping.setText(_user.billing.first_name)
        edt_lname_shipping.setText(_user.billing.last_name)
        edt_company_shipping.setText(_user.billing.company)
        edt_house_number_shipping.setText(_user.billing.address_1)
        edt_apartment_shipping.setText(_user.billing.address_2)
        edt_city_shipping.setText(_user.billing.city)
        edt_pcode_shipping.setText(_user.billing.postcode)


    }


    private fun updateCart() {
        var cartItemQty = 0
        var cart = appPrefs.getCartItemPrefs()

        if (cart.product.isEmpty()) {
            appCompatImageView4.visibility = View.GONE
            txt_cart_count.text = "0"
        } else {
            for (item in cart.product) {
                cartItemQty += item.quantity
            }
            appCompatImageView4.visibility = View.VISIBLE
            txt_cart_count.text = cartItemQty.toString()
        }

    }

    private fun setupSearchBar() {
        edit_text_product_search.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mainActivity.hideKeyboard()
                searchProducts()
                return@OnEditorActionListener true
            }
            false
        })
    }


    private fun searchProducts() {
        mainActivity.hideKeyboard()
        viewmodel.searchQuery.value = edit_text_product_search.text.toString().trim()
        NavHostFragment.findNavController(this).navigate(R.id.fragment_account_to_search)

        edit_text_product_search.setText("")
    }
}