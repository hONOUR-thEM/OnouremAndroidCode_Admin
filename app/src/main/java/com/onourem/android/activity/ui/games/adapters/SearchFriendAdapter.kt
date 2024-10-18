package com.onourem.android.activity.ui.games.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemUserSearchBinding
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class SearchFriendAdapter(
    private val userList: MutableList<UserList>,
    private val onItemClickListener: OnItemClickListener<UserList>?
) : RecyclerView.Adapter<SearchFriendAdapter.SearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_search, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun reset() {
        userList.clear()
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ItemUserSearchBinding?
        var options = RequestOptions()
            .fitCenter()
            .placeholder(R.drawable.default_user_profile_image)
            .error(R.drawable.default_user_profile_image)

        fun bind(userList: UserList) {
            Glide.with(binding!!.rivProfile)
                .load(userList.profilePicture)
                .apply(options)
                .into(binding.rivProfile)
            binding.ivCheck.visibility =
                if (userList.isSelected) View.VISIBLE else View.GONE
            binding.rivProfileIcon.visibility =
                if (!TextUtils.isEmpty(userList.status) && userList.status.equals(
                        "Friends",
                        ignoreCase = true
                    )
                ) View.VISIBLE else View.GONE
            binding.tvName.text = String.format("%s %s", userList.firstName, userList.lastName)
        }

        init {
            binding = DataBindingUtil.bind(itemView)
            itemView.setOnClickListener(ViewClickListener { v: View? ->
                val user = userList[bindingAdapterPosition]
                user.isSelected = !user.isSelected
                onItemClickListener?.onItemClick(user)
                notifyItemChanged(bindingAdapterPosition)
            })
        }
    }
}