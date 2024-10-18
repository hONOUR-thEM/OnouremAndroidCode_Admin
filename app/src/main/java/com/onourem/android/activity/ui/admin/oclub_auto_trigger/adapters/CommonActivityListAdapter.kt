package com.onourem.android.activity.ui.admin.oclub_auto_trigger.adapters

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemAdminQuestionRowBinding
import com.onourem.android.activity.ui.admin.create.question_schedule.AdminActivityResponse
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.onourem.android.activity.ui.utils.Events
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

internal class CommonActivityListAdapter(
    adminActivityResponseList: MutableList<AdminActivityResponse>,
    private var onItemClickListener: OnItemClickListener<Triple<Int, AdminActivityResponse, Int>>
) : PaginationRVAdapter<AdminActivityResponse?>(
    ArrayList(
        adminActivityResponseList
    )
) {

    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(1)) //15
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)


    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return SingleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_admin_question_row, parent, false)
        )
    }

    override fun emptyLoadingItem(): AdminActivityResponse {
        return AdminActivityResponse()
    }

    override fun emptyFooterItem(): AdminActivityResponse {
        return AdminActivityResponse()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<Triple<Int, AdminActivityResponse, Int>>) {
        this.onItemClickListener = onItemClickListener
    }

    fun modifyItem(position: Int, model: AdminActivityResponse) {
        items[position] = model
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    internal inner class SingleViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val itemBinding: ItemAdminQuestionRowBinding = ItemAdminQuestionRowBinding.bind(itemView)

        init {
            itemBinding.root.setOnClickListener(ViewClickListener { v1: View? ->
                val notificationItem = items[bindingAdapterPosition]
                onItemClickListener.onItemClick(Triple(CLICK_WHOLE, notificationItem, bindingAdapterPosition))
            })

            itemBinding.txtSubText.setOnClickListener(ViewClickListener { v1: View? ->
                val notificationItem = items[bindingAdapterPosition]
                onItemClickListener.onItemClick(Triple(CLICK_TRIGGER_IN_OCLUB, notificationItem, bindingAdapterPosition))

            })

            itemBinding.txtDelete.setOnClickListener(ViewClickListener { view: View? ->
                val notificationItem = items[bindingAdapterPosition]
                if (!notificationItem!!.activityTriggered && !notificationItem.activityScheduled) {
                    onItemClickListener.onItemClick(Triple(CLICK_DELETE, notificationItem, bindingAdapterPosition))
                }
            })
        }

        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {

            val adminActivityResponse = items[position]

            itemBinding.tvQuestionText.text = adminActivityResponse!!.activityText

            itemBinding.txtDate.text = if (adminActivityResponse.activityTriggered) {
                "Triggered On ${Events.getServerTimeConversation(adminActivityResponse.scheduledTime!!)}"
            } else if (adminActivityResponse.activityScheduled) {
                "Scheduled For ${Events.getServerTimeConversation(adminActivityResponse.scheduledTime!!)}"
            } else {
                "Created On  ${Events.getServerTimeConversation(adminActivityResponse.createdTime!!)}"
            }

            itemBinding.txtQuestionFor.text =
                String.format(
                    "ID: %s | ActivityType: %s | Category: %s",
                    adminActivityResponse.activityId,
                    adminActivityResponse.activityType,
                    if (adminActivityResponse.activityType == "Card") {
                        "Card"
                    } else {
                        "Question"
                    }
                )

            itemBinding.txtStatus.text = if (adminActivityResponse.activityTriggered) {
                "Triggered"
            } else if (adminActivityResponse.activityScheduled) {
                "Scheduled"
            } else {
                "Unscheduled"
            }


            itemBinding.txtQuestionFor.setTextColor(
                ContextCompat.getColor(
                    itemBinding.root.context,
                    R.color.black
                )
            )

            itemBinding.txtSubText.text = "Auto Trigger"

            itemBinding.txtNotify.visibility = View.GONE

//            if (selectedRadioButton.equals("Unscheduled", ignoreCase = true)) {
//                itemBinding.txtNotify.visibility = View.GONE
//            } else {
//                itemBinding.txtNotify.visibility = View.VISIBLE
//            }

//            itemBinding.txtSubText.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorPrimary))

            if (!TextUtils.isEmpty(adminActivityResponse.activityImageUrl)) {
                itemBinding.ivQuestionImage.visibility = View.VISIBLE
                Glide.with(itemBinding.root.context)
                    .load(adminActivityResponse.activityImageUrl)
                    .apply(options)
                    .into(itemBinding.ivQuestionImage)

                if (!TextUtils.isEmpty(adminActivityResponse.activityVideoUrl) || !TextUtils.isEmpty(adminActivityResponse.youtubeLink)) {
                    itemBinding.ivPlayVideo.visibility = View.VISIBLE
                } else {
                    itemBinding.ivPlayVideo.visibility = View.GONE
                }
            } else {
                itemBinding.ivQuestionImage.visibility = View.GONE
            }

            itemBinding.parent.setCardBackgroundColor(
                if (adminActivityResponse!!.activityTriggered && adminActivityResponse.activityScheduled) {
                    ContextCompat.getColor(itemView.context, R.color.good_green_color_as)
                } else if (!adminActivityResponse!!.activityTriggered && adminActivityResponse.activityScheduled) {
                    ContextCompat.getColor(itemView.context, R.color.color_bg_dashboard)
                } else {
                    ContextCompat.getColor(itemView.context, R.color.gray_color)
                }
            )

        }
    }

    companion object {
        var CLICK_TRIGGER_IN_OCLUB = 0
        var CLICK_DELETE = 2
        var CLICK_WHOLE = 1
    }
}