package com.onourem.android.activity.ui.admin.main.subscriptions.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemOclubSettingsRowBinding
import com.onourem.android.activity.models.OClubSettingsItem
import com.onourem.android.activity.ui.admin.create.surveys.AdminSurveyResponse
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class PlayGroupsAdapter(
    private val settingsItems: ArrayList<OClubSettingsItem>,
    private var onItemClickListener: OnItemClickListener<Triple<Int, OClubSettingsItem, Int>>
) : RecyclerView.Adapter<PlayGroupsAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_oclub_settings_row, parent, false)
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
        private val rowBinding: ItemOclubSettingsRowBinding? = DataBindingUtil.bind(itemView)
        var options = RequestOptions()
            .fitCenter()

        @SuppressLint("SetTextI18n")
        fun bind(cClubSettingsItem: OClubSettingsItem) {
            rowBinding!!.tvName.text = "${cClubSettingsItem.playGroupId} - ${cClubSettingsItem.playGroupName}"
            rowBinding.tvCommentsEnabledValue.text = if (cClubSettingsItem.commentsEnabled == "Y") {
                "Enabled"
            } else {
                "Disabled"
            }
            rowBinding.tvInviteLinkEnabledValue.text = if (cClubSettingsItem.inviteLinkEnabled == "Y") {
                "Enabled"
            } else {
                "Disabled"
            }

            rowBinding.tvInviteLinkEnabledValue.setTextColor(
                if (cClubSettingsItem.inviteLinkEnabled == "Y") {
                    ContextCompat.getColor(itemView.context, R.color.good_green_color)
                } else {
                    ContextCompat.getColor(itemView.context, R.color.good_red_color)
                }
            )

            rowBinding.tvCommentsEnabledValue.setTextColor(
                if (cClubSettingsItem.commentsEnabled == "Y") {
                    ContextCompat.getColor(itemView.context, R.color.good_green_color)
                } else {
                    ContextCompat.getColor(itemView.context, R.color.good_red_color)
                }
            )
        }

        init {
            rowBinding?.tvCommentsEnabledValue?.setOnClickListener(ViewClickListener { v1: View? ->
                val oClubSettingsItem = settingsItems[bindingAdapterPosition]
                onItemClickListener.onItemClick(Triple(CLICK_COMMENTS, oClubSettingsItem, bindingAdapterPosition))
            })

            rowBinding?.tvInviteLinkEnabledValue?.setOnClickListener(ViewClickListener { v1: View? ->
                val oClubSettingsItem = settingsItems[bindingAdapterPosition]
                onItemClickListener.onItemClick(Triple(CLICK_LINKS, oClubSettingsItem, bindingAdapterPosition))
            })
        }

    }

    companion object {
        var CLICK_LINKS = 6
        var CLICK_COMMENTS = 7
    }
}