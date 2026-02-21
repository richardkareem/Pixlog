package com.richard.pixlog.data.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.richard.pixlog.databinding.ItemEndOfPaginationBinding

class EndOfPaginationAdapter : RecyclerView.Adapter<EndOfPaginationAdapter.EndOfPaginationViewHolder>() {

    private var showEndMessage = false

    fun showEndMessage(show: Boolean) {
        showEndMessage = show
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EndOfPaginationViewHolder {
        val binding = ItemEndOfPaginationBinding.inflate(LayoutInflater.from(parent.context))
        return EndOfPaginationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: EndOfPaginationViewHolder,
        position: Int
    ) {
        holder.bind(showEndMessage)
    }

    override fun getItemCount() = if(showEndMessage) 1 else 0

    class EndOfPaginationViewHolder(private val binding: ItemEndOfPaginationBinding)
        : RecyclerView.ViewHolder(binding.root){
            // end of the paging will showing the message
        fun bind(showMessage: Boolean) {
            binding.endMessage.isVisible = showMessage
        }
    }
}