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
import com.richard.pixlog.data.local.entity.ListStoryEntity
import com.richard.pixlog.databinding.CardHomeBinding
import com.richard.pixlog.ui.screen.detailStory.DetailStoryActivity
import com.richard.pixlog.utils.formatTimeAgo

class HomeAdapter : PagingDataAdapter<ListStoryEntity, HomeAdapter.MyViewHolder>(DIFF_CALLBACK) {

    // binding to component that already created
    class MyViewHolder(val binding: CardHomeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryEntity) {
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
                // Convert ListStoryEntity to ListStory for intent
                val storyResponse = com.richard.pixlog.data.remote.response.ListStory(
                    id = story.id,
                    name = story.name,
                    description = story.description,
                    photoUrl = story.photoUrl,
                    createdAt = story.createdAt,
                    lat = story.lat,
                    lon = story.lon
                )
                intent.putExtra("Story", storyResponse)
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
         val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryEntity>() {
            override fun areItemsTheSame(oldItem: ListStoryEntity, newItem: ListStoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryEntity, newItem: ListStoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}