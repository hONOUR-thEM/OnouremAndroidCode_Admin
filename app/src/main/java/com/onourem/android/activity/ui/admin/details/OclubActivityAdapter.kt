package com.onourem.android.activity.ui.admin.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemAutoTriggeredOclubActivityBinding
import com.onourem.android.activity.models.AutoTrigggerDailyActivityRes
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class OclubActivityAdapter(
    private val settingsItems: ArrayList<AutoTrigggerDailyActivityRes>,
    private val onItemClickListener: OnItemClickListener<AutoTrigggerDailyActivityRes>?
) : RecyclerView.Adapter<OclubActivityAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_auto_triggered_oclub_activity, parent, false)
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
        private val itemBinding: ItemAutoTriggeredOclubActivityBinding? = DataBindingUtil.bind(itemView)
        var options = RequestOptions()
            .fitCenter()

        fun bind(adminActivityResponse: AutoTrigggerDailyActivityRes) {
            itemBinding?.tvQuestionText?.text = adminActivityResponse.activityText

            itemBinding?.txtQuestionFor?.text =
                String.format(
                    "ID: %s | ActivityType: %s | OclubCategory: %s | \nStatus: %s | DayNumber: %s | DayPriority: %s",
                    adminActivityResponse.activityId,
                    adminActivityResponse.activityType,
                    adminActivityResponse.categoryName,
                    if (adminActivityResponse.status == "0") {
                        "Active"
                    } else {
                        "In-Active"
                    },
                    adminActivityResponse.dayNumber,
                    adminActivityResponse.dayPriority
                )

            if (adminActivityResponse.status == "0") {
                itemBinding?.txtQuestionFor?.setTextColor(
                    ContextCompat.getColor(
                        itemBinding.root.context,
                        R.color.good_green_color
                    )
                )
            } else {
                itemBinding?.txtQuestionFor?.setTextColor(
                    ContextCompat.getColor(
                        itemBinding.root.context,
                        R.color.good_red_color_as
                    )
                )
            }


//            itemBinding?.txtQuestionFor?.setTextColor(
//                ContextCompat.getColor(
//                    itemBinding.root.context,
//                    R.color.good_green_color
//                )
//            )

//            if (!TextUtils.isEmpty(adminActivityResponse.activityImageUrl)) {
//                itemBinding?.ivQuestionImage?.visibility = View.VISIBLE
//                Glide.with(itemBinding?.root!!.context)
//                    .load(adminActivityResponse.activityImageUrl)
//                    .apply(options)
//                    .into(itemBinding.ivQuestionImage)
//
//                if (!TextUtils.isEmpty(adminActivityResponse.activityVideoUrl) || !TextUtils.isEmpty(adminActivityResponse.youtubeLink)) {
//                    itemBinding.ivPlayVideo.visibility = View.VISIBLE
//                } else {
//                    itemBinding.ivPlayVideo.visibility = View.GONE
//                }
//            } else {
//                itemBinding.ivQuestionImage.visibility = View.GONE
//            }
        }

        init {
            itemBinding?.txtDate?.setOnClickListener(ViewClickListener { v: View? ->
                if (onItemClickListener != null) {
                    val settingsItem = settingsItems[bindingAdapterPosition]
                    onItemClickListener.onItemClick(settingsItem)
                }
            })
        }
    }
}