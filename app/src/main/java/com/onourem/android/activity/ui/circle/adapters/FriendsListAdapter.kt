package com.onourem.android.activity.ui.circle.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemFriendBinding
import com.onourem.android.activity.models.User
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener

class FriendsListAdapter(
    private val visibleDataList: ArrayList<User>,
    private val onItemClickListener: OnItemClickListener<UserList>
) : RecyclerView.Adapter<FriendsListAdapter.FriendViewHolder>() {
    private val options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
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
        private val itemBinding: ItemFriendBinding
        fun bind(user: User) {
            itemBinding.tvName.text = String.format("%s %s", user.firstName, user.lastName)

            AppUtilities.setUserProfile(
                itemBinding.root.context,
                itemBinding.rivProfile,
                user.profilePicture
            )

//            if (user.isSelected()) {
//                if (user.isAlreadyGroupMember()) {
//                    itemBinding.ivCheck.setImageDrawable(AppCompatResources.getDrawable(itemBinding.getRoot().getContext(), R.drawable.ic_checkbox_off_background));
//                } else {
//                    itemBinding.ivCheck.setImageDrawable(AppCompatResources.getDrawable(itemBinding.getRoot().getContext(), R.drawable.ic_checkbox_on_background));
//                }
//                itemBinding.ivCheck.setVisibility(View.VISIBLE);
//
//            } else {
//                itemBinding.ivCheck.setImageDrawable(null);
//                itemBinding.ivCheck.setVisibility(View.INVISIBLE);
//            }
            Utilities.verifiedUserType(
                itemBinding.root.context,
                user.userTypeId,
                itemBinding.ivIconVerified
            )
        }

        override fun onClick(v: View) {}

        init {
            itemBinding = ItemFriendBinding.bind(itemView)
        }
    }
}