package com.thikar.githubusers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thikar.githubusers.R
import com.thikar.githubusers.api.UserDetails
import com.thikar.githubusers.databinding.ItemListBinding

class UserListAdapter(val onItemClick: (UserDetails) -> Unit) :
    ListAdapter<UserDetails, UserItemViewHolder>(UserComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserItemViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserItemViewHolder(binding,
            onItemClick = {position ->
                val userDetails = getItem(position)
                if (userDetails != null) {
                    onItemClick(userDetails)
                }
            })
    }

    override fun onBindViewHolder(holder: UserItemViewHolder, position: Int) {
        val curItem = getItem(position)
        if (curItem != null) {
            holder.bind(curItem)
        }
    }
}

class UserItemViewHolder(
    private val binding: ItemListBinding,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: UserDetails) {
        binding.apply {
            binding.tvUserId.text = item.id.toString()

            Glide.with(itemView)
                .load(item.imageUrl)
                .error(R.drawable.image_placeholder)
                .into(imvUser)
        }
    }

    init {
        binding.apply {
            root.setOnClickListener {
                val position = adapterPosition
                    onItemClick(position)
            }
        }
    }
}

class UserComparator : DiffUtil.ItemCallback<UserDetails>() {
    override fun areItemsTheSame(oldItem: UserDetails, newItem: UserDetails) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: UserDetails, newItem: UserDetails) =
        oldItem.id == newItem.id
}