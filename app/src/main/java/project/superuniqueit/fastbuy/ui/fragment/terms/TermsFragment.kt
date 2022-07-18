package project.superuniqueit.fastbuy.ui.fragment.terms

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
import androidx.lifecycle.Observer
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_terms.*
import kotlinx.android.synthetic.main.fragment_terms.appCompatImageView4
import kotlinx.android.synthetic.main.fragment_terms.edit_text_product_search
import kotlinx.android.synthetic.main.fragment_terms.ic_search
import kotlinx.android.synthetic.main.fragment_terms.img_account
import kotlinx.android.synthetic.main.fragment_terms.img_navigation
import kotlinx.android.synthetic.main.fragment_terms.txt_cart_count
import project.superuniqueit.fastbuy.R
import project.superuniqueit.fastbuy.data.model.FastBuyResult
import project.superuniqueit.fastbuy.services.perfrences.AppPrefs
import project.superuniqueit.fastbuy.ui.activity.MainActivity
import project.superuniqueit.fastbuy.viewmodels.home.HomeViewModel


class TermsFragment : Fragment() ,View.OnClickListener{
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
        return inflater.inflate(R.layout.fragment_terms, container, false)
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

        setupSearchBar()
        getTermPage()




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
            R.id.img_account -> NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_terms_to_account)
            R.id.img_navigation -> mainActivity.openDrawer()
        }

    }

    private fun getTermPage() {
        if(!viewmodel.termsAndCondition.value.isNullOrEmpty()){
            webview_terms.loadDataWithBaseURL(
                "",
                viewmodel.termsAndCondition.value!!,
                "text/html; charset=UTF-8",
                null,
                null
            )

        }else {
            cl_term_progress.visibility = View.VISIBLE
            viewmodel.getPages(2183).observe(viewLifecycleOwner, Observer {
                when (it) {
                    is FastBuyResult.Success -> {

                        viewmodel.termsAndCondition.value = it.data.content.rendered
                        webview_terms.loadDataWithBaseURL(
                            "",
                            it.data.content.rendered,
                            "text/html; charset=UTF-8",
                            null,
                            null
                        )
                        cl_term_progress.visibility = View.GONE
                    }
                    is FastBuyResult.ExceptionError.ExError -> cl_term_progress.visibility = View.GONE
                    is FastBuyResult.LogicalError.LogError ->  cl_term_progress.visibility = View.GONE

                }


            })

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
            NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_terms_to_cart)
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
        NavHostFragment.findNavController(this).navigate(R.id.fragment_terms_to_search)

        edit_text_product_search.setText("")
    }
}