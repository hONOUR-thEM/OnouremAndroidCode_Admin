package com.onourem.android.activity.ui.games.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.makeramen.roundedimageview.RoundedImageView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*

class MultiChoiceShareWithFriendsAdapter(private val list: List<UserList>) :
    RecyclerView.Adapter<MultiChoiceShareWithFriendsAdapter.FriendsViewHolder>(), Filterable {
    private val options = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private var listFiltered: List<UserList>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        return FriendsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_search, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val userList = listFiltered[position]
        holder.bind(userList)
    }

    override fun getItemCount(): Int {
        return listFiltered.size
    }

    val selected: List<UserList>
        get() {
            val selected: MutableList<UserList> = ArrayList()
            for (playGroup in listFiltered) {
                if (playGroup.isSelected) {
                    selected.add(playGroup)
                }
            }
            return selected
        }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                listFiltered = if (charString.isEmpty()) {
                    list
                } else {
                    val filteredList: MutableList<UserList> = ArrayList()
                    for (row in list) {

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
                filterResults.values = listFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                listFiltered = filterResults.values as ArrayList<UserList>
                notifyDataSetChanged()
            }
        }
    }

    inner class FriendsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: AppCompatImageView
        private val textView: AppCompatTextView
        private val rivProfile: RoundedImageView
        private val ivIconVerified: RoundedImageView
        fun bind(userList: UserList) {
            Glide.with(itemView.context)
                .load(userList.profilePicture)
                .apply(options)
                .into(rivProfile)
            textView.text = String.format("%s %s", userList.firstName, userList.lastName)
            if (userList.isSelected) {
                imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        imageView.context,
                        R.drawable.ic_checkbox_on_background
                    )
                )
                imageView.visibility = View.VISIBLE
            } else {
                imageView.setImageDrawable(null)
                imageView.visibility = View.INVISIBLE
            }
            Utilities.verifiedUserType(itemView.context, userList.userType, ivIconVerified)
        }

        init {
            view.setOnClickListener(ViewClickListener { v: View? ->
                val position = bindingAdapterPosition
                val clickedItem = listFiltered[position]
                clickedItem.isSelected = !clickedItem.isSelected
                notifyItemChanged(position, clickedItem)
            })
            textView = view.findViewById(R.id.tvName)
            rivProfile = view.findViewById(R.id.rivProfile)
            val rivProfileIcon = view.findViewById<RoundedImageView>(R.id.rivProfileIcon)
            ivIconVerified = view.findViewById(R.id.ivIconVerified)
            rivProfileIcon.visibility = View.GONE
            imageView = view.findViewById(R.id.ivCheck)
        }
    }

    init {
        listFiltered = list
    }
}