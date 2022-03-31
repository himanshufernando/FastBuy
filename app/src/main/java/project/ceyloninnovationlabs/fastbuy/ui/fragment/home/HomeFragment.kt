package project.ceyloninnovationlabs.fastbuy.ui.fragment.home

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
import androidx.lifecycle.Observer
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.appCompatImageView4
import kotlinx.android.synthetic.main.fragment_home.cl_home_progress
import kotlinx.android.synthetic.main.fragment_home.edit_text_product_search
import kotlinx.android.synthetic.main.fragment_home.ic_search
import kotlinx.android.synthetic.main.fragment_home.txt_cart_count
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import project.ceyloninnovationlabs.fastbuy.FastBuy
import project.ceyloninnovationlabs.fastbuy.R
import project.ceyloninnovationlabs.fastbuy.data.model.FastBuyResult
import project.ceyloninnovationlabs.fastbuy.data.model.product.Product
import project.ceyloninnovationlabs.fastbuy.data.model.SliderImage
import project.ceyloninnovationlabs.fastbuy.services.listeners.OnNavigationListener
import project.ceyloninnovationlabs.fastbuy.services.perfrences.AppPrefs
import project.ceyloninnovationlabs.fastbuy.ui.activity.MainActivity
import project.ceyloninnovationlabs.fastbuy.ui.adapter.PagingLoadStateAdapter
import project.ceyloninnovationlabs.fastbuy.ui.dialog.InfoDialog
import project.ceyloninnovationlabs.fastbuy.ui.fragment.home.featured.FeaturedProductAdapter
import project.ceyloninnovationlabs.fastbuy.ui.fragment.home.onsale.OnSaleProductAdapter
import project.ceyloninnovationlabs.fastbuy.ui.fragment.home.recent.RecentProductAdapter
import project.ceyloninnovationlabs.fastbuy.ui.fragment.home.topsale.TopSaleAdapter
import project.ceyloninnovationlabs.fastbuy.viewmodels.home.HomeViewModel
import java.util.ArrayList


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : Fragment(), InfoDialog.InfoDialogListener, View.OnClickListener,
    OnNavigationListener {


   private val viewmodel: HomeViewModel by activityViewModels()

    private val adapter = SliderAdapter()
    lateinit var mainActivity: MainActivity
    var listItems = ArrayList<SliderImage>()
    var counter = 0

    lateinit var repeatJob: Job

    var appPrefs = AppPrefs

    private lateinit var onSaleJob: Job
    private val onSaleAdapter = OnSaleProductAdapter()

    private lateinit var featuredJob: Job
    private val featuredAdapter = FeaturedProductAdapter()

    private val adapterTopSale = TopSaleAdapter()

    private lateinit var recentJob: Job
    private val recentAdapter = RecentProductAdapter()

    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mainActivity = (activity as MainActivity)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("xxxxxxxxxxxxxxx onViewCreated")

        ic_search.setOnClickListener(this)
        txt_onsale_more.setOnClickListener(this)
        txt_featured_more.setOnClickListener(this)
        txt_top_selling_more.setOnClickListener(this)
        txt_recent_more.setOnClickListener(this)
        cl_home_cart.setOnClickListener(this)
        img_account.setOnClickListener(this)
        img_navigation.setOnClickListener(this)

        FastBuy.setOnNavigationListener(this)


        setupSliderViewPager()
        getSlider()

        initOnSaleRecyclerView()
        initFeaturedRecyclerView()
        initTopSaleRecyclerView()
        initRecentRecyclerView()

        setupSearchBar()


        if (!InternetConnection.checkInternetConnection()) {
            showInfoDialog(
                getString(R.string.no_internet),
                3
            )
        } else {
            getOnSaleProducts()

        }





    }

    override fun onResume() {
        super.onResume()

        updateCart()
        if(cl_home_progress != null ){
            if(cl_home_progress.isVisible){
                cl_home_progress.visibility = View.GONE
            }
        }

    }

    override fun onStop() {
        super.onStop()


        if (::repeatJob.isInitialized) {
            repeatJob.cancel()
        }
        if (::onSaleJob.isInitialized) {
            onSaleJob.cancel()
        }
        if (::featuredJob.isInitialized) {
            featuredJob.cancel()
        }
        if (::recentJob.isInitialized) {
            recentJob.cancel()
        }


    }

    override fun onDestroy() {
        super.onDestroy()

    }


    override fun onInfoDialogPositive(code: Int) {
        when (code) {
            1 -> {
                //cl_home_progress.isVisible = true
                onSaleAdapter.retry()
            }
            2 -> {
                //  cl_home_progress.isVisible = true
                featuredAdapter.retry()
            }
            3 -> {
                if (InternetConnection.checkInternetConnection()) {
                    getOnSaleProducts()
                    getFeaturedProducts()
                }
            }
        }

    }


    override fun onClick(v: View) {
        if ((SystemClock.elapsedRealtime() - mLastClickTime < 1500) || cl_home_progress.isVisible) { return }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (v.id) {
            R.id.ic_search -> searchProducts()
            R.id.txt_onsale_more ->moreProducts(1)
            R.id.txt_featured_more ->moreProducts(2)
            R.id.txt_top_selling_more -> moreProducts(3)
            R.id.txt_recent_more -> moreProducts(4)
            R.id.cl_home_cart ->goToCart()
            R.id.img_account -> NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_home_to_account)
            R.id.img_navigation -> mainActivity.openDrawer()
        }

    }


    private fun goToCart(){
        var cart = appPrefs.getCartItemPrefs()
        if(cart.product.isEmpty()){
            Toast.makeText(requireContext(), "Your cart is currently empty !!", Toast.LENGTH_SHORT).show()
        }else{
            NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_home_to_cart)
        }
    }

    private fun productDetailsView(product : Product){
        if(product == null){
            Toast.makeText(requireContext(), "Please select the product again !!", Toast.LENGTH_SHORT).show()
            return
        }

        if(product.id == 0){
            Toast.makeText(requireContext(), "Please select the product again !!", Toast.LENGTH_SHORT).show()
            return
        }
        cl_home_progress.visibility = View.VISIBLE
        val bundle = bundleOf(Pair("product", product))
        NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_home_to_details, bundle)
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
        NavHostFragment.findNavController(this).navigate(R.id.fragment_home_to_search)

        edit_text_product_search.setText("")
    }

    private fun moreProducts(status : Int) {
        mainActivity.hideKeyboard()
        viewmodel.searchQuery.value = ""
        viewmodel.moreProductStatus.value = status
        NavHostFragment.findNavController(this).navigate(R.id.fragment_home_to_search)
    }

    private fun getOnSaleProducts() {
        if (::onSaleJob.isInitialized) {
            onSaleJob.cancel()
        }


        onSaleJob = lifecycleScope.launch {
            viewmodel.getOnSaleProducts().collectLatest {
                onSaleAdapter.submitData(it)
            }
        }
    }


    private fun initOnSaleRecyclerView() {
        recyclerView_onsale.adapter = onSaleAdapter
        recyclerView_onsale.adapter = onSaleAdapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter { onSaleAdapter.retry() },
            footer = PagingLoadStateAdapter { onSaleAdapter.retry() }
        )
        onSaleAdapter.addLoadStateListener { loadState ->
            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && onSaleAdapter.itemCount == 0

            if (btn_home_retry_button != null) {
                btn_home_retry_button.isVisible = loadState.source.refresh is LoadState.Error
            }
            if (cl_onsale_progress != null) {
                cl_onsale_progress.isVisible = loadState.source.refresh is LoadState.Loading
            }




            if (isListEmpty) {
                if (txt_onsale != null) {
                    txt_onsale.visibility = View.GONE
                }
                if (txt_onsale_more != null) {
                    txt_onsale_more.visibility = View.GONE
                }

            } else {
                if (txt_onsale != null) {
                    txt_onsale.visibility = View.VISIBLE
                }
                if (txt_onsale_more != null) {
                    txt_onsale_more.visibility = View.VISIBLE
                }
                getFeaturedProducts()

            }


            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                showInfoDialog(
                    getString(R.string.failed_to_connect_to_server),
                    1
                )
            }
        }

        btn_home_retry_button.setOnClickListener {
            onSaleAdapter.retry()
        }

        onSaleAdapter.setOnItemClickListener(object : OnSaleProductAdapter.ClickListener {
            override fun onClick(product: Product, aView: View, position: Int) {
                productDetailsView(product)
            }
        })

    }


    private fun getFeaturedProducts() {
        if (::featuredJob.isInitialized) {
            featuredJob.cancel()
        }

        featuredJob = lifecycleScope.launch {
            viewmodel.getFeaturedProducts().collectLatest {
                featuredAdapter.submitData(it)
            }
        }
    }


    private fun initFeaturedRecyclerView() {

        recyclerView_featured.adapter = featuredAdapter
        recyclerView_featured.adapter = featuredAdapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter { featuredAdapter.retry() },
            footer = PagingLoadStateAdapter { featuredAdapter.retry() }
        )

        featuredAdapter.addLoadStateListener { loadState ->

            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && featuredAdapter.itemCount == 0
            if (btn_home_retry_button != null) {
                btn_home_retry_button.isVisible = loadState.source.refresh is LoadState.Error
            }
            if (cl_featured_progress != null) {
                cl_featured_progress.isVisible = loadState.source.refresh is LoadState.Loading
            }





            if (isListEmpty) {
                if (txt_featured != null) {
                    txt_featured.visibility = View.GONE
                }
                if (txt_featured_more != null) {
                    txt_featured_more.visibility = View.GONE
                }


            } else {
                if (txt_featured != null) {
                    txt_featured.visibility = View.VISIBLE
                }
                if (txt_featured_more != null) {
                    txt_featured_more.visibility = View.VISIBLE
                }
                getTopSellingProducts()
            }


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



        featuredAdapter.setOnItemClickListener(object : FeaturedProductAdapter.ClickListener {
            override fun onClick(product: Product, aView: View, position: Int) {
                productDetailsView(product)
            }
        })
    }


    private fun getTopSellingProducts() {
        viewmodel.getTopSellingProducts().observe(viewLifecycleOwner, Observer {
            when (it) {
                is FastBuyResult.Success -> {
                    txt_top_selling.visibility = View.VISIBLE
                    txt_top_selling_more.visibility = View.VISIBLE

                    adapterTopSale.submitList(it.data)
                    getRecentProducts()
                }
                is FastBuyResult.ExceptionError.ExError -> {
                }
                is FastBuyResult.LogicalError.LogError -> {
                }
            }


        })
    }


    private fun initTopSaleRecyclerView() {
        recyclerView_top_selling.adapter = adapterTopSale
        adapterTopSale.setOnItemClickListener(object : TopSaleAdapter.ClickListener {
            override fun onClick(product: Product, aView: View, position: Int) {
                productDetailsView(product)
            }
        })

    }


    private fun getRecentProducts() {
        if (::recentJob.isInitialized) {
            recentJob.cancel()
        }

        recentJob = lifecycleScope.launch {
            viewmodel.getRecentProducts().collectLatest {
                recentAdapter.submitData(it)
            }
        }
    }

    private fun initRecentRecyclerView() {

        recyclerView_recent.adapter = recentAdapter
        recyclerView_recent.adapter = recentAdapter.withLoadStateHeaderAndFooter(
            header = PagingLoadStateAdapter { recentAdapter.retry() },
            footer = PagingLoadStateAdapter { recentAdapter.retry() }
        )

        recentAdapter.addLoadStateListener { loadState ->

            val isListEmpty =
                loadState.refresh is LoadState.NotLoading && recentAdapter.itemCount == 0

            if (isListEmpty) {
                txt_recent.visibility = View.GONE
                txt_recent_more.visibility = View.GONE
            } else {
                txt_recent.visibility = View.VISIBLE
                txt_recent_more.visibility = View.VISIBLE

            }


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

        btn_home_retry_button.setOnClickListener {
            recentAdapter.retry()
        }

        recentAdapter.setOnItemClickListener(object : RecentProductAdapter.ClickListener {
            override fun onClick(product: Product, aView: View, position: Int) {
                productDetailsView(product)
            }
        })
    }


    private fun getSlider() {
        viewmodel.getSlider().observe(viewLifecycleOwner, Observer {
            when (it) {
                is FastBuyResult.Success -> {
                    println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa : "+it.data)
                    adapter.submitList(it.data)
                    listItems = it.data
                    repeatJob = startRepeatingJob(1500L)
                }
                is FastBuyResult.ExceptionError.ExError -> {
                }
                is FastBuyResult.LogicalError.LogError -> {
                }
            }


        })
    }


    private fun setupSliderViewPager() {
        view_pager_slider.adapter = adapter
        view_pager_slider.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        view_pager_slider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

            }
        })

        adapter.setOnItemClickListener(object : SliderAdapter.ClickListener {
            override fun onClick(selectedRecoding: SliderImage, aView: Int, position: Int) {
            }
        })
    }

    private fun startRepeatingJob(timeInterval: Long): Job {
        return CoroutineScope(Dispatchers.Default).launch {
            while (NonCancellable.isActive) {
                if (view_pager_slider != null) {
                    view_pager_slider.currentItem = counter
                    if (listItems.size <= counter) {
                        counter = 0

                    } else {
                        counter += 1

                    }
                    //repeate Task Here
                    delay(timeInterval)
                }


            }
        }
    }


    private fun showInfoDialog(message: String, code: Int) {
        val dialogInfoFragment = InfoDialog.newInstance(message, code)
        dialogInfoFragment.setListener(this)
        dialogInfoFragment.show(activity?.supportFragmentManager!!, "Success")

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

    override fun onNavigationListener(layout: Int) {


    }


}