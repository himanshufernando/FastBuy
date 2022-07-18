package project.superuniqueit.fastbuy.ui.adapter

import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import project.superuniqueit.fastbuy.FastBuy
import project.superuniqueit.fastbuy.R
import project.superuniqueit.fastbuy.data.model.product.Category
import project.superuniqueit.fastbuy.data.model.product.Image
import project.superuniqueit.fastbuy.data.model.product.Product
import project.superuniqueit.fastbuy.ui.customview.ColorRatingBar
import java.lang.NumberFormatException
import java.text.DecimalFormat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import project.superuniqueit.fastbuy.data.model.orderoutput.LineItem
import project.superuniqueit.fastbuy.data.model.orderoutput.PastOrder
import project.superuniqueit.fastbuy.data.model.past.Orders
import project.superuniqueit.fastbuy.ui.fragment.lastorder.LastOrderItemsAdapter
import project.superuniqueit.fastbuy.ui.fragment.pastorderdetails.PastOrderItemsAdapter
import java.util.*
import kotlin.collections.ArrayList


object CustomBindingAdapter {


    @BindingAdapter("setListProductCategories")
    @JvmStatic
    fun setListProductCategories(view: TextView, categories: ArrayList<Category>) {
        var allCategories = ""
        categories.map {
            if (allCategories.isEmpty()) {
                allCategories += it.name.trim()
            } else {
                allCategories = allCategories + ", " + it.name.trim()
            }

        }
        view.text = allCategories
    }


