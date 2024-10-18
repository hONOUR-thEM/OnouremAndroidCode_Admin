package com.onourem.android.activity.ui.games.adapters

import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.makeramen.roundedimageview.RoundedImageView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.GroupMember
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class PrivacyMembersListAdapter(
    private val visibleDataList: List<GroupMember>,
    private val onItemClickListener: OnItemClickListener<Pair<String, GroupMember>>?,
    private val loggedInUserId: String
) : RecyclerView.Adapter<PrivacyMembersListAdapter.FriendViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_friend_row, parent, false)
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
        private val ivMoreAction: AppCompatImageView
        private val tvAdmin: View
        private val options = RequestOptions()
            .fitCenter()
            .placeholder(R.drawable.default_user_profile_image)
            .error(R.drawable.default_user_profile_image)

        fun bind(user: GroupMember) {
            tvName.text = user.firstName + " " + user.lastName
            if (user.userId.equals(loggedInUserId, ignoreCase = true)) {
                tvAdmin.visibility = View.VISIBLE
            } else {
                tvAdmin.visibility = View.GONE
            }
            Glide.with(itemView.context)
                .load(user.profilePicture)
                .apply(options)
                .into(rivImageView)
            Utilities.verifiedUserType(itemView.context, user.userTypeId, ivIconVerified)
        }

        init {
            tvName = itemView.findViewById(R.id.tvName)
            ivMoreAction = itemView.findViewById(R.id.ivUserMoreAction)
            rivImageView = itemView.findViewById(R.id.rivProfile)
            ivIconVerified = itemView.findViewById(R.id.ivIconVerified)
            tvAdmin = itemView.findViewById(R.id.tvAdmin)
            ivMoreAction.visibility = View.VISIBLE
            ivMoreAction.setOnClickListener(ViewClickListener { v: View? ->
                val user = visibleDataList[bindingAdapterPosition]
                onItemClickListener?.onItemClick(Pair(ACTION_MORE, user))
            })
            itemView.setOnClickListener(ViewClickListener { v: View? ->
                val user = visibleDataList[bindingAdapterPosition]
                onItemClickListener?.onItemClick(Pair(ACTION_ROW, user))
            })
        }
    }

    companion object {
        const val ACTION_MORE = "MORE"
        const val ACTION_ROW = "ROW"
    }
}