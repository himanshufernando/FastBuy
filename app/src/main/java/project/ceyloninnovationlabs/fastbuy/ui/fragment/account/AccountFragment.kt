package project.ceyloninnovationlabs.fastbuy.ui.fragment.account

import android.app.Activity
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_account.*
import project.ceyloninnovationlabs.fastbuy.R
import project.ceyloninnovationlabs.fastbuy.services.perfrences.AppPrefs
import project.ceyloninnovationlabs.fastbuy.ui.activity.MainActivity
import project.ceyloninnovationlabs.fastbuy.viewmodels.home.HomeViewModel


class AccountFragment : Fragment() ,View.OnClickListener {

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
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mainActivity = (activity as MainActivity)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ic_search.setOnClickListener(this)

        setupSearchBar()

    }
    override fun onResume() {
        super.onResume()

        updateCart()
    }

    override fun onStop() {
        super.onStop()

    }


    override fun onClick(v: View) {
        if ((SystemClock.elapsedRealtime() - mLastClickTime < 1500) || cl_account_progress.isVisible) { return }
        mLastClickTime = SystemClock.elapsedRealtime()
        when(v.id){
            R.id.ic_search ->searchProducts()
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