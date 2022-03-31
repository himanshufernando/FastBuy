package project.ceyloninnovationlabs.fastbuy.ui.fragment.checkout

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import kotlinx.android.synthetic.main.fragment_check_out.*
import kotlinx.android.synthetic.main.fragment_check_out.recyclerView_cart_items
import project.ceyloninnovationlabs.fastbuy.R
import project.ceyloninnovationlabs.fastbuy.data.model.coupon.Coupon
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_check_out.cl_account
import kotlinx.android.synthetic.main.fragment_check_out.cl_facebook
import kotlinx.android.synthetic.main.fragment_check_out.cl_google
import kotlinx.android.synthetic.main.fragment_check_out.edt_apartment_shipping
import kotlinx.android.synthetic.main.fragment_check_out.edt_city_shipping
import kotlinx.android.synthetic.main.fragment_check_out.edt_company_shipping
import kotlinx.android.synthetic.main.fragment_check_out.edt_fname
import kotlinx.android.synthetic.main.fragment_check_out.edt_fname_shipping
import kotlinx.android.synthetic.main.fragment_check_out.edt_house_number_shipping
import kotlinx.android.synthetic.main.fragment_check_out.edt_lname
import kotlinx.android.synthetic.main.fragment_check_out.edt_lname_shipping
import kotlinx.android.synthetic.main.fragment_check_out.edt_pcode_shipping
import kotlinx.android.synthetic.main.fragment_check_out.img_navigation
import project.ceyloninnovationlabs.fastbuy.data.model.FastBuyResult
import project.ceyloninnovationlabs.fastbuy.data.model.user.User
import project.ceyloninnovationlabs.fastbuy.data.model.orderoutput.Billing
import project.ceyloninnovationlabs.fastbuy.data.model.orderoutput.PastOrder
import project.ceyloninnovationlabs.fastbuy.data.model.orderoutput.Shipping
import project.ceyloninnovationlabs.fastbuy.services.perfrences.AppPrefs
import project.ceyloninnovationlabs.fastbuy.ui.activity.MainActivity
import project.ceyloninnovationlabs.fastbuy.viewmodels.home.HomeViewModel
import retrofit2.HttpException
import java.net.SocketTimeoutException


class CheckOutFragment : Fragment(), View.OnClickListener {

    private val viewmodel: HomeViewModel by activityViewModels()
    lateinit var mainActivity: MainActivity
    var appPrefs = AppPrefs

    var shipToDifferent = false
    var isAgreedToTerms = false

    lateinit var order: PastOrder
    var shippingType = "Flat rate"


    var adapter = CheckoutItemsAdapter()

    companion object {
        val postalCodeList = listOf(
            11500,
            11420,
            11522,
            11524,
            11526,
            11538,
            11244,
            11350,
            11536,
            11558,
            11270,
            11250,
            11264,
            11380,
            61130,
            61192,
            61154,
            11265,
            11320,
            10662,
            11640,
            11260,
            11234,
            11370,
            61138,
            61150,
            61210,
            11400,
            11010,
            11532,
            11300,
            11550,
            61110,
            61144,
            10100,
            10107
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_check_out, container, false)
    }


    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mainActivity = (activity as MainActivity)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        txt_coupon_value.setOnClickListener(this)
        btn_proceed.setOnClickListener(this)
        txt_terms.setOnClickListener(this)
        img_send.setOnClickListener(this)
        cl_google.setOnClickListener(this)
        cl_facebook.setOnClickListener(this)
        img_navigation.setOnClickListener(this)


        edt_pcode.addTextChangedListener(textWatcher)
        edt_pcode_shipping.addTextChangedListener(textWatcher)

        order = appPrefs.getCartItemPrefs()

        setCartInitData()
        setShippingCheckBox()
        setShippingMethodsRadioButton()
        setTermsCheckBox()
        setPaymentRadioBtn()
        setShippingRadioBtn()

