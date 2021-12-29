package project.ceyloninnovationlabs.fastbuy.ui.fragment.lastorder


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import project.ceyloninnovationlabs.fastbuy.data.model.orderoutput.LineItem
import project.ceyloninnovationlabs.fastbuy.databinding.ListLastorderItemBinding


class LastOrderItemsAdapter : ListAdapter<LineItem, RecyclerView.ViewHolder>(LASTORDER_ITEMS_COMPARATOR) {
    companion object {
        private val LASTORDER_ITEMS_COMPARATOR = object : DiffUtil.ItemCallback<LineItem>() {
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
            ListLastorderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    class ItemViewHolder(private val binding: ListLastorderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rec: LineItem) {
            binding.apply {
                product = rec
                executePendingBindings()
            }

        }
    }

}