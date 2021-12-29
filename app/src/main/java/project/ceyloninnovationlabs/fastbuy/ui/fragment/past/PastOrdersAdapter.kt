package project.ceyloninnovationlabs.fastbuy.ui.fragment.past

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import project.ceyloninnovationlabs.fastbuy.data.model.past.Orders
import project.ceyloninnovationlabs.fastbuy.databinding.ListPastBinding


class PastOrdersAdapter : PagingDataAdapter<Orders, RecyclerView.ViewHolder>(PAST_COMPARATOR) {

    companion object {
        private val PAST_COMPARATOR = object : DiffUtil.ItemCallback<Orders>() {
            override fun areItemsTheSame(oldItem: Orders, newItem: Orders): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Orders, newItem: Orders): Boolean =
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
            ListPastBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            mClickListener
        )
    }

    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }

    interface ClickListener {
        fun onClick(selectedOrder: Orders, aView: View, position: Int)
    }

    class ItemViewHolder(private val binding: ListPastBinding ,var mClickListener: ClickListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener { binding.pastorder?.let { selectedOrder -> mClickListener.onClick(selectedOrder,it,absoluteAdapterPosition) } }
        }
        fun bind(rec: Orders) {
            binding.apply { pastorder = rec
                executePendingBindings()
            }

        }
    }
}

