package com.onourem.android.activity.ui.people

import com.onourem.android.activity.ui.games.fragments.UserRelation.Companion.getStatus
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import android.view.ViewGroup
import com.onourem.android.activity.ui.survey.BaseViewHolder
import android.view.LayoutInflater
import com.onourem.android.activity.ui.games.fragments.UserRelation
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.people.UserListAdapter
import android.widget.Filter.FilterResults
import android.annotation.SuppressLint
import android.util.Pair
import android.view.View
import com.onourem.android.activity.ui.people.UserListFOFAdapter
import javax.inject.Inject
import com.onourem.android.activity.repository.UserListRepositoryImpl
import com.onourem.android.activity.repository.FriendsRepositoryImpl
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.repository.UserListRepository
import com.onourem.android.activity.repository.FriendsRepository
import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.ItemUserSearchRowBinding
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.UserActionStandardResponse
import com.onourem.android.activity.models.BlockListResponse
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import java.util.*

class UserListAdapter(
    private val visibleDataList: List<UserList>,
    private val onItemClickListener: OnItemClickListener<Pair<String, UserList>>?
) : PaginationRVAdapter<UserList?>(
    visibleDataList
) {
    private val options = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)

    override fun emptyLoadingItem(): UserList {
        return UserList()
    }

    override fun emptyFooterItem(): UserList {
        return UserList()
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return FriendViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_search_row, parent, false)
        )
    }

    fun notifyDataUpdated(data: UserList) {
        notifyItemChanged(visibleDataList.indexOf(data))
    }

    internal inner class FriendViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val itemBinding: ItemUserSearchRowBinding
        override fun onBind(position: Int) {
            itemBinding.tvIgnore.visibility = View.GONE
            itemBinding.tvIsFriend.visibility = View.GONE
            val user = items[position]!!
            itemBinding.tvName.text =
                String.format(Locale.getDefault(), "%s %s", user.firstName, user.lastName)
            when (getStatus(user.status)) {
                UserRelation.FRIENDS -> {
                    itemBinding.tvIsFriend.backgroundTintList =
                        AppCompatResources.getColorStateList(itemView.context, R.color.color_blue)
                    itemBinding.tvIsFriend.text = "\u2713 Friends"
                    itemBinding.tvIsFriend.visibility = View.VISIBLE
                }
                UserRelation.ADD_FRIEND -> {
                    itemBinding.tvIsFriend.backgroundTintList =
                        AppCompatResources.getColorStateList(itemView.context, R.color.color_green)
                    itemBinding.tvIsFriend.text = "\u002B Friend"
                    itemBinding.tvIsFriend.visibility = View.VISIBLE
                }
                UserRelation.ACCEPT_REQUEST -> {
                    //                    DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.color_green));
                    itemBinding.tvIsFriend.backgroundTintList =
                        AppCompatResources.getColorStateList(itemView.context, R.color.color_green)
                    itemBinding.tvIsFriend.text = "Accept"
                    //                    Drawable ignoreDrawable = AppCompatResources.getDrawable(itemView.getContext(), R.drawable.shape_filled_rectangle_friend_status);
//                    DrawableCompat.setTint(ignoreDrawable, itemView.getResources().getColor(R.color.color_red));
                    itemBinding.tvIgnore.text = "Ignore"
                    itemBinding.tvIgnore.backgroundTintList =
                        AppCompatResources.getColorStateList(itemView.context, R.color.color_pink)
                    //                    itemBinding.tvIgnore.setBackgroundDrawable(ignoreDrawable);
                    itemBinding.tvIgnore.visibility = View.VISIBLE
                    itemBinding.tvIsFriend.visibility = View.VISIBLE
                }
                UserRelation.CANCEL_REQUEST -> {
                    //                    DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.color_red));
                    itemBinding.tvIsFriend.backgroundTintList =
                        AppCompatResources.getColorStateList(itemView.context, R.color.color_pink)
                    itemBinding.tvIsFriend.text = "Cancel"
                    itemBinding.tvIsFriend.visibility = View.VISIBLE
                }
                UserRelation.NONE -> {}
            }
            Glide.with(itemView.context)
                .load(user.profilePicture)
                .apply(options)
                .into(itemBinding.rivProfile)
            Utilities.verifiedUserType(
                itemBinding.root.context,
                user.userType,
                itemBinding.ivIconVerified
            )
        }

        init {
            itemBinding = ItemUserSearchRowBinding.bind(itemView)
            itemBinding.tvIgnore.setOnClickListener(ViewClickListener { v: View? ->
                val user = visibleDataList[bindingAdapterPosition]
                onItemClickListener?.onItemClick(Pair(ACTION_BUTTON_IGNORE_FRIEND, user))
            })
            itemBinding.tvIsFriend.setOnClickListener(ViewClickListener { v: View? ->
                val user = visibleDataList[bindingAdapterPosition]
                onItemClickListener?.onItemClick(Pair(ACTION_BUTTON_IS_FRIEND, user))
            })
            itemView.setOnClickListener(ViewClickListener { v: View? ->
                val user = visibleDataList[bindingAdapterPosition]
                onItemClickListener?.onItemClick(Pair(ACTION_ROW, user))
            })
        }
    }

    companion object {
        const val ACTION_BUTTON_IS_FRIEND = "ACTION_IS_FRIEND"
        const val ACTION_BUTTON_IGNORE_FRIEND = "ACTION_IGNORE_FRIEND"
        const val ACTION_ROW = "ROW"
    }
}