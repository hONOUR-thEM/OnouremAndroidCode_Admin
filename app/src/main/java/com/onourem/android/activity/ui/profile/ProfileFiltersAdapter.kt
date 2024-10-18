package com.onourem.android.activity.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemProfileFilterBinding
import com.onourem.android.activity.databinding.ItemProfileRowBinding
import com.onourem.android.activity.databinding.ItemSettingsRowBinding
import com.onourem.android.activity.models.ProfileFilterItem
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class ProfileFiltersAdapter(
    private val items: ArrayList<ProfileFilterItem>,
    private val onItemClickListener: OnItemClickListener<ProfileFilterItem>?
) : RecyclerView.Adapter<ProfileFiltersAdapter.ProfileFilterHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileFilterHolder {
        return ProfileFilterHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_profile_filter, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProfileFilterHolder, position: Int) {
        val options = items[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ProfileFilterHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        private val rowBinding: ItemProfileFilterBinding? = DataBindingUtil.bind(itemView)
        var options = RequestOptions()
            .fitCenter()

        fun bind(item: ProfileFilterItem) {
            rowBinding!!.tvHeading.text = item.title
        }

        init {
            itemView.setOnClickListener(ViewClickListener {
                if (onItemClickListener != null) {
                    val settingsItem = items[bindingAdapterPosition]
                    onItemClickListener.onItemClick(settingsItem)
                }
            })
        }
    }
}