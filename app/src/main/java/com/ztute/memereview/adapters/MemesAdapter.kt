package com.ztute.memereview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ztute.memereview.databinding.MemeItemBinding
import com.ztute.memereview.domain.model.Meme

class MemesAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Meme, MemesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(MemeItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memeItem = getItem(position)
        holder.itemView.setOnClickListener { onClickListener.onClick(memeItem) }
        holder.bind(getItem(position))
    }

    class ViewHolder(private var binding: MemeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(meme: Meme) {
            binding.meme = meme
            binding.executePendingBindings()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Meme>() {
        override fun areItemsTheSame(oldItem: Meme, newItem: Meme): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Meme, newItem: Meme): Boolean {
            return oldItem == newItem
        }
    }

    //create a named lambda
    class OnClickListener(val clickListener: (meme: Meme) -> Unit) {
        fun onClick(meme: Meme) {
            clickListener(meme)
        }
    }
}