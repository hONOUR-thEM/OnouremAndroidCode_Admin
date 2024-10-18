package com.onourem.android.activity.ui.admin.main.portal

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemOclubSettingsRowBinding
import com.onourem.android.activity.databinding.ItemPortalUserRowBinding
import com.onourem.android.activity.models.PortalUserItem
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class PortalUsersAdapter(
    private val items: ArrayList<String>,
    private var onItemClickListener: OnItemClickListener<Triple<Int, String, Int>>
) : RecyclerView.Adapter<PortalUsersAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_portal_user_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val options = items[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class SettingsViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        private val rowBinding: ItemPortalUserRowBinding? = DataBindingUtil.bind(itemView)
        var options = RequestOptions()
            .fitCenter()

        @SuppressLint("SetTextI18n")
        fun bind(item: String) {
            rowBinding!!.tvName.text = item
        }

        init {
//            rowBinding?.tvCommentsEnabledValue?.setOnClickListener(ViewClickListener { v1: View? ->
//                val oClubSettingsItem = items[bindingAdapterPosition]
//                onItemClickListener.onItemClick(Triple(CLICK_COMMENTS, oClubSettingsItem, bindingAdapterPosition))
//            })
//
//            rowBinding?.tvInviteLinkEnabledValue?.setOnClickListener(ViewClickListener { v1: View? ->
//                val oClubSettingsItem = items[bindingAdapterPosition]
//                onItemClickListener.onItemClick(Triple(CLICK_LINKS, oClubSettingsItem, bindingAdapterPosition))
//            })
        }

    }

    companion object {
        var CLICK_LINKS = 6
        var CLICK_COMMENTS = 7
    }
}