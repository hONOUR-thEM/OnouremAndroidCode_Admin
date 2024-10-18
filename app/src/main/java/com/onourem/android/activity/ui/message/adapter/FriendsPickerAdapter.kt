package com.onourem.android.activity.ui.message.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemFriendAddBinding
import com.onourem.android.activity.models.UserList
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*

class FriendsPickerAdapter(
    private val onItemClickListener: OnItemClickListener<UserList>?,
) : RecyclerView.Adapter<FriendsPickerAdapter.SongsViewHolder>(), Filterable {
    private var list = mutableListOf<UserList>()
    private var listFiltered = mutableListOf<UserList>()

    var options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SongsViewHolder {
        return SongsViewHolder(
            LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_friend_add, viewGroup, false)
        )
    }

    override fun onBindViewHolder(viewHolder: SongsViewHolder, position: Int) {
        val song = listFiltered[position]
        viewHolder.bind(song)
    }

    override fun getItemCount(): Int {
        return listFiltered.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addSongs(songs: MutableList<UserList>) {
        list.clear()
        list.addAll(songs)
        listFiltered.clear()
        listFiltered.addAll(songs)
        notifyDataSetChanged()
    }

    inner class SongsViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        private val rowBinding: ItemFriendAddBinding? = DataBindingUtil.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bind(user: UserList) {

            rowBinding!!.tvName.text = user.firstName + " " + user.lastName
            rowBinding!!.ivCheck.visibility = View.GONE
            Glide.with(itemView.context)
                .load(user.profilePicture)
                .apply(options)
                .into(rowBinding!!.rivProfile)

        }

        init {
            rowBinding!!.parent.setOnClickListener(ViewClickListener {
                onItemClickListener?.onItemClick(listFiltered[bindingAdapterPosition])
            })
        }
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    listFiltered = list
                } else {
                    val filteredList: MutableList<UserList> = ArrayList()
                    for (row in list) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.firstName!!.lowercase(Locale.ROOT).contains(
                                charString.lowercase(
                                    Locale.ROOT
                                )
                            ) || row.lastName!!.lowercase(Locale.ROOT).contains(
                                charString.lowercase(
                                    Locale.ROOT
                                )
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }
                    listFiltered = filteredList
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

}
