package project.ceyloninnovationlabs.fastbuy.ui.fragment.search

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
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_product_search.*
import kotlinx.android.synthetic.main.fragment_product_search.cl_home_progress
import kotlinx.android.synthetic.main.fragment_product_search.edit_text_product_search
import kotlinx.android.synthetic.main.fragment_product_search.ic_search
import kotlinx.android.synthetic.main.fragment_product_search.img_account
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import project.ceyloninnovationlabs.fastbuy.R
import project.ceyloninnovationlabs.fastbuy.data.model.product.Product
import project.ceyloninnovationlabs.fastbuy.services.perfrences.AppPrefs
import project.ceyloninnovationlabs.fastbuy.ui.activity.MainActivity
import project.ceyloninnovationlabs.fastbuy.ui.adapter.PagingLoadStateAdapter
import project.ceyloninnovationlabs.fastbuy.ui.dialog.InfoDialog
import project.ceyloninnovationlabs.fastbuy.ui.fragment.home.ProductItemDecoration
import project.ceyloninnovationlabs.fastbuy.viewmodels.home.HomeViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProductSearchFragment : Fragment(), InfoDialog.InfoDialogListener, View.OnClickListener {


    private val viewmodel: HomeViewModel by activityViewModels()

    lateinit var mainActivity: MainActivity


    private lateinit var searchJob: Job
    private val searchAdapter = SearchProductAdapter()
    var appPrefs = AppPrefs
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_search, container, false)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mainActivity = (activity as MainActivity)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.lockDrawer()


        ic_search.setOnClickListener(this)
        img_back.setOnClickListener(this)
        cl_search_cart.setOnClickListener(this)
        img_account.setOnClickListener(this)

        initSearchRecyclerView()


        if (viewmodel.moreProductStatus.value == 0) {
            if(!viewmodel.searchQuery.value.isNullOrEmpty()){
                edit_text_product_search.setText(viewmodel.searchQuery.value)
            }
            searchProducts()

        } else {
            moreProducts()
        }


    }

    override fun onResume() {
        super.onResume()

        searchProducts()
    }

    override fun onStop() {
        super.onStop()

        searchJob.cancel()
    }

    override fun onInfoDialogPositive(code: Int) {

    }

    override fun onClick(v: View) {
        if ((SystemClock.elapsedRealtime() - mLastClickTime < 1500) || cl_home_progress.isVisible) { return }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (v.id) {
            R.id.img_back -> mainActivity.onBackPressed()
            R.id.ic_search -> searchProducts()
            R.id.cl_search_cart -> goToCart()
            R.id.img_account -> NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_search_to_account)
        }

    }


    private fun goToCart(){
        var cart = appPrefs.getCartItemPrefs()
        if(cart.product.isEmpty()){
            Toast.makeText(requireContext(), "Your cart is currently empty !!", Toast.LENGTH_SHORT).show()
        }else{
            NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_search_to_cart)
        }
    }

    private fun productDetailsView(product: Product) {
        if (product == null) {
            Toast.makeText(
                requireContext(),
                "Please select the product again !!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (product.id == 0) {
            Toast.makeText(
                requireContext(),
                "Please select the product again !!",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        cl_home_progress.visibility = View.VISIBLE
        val bundle = bundleOf(Pair("product", product))
        NavHostFragment.findNavController(requireParentFragment())
            .navigate(R.id.fragment_search_to_details, bundle)
    }


    private fun searchProducts() {
        mainActivity.hideKeyboard()
        searchJob = lifecycleScope.launch {
            viewmodel.searchProducts(edit_text_product_search.text.toString().trim())
                .collectLatest {
                    searchAdapter.submitData(it)
                }
        }
    }


    private fun moreProducts() {
        searchJob = lifecycleScope.launch {
            viewmodel.moreProducts(viewmodel.moreProductStatus.value!!).collectLatest {
                searchAdapter.submitData(it)
            }
        }
    }


    private fun initSearchRecyclerView() {

        recyclerView_more.adapter = searchAdapter
        recyclerView_more.addItemDecoration(ProductItemDecoration())
        recyclerView_more.adapter = searchAdapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter { searchAdapter.retry() },
            footer = PagingLoadStateAdapter { searchAdapter.retry() }
        )

        searchAdapter.addLoadStateListener { loadState ->
            btn_search_retry_button.isVisible = loadState.source.refresh is LoadState.Error
            cl_home_progress.isVisible = loadState.source.refresh is LoadState.Loading
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                showInfoDialog(
                    getString(R.string.failed_to_connect_to_server),
                    2
                )
            }
        }

        btn_search_retry_button.setOnClickListener {
            searchAdapter.retry()
        }

        searchAdapter.setOnItemClickListener(object : SearchProductAdapter.ClickListener {
            override fun onClick(product: Product, aView: View, position: Int) {
                productDetailsView(product)
            }
        })

        edit_text_product_search.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mainActivity.hideKeyboard()
                searchProducts()
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun showInfoDialog(message: String, code: Int) {
        val dialogInfoFragment = InfoDialog.newInstance(message, code)
        dialogInfoFragment.setListener(this)
        dialogInfoFragment.show(activity?.supportFragmentManager!!, "Success")

    }
}