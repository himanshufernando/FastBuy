package project.ceyloninnovationlabs.fastbuy.ui.fragment.cart

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import project.ceyloninnovationlabs.fastbuy.data.model.product.Product
import project.ceyloninnovationlabs.fastbuy.databinding.ListCartItemBinding
import java.lang.NumberFormatException


class CartItemsAdapter : ListAdapter<Product, RecyclerView.ViewHolder>(CART_ITEMS_COMPARATOR) {
    companion object {
        private val CART_ITEMS_COMPARATOR = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem == newItem
        }
    }

    lateinit var mClickListener: ClickListener

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as ItemViewHolder).bind(item!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            ListCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            mClickListener
        )
    }

    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }

    interface ClickListener {
        fun onClick(selectedCart: Product, aView: View, position: Int)
        fun onTextChanged(selectedCart: Product, value: Int, position: Int)
    }

    class ItemViewHolder(private val binding: ListCartItemBinding, var mClickListener: ClickListener) : RecyclerView.ViewHolder(binding.root) {


        init {

          binding.editTextItemQty.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable?) {

                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                   var qty = 0
                    try {
                        qty = s.toString().toInt()
                        if(binding.product!!.quantity != qty){
                            mClickListener.onTextChanged(binding.product!!,qty,absoluteAdapterPosition)
                        }



                    }catch (num : NumberFormatException){

                    }

                }
            })



            binding.setClickListener {
                binding.product?.let { selectedRecoding ->
                    mClickListener.onClick(
                        selectedRecoding,
                        it,
                        absoluteAdapterPosition
                    )
                }
            }
        }

        fun bind(rec: Product) {
            binding.apply {
                product = rec
                executePendingBindings()
            }

        }
    }

}