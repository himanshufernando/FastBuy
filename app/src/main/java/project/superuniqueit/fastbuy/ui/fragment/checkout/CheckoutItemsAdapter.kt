package project.superuniqueit.fastbuy.ui.fragment.checkout


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import project.superuniqueit.fastbuy.data.model.product.Product
import project.superuniqueit.fastbuy.databinding.ListCheckoutItemBinding


class CheckoutItemsAdapter : ListAdapter<Product, RecyclerView.ViewHolder>(CHECKOUT_ITEMS_COMPARATOR) {
    companion object {
        private val CHECKOUT_ITEMS_COMPARATOR = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as ItemViewHolder).bind(item!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            ListCheckoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    class ItemViewHolder(private val binding: ListCheckoutItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rec: Product) {
            binding.apply {
                product = rec
                executePendingBindings()
            }

        }
    }

}