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
import android.widget.Filter
import android.widget.Filterable
import com.onourem.android.activity.ui.people.UserListFOFAdapter
import javax.inject.Inject
import com.onourem.android.activity.repository.UserListRepositoryImpl
import com.onourem.android.activity.repository.FriendsRepositoryImpl
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.repository.UserListRepository
import com.onourem.android.activity.repository.FriendsRepository
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.ItemUserSearchRowBinding
import com.onourem.android.activity.models.UserListResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.UserActionStandardResponse
import com.onourem.android.activity.models.BlockListResponse
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import java.util.*

class UserListFOFAdapter(
    private val visibleDataList: List<UserList>,
    onItemClickListener: OnItemClickListener<Pair<String, UserList>>?
) : RecyclerView.Adapter<UserListFOFAdapter.FriendViewHolder>(), Filterable {
    private val onItemClickListener: OnItemClickListener<Pair<String, UserList>>?
    private val options = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private var visibleDataListFiltered: List<UserList>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_search_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val item = visibleDataListFiltered[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return visibleDataListFiltered.size
    }

    fun notifyDataUpdated(data: UserList) {
        notifyItemChanged(visibleDataListFiltered.indexOf(data))
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                visibleDataListFiltered = if (charString.isEmpty()) {
                    visibleDataList
                } else {
                    val filteredList: MutableList<UserList> = ArrayList()
                    for (row in visibleDataList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.firstName!!.lowercase(Locale.getDefault())
                                .contains(charString.lowercase(Locale.getDefault()))
                            || row.lastName!!.contains(charSequence)
                        ) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = visibleDataListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                visibleDataListFiltered = filterResults.values as ArrayList<UserList>
                notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemBinding: ItemUserSearchRowBinding
        fun bind(user: UserList) {
            itemBinding.tvIgnore.visibility = View.GONE
            itemBinding.tvIsFriend.visibility = View.GONE
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
        }

        init {
            itemBinding = ItemUserSearchRowBinding.bind(itemView)
            itemBinding.tvIgnore.setOnClickListener(ViewClickListener { v: View? ->
                val user = visibleDataListFiltered[bindingAdapterPosition]
                onItemClickListener?.onItemClick(Pair(ACTION_BUTTON_IGNORE_FRIEND, user))
            })
            itemBinding.tvIsFriend.setOnClickListener(ViewClickListener { v: View? ->
                val user = visibleDataListFiltered[bindingAdapterPosition]
                onItemClickListener?.onItemClick(Pair(ACTION_BUTTON_IS_FRIEND, user))
            })
            itemView.setOnClickListener(ViewClickListener { v: View? ->
                val user = visibleDataListFiltered[bindingAdapterPosition]
                onItemClickListener?.onItemClick(Pair(ACTION_ROW, user))
            })
        }
    }

    companion object {
        const val ACTION_BUTTON_IS_FRIEND = "ACTION_IS_FRIEND"
        const val ACTION_BUTTON_IGNORE_FRIEND = "ACTION_IGNORE_FRIEND"
        const val ACTION_ROW = "ROW"
    }

    init {
        visibleDataListFiltered = visibleDataList
        this.onItemClickListener = onItemClickListener
    }
}