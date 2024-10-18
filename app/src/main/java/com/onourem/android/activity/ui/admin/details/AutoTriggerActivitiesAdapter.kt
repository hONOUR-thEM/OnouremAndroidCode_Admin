package com.onourem.android.activity.ui.admin.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemSettingsRowBinding
import com.onourem.android.activity.models.SettingsItem
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class AutoTriggerActivitiesAdapter(
    private val settingsItems: ArrayList<SettingsItem>,
    private val onItemClickListener: OnItemClickListener<SettingsItem>?
) : RecyclerView.Adapter<AutoTriggerActivitiesAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_settings_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val options = settingsItems[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return settingsItems.size
    }

    inner class SettingsViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        private val rowBinding: ItemSettingsRowBinding? = DataBindingUtil.bind(itemView)
        var options = RequestOptions()
            .fitCenter()

        fun bind(settingsItem: SettingsItem) {
            Glide.with(rowBinding!!.rivProfile)
                .load(settingsItem.image)
                .apply(options)
                .into(rowBinding!!.rivProfile)
            rowBinding!!.tvName.text = settingsItem.title
        }

        init {
            itemView.setOnClickListener(ViewClickListener { v: View? ->
                if (onItemClickListener != null) {
                    val settingsItem = settingsItems[bindingAdapterPosition]
                    onItemClickListener.onItemClick(settingsItem)
                }
            })
        }
    }
}