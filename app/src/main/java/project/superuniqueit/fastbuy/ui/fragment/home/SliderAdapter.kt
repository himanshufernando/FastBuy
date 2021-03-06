package project.superuniqueit.fastbuy.ui.fragment.home

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import kotlinx.android.synthetic.main.list_slide_show.view.*
import project.superuniqueit.fastbuy.R
import project.superuniqueit.fastbuy.data.model.SliderImage


class SliderAdapter : ListAdapter<SliderImage, SliderAdapter.DiffUtilCategoryItemViewHolder>(
        DIFF_UTIL_ITEM_CALLBACK
    ) {


    lateinit var mClickListener: ClickListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiffUtilCategoryItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_slide_show, parent, false)
        return DiffUtilCategoryItemViewHolder(view,mClickListener)
    }

    override fun onBindViewHolder(holder: DiffUtilCategoryItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item!!)
    }



    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }

    interface ClickListener {
        fun onClick(selectedRecoding: SliderImage, aView: Int, position: Int)

    }

    class DiffUtilCategoryItemViewHolder(itemView: View,var mClickListener: ClickListener) : RecyclerView.ViewHolder(itemView) {

        private lateinit var selectItem: SliderImage



        init {

            itemView.img_slid.setOnClickListener {

            }
        }

        fun bind(rec: SliderImage) {
            selectItem = rec

            itemView.img_slid.load("https://fastbuy.lk/app-slider/"+selectItem.name) {
                memoryCachePolicy(CachePolicy.ENABLED)
                allowHardware(false)
                crossfade(true)
                bitmapConfig(Bitmap.Config.RGB_565)

            }


        }



    }

    companion object {
        private val DIFF_UTIL_ITEM_CALLBACK =
            object : DiffUtil.ItemCallback<SliderImage>() {
                override fun areItemsTheSame(oldItem: SliderImage, newItem: SliderImage) =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: SliderImage, newItem: SliderImage) =
                    oldItem == newItem
            }
    }
}
