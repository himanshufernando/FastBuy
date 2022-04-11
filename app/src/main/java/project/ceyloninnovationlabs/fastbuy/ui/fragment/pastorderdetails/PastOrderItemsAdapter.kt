package project.ceyloninnovationlabs.fastbuy.ui.fragment.pastorderdetails


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import project.ceyloninnovationlabs.fastbuy.data.model.past.LineItem
import project.ceyloninnovationlabs.fastbuy.databinding.ListPastorderItemBinding


class PastOrderItemsAdapter : ListAdapter<LineItem, RecyclerView.ViewHolder>(PASTORDER_ITEMS_COMPARATOR) {
    companion object {
        private val PASTORDER_ITEMS_COMPARATOR = object : DiffUtil.ItemCallback<LineItem>() {
            override fun areItemsTheSame(oldItem: LineItem, newItem: LineItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: LineItem, newItem: LineItem): Boolean =
                oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as ItemViewHolder).bind(item!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            ListPastorderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    class ItemViewHolder(private val binding: ListPastorderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rec: LineItem) {
            binding.apply {
                product = rec
                executePendingBindings()
            }

        }
    }

}