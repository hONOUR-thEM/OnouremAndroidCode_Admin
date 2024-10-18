package com.onourem.android.activity.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemShareSomethingBinding

class ShareSomethingAdapter(
    private val visibleDataList: ArrayList<String>,
) : RecyclerView.Adapter<ShareSomethingAdapter.FriendViewHolder>() {
    private val options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_share_something, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(visibleDataList[position])
    }

    override fun getItemCount(): Int {
        return visibleDataList.size
    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val itemBinding: ItemShareSomethingBinding
        fun bind(user: String) {
            itemBinding.txtItem.text = String.format("%s", user)
        }

        override fun onClick(v: View) {}

        init {
            itemBinding = ItemShareSomethingBinding.bind(itemView)
        }
    }
}