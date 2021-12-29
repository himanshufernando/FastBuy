package project.ceyloninnovationlabs.fastbuy.ui.fragment.warranty

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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.shagi.materialdatepicker.date.DatePickerFragmentDialog
import kotlinx.android.synthetic.main.fragment_contact.appCompatImageView4
import kotlinx.android.synthetic.main.fragment_contact.edit_text_product_search
import kotlinx.android.synthetic.main.fragment_contact.ic_search
import kotlinx.android.synthetic.main.fragment_contact.img_account
import kotlinx.android.synthetic.main.fragment_contact.img_navigation
import kotlinx.android.synthetic.main.fragment_contact.txt_cart_count
import kotlinx.android.synthetic.main.fragment_warranty.*
import kotlinx.android.synthetic.main.fragment_warranty.edit_text_fname
import kotlinx.android.synthetic.main.fragment_warranty.edit_text_lname
import project.ceyloninnovationlabs.fastbuy.R
import project.ceyloninnovationlabs.fastbuy.services.maildroidx.MaildroidX
import project.ceyloninnovationlabs.fastbuy.services.maildroidx.MaildroidXType
import project.ceyloninnovationlabs.fastbuy.services.perfrences.AppPrefs
import project.ceyloninnovationlabs.fastbuy.ui.activity.MainActivity
import project.ceyloninnovationlabs.fastbuy.viewmodels.home.HomeViewModel
import java.util.*


class WarrantyFragment : Fragment(), View.OnClickListener {

    private val viewmodel: HomeViewModel by activityViewModels()
    lateinit var mainActivity: MainActivity
    var appPrefs = AppPrefs
    private var mLastClickTime: Long = 0

    lateinit var alertInfoDialog: AlertDialog
    lateinit var alertInfobuilder: AlertDialog.Builder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_warranty, container, false)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mainActivity = (activity as MainActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ic_search.setOnClickListener(this)
        img_navigation.setOnClickListener(this)
        cl_warranty_cart.setOnClickListener(this)
        img_account.setOnClickListener(this)
        btn_submit.setOnClickListener(this)
        img_calender.setOnClickListener(this)



        setupSearchBar()
        setupInfoAlert()
    }

    override fun onResume() {
        super.onResume()
        updateCart()
    }

    override fun onClick(v: View) {
        if ((SystemClock.elapsedRealtime() - mLastClickTime < 1500)) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (v.id) {
            R.id.ic_search -> searchProducts()
            R.id.cl_warranty_cart -> goToCart()
            R.id.img_account -> NavHostFragment.findNavController(requireParentFragment())
                .navigate(R.id.fragment_warranty_to_account)
            R.id.img_navigation -> mainActivity.openDrawer()
            R.id.img_calender -> setCalenderView()
            R.id.btn_submit -> {
                submitWarranty()
            }
        }

    }

    private fun setupInfoAlert() {
        alertInfoDialog = requireContext()?.let {
            alertInfobuilder = AlertDialog.Builder(it)
            alertInfobuilder.apply {
                setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User clicked OK button
                    })

            }
            alertInfobuilder.create()
        }

    }

    private fun submitWarranty(){
        mainActivity.hideKeyboard()

        when{
            !InternetConnection.checkInternetConnection() -> showToastError("Network", getString(R.string.no_internet), R.color.app_text_red)
            edit_text_fname.text.toString().isNullOrEmpty() -> showToastError("Error", "First name field is required ", R.color.app_text_red)
            edit_text_lname.text.toString().isNullOrEmpty() -> showToastError("Error", "Last name field is required ", R.color.app_text_red)
            edit_text_invoice_num.text.toString().isNullOrEmpty() -> showToastError("Error", "Invoice number field is required ", R.color.app_text_red)
            txt_invoice_date.text.toString().isNullOrEmpty() -> showToastError("Error", "Invoice date field is required ", R.color.app_text_red)
            edit_text_item.text.toString().isNullOrEmpty() -> showToastError("Error", "Item for warranty field is required ", R.color.app_text_red)
            edit_text_serial.text.toString().isNullOrEmpty() -> showToastError("Error", "Serial number field is required ", R.color.app_text_red)
            else ->{

                sendSMS()
            }

        }



    }


    private fun sendSMS(){
        var confrimHTML ="<p>First Name : "+ edit_text_fname.text.toString() +"</p>\n" +
                "<p>Last Name: "+ edit_text_lname.text.toString() +"</p>\n" +
                "<p>Invoice number : "+ edit_text_invoice_num.text.toString() +"</p>\n" +
                "<p>Invoice Date:  "+ txt_invoice_date.text.toString() +"</p>\n" +
                "<p>item:  "+ edit_text_item.text.toString() +"</p>\n" +
                "<p>Serial : "+ edit_text_serial.text.toString() +"</p>"

        MaildroidX.Builder()
            .smtp("mail.fastbuy.lk")
            .smtpUsername("android@fastbuy.lk")
            .smtpPassword("7I7cgcAMKA")
            .port("465")
            .type(MaildroidXType.HTML)
            .body(confrimHTML)
            .to("Info@fastbuy.lk")
            .from("android@fastbuy.lk")
            .subject( "Warranty Registration")
            .onCompleteCallback(object : MaildroidX.onCompleteCallback {
                override val timeout: Long = 10000
                override fun onSuccess() {

                }

                override fun onFail(errorMessage: String) {

                }
            })
            .mail()

        showToastError("Send", "Thanks for contacting us! We will be in touch with you shortly.", R.color.app_blue)

        edit_text_fname.setText("")
        edit_text_lname.setText("")
        edit_text_invoice_num.setText("")
        txt_invoice_date.setText("")
        edit_text_item.setText("")
        edit_text_serial.setText("")
    }


    private fun setCalenderView() {
        val c: Calendar = Calendar.getInstance()
        val dialog = DatePickerFragmentDialog.newInstance({ view, year, monthOfYear, dayOfMonth ->
            var date = "$dayOfMonth/$monthOfYear/$year"
            txt_invoice_date.text = date
        },  c.get(Calendar.YEAR),  c.get(Calendar.MONTH), Calendar.DATE)

        dialog.show(activity?.supportFragmentManager!!, "tag")

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

    private fun goToCart() {
        var cart = appPrefs.getCartItemPrefs()
        if (cart.product.isEmpty()) {
            Toast.makeText(requireContext(), "Your cart is currently empty !!", Toast.LENGTH_SHORT)
                .show()
        } else {
            NavHostFragment.findNavController(requireParentFragment())
                .navigate(R.id.fragment_warranty_to_cart)
        }
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

    private fun searchProducts() {
        mainActivity.hideKeyboard()
        viewmodel.searchQuery.value = edit_text_product_search.text.toString().trim()
        NavHostFragment.findNavController(this).navigate(R.id.fragment_warranty_to_search)

        edit_text_product_search.setText("")
    }

    private fun showToastError(title : String,message : String,typeColor : Int){
        alertInfobuilder?.setMessage(message).setTitle(title)
        alertInfoDialog.show()
    }


}