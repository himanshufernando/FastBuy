package project.ceyloninnovationlabs.fastbuy.ui.fragment.productdetails

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import project.ceyloninnovationlabs.fastbuy.data.model.product.Image
import project.ceyloninnovationlabs.fastbuy.databinding.ListImageGalleryBinding


class ProductImageAdapter : ListAdapter<Image, RecyclerView.ViewHolder>(IMAGE_COMPARATOR) {
    companion object {
        private val IMAGE_COMPARATOR = object : DiffUtil.ItemCallback<Image>() {
            override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean =
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
            ListImageGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            mClickListener
        )
    }

    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }

    interface ClickListener {
        fun onClick(selectedImage: Image, aView: View, position: Int)
    }

    class ItemViewHolder(
        private val binding: ListImageGalleryBinding,
        var mClickListener: ClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener {
                binding.image?.let { selectedRecoding ->
                    mClickListener.onClick(
                        selectedRecoding,
                        it,
                        absoluteAdapterPosition
                    )
                }
            }
        }

        fun bind(rec: Image) {
            binding.apply {
                image = rec
                executePendingBindings()
            }

        }
    }

}