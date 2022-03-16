package project.ceyloninnovationlabs.fastbuy.ui.fragment.cart

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
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.appCompatImageView4
import kotlinx.android.synthetic.main.fragment_cart.edit_text_product_search
import kotlinx.android.synthetic.main.fragment_cart.ic_search
import kotlinx.android.synthetic.main.fragment_cart.img_account
import kotlinx.android.synthetic.main.fragment_cart.txt_cart_count
import project.ceyloninnovationlabs.fastbuy.R
import project.ceyloninnovationlabs.fastbuy.data.model.FastBuyResult
import project.ceyloninnovationlabs.fastbuy.data.model.coupon.Coupon
import project.ceyloninnovationlabs.fastbuy.data.model.product.Product
import project.ceyloninnovationlabs.fastbuy.services.perfrences.AppPrefs
import project.ceyloninnovationlabs.fastbuy.ui.activity.MainActivity
import project.ceyloninnovationlabs.fastbuy.viewmodels.home.HomeViewModel


class CartFragment : Fragment(), View.OnClickListener {
    private val viewmodel: HomeViewModel by activityViewModels()
    lateinit var mainActivity: MainActivity
    var appPrefs = AppPrefs
    var cartItemsAdapter = CartItemsAdapter()
    private var mLastClickTime: Long = 0




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mainActivity = (activity as MainActivity)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btn_apply_coupon.setOnClickListener(this)
        btn_remove.setOnClickListener(this)
        ic_search.setOnClickListener(this)
        img_account.setOnClickListener(this)
        btn_proceed.setOnClickListener(this)
        img_navigation.setOnClickListener(this)


