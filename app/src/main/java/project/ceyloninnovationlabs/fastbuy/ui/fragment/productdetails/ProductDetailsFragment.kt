package project.ceyloninnovationlabs.fastbuy.ui.fragment.productdetails

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_product_details.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import project.ceyloninnovationlabs.fastbuy.R
import project.ceyloninnovationlabs.fastbuy.data.model.product.Image
import project.ceyloninnovationlabs.fastbuy.data.model.product.Product
import project.ceyloninnovationlabs.fastbuy.databinding.FragmentProductDetailsBinding
import project.ceyloninnovationlabs.fastbuy.ui.activity.MainActivity
import project.ceyloninnovationlabs.fastbuy.viewmodels.home.HomeViewModel
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_product_details.appCompatImageView4
import kotlinx.android.synthetic.main.fragment_product_details.edit_text_product_search
import kotlinx.android.synthetic.main.fragment_product_details.ic_search
import kotlinx.android.synthetic.main.fragment_product_details.img_account
import kotlinx.android.synthetic.main.fragment_product_details.txt_cart_count
import project.ceyloninnovationlabs.fastbuy.services.perfrences.AppPrefs



@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProductDetailsFragment : Fragment(), View.OnClickListener {

    private val viewmodel: HomeViewModel by activityViewModels()
    lateinit var binding: FragmentProductDetailsBinding

    lateinit var mainActivity: MainActivity

    lateinit var selectedProduct: Product

    var imageAdapter = ProductImageAdapter()
    var isGiftWrapping = false

    var appPrefs = AppPrefs
    private var mLastClickTime: Long = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (arguments?.containsKey("product")!!) {
            selectedProduct = arguments?.getParcelable<Product>("product")!!
        }
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_product_details, container, false)
        binding.homeViewModel = viewmodel
        return binding.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mainActivity = (activity as MainActivity)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btn_home_addtocart.setOnClickListener(this)
        cl_details_cart.setOnClickListener(this)
        ic_search.setOnClickListener(this)
        img_account.setOnClickListener(this)


        initProductData()
        mainActivity.unLockDrawer()
        initProductImageRecyclerView()
        initCheckBox()
        initItemQtyTextChangeListener()
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
        if ((SystemClock.elapsedRealtime() - mLastClickTime < 1500)) { return }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (v.id) {
            R.id.btn_home_addtocart -> addToCart()
            R.id.cl_details_cart -> goToCart()
            R.id.ic_search -> searchProducts()
            R.id.img_account -> NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_details_to_account)
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
        NavHostFragment.findNavController(this).navigate(R.id.fragment_details_to_search)

        edit_text_product_search.setText("")
    }

    private fun goToCart(){
        var cart = appPrefs.getCartItemPrefs()
        if(cart.product.isEmpty()){
            Toast.makeText(requireContext(), "Your cart is currently empty !!", Toast.LENGTH_SHORT).show()
        }else{
            NavHostFragment.findNavController(requireParentFragment()).navigate(R.id.fragment_details_to_cart)
        }
    }



    private fun addToCart(){
        var isItemAdded = false
        var cart = appPrefs.getCartItemPrefs()

        if(selectedProduct.stock_quantity == 0){
            Toast.makeText(requireContext(), "Out of stoke!!", Toast.LENGTH_SHORT).show()
            return
        }


        if(cart.product.isEmpty()){
            cart.product.add(selectedProduct)
        }else{
            for(item in cart.product){
                if(item.id == selectedProduct.id){
                    isItemAdded = true
                    item.quantity = item.quantity + selectedProduct.quantity
                }
            }
            if(!isItemAdded){
                cart.product.add(selectedProduct)
            }
        }

        cart.subtotal = viewmodel.itemValue.value!!
        cart.total = viewmodel.itemValue.value!!.toString()

        appPrefs.setCartItemPrefs(cart)

        Toast.makeText(requireContext(), "Item added to cart", Toast.LENGTH_SHORT).show()
        goToCart()
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



    private fun initItemQtyTextChangeListener() {
        edit_text_item_qty.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var count = 1
                try {
                    count = s.toString().toInt()
                    txt_cart_item.text = count.toString() + " " + selectedProduct.name

                } catch (ex: NumberFormatException) {

                }
                viewmodel.itemQty.value = count
                viewmodel.itemValue.value = count * selectedProduct.sale_price.toDouble()
                if (isGiftWrapping) {
                    viewmodel.itemValue.value = viewmodel.itemValue.value!! + 200.00
                }

                selectedProduct.quantity = count
                txt_sub_total.text = "Subtotal Rs." + viewmodel.itemValue.value.toString() + ".00"


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            }
        })

    }

    private fun initProductImageRecyclerView() {
        recyclerView_image_gallery.adapter = imageAdapter
        imageAdapter.setOnItemClickListener(object : ProductImageAdapter.ClickListener {
            override fun onClick(selectedImage: Image, aView: View, position: Int) {
                for (item in selectedProduct.images) {
                    item.isSelect = false
                }
                selectedImage.isSelect = true
                imageAdapter.notifyDataSetChanged()
                setMainImage(selectedImage.src)

            }
        })
    }

    private fun initCheckBox() {
        checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, status ->
            isGiftWrapping = status
            selectedProduct.isGiftWrapping = status
            mainActivity.hideKeyboard()

            if (status) {
                txt_cart_gift_item.visibility = View.VISIBLE
                txt_cart_gift_item_price.visibility = View.VISIBLE

                viewmodel.itemValue.value = viewmodel.itemValue.value!! + 200
                txt_sub_total.text = "Subtotal Rs." + viewmodel.itemValue.value.toString() + ".00"

            } else {
                txt_cart_gift_item.visibility = View.GONE
                txt_cart_gift_item_price.visibility = View.GONE
                viewmodel.itemValue.value = viewmodel.itemValue.value!! - 200

                txt_sub_total.text = "Subtotal Rs." + viewmodel.itemValue.value.toString() + ".00"

            }
        })
    }

    private fun initProductData() {
        if (!::selectedProduct.isInitialized) {
            mainActivity.onBackPressed()
        } else {
            binding.product = selectedProduct

            if(selectedProduct.images.isNotEmpty()){
                setMainImage(selectedProduct.images.first().src)
                selectedProduct.images.first().isSelect = true
                imageAdapter.submitList(selectedProduct.images)
            }




            webview_short_description.loadData(
                selectedProduct.short_description,
                "text/html",
                "UTF-8"
            )

            val document: Document = Jsoup.parse(selectedProduct.description)
            document.select("img").remove()
            val content = document.toString()
            webview_description.loadData(content, "text/html", "UTF-8")

            txt_cart_item.text = "1 " + selectedProduct.name
            txt_sub_total.text = "Subtotal Rs." + selectedProduct.sale_price + ".00"

            try {
                viewmodel.itemValue.value = selectedProduct.sale_price.toDouble()
            }catch (num : java.lang.NumberFormatException){
                viewmodel.itemValue.value = selectedProduct.price.toDouble()
            }


            selectedProduct.quantity = 1
        }
    }

    private fun setMainImage(url: String) {
        try {
            if (url != null) {
                pr_main.visibility = View.VISIBLE
                Glide.with(this)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_fastbuy_logo_v2)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: com.bumptech.glide.load.DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            pr_main.visibility = View.GONE
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            pr_main.visibility = View.GONE
                            return false
                        }
                    }).into(img_main)

            } else {
                img_main.setImageResource(R.drawable.ic_fastbuy_logo_v2)
            }
        } catch (e: Exception) {
            img_main.setImageResource(R.drawable.ic_fastbuy_logo_v2)
        }

    }


}