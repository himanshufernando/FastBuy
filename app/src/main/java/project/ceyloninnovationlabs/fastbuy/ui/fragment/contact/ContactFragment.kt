package project.ceyloninnovationlabs.fastbuy.ui.fragment.contact

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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.android.synthetic.main.fragment_contact.edit_text_product_search
import kotlinx.android.synthetic.main.fragment_contact.ic_search
import kotlinx.android.synthetic.main.fragment_contact.img_account
import kotlinx.android.synthetic.main.fragment_contact.img_navigation
import project.ceyloninnovationlabs.fastbuy.R
import project.ceyloninnovationlabs.fastbuy.services.maildroidx.MaildroidX
import project.ceyloninnovationlabs.fastbuy.services.maildroidx.MaildroidXType
import project.ceyloninnovationlabs.fastbuy.services.perfrences.AppPrefs
import project.ceyloninnovationlabs.fastbuy.ui.activity.MainActivity
import project.ceyloninnovationlabs.fastbuy.viewmodels.home.HomeViewModel



class ContactFragment : Fragment() ,View.OnClickListener{

    private val viewmodel: HomeViewModel by activityViewModels()
    lateinit var mainActivity: MainActivity
    var appPrefs = AppPrefs
    private var mLastClickTime: Long = 0




    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                context as Activity,
                R.raw.new_map_style
            )
        );


        val mapLocation =  LatLng(7.21125,79.840252)
        googleMap.addMarker(MarkerOptions().position(mapLocation).title("Super Unique IT Solutions ( fastbuy.lk )"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapLocation, 17f));


    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mainActivity = (activity as MainActivity)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ic_search.setOnClickListener(this)
        img_navigation.setOnClickListener(this)
        cl_contact_cart.setOnClickListener(this)
        img_account.setOnClickListener(this)
        btn_proceed.setOnClickListener(this)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)


        setupSearchBar()


    }

    override fun onResume() {
        super.onResume()
        updateCart()
    }
    override fun onClick(v: View) {
        if ((SystemClock.elapsedRealtime() - mLastClickTime < 1500) ) { return }
        mLastClickTime = SystemClock.elapsedRealtime()
        when(v.id){
            R.id.ic_search -> searchProducts()
            R.id.cl_contact_cart ->goToCart()
            R.id.img_account -> NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_contact_to_account)
            R.id.img_navigation -> mainActivity.openDrawer()
            R.id.btn_proceed ->sendContact()
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

    private fun goToCart(){
        var cart = appPrefs.getCartItemPrefs()
        if(cart.product.isEmpty()){
            Toast.makeText(requireContext(), "Your cart is currently empty !!", Toast.LENGTH_SHORT).show()
        }else{
            NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_contact_to_cart)
        }
    }

    private fun updateCart(){
        var cartItemQty = 0
        var cart = appPrefs.getCartItemPrefs()

        if(cart.product.isEmpty()){
            appCompatImageView4.visibility = View.GONE
            txt_cart_count.text = "0"
        }else{
            for(item in cart.product){
                cartItemQty += item.quantity
            }

            appCompatImageView4.visibility = View.VISIBLE
            txt_cart_count.text =cartItemQty.toString()
        }

    }

    private fun searchProducts() {
        mainActivity.hideKeyboard()
        viewmodel.searchQuery.value = edit_text_product_search.text.toString().trim()
        NavHostFragment.findNavController(this).navigate(R.id.fragment_contact_to_search)

        edit_text_product_search.setText("")
    }


    private fun sendContact(){
        mainActivity.hideKeyboard()

        when {
            !InternetConnection.checkInternetConnection() -> showToastError("Network", getString(R.string.no_internet), R.color.app_text_red)
            edit_text_fname.text.toString().isNullOrEmpty() -> showToastError("Error", "First name field is required ", R.color.app_text_red)
            edit_text_lname.text.toString().isNullOrEmpty() -> showToastError("Error", "Last name field is required ", R.color.app_text_red)
            edit_text_email.text.toString().isNullOrEmpty() -> showToastError("Error", "Email field is required ", R.color.app_text_red)
            edit_text_comment.text.toString().isNullOrEmpty() -> showToastError("Error", "Comment or Message field is required ", R.color.app_text_red)
            else ->{
                sendContactDetails()
            }

        }

    }


    private fun showToastError(title : String,message : String,typeColor : Int){
        val alertInfoDialog : AlertDialog = requireContext()?.let { alert ->
            val alertInfobuilder:AlertDialog.Builder = AlertDialog.Builder(alert)
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




    fun sendContactDetails() {
       var confrimHTML = "<p>First Name: "+ edit_text_fname.text.toString() +"</p>\n" +
               "<p>Last Name: "+ edit_text_lname.text.toString() +"</p>\n" +
               "<p>Email: <a href=\"mailto:"+ edit_text_email.text.toString() +"\">"+ edit_text_email.text.toString() +"</a></p>\n" +
               "<p>Message: "+ edit_text_comment.text.toString() +"</p>\n" +
               "<p>&nbsp;</p>"

        MaildroidX.Builder()
            .smtp("mail.fastbuy.lk")
            .smtpUsername("android@fastbuy.lk")
            .smtpPassword("7I7cgcAMKA")
            .port("465")
            .type(MaildroidXType.HTML)
            .body(confrimHTML)
            .to("Info@fastbuy.lk")
            .from("android@fastbuy.lk")
            .subject( "Web Contact")
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
        edit_text_email.setText("")
        edit_text_comment.setText("")

    }




}