        initProductCartRecyclerView()
        setCartInitData()
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
        if ((SystemClock.elapsedRealtime() - mLastClickTime < 1500) || cl_cart_progress.isVisible) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (v.id) {
            R.id.img_navigation ->mainActivity.openDrawer()
            R.id.btn_apply_coupon -> {
                mainActivity.hideKeyboard()
                cl_cart_progress.visibility = View.VISIBLE
                couponsValidate(edit_text_coupan.text.toString().trim())
            }
            R.id.btn_remove -> {
                var cart = appPrefs.getCartItemPrefs()
                cart.coupon = Coupon()
                cart.shippingCost = 0.0
                appPrefs.setCartItemPrefs(cart)
                setCartInitData()
            }
            R.id.ic_search -> searchProducts()
            R.id.img_account -> NavHostFragment.findNavController(requireParentFragment())
                .navigate(R.id.fragment_cart_to_account)
            R.id.btn_proceed -> {
                var cart = appPrefs.getCartItemPrefs()
                var cartQuntity = 0
                for (item in cart.product) {
                    cartQuntity += item.quantity
                }
                if (cart.product.isEmpty() || cartQuntity == 0) {
                    Toast.makeText(requireContext(), "Your cart is empty ", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    NavHostFragment.findNavController(requireParentFragment())
                        .navigate(R.id.fragment_cart_to_checkout)
                }

            }
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
        NavHostFragment.findNavController(this).navigate(R.id.fragment_cart_to_search)

        edit_text_product_search.setText("")
    }

    private fun setCartInitData() {
        var cart = appPrefs.getCartItemPrefs()
        cartItemsAdapter.submitList(cart.product)


        var subtotal = 0
        for (item in cart.product) {
            var itemTotal = item.sale_price.toInt() * item.quantity
            if (item.isGiftWrapping) {
                itemTotal += 200
            }
            subtotal += itemTotal
        }

        txt_cart_tot_sub.text = "Rs.$subtotal.00"
        cart.subtotal = subtotal.toDouble()




        when (cart.coupon.discount_type) {
            "fixed_cart" -> {

                txt_cart_coupon.visibility = View.VISIBLE
                view_3_1.visibility = View.VISIBLE
                btn_remove.visibility = View.VISIBLE

                txt_cart_coupon.text = "-Rs." + cart.coupon.amount
                var couponAmount = cart.coupon.amount.toDouble()

                subtotal = (subtotal - couponAmount.toInt())

            }
            "percent" -> {
                txt_cart_coupon.visibility = View.VISIBLE
                view_3_1.visibility = View.VISIBLE
                btn_remove.visibility = View.VISIBLE

                var discountAmount = (subtotal * (cart.coupon.amount.toDouble() / 100.00))
                txt_cart_coupon.text = "-(" + cart.coupon.amount + " %) Rs." + discountAmount
                subtotal = (subtotal - discountAmount).toInt()

            }
            "fixed_product" -> {
                txt_cart_coupon.visibility = View.GONE
                view_3_1.visibility = View.GONE
                btn_remove.visibility = View.GONE

            }
            else -> {
                txt_cart_coupon.visibility = View.GONE
                view_3_1.visibility = View.GONE
                btn_remove.visibility = View.GONE
            }

        }


        txt8.text = "Rs.$subtotal.00"

        cart.total = subtotal.toString()
        cart.shippingCost = 0.0
        appPrefs.setCartItemPrefs(cart)


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

    private fun initProductCartRecyclerView() {
        recyclerView_cart_items.adapter = cartItemsAdapter
        cartItemsAdapter.setOnItemClickListener(object : CartItemsAdapter.ClickListener {
            override fun onClick(selectedProduct: Product, aView: View, position: Int) {
                when (aView.id) {
                    R.id.img1 -> {
                        var cart = appPrefs.getCartItemPrefs()
                        var proList = cart.product
                        var proNewList = ArrayList<Product>()

                        for (item in proList) {
                            if (item.id != selectedProduct.id) {
                                proNewList.add(item)
                            }
                        }
                        cart.product = proNewList
                        cart.shippingCost = 0.0
                        appPrefs.setCartItemPrefs(cart)
                        setCartInitData()
                        updateCart()

                    }

                    R.id.btn_add -> {
                        var cart = appPrefs.getCartItemPrefs()

                        for ((index, value) in cart.product.withIndex()) {
                            if (value.id == selectedProduct.id) {
                                if (selectedProduct.stock_quantity > selectedProduct.quantity) {
                                    cart.product[index].quantity += 1
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Not enough quantity ",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        }

                        cart.shippingCost = 0.0
                        appPrefs.setCartItemPrefs(cart)
                        setCartInitData()
                        updateCart()


                    }
                    R.id.btn_minus -> {

                        var cart = appPrefs.getCartItemPrefs()

                        for ((index, value) in cart.product.withIndex()) {
                            if (value.id == selectedProduct.id) {
                                if (value.quantity > 1) {
                                    cart.product[index].quantity -= 1
                                }

                            }
                        }
                        cart.shippingCost = 0.0
                        appPrefs.setCartItemPrefs(cart)
                        setCartInitData()
                        updateCart()


                    }
                }

            }

        })

    }

    private fun updateCartQty(selectedCart: Product, qty: Int, position: Int) {

        selectedCart.quantity = qty
        cartItemsAdapter.notifyItemChanged(position)

        var cart = appPrefs.getCartItemPrefs()
        cart.product[position].quantity = qty


        cart.shippingCost = 0.0
        appPrefs.setCartItemPrefs(cart)


        var updatedcart = appPrefs.getCartItemPrefs()

        var subtotal = 0
        for (item in updatedcart.product) {
            var itemTotal = item.sale_price.toInt() * item.quantity

            if (item.isGiftWrapping) {
                itemTotal += 200
            }
            subtotal += itemTotal

        }

        txt8.text = "Rs.$subtotal.00"
        txt_cart_tot_sub.text = "Rs.$subtotal.00"

        updateCart()

    }

    private fun couponsValidate(_code: String) {
        viewmodel.couponsValidate(_code).observe(viewLifecycleOwner, Observer {
            when (it) {
                is FastBuyResult.Success -> {
                    cl_cart_progress.visibility = View.GONE
                    var cart = appPrefs.getCartItemPrefs()

                    if (it.data.data[0].discount_type != "fixed_product") {
                        cart.coupon = it.data.data[0]
                        cart.shippingCost = 0.0
                        appPrefs.setCartItemPrefs(cart)


                       val alertInfoDialog : AlertDialog = requireContext()?.let {
                           val alertInfobuilder:AlertDialog.Builder = AlertDialog.Builder(it)
                            alertInfobuilder.apply {
                                setPositiveButton("OK",
                                    DialogInterface.OnClickListener { dialog, id ->
                                        // User clicked OK button
                                    })
                            }
                            alertInfobuilder?.setMessage("Coupon code applied successfully").setTitle("Coupon")
                            alertInfobuilder.create()
                        }
                        alertInfoDialog.show()

                        setCartInitData()
                    } else {
                        val alertInfoDialog : AlertDialog = requireContext()?.let {
                            val alertInfobuilder:AlertDialog.Builder = AlertDialog.Builder(it)
                            alertInfobuilder.apply {
                                setPositiveButton("OK",
                                    DialogInterface.OnClickListener { dialog, id ->
                                        // User clicked OK button
                                    })
                            }
                            alertInfobuilder?.setMessage("Sorry, this coupon is not applicable to selected product").setTitle("Coupon")
                            alertInfobuilder.create()
                        }
                        alertInfoDialog.show()
                    }


                }
                is FastBuyResult.ExceptionError.ExError -> {
                    cl_cart_progress.visibility = View.GONE

                    val alertInfoDialog : AlertDialog = requireContext()?.let { alert ->
                        val alertInfobuilder:AlertDialog.Builder = AlertDialog.Builder(alert)
                        alertInfobuilder.apply {
                            setPositiveButton("OK",
                                DialogInterface.OnClickListener { dialog, id ->
                                    // User clicked OK button
                                })
                        }
                        alertInfobuilder?.setMessage(it.exception.message!!)
                        alertInfobuilder.create()
                    }
                    alertInfoDialog.show()



                }
                is FastBuyResult.LogicalError.LogError -> {
                    cl_cart_progress.visibility = View.GONE

                    val alertInfoDialog : AlertDialog = requireContext()?.let { alert ->
                        val alertInfobuilder:AlertDialog.Builder = AlertDialog.Builder(alert)
                        alertInfobuilder.apply {
                            setPositiveButton("OK",
                                DialogInterface.OnClickListener { dialog, id ->
                                    // User clicked OK button
                                })
                        }
                        alertInfobuilder?.setMessage(it.exception.message!!)
                        alertInfobuilder.create()
                    }
                    alertInfoDialog.show()


                }
            }


        })
    }


}