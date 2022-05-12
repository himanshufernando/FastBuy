package project.ceyloninnovationlabs.fastbuy.ui.fragment.past

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
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import kotlinx.android.synthetic.main.fragment_past.*
import kotlinx.android.synthetic.main.fragment_past.appCompatImageView4
import kotlinx.android.synthetic.main.fragment_past.btn_home_retry_button
import kotlinx.android.synthetic.main.fragment_past.cl_contact_cart
import kotlinx.android.synthetic.main.fragment_past.edit_text_product_search
import kotlinx.android.synthetic.main.fragment_past.ic_search
import kotlinx.android.synthetic.main.fragment_past.img_account
import kotlinx.android.synthetic.main.fragment_past.img_navigation
import kotlinx.android.synthetic.main.fragment_past.txt_cart_count
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import project.ceyloninnovationlabs.fastbuy.R
import project.ceyloninnovationlabs.fastbuy.data.model.FastBuyResult
import project.ceyloninnovationlabs.fastbuy.data.model.orderoutput.PastOrder
import project.ceyloninnovationlabs.fastbuy.data.model.past.Orders
import project.ceyloninnovationlabs.fastbuy.services.perfrences.AppPrefs
import project.ceyloninnovationlabs.fastbuy.ui.activity.MainActivity
import project.ceyloninnovationlabs.fastbuy.ui.adapter.PagingLoadStateAdapter
import project.ceyloninnovationlabs.fastbuy.viewmodels.home.HomeViewModel


class PastFragment : Fragment() ,View.OnClickListener{


    private val viewmodel: HomeViewModel by activityViewModels()
    lateinit var mainActivity: MainActivity
    var appPrefs = AppPrefs
    private var mLastClickTime: Long = 0

    private lateinit var ordersJob: Job
    private val ordersAdapter = PastOrdersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_past, container, false)
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


        initOrdersRecyclerView()
        setupSearchBar()
        getOrders()

    }

    override fun onResume() {
        super.onResume()
        updateCart()
    }


    override fun onClick(p0: View) {
        if ((SystemClock.elapsedRealtime() - mLastClickTime < 1500) ) { return }
        mLastClickTime = SystemClock.elapsedRealtime()
        when(p0.id){
            R.id.ic_search -> searchProducts()
            R.id.cl_contact_cart ->goToCart()
            R.id.img_account -> NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_past_to_account)
            R.id.img_navigation -> mainActivity.openDrawer()

        }
    }
    private fun getOrders() {
        viewmodel.getUser().observe(viewLifecycleOwner, Observer {
            when (it) {
                is FastBuyResult.Success -> {
                    if(it.data.id == 0){
                        val lastorder =  appPrefs.getLastOrderPrefs()
                        if(lastorder.date_created.isNullOrEmpty()){
                            recyclerView_past.visibility = View.GONE
                            txt1.visibility = View.VISIBLE
                        }else{
                            viewmodel.lastOrder.value =lastorder
                            NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_past_to_last)
                        }

                    }else{
                        if (::ordersJob.isInitialized) {
                            ordersJob.cancel()
                        }
                        ordersJob = lifecycleScope.launch {
                            viewmodel.getCustomersOrders(it.data.id).collectLatest { it ->
                                ordersAdapter.submitData(it)
                            }
                        }
                    }

                }
            }

        })

    }

    private fun initOrdersRecyclerView() {

        recyclerView_past.adapter = ordersAdapter
        recyclerView_past.adapter = ordersAdapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter { ordersAdapter.retry() },
            footer = PagingLoadStateAdapter { ordersAdapter.retry() }
        )
        ordersAdapter.addLoadStateListener { loadState ->
            val isListEmpty = loadState.refresh is LoadState.NotLoading && ordersAdapter.itemCount == 0
            if(isListEmpty){
                recyclerView_past.visibility = View.GONE
               txt1.visibility = View.VISIBLE

            }else{
                recyclerView_past.visibility = View.VISIBLE
                txt1.visibility = View.GONE
            }

            if (btn_home_retry_button != null) {
                btn_home_retry_button.isVisible = loadState.source.refresh is LoadState.Error
            }
            if (cl_past_progress != null) {
                cl_past_progress.isVisible = loadState.source.refresh is LoadState.Loading
            }



            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(requireContext(), getString(R.string.failed_to_connect_to_server), Toast.LENGTH_LONG).show()
            }
        }

        btn_home_retry_button.setOnClickListener {
            ordersAdapter.retry()
        }

        ordersAdapter.setOnItemClickListener(object : PastOrdersAdapter.ClickListener {
            override fun onClick(order: Orders, aView: View, position: Int) {
                viewmodel.pastOrder.value = order
                goToOrderDetails()

            }
        })

    }



    private fun searchProducts() {
        mainActivity.hideKeyboard()
        viewmodel.searchQuery.value = edit_text_product_search.text.toString().trim()
        NavHostFragment.findNavController(this).navigate(R.id.fragment_past_to_search)
        edit_text_product_search.setText("")
    }

    private fun goToOrderDetails() =  NavHostFragment.findNavController(this).navigate(R.id.fragment_order_to_orderdetails)

    private fun goToCart(){
        var cart = appPrefs.getCartItemPrefs()
        if(cart.product.isEmpty()){
            Toast.makeText(requireContext(), "Your cart is currently empty !!", Toast.LENGTH_SHORT).show()
        }else{
            NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_past_to_cart)
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

}