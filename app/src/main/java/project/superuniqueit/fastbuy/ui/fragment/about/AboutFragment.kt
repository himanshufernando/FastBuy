package project.superuniqueit.fastbuy.ui.fragment.about
import android.app.Activity
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
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.fragment_about.appCompatImageView4
import kotlinx.android.synthetic.main.fragment_about.edit_text_product_search
import kotlinx.android.synthetic.main.fragment_about.ic_search
import kotlinx.android.synthetic.main.fragment_about.img_navigation
import kotlinx.android.synthetic.main.fragment_about.txt_cart_count
import project.superuniqueit.fastbuy.R
import project.superuniqueit.fastbuy.services.perfrences.AppPrefs
import project.superuniqueit.fastbuy.ui.activity.MainActivity
import project.superuniqueit.fastbuy.viewmodels.home.HomeViewModel



class AboutFragment : Fragment(),View.OnClickListener {

    private val viewmodel: HomeViewModel by activityViewModels()
    lateinit var mainActivity: MainActivity
    var appPrefs = AppPrefs
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mainActivity = (activity as MainActivity)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ic_search.setOnClickListener(this)
        img_navigation.setOnClickListener(this)
        cl_about_cart.setOnClickListener(this)
        img_account.setOnClickListener(this)

        setupSearchBar()

       var about = "</p><h2 class=\\\"align-top\\\">What we really do?</h2><p>Super Unique established in the sphere of information technology peripherals with a capital investment of small scale now has gradually grown up in height and weight within a short span of time. Initially setting up in business in the outskirts of the city of Negombo, we have by now established ourselves in the heart of the city and are still expanding. Our dexterous professionals are ever dedicated to their tasks under the dynamic leadership of Mr. Nishantha Fernando who leads by example with his determination of success to overcome all obstacles to steer the organization to the soaring heights in the world of modern information technology. Being providers of customer care services the organization highly values the concepts of our profession bound together with honesty, understanding and prompt attention to the requirements of the clientele to provide high quality service to the entire satisfaction of the client.</p><h2 class=\\\"align-top\\\">Our Vision</h2><p>To entirely commit ourselves to offer quality service to provide solutions to every unsolvable and complicated problems encountered by our clients in their pers<span class=\\\"text_exposed_show\\\">onal and professional run and constantly pursue to achieve our set targets by constantly being on the alert to instantly respond to the resolution of any and all technical and marketing obstacles.</span><h2 class=\\\"align-top\\\">History of Beginning</h2><ul><li>2006 Started Journey Super Unique Communication</li><li>2008 Started Super Unique IT Solutions</li><li>2010 Started www.fastbuy.lk Super Unique IT Solution Ecommerce</li></ul><p>Now We are providing island-wide services"

        webview_about.loadDataWithBaseURL(
            "",
            about,
            "text/html; charset=UTF-8",
            null,
            null
        )

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
            R.id.cl_about_cart ->goToCart()
            R.id.img_account -> NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_about_to_account)
            R.id.img_navigation -> mainActivity.openDrawer()
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
            NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_about_to_cart)
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
        NavHostFragment.findNavController(this).navigate(R.id.fragment_about_to_search)

        edit_text_product_search.setText("")
    }




}