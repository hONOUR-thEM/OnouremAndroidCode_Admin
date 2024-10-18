package com.onourem.android.activity.ui.admin.main.subscriptions.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemPackageItemBinding
import com.onourem.android.activity.databinding.ItemSettingsRowBinding
import com.onourem.android.activity.models.InstituteNameId
import com.onourem.android.activity.models.PackageNameId
import com.onourem.android.activity.models.SettingsItem
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class InstituteAdapter(
    private val settingsItems: ArrayList<InstituteNameId>,
    private val onItemClickListener: OnItemClickListener<InstituteNameId>?
) : RecyclerView.Adapter<InstituteAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_package_item, parent, false)
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
        private val rowBinding: ItemPackageItemBinding? = DataBindingUtil.bind(itemView)
        var options = RequestOptions()
            .fitCenter()

        @SuppressLint("SetTextI18n")
        fun bind(settingsItem: InstituteNameId) {
            rowBinding!!.tvName.text = settingsItem.name?.substringAfter("=")
            rowBinding.txtDate.text = "Institute Id = " + settingsItem.id
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