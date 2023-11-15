package com.ajiananta.submisiintermediate.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ajiananta.submisiintermediate.api.response.ListStoryItem
import com.ajiananta.submisiintermediate.databinding.StoryItemBinding
import com.ajiananta.submisiintermediate.utils.getAddName
import com.ajiananta.submisiintermediate.utils.withDateFormat
import com.ajiananta.submisiintermediate.view.detail.DetailStoriesActivity
import com.bumptech.glide.Glide

class StoriesAdapter: PagingDataAdapter<ListStoryItem, StoriesAdapter.MyViewHolderStories>(DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: MyViewHolderStories, position: Int) {
        val dataItem = getItem(position)
        if (dataItem != null) {
            holder.bind(dataItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolderStories {
        val binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolderStories(binding)
    }

    class MyViewHolderStories(private val binding: StoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(items: ListStoryItem) {
            fun isValidLatitude(latitude: Double): Boolean {
                return latitude >= -90.0 && latitude <= 90.0
            }
            Glide.with(binding.root.context)
                .load(items.photoUrl)
                .into(binding.tvItemPhoto)
            binding.tvItemName.text = items.name
            binding.tvItemDesc.text = items.description
            binding.tvItemDate.text = items.createdAt.withDateFormat()
            if (isValidLatitude(items.lat)) {
                binding.tvItemLocation.text = getAddName(binding.root.context, items.lat, items.lon)
            } else
                binding.tvItemLocation.text = "Location is Error"
            if (items.lon == 0.0 && items.lat == 0.0) {
                binding.ivLocationIcon.visibility = android.view.View.INVISIBLE
                binding.tvItemLocation.visibility = android.view.View.INVISIBLE
            } else {
                binding.ivLocationIcon.visibility = android.view.View.VISIBLE
                binding.tvItemLocation.visibility = android.view.View.VISIBLE
            }
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoriesActivity::class.java)
                intent.putExtra(DetailStoriesActivity.NAME, items.name)
                intent.putExtra(DetailStoriesActivity.DATE, items.createdAt)
                intent.putExtra(DetailStoriesActivity.DESCRIPTION, items.description)
                intent.putExtra(DetailStoriesActivity.IMAGE_URL, items.photoUrl)
                intent.putExtra(DetailStoriesActivity.LONGITUDE, items.lon.toString())
                intent.putExtra(DetailStoriesActivity.LATITUDE, items.lat.toString())
                itemView.context.startActivity(intent)
            }

        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}