package project.ceyloninnovationlabs.fastbuy.ui.adapter

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
import project.ceyloninnovationlabs.fastbuy.FastBuy
import project.ceyloninnovationlabs.fastbuy.R
import project.ceyloninnovationlabs.fastbuy.data.model.product.Category
import project.ceyloninnovationlabs.fastbuy.data.model.product.Image
import project.ceyloninnovationlabs.fastbuy.data.model.product.Product
import project.ceyloninnovationlabs.fastbuy.ui.customview.ColorRatingBar
import java.lang.NumberFormatException
import java.text.DecimalFormat

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


    @BindingAdapter("addItemQuantity")
    @JvmStatic
    fun addItemQuantity(view: AppCompatEditText, quantity: Int) {
        view.setText(quantity.toString())
    }



}