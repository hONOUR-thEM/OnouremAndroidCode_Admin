package com.onourem.android.activity.ui.games.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemFriendAddBinding
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import java.util.*

class FriendsSearchListAdapter(
    private var visibleDataList: List<UserList>?,
    playgroupUserLimit: Int,
    onItemClickListener: OnItemClickListener<UserList>
) : RecyclerView.Adapter<FriendsSearchListAdapter.FriendViewHolder>(), Filterable {
    private val dataSetFilter = DataSetFilter()
    private val onItemClickListener: OnItemClickListener<UserList>
    private val playgroupUserLimit: Int
    private val options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private var originalDataList: List<UserList>?
    private var counter = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_friend_add, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(visibleDataList!![position])
    }

    override fun getItemCount(): Int {
        return visibleDataList!!.size
    }

    override fun getFilter(): Filter {
        return dataSetFilter
    }

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val itemBinding: ItemFriendAddBinding
        fun bind(user: UserList) {
            itemBinding.tvName.text = String.format("%s %s", user.firstName, user.lastName)
            Glide.with(itemView.context)
                .load(user.profilePicture)
                .apply(options)
                .into(itemBinding.rivProfile)
            if (user.isSelected) {
                if (user.isAlreadyGroupMember) {
                    itemBinding.ivCheck.setImageDrawable(
                        AppCompatResources.getDrawable(
                            itemBinding.root.context,
                            R.drawable.ic_checkbox_off_background
                        )
                    )
                } else {
                    itemBinding.ivCheck.setImageDrawable(
                        AppCompatResources.getDrawable(
                            itemBinding.root.context,
                            R.drawable.ic_checkbox_on_background
                        )
                    )
                }
                itemBinding.ivCheck.visibility = View.VISIBLE
            } else {
                itemBinding.ivCheck.setImageDrawable(null)
                itemBinding.ivCheck.visibility = View.INVISIBLE
            }
            Utilities.verifiedUserType(
                itemBinding.root.context,
                user.userType,
                itemBinding.ivIconVerified
            )
        }

        override fun onClick(v: View) {
            if (counter <= playgroupUserLimit) {
                val item = visibleDataList!![bindingAdapterPosition]
                if (!item.isAlreadyGroupMember) {
                    counter++
                    item.isSelected = !item.isSelected
                    notifyItemChanged(bindingAdapterPosition, item)
                    onItemClickListener.onItemClick(visibleDataList!![bindingAdapterPosition])
                } else {
                    Toast.makeText(
                        itemBinding.root.context,
                        item.firstName + " is already added to this O-Club",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    itemBinding.root.context,
                    "You can add maximum $playgroupUserLimit users at a time. You can add more friends by coming back to this screen again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        init {
            itemBinding = ItemFriendAddBinding.bind(itemView)
            //            itemBinding.getRoot().setOnClickListener(this);
//            itemBinding.rivProfile.setOnClickListener(this);
//            itemBinding.tvName.setOnClickListener(this);
            itemView.setOnClickListener(this)

//            itemView.setClickable(true);
//            itemBinding.rivProfile.setClickable(true);
//            itemBinding.tvName.setClickable(true);
//            itemBinding.getRoot().setClickable(true);
        }
    }

    inner class DataSetFilter : Filter() {
        private val lock = Any()
        override fun performFiltering(prefix: CharSequence): FilterResults {
            val results = FilterResults()
            if (originalDataList == null) {
                synchronized(lock) { originalDataList = ArrayList(visibleDataList) }
            }
            if (prefix == null || prefix.length == 0) {
                synchronized(lock) {
                    results.values = originalDataList
                    results.count = originalDataList!!.size
                }
            } else {
                val searchStrLowerCase = prefix.toString().lowercase(Locale.getDefault())
                val matchValues = ArrayList<UserList>()
                for (dataItem in originalDataList!!) {
                    if ((dataItem.firstName + " " + dataItem.lastName).lowercase(Locale.getDefault())
                            .startsWith(searchStrLowerCase)
                    ) {
                        matchValues.add(dataItem)
                    }
                }
                results.values = matchValues
                results.count = matchValues.size
            }
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            visibleDataList = if (results.values != null) {
                results.values as ArrayList<UserList>
            } else {
                null
            }
            notifyDataSetChanged()
        }
    }

    init {
        originalDataList = visibleDataList
        this.onItemClickListener = onItemClickListener
        this.playgroupUserLimit = playgroupUserLimit
    }
}