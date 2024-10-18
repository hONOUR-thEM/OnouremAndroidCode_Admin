package com.onourem.android.activity.ui.games.adapters

import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemFriendRowBinding
import com.onourem.android.activity.models.PlayGroupUserInfoList
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.games.fragments.UserRelation
import com.onourem.android.activity.ui.games.fragments.UserRelation.Companion.getStatus
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class MembersListAdapter(
    private val visibleDataList: List<PlayGroupUserInfoList>,
    private val preferenceHelper: SharedPreferenceHelper,
    private val createdBy: String,
    private val onItemClickListener: OnItemClickListener<Pair<String, PlayGroupUserInfoList>>?,
    private val loggedInUserId: String,
    private val isUserAdmin: String
) : RecyclerView.Adapter<MembersListAdapter.FriendViewHolder>() {
    private val options = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)

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

    fun notifyDataUpdated(data: PlayGroupUserInfoList) {
        notifyItemChanged(visibleDataList.indexOf(data))
    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemBinding: ItemFriendRowBinding
        fun bind(user: PlayGroupUserInfoList) {
            itemBinding.tvIgnore.visibility = View.GONE
            itemBinding.tvIsFriend.visibility = View.GONE
            itemBinding.tvName.text = user.userName
            when (getStatus(user.userRelation)) {
                UserRelation.FRIENDS -> {
                    //                    DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.color_light_blue));
                    itemBinding.tvIsFriend.backgroundTintList =
                        AppCompatResources.getColorStateList(itemView.context, R.color.color_blue)
                    itemBinding.tvIsFriend.text = "\u2713 Friends"
                    itemBinding.tvIsFriend.visibility = View.VISIBLE
                }
                UserRelation.ADD_FRIEND -> {
                    //                    DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.color_green));
                    itemBinding.tvIsFriend.backgroundTintList =
                        AppCompatResources.getColorStateList(itemView.context, R.color.color_green)
                    itemBinding.tvIsFriend.text = "\u002B Friend"
                    itemBinding.tvIsFriend.visibility = View.VISIBLE
                }
                UserRelation.ACCEPT_REQUEST -> {
                    //                    DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.color_green));
                    itemBinding.tvIsFriend.text = "Accept"
                    itemBinding.tvIsFriend.backgroundTintList =
                        AppCompatResources.getColorStateList(itemView.context, R.color.color_green)
                    //                    Drawable ignoreDrawable = AppCompatResources.getDrawable(itemView.getContext(), R.drawable.shape_filled_rectangle_friend_status);
//                    DrawableCompat.setTint(ignoreDrawable, itemView.getResources().getColor(R.color.color_pink));
                    itemBinding.tvIgnore.text = "Ignore"
                    itemBinding.tvIgnore.backgroundTintList =
                        AppCompatResources.getColorStateList(itemView.context, R.color.color_pink)

//                    itemBinding.tvIgnore.setBackgroundDrawable(ignoreDrawable);
                    itemBinding.tvIsFriend.visibility = View.VISIBLE
                    itemBinding.tvIgnore.visibility = View.VISIBLE
                }
                UserRelation.CANCEL_REQUEST -> {
                    //                    DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.color_pink));
                    itemBinding.tvIsFriend.text = "Cancel"
                    itemBinding.tvIsFriend.backgroundTintList =
                        AppCompatResources.getColorStateList(itemView.context, R.color.color_pink)
                    itemBinding.tvIsFriend.visibility = View.VISIBLE
                }
                UserRelation.NONE -> {}
            }
            if (loggedInUserId.equals(user.userId, ignoreCase = true) || isUserAdmin.equals(
                    "C",
                    ignoreCase = true
                ) || isUserAdmin.equals("Y", ignoreCase = true)
            ) {
                if (createdBy.equals(
                        user.userId,
                        ignoreCase = true
                    ) && !loggedInUserId.equals(user.userId, ignoreCase = true)
                ) {
                    itemBinding.ivUserMoreAction.visibility = View.INVISIBLE
                } else {
                    itemBinding.ivUserMoreAction.visibility = View.VISIBLE
                }
            } else {
                itemBinding.ivUserMoreAction.visibility = View.INVISIBLE
            }
            if (user.isUserAdmin.equals("Y", ignoreCase = true)) {
                itemBinding.tvAdmin.visibility = View.VISIBLE
                itemBinding.tvAdmin.text = itemView.context.getString(R.string.label_admin)
            } else if (user.isUserAdmin.equals("C", ignoreCase = true)) {
                itemBinding.tvAdmin.visibility = View.VISIBLE
                itemBinding.tvAdmin.text = itemView.context.getString(R.string.label_creator)
            } else {
                itemBinding.tvAdmin.visibility = View.GONE
            }
            //            boolean isAdmin = !TextUtils.isEmpty(user.getIsUserAdmin()) && user.getIsUserAdmin().equalsIgnoreCase("Y");
//
//            itemBinding.tvAdmin.setVisibility(!isAdmin ? View.GONE : View.VISIBLE);

//            itemBinding.tvIsFriend.setBackgroundDrawable(drawable);
            Glide.with(itemView.context)
                .load(user.userProfilePicUrl)
                .apply(options)
                .into(itemBinding.rivProfile)
            Utilities.verifiedUserType(
                itemBinding.root.context,
                user.userType,
                itemBinding.ivIconVerified
            )
        }

        init {
            itemBinding = ItemFriendRowBinding.bind(itemView)
            itemBinding.tvIsFriend.visibility = View.VISIBLE
            itemBinding.ivUserMoreAction.visibility = View.VISIBLE
            itemBinding.tvIgnore.setOnClickListener(ViewClickListener { v: View? ->
                val user = visibleDataList[bindingAdapterPosition]
                onItemClickListener?.onItemClick(Pair(ACTION_IGNORE_FRIEND, user))
            })
            itemBinding.tvIsFriend.setOnClickListener(ViewClickListener { v: View? ->
                val user = visibleDataList[bindingAdapterPosition]
                onItemClickListener?.onItemClick(Pair(ACTION_IS_FRIEND, user))
            })
            itemBinding.ivUserMoreAction.setOnClickListener(ViewClickListener { v: View? ->
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
        const val ACTION_IS_FRIEND = "ACTION_IS_FRIEND"
        const val ACTION_IGNORE_FRIEND = "ACTION_IGNORE_FRIEND"
        const val ACTION_MORE = "MORE"
        const val ACTION_ROW = "ROW"
    }
}