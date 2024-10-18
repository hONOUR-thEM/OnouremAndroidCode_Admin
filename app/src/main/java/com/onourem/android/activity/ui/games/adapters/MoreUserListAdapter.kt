package com.onourem.android.activity.ui.games.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.makeramen.roundedimageview.RoundedImageView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class MoreUserListAdapter(
    private val visibleDataList: List<UserList>,
    private val onItemClickListener: OnItemClickListener<UserList>?
) : RecyclerView.Adapter<MoreUserListAdapter.FriendViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_more_user_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(visibleDataList[position])
    }

    override fun getItemCount(): Int {
        return visibleDataList.size
    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: AppCompatTextView
        private val rivImageView: RoundedImageView
        private val ivIconVerified: RoundedImageView
        private val options = RequestOptions()
            .fitCenter()
            .placeholder(R.drawable.default_user_profile_image)
            .error(R.drawable.default_user_profile_image)

        @SuppressLint("SetTextI18n")
        fun bind(user: UserList) {
            tvName.text = user.firstName
            Glide.with(itemView.context)
                .load(user.profilePicture)
                .apply(options)
                .into(rivImageView)
            Utilities.verifiedUserType(itemView.context, user.userType, ivIconVerified)
        }

        init {
            tvName = itemView.findViewById(R.id.tvName)
            rivImageView = itemView.findViewById(R.id.rivProfile)
            ivIconVerified = itemView.findViewById(R.id.ivIconVerified)
            itemView.setOnClickListener(ViewClickListener { v: View? ->
                val user = visibleDataList[bindingAdapterPosition]
                onItemClickListener?.onItemClick(user)
            })
        }
    }
}