    @BindingAdapter("setProductItemImage")
    @JvmStatic
    fun setProductItemImage(view: AppCompatImageView, images: ArrayList<Image>) {
        try {
            if (images[0].src != null) {
                Glide.with(FastBuy.applicationContext())
                    .load(images[0].src)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_fastbuy_logo_v2)
                    .into(view)
            } else {
                view.setImageResource(R.drawable.ic_fastbuy_logo_v2)
            }
        } catch (e: Exception) {
            view.setImageResource(R.drawable.ic_fastbuy_logo_v2)
        }
    }

    @BindingAdapter("setProductRatingStar")
    @JvmStatic
    fun setProductRatingStar(view: ColorRatingBar, rating: String) {

        if (rating == "0.00") {
            view.visibility = View.GONE
        } else {
            try {
                var rat = rating.toFloat()
                view.rating = rat
            } catch (num: NumberFormatException) {
                view.visibility = View.GONE
            }
        }

    }


    @BindingAdapter("setProductRatingCount")
    @JvmStatic
    fun setProductRatingCount(view: AppCompatTextView, ratingCount: Int) {
        if (ratingCount <= 0) {
            view.visibility = View.GONE
        } else {
            view.text = "( $ratingCount customer reviews)"
        }

    }


    @BindingAdapter("setProductStockAvailability")
    @JvmStatic
    fun setProductStockAvailability(view: AppCompatTextView, product: Product) {
        if (product.stock_quantity == 0) {
            if (product.backorders_allowed) {
                view.setTextColor(Color.parseColor("#fed700"))
                view.text = "Available on backorder"
            } else {
                view.setTextColor(Color.parseColor("#dc3545"))
                view.text = "Out of stock"
            }
        } else {
            if (product.backorders_allowed) {
                view.text = product.stock_quantity.toString() + " in stock (can be backordered)"
            } else {
                view.text = product.stock_quantity.toString() + " in stock"
            }
        }
    }


    @BindingAdapter("setProductImage")
    @JvmStatic
    fun setProductImage(view: AppCompatImageView, images: ArrayList<Image>) {
        try {
            if (images[0].src != null) {
                Glide.with(FastBuy.applicationContext())
                    .load(images[0].src)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_fastbuy_logo_v2)
                    .into(view)
            } else {
                view.setImageResource(R.drawable.ic_fastbuy_logo_v2)
            }
        } catch (e: Exception) {
            view.setImageResource(R.drawable.ic_fastbuy_logo_v2)
        }

    }


    @BindingAdapter("setProductImageGallery")
    @JvmStatic
    fun setProductImageGallery(view: AppCompatImageView, images: String) {
        try {
            Glide.with(FastBuy.applicationContext())
                .load(images)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_fastbuy_logo_v2)
                .into(view)

        } catch (e: Exception) {
            view.setImageResource(R.drawable.ic_fastbuy_logo_v2)
        }

    }


    @BindingAdapter("setProductImageGalleryHighlighter")
    @JvmStatic
    fun setProductImageGalleryHighlighter(view: View, isSelect: Boolean) {
        view.isVisible = isSelect

    }


    @BindingAdapter("setProductProgress")
    @JvmStatic
    fun setProductProgress(view: ProgressBar, product: Product) {
        try {
            GlobalScope.launch(context = Dispatchers.Main) {
                delay(3000)
                view.visibility = View.GONE
            }

        } catch (e: Exception) {
            view.visibility = View.GONE
        }
    }


    @BindingAdapter("setProductDiscount")
    @JvmStatic
    fun setProductDiscount(view: TextView, product: Product) {
        try {
            var prentage =
                (product.regular_price.toDouble() - product.sale_price.toDouble()) / (product.regular_price.toDouble() / 100.00)
            val rounded = String.format("%.0f", prentage)
            view.text = "-$rounded%"
            view.visibility = View.VISIBLE
        } catch (e: Exception) {
            view.visibility = View.GONE
        }
    }


    @BindingAdapter("setSellingPrice")
    @JvmStatic
    fun setSellingPrice(view: TextView, price: String) {
        try {
            val decim = DecimalFormat("#,###.##")
            view.text = "Rs." + decim.format(price.toDouble()) + ".00"
        } catch (e: Exception) {
            view.visibility = View.GONE
        }
    }


    @BindingAdapter("setRegularPrice")
    @JvmStatic
    fun setRegularPrice(view: TextView, price: String) {
        try {

            val decim = DecimalFormat("#,###.##")
            view.text = "Rs." + decim.format(price.toDouble()) + ".00"
        } catch (e: Exception) {
            view.visibility = View.GONE
        }
    }


    @BindingAdapter("setCartSubtotel")
    @JvmStatic
    fun setCartSubtotel(view: TextView, product: Product) {
        try {
            var sub = product.sale_price.toInt() * product.quantity
            if (product.isGiftWrapping) {
                sub += 200
            }

            view.text = "Rs.$sub.00"

        } catch (e: Exception) {
            view.visibility = View.GONE
        }
    }


    @BindingAdapter("setIsGiftWrapping")
    @JvmStatic
    fun setIsGiftWrapping(view: TextView, isRead: Boolean) {
        if (isRead) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }


    @BindingAdapter("setQuantityToCartItems")
    @JvmStatic
    fun setQuantityToCartItems(view: AppCompatEditText, product: String) {
        view.setText("ss")
    }


    @BindingAdapter("setProductDetailsToCheckoutCart")
    @JvmStatic
    fun setProductDetailsToCheckoutCart(view: AppCompatTextView, product: Product) {
        if (product.isGiftWrapping) {
            view.text = product.name + "(Gift wrapping)" + " X " + (product.quantity.toString())
        } else {
            view.text = product.name + " X " + (product.quantity.toString())
        }


    }

    @BindingAdapter("setProductDetailsToLastOrder")
    @JvmStatic
    fun setProductDetailsToLastOrder(view: AppCompatTextView, product: LineItem) {
        view.text = product.name + " X " + (product.quantity.toString())
    }


    @BindingAdapter("setOrderProductDetailsToLastOrder")
    @JvmStatic
    fun setOrderProductDetailsToLastOrder(view: AppCompatTextView, product: project.superuniqueit.fastbuy.data.model.past.LineItem) {
        view.text = product.name + " X " + (product.quantity.toString())
    }


    @BindingAdapter("setVisibilityBankDetails")
    @JvmStatic
    fun setVisibilityBankDetails(view: ConstraintLayout, payment: String) {
        if (payment == "bacs") {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }


    @BindingAdapter("setProductList")
    @JvmStatic
    fun setProductList(view: RecyclerView, order: PastOrder) {
        if (!order.line_items.isNullOrEmpty()) {
            val proAdapter = LastOrderItemsAdapter()
            view.adapter = proAdapter
            proAdapter.submitList(order.line_items)
        }
    }

    @BindingAdapter("setPastOrderProductList")
    @JvmStatic
    fun setPastOrderProductList(view: RecyclerView, order: Orders) {
        if (!order.line_items.isNullOrEmpty()) {
            val proAdapter = PastOrderItemsAdapter()
            view.adapter = proAdapter
            proAdapter.submitList(order.line_items)
        }
    }


    @BindingAdapter("setLastOrderDetails")
    @JvmStatic
    fun setLastOrderDetails(view: AppCompatTextView, company: String) {
        if (company.isNullOrEmpty()) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
            view.text = company
        }
    }


    @BindingAdapter("setPastOrderDate")
    @JvmStatic
    fun setPastOrderDate(view: AppCompatTextView, date: String) {
        view.text = date.substring(0, 10)
    }


    @BindingAdapter("setPastOrderTotal")
    @JvmStatic
    fun setPastOrderTotal(view: AppCompatTextView, orders: Orders) {
        var qty = 0
        for (item in orders.line_items){
            qty += item.quantity
        }
        view.text = orders.total+" for "+qty+" Items"
    }


}