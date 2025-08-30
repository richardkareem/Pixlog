package com.richard.pixlog.ui.screen.home

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.richard.pixlog.data.remote.response.ListStory
import com.richard.pixlog.databinding.CardHomeBinding
import com.richard.pixlog.ui.screen.detailStory.DetailStoryActivity
import com.richard.pixlog.utils.formatTimeAgo

class HomeAdapter : PagingDataAdapter<ListStory, HomeAdapter.MyViewHolder>(DIFF_CALLBACK) {

    // binding to component that already created
    class MyViewHolder(val binding: CardHomeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStory) {
            Log.d("HomeAdapter", "bind: $story")
            binding.tvName.text = story.name
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.ivAvatar)
            binding.tvName.text = story.name
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.ivContent)
            binding.tvDescription.text = story.description.trim()
            binding.tvLastUpdate.text = itemView.context.formatTimeAgo(story.createdAt)

            itemView.setOnClickListener {
                //shared animation
                val optionsCompat : ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(binding.tvName, "name"),
                    Pair(binding.ivAvatar, "avatar"),
                    Pair(binding.ivContent, "imageDescription"),
                    Pair(binding.tvDescription, "description"),
                    Pair(binding.tvLastUpdate, "updateDate"),
                    Pair(binding.tvLocation, "location")
                )
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra("Story", story)
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CardHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}