        googleSignObserver()
        setSavedUserData()
        payherePaymentCallBack()

    }

    override fun onResume() {
        super.onResume()
        order = appPrefs.getCartItemPrefs()
        setAmount()
    }

    override fun onStop() {
        super.onStop()
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            if (!s.isNullOrEmpty()) {
                var pcode = try {
                    s.toString().toInt()
                } catch (num: NumberFormatException) {
                    0
                }
                calculateShipping(pcode)
            }


        }
    }


    private fun setCartInitData() {
        adapter.submitList(order.product)
        recyclerView_cart_items.adapter = adapter

    }

    private fun setAmount() {


        txt_subtotal.text = "Rs. " + String.format("%.2f", order.subtotal)


        var tot = order.total.toDouble() + order.shippingCost
        txt_total.text = "Rs. " + String.format("%.2f", tot)


        when (order.coupon.discount_type) {
            "fixed_cart" -> {

                txt_coupon.visibility = View.VISIBLE
                txt_coupon_value.visibility = View.VISIBLE
                view_coupon.visibility = View.VISIBLE
                txt_coupon_value.text = "-Rs." + order.coupon.amount + "[ Remove ]"


            }
            "percent" -> {
                txt_coupon.visibility = View.VISIBLE
                txt_coupon_value.visibility = View.VISIBLE
                view_coupon.visibility = View.VISIBLE
                txt_coupon_value.text = "-(" + order.coupon.amount + " %) [ Remove ]"


            }
            "fixed_product" -> {
                txt_coupon.visibility = View.GONE
                txt_coupon_value.visibility = View.GONE
                view_coupon.visibility = View.GONE

            }
            else -> {
                txt_coupon.visibility = View.GONE
                txt_coupon_value.visibility = View.GONE
                view_coupon.visibility = View.GONE
            }

        }

    }

    private fun setShippingMethodsRadioButton() {

        radioButton_free.setOnCheckedChangeListener { buttonView, isChecked ->
            mainActivity.hideKeyboard()
            if (isChecked) {
                cl_pickup.visibility = View.GONE
                radioGroup_payment.visibility = View.VISIBLE
                shippingType = "Free shipping"
                order.paymentType = ""
            } else {
                cl_pickup.visibility = View.VISIBLE
                radioGroup_payment.visibility = View.GONE
                shippingType = "Local pickup"

                order.paymentType = "cop"
            }

        }

        radio_pickup.setOnCheckedChangeListener { buttonView, isChecked ->
            mainActivity.hideKeyboard()
            if (isChecked) {
                cl_pickup.visibility = View.VISIBLE
                radioGroup_payment.visibility = View.GONE
                shippingType = "Local pickup"

                order.paymentType = "cop"
            } else {
                cl_pickup.visibility = View.GONE
                radioGroup_payment.visibility = View.VISIBLE
                shippingType = "Free shipping"
                order.paymentType = ""

            }

        }
    }


    private fun calculateShipping(postcode: Int) {
        if (postalCodeList.contains(postcode)) {
            // if (postcode == 11500) {
            order.shippingCost = 0.0

            radioGroup_shipping.visibility = View.VISIBLE
            cl_flat_rate.visibility = View.GONE
            radioButton_free.isChecked = true


            radioButton_cashon.visibility = View.VISIBLE
            txt_cashon.visibility = View.VISIBLE

            shippingType = "Free shipping"

        } else {
            var shipping = 0.0
            shippingType = "Flat rate"
            radioGroup_shipping.visibility = View.GONE
            cl_flat_rate.visibility = View.VISIBLE

            radioButton_cashon.visibility = View.GONE
            txt_cashon.visibility = View.GONE

            for (item in order.product) {
                when (item.shipping_class_id) {
                    3593 -> {
                        viewmodel.shippingMethods.value.let {
                            shipping += it?.settings?.class_cost_3593?.value?.toDouble()!!
                        }
                    }
                    3592 -> {
                        viewmodel.shippingMethods.value.let {
                            shipping += it?.settings?.class_cost_3592?.value?.toDouble()!!
                        }
                    }
                    3591 -> {
                        viewmodel.shippingMethods.value.let {
                            shipping += it?.settings?.class_cost_3591?.value?.toDouble()!!
                        }
                    }
                    else -> {
                        shipping += 299.00

                    }
                }
            }
            order.shippingCost = shipping
            appPrefs.setCartItemPrefs(order)
            txt_flat_rate.text = "Rs. " + order.shippingCost.toString()

        }

        setAmount()

    }

    private fun setSavedUserData() {
        var _user = appPrefs.getUserPrefs()
        if (_user.facebook_id.isNotEmpty() || _user.google_id.isNotEmpty()) {
            cl_account.visibility = View.GONE

            edt_fname.setText(_user.first_name)
            edt_lname.setText(_user.last_name)
            edt_email.setText(_user.email)

        } else {
            cl_account.visibility = View.VISIBLE

            edt_fname.setText(_user.billing.first_name)
            edt_lname.setText(_user.billing.last_name)
            edt_email.setText(_user.billing.email)

        }

        edt_company.setText(_user.billing.company)
        edt_house_number.setText(_user.billing.address_1)
        edt_apartment.setText(_user.billing.address_2)
        edt_city.setText(_user.billing.city)
        edt_pcode.setText(_user.billing.postcode)

        if (validatePhoneNumber(_user.billing.phone)) {
            edt_phone.setText(_user.billing.phone)
        }


    }

    private fun googleSignObserver() {


        val signObserver = Observer<User> { _user ->


            if (_user.email.isNotEmpty()) {
                cl_account.visibility = View.GONE
                edt_email.isEnabled = false
            } else {
                cl_account.visibility = View.VISIBLE
                edt_email.isEnabled = true
            }



            edt_fname.setText(_user.first_name)
            edt_lname.setText(_user.last_name)
            edt_email.setText(_user.email)

            if (edt_company.text.toString().isNullOrEmpty()) {
                edt_company.setText(_user.billing.company)
            }

            if (edt_house_number.text.toString().isNullOrEmpty()) {
                edt_house_number.setText(_user.billing.address_1)
            }
            if (edt_apartment.text.toString().isNullOrEmpty()) {
                edt_apartment.setText(_user.billing.address_2)
            }

            if (edt_city.text.toString().isNullOrEmpty()) {
                edt_city.setText(_user.billing.city)
            }
            if (edt_pcode.text.toString().isNullOrEmpty()) {
                edt_pcode.setText(_user.billing.postcode)
            }

            if (edt_phone.text.toString().isNullOrEmpty()) {
                if (validatePhoneNumber(_user.billing.phone)) {
                    edt_phone.setText(_user.billing.phone)
                }

            }

            if (shipToDifferent) {
                if (edt_company_shipping.text.toString().isNullOrEmpty()) {
                    edt_company_shipping.setText(_user.shipping.company)
                }

                if (edt_house_number_shipping.text.toString().isNullOrEmpty()) {
                    edt_house_number_shipping.setText(_user.shipping.address_1)
                }
                if (edt_apartment_shipping.text.toString().isNullOrEmpty()) {
                    edt_apartment_shipping.setText(_user.shipping.address_2)
                }

                if (edt_city_shipping.text.toString().isNullOrEmpty()) {
                    edt_city_shipping.setText(_user.shipping.city)
                }
                if (edt_pcode_shipping.text.toString().isNullOrEmpty()) {
                    edt_pcode_shipping.setText(_user.shipping.postcode)
                }
            }


        }
        viewmodel.googleSign.observe(viewLifecycleOwner, signObserver)
    }


    private fun setTermsCheckBox() {
        cb_terms.setOnCheckedChangeListener { _, isChecked ->
            mainActivity.hideKeyboard()
            order.isAgreedToTerms = isChecked
        }

    }


    private fun setPaymentRadioBtn() {
        radioGroup_payment.setOnCheckedChangeListener { group, checkedId ->
            mainActivity.hideKeyboard()
            when (checkedId) {
                R.id.radioButton_bank_transfer -> {
                    order.paymentType = "bacs"
                    order.paymentGatewayValue = 0.0
                    order.cashOnDeliveryValue = 0.0
                    order.finaltotal = order.total.toDouble()
                    txt_total.text = "Rs. " + String.format("%.2f", order.total.toDouble())

                }
                R.id.radioButton_cashon -> {
                    order.paymentType = "cod"
                    order.paymentGatewayValue = 0.0
                    order.cashOnDeliveryValue = 399.00
                    order.finaltotal = order.total.toDouble() + 399.00
                    txt_total.text =
                        "Rs. " + String.format("%.2f", (order.total.toDouble() + 399.00))
                }
                R.id.radioButton_payhere -> {
                    order.paymentType = "payhere"
                    var payhereVal = (order.total.toDouble() * 2) / 100.00
                    order.paymentGatewayValue = payhereVal
                    order.cashOnDeliveryValue = 0.0
                    order.finaltotal = order.total.toDouble() + order.paymentGatewayValue
                    txt_total.text = "Rs. " + String.format(
                        "%.2f",
                        (order.total.toDouble() + order.paymentGatewayValue)
                    )
                }
                else -> ""
            }

        }


    }


    private fun setShippingRadioBtn() {
        radioGroup_shipping.setOnCheckedChangeListener { group, checkedId ->
            mainActivity.hideKeyboard()
            order.shippingType = when (checkedId) {
                R.id.radioButton_free -> "Free shipping"
                R.id.radio_pickup -> "Local pickup"
                else -> ""
            }
        }
    }


    private fun setShippingCheckBox() {
        cb_ship.setOnCheckedChangeListener { _, isChecked ->
            mainActivity.hideKeyboard()
            shipToDifferent = isChecked
            if (isChecked) {
                cl_shipping_details.visibility = View.VISIBLE
                var _user = appPrefs.getUserPrefs()

                if (edt_company_shipping.text.toString().isNullOrEmpty()) {
                    edt_company_shipping.setText(_user.shipping.company)
                }

                if (edt_house_number_shipping.text.toString().isNullOrEmpty()) {
                    edt_house_number_shipping.setText(_user.shipping.address_1)
                }
                if (edt_apartment_shipping.text.toString().isNullOrEmpty()) {
                    edt_apartment_shipping.setText(_user.shipping.address_2)
                }

                if (edt_city_shipping.text.toString().isNullOrEmpty()) {
                    edt_city_shipping.setText(_user.shipping.city)
                }
                if (edt_pcode_shipping.text.toString().isNullOrEmpty()) {
                    edt_pcode_shipping.setText(_user.shipping.postcode)
                }

            } else {
                cl_shipping_details.visibility = View.GONE
                edt_company_shipping.setText("")
                edt_house_number_shipping.setText("")
                edt_apartment_shipping.setText("")
                edt_city_shipping.setText("")
                edt_pcode_shipping.setText("")

            }
        }

    }

    private fun proceed() {

        var _billing = Billing().apply {
            first_name = edt_fname.text.toString()
            last_name = edt_lname.text.toString()
            company = edt_company.text.toString()
            address_1 = edt_house_number.text.toString()
            address_2 = edt_apartment.text.toString()
            city = edt_city.text.toString()
            postcode = edt_pcode.text.toString()
            email = edt_email.text.toString()
            phone = edt_phone.text.toString().trim()
        }

        order.billing = _billing

        lateinit var _shipping: Shipping
        if (shipToDifferent) {
            _shipping = Shipping().apply {
                first_name = edt_fname_shipping.text.toString()
                last_name = edt_lname_shipping.text.toString()
                company = edt_company_shipping.text.toString()
                address_1 = edt_house_number_shipping.text.toString()
                address_2 = edt_apartment_shipping.text.toString()
                city = edt_city_shipping.text.toString()
                postcode = edt_pcode_shipping.text.toString().trim()
            }
        } else {
            _shipping = Shipping().apply {
                first_name = edt_fname.text.toString()
                last_name = edt_lname.text.toString()
                last_name = edt_lname.text.toString()
                company = edt_company.text.toString()
                address_1 = edt_house_number.text.toString()
                address_2 = edt_apartment.text.toString()
                city = edt_city.text.toString()
                postcode = edt_pcode.text.toString().trim()
                phone = edt_phone.text.toString().trim()
            }
        }

        order.shipping = _shipping
        order.shippingType = shippingType

        cl_cart_progress.visibility = View.VISIBLE
        viewmodel.newOrder(order).observe(viewLifecycleOwner, Observer {
            when (it) {
                is FastBuyResult.Success -> {
                    viewmodel.lastOrder.value = it.data
                    if (it.data.payment_method == "payhere") {
                        mainActivity.payhereCall(it.data)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Your order successfully pleased",
                            Toast.LENGTH_SHORT
                        ).show()

                        appPrefs.setLastOrderPrefs(it.data)
                        appPrefs.setCartItemPrefs(PastOrder())

                        NavHostFragment.findNavController(requireParentFragment())
                            .navigate(R.id.fragment_checkout_to_last)

                    }

                }
                is FastBuyResult.ExceptionError.ExError -> {
                    cl_cart_progress.visibility = View.GONE
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
                    cl_cart_progress.visibility = View.GONE
                    showToastError(
                        "Error",
                        it.exception.message,
                        R.color.app_text_red
                    )
                }
            }
        })

    }

    private fun payherePaymentCallBack() {
        viewmodel.payherePaymentCallBack.observe(viewLifecycleOwner, Observer {callback ->
            cl_cart_progress.visibility = View.VISIBLE
            viewmodel.lastOrder.value?.id?.let { lastorder ->
                viewmodel.updateOrder(lastorder, callback).observe(viewLifecycleOwner, Observer {
                    when (it) {
                        is FastBuyResult.Success -> {
                            cl_cart_progress.visibility = View.GONE
                            if(callback == 5){
                                Toast.makeText(
                                    requireContext(),
                                    "Your order successfully pleased",
                                    Toast.LENGTH_SHORT
                                ).show()

                                appPrefs.setLastOrderPrefs(it.data)
                                appPrefs.setCartItemPrefs(PastOrder())

                                NavHostFragment.findNavController(requireParentFragment())
                                    .navigate(R.id.fragment_checkout_to_last)
                            }



                        }
                        is FastBuyResult.ExceptionError.ExError -> {
                            cl_cart_progress.visibility = View.GONE
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
                            cl_cart_progress.visibility = View.GONE
                            showToastError(
                                "Error",
                                it.exception.message,
                                R.color.app_text_red
                            )
                        }
                    }

                })
            }
        })

    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_navigation -> mainActivity.openDrawer()
            R.id.cl_google -> mainActivity.googleSignIn()
            R.id.cl_facebook -> mainActivity.facebooklogin()
            R.id.txt_coupon_value -> {
                order.coupon = Coupon()
                order.total = order.subtotal.toString()
                appPrefs.setCartItemPrefs(order)
                setAmount()
            }
            R.id.btn_proceed -> {
                proceed()
            }

            R.id.img_send -> {
                proceed()
            }

            R.id.txt_terms -> {
                if (!InternetConnection.checkInternetConnection()) {
                    showToastError(
                        "Network Error",
                        resources.getString(R.string.no_internet),
                        R.color.app_text_red
                    )
                } else {
                    val url = "https://www.fastbuy.lk/terms-and-conditions/"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    mainActivity.startActivity(i)
                }
            }
        }
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

    fun validatePhoneNumber(phone: String): Boolean {
        if (phone.length != 10) {
            return false
        }

        return phone.matches("-?\\d+(\\.\\d+)?".toRegex())


    }

}

