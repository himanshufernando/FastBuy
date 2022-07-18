package project.superuniqueit.fastbuy.ui.fragment.home.onsale

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import project.superuniqueit.fastbuy.data.model.product.Product
import project.superuniqueit.fastbuy.databinding.ListItemMainBinding


class OnSaleProductAdapter : PagingDataAdapter<Product, RecyclerView.ViewHolder>(ONSALE_COMPARATOR) {

    companion object {
        private val ONSALE_COMPARATOR = object : DiffUtil.ItemCallback<Product>() {
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
            ListItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            mClickListener
        )
    }

    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }

    interface ClickListener {
        fun onClick(selectedRecoding: Product, aView: View, position: Int)
    }

    class ItemViewHolder(private val binding: ListItemMainBinding ,var mClickListener: ClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener { binding.product?.let { selectedRecoding -> mClickListener.onClick(selectedRecoding,it,absoluteAdapterPosition) } }
        }
        fun bind(rec: Product) {
            binding.apply { product = rec
                executePendingBindings()
            }

        }
    }
}

