package project.ceyloninnovationlabs.fastbuy.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import project.ceyloninnovationlabs.fastbuy.R
import project.ceyloninnovationlabs.fastbuy.databinding.LayoutPagingLoadStateBinding


class PagingLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<PagingLoadStateAdapter.ReposLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: ReposLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): PagingLoadStateAdapter.ReposLoadStateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_paging_load_state, parent, false)
        val binding = LayoutPagingLoadStateBinding.bind(view)
        return ReposLoadStateViewHolder(binding, retry)

    }

   inner class ReposLoadStateViewHolder(private val binding: LayoutPagingLoadStateBinding, retry: () -> Unit) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.also {
                it.setOnClickListener { retry.invoke() }
            }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = loadState.error.localizedMessage
            }
            binding.gif1.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState !is LoadState.Loading
            binding.errorMsg.isVisible = loadState !is LoadState.Loading
        }


    }

}