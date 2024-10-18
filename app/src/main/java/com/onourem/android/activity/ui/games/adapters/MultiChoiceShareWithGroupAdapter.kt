package com.onourem.android.activity.ui.games.adapters

import android.text.TextUtils
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedImageView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.GroupMember
import com.onourem.android.activity.models.PlayGroup
import com.onourem.android.activity.models.SettingsItem
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*

class MultiChoiceShareWithGroupAdapter(private val playGroups: List<PlayGroup>,private val onItemClickListener: OnItemClickListener<Pair<Int, PlayGroup>>?,
) :
    RecyclerView.Adapter<MultiChoiceShareWithGroupAdapter.PrivacyViewHolder>(), Filterable {
    private var playGroupsFiltered: List<PlayGroup>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivacyViewHolder {
        return PrivacyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_share_with_group, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PrivacyViewHolder, position: Int) {
        val playGroup = playGroupsFiltered[position]
        holder.bind(playGroup)
    }

    override fun getItemCount(): Int {
        return playGroupsFiltered.size
    }

    val selected: List<PlayGroup>
        get() {
            val selected: MutableList<PlayGroup> = ArrayList()
            for (playGroup in playGroupsFiltered) {
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
                playGroupsFiltered = if (charString.isEmpty()) {
                    playGroups
                } else {
                    val filteredList: MutableList<PlayGroup> = ArrayList()
                    for (row in playGroups) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.playGroupName!!.lowercase(Locale.getDefault()).contains(
                                charString.lowercase(
                                    Locale.getDefault()
                                )
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = playGroupsFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                playGroupsFiltered = filterResults.values as ArrayList<PlayGroup>
                notifyDataSetChanged()
            }
        }
    }

    inner class PrivacyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: AppCompatImageView
        private val textView: AppCompatTextView
        private val ivIconVerified: RoundedImageView
        fun bind(playGroup: PlayGroup) {
            textView.text = playGroup.playGroupName
            if (playGroup.isSelected && !playGroup.isDisable) {
                imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        imageView.context,
                        R.drawable.ic_checkbox_on_background
                    )
                )
            } else if (playGroup.isDisable) {
                imageView.setImageDrawable(
                    AppCompatResources.getDrawable(
                        imageView.context,
                        R.drawable.ic_checkbox_off_background
                    )
                )
            } else {
                imageView.setImageDrawable(null)
            }
            if (!TextUtils.isEmpty(playGroup.playGroupTypeId) && playGroup.playGroupTypeId.equals(
                    "1",
                    ignoreCase = true
                )
            ) {
                ivIconVerified.visibility = View.VISIBLE
            } else {
                ivIconVerified.visibility = View.GONE
            }
        }

        init {
            view.setOnClickListener(ViewClickListener { v: View? ->

                if (onItemClickListener != null) {
                    val settingsItem = playGroupsFiltered[bindingAdapterPosition]
                    if(!settingsItem.isDisable){
                        onItemClickListener.onItemClick(Pair(bindingAdapterPosition, settingsItem))
                    }
                }

            })
            textView = view.findViewById(R.id.tvTitle)
            imageView = view.findViewById(R.id.ivCheck)
            ivIconVerified = view.findViewById(R.id.ivIconVerified)
        }
    }

    init {
        playGroupsFiltered = playGroups
    }
}