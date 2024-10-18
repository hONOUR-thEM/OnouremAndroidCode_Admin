package com.onourem.android.activity.ui.admin.create.adapters

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
import com.onourem.android.activity.ui.admin.create.external_posts.ExternalActivityData
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.onourem.android.activity.ui.utils.Events
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ExternalListAdapter(
    externalActivityDataList: MutableList<ExternalActivityData>,
    private var onItemClickListener: OnItemClickListener<Triple<Int, ExternalActivityData, Int>>
) : PaginationRVAdapter<ExternalActivityData?>(
    ArrayList(
        externalActivityDataList
    )
) {

    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(1)) //15
        .placeholder(R.drawable.shape_image_video_placeholder)
        .error(R.drawable.shape_image_video_placeholder)


    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return SingleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_admin_question_row, parent, false)
        )
    }

    override fun emptyLoadingItem(): ExternalActivityData {
        return ExternalActivityData()
    }

    override fun emptyFooterItem(): ExternalActivityData {
        return ExternalActivityData()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<Triple<Int, ExternalActivityData, Int>>) {
        this.onItemClickListener = onItemClickListener
    }

    fun modifyItem(position: Int, model: ExternalActivityData) {
        items[position] = model
        notifyItemChanged(position)
    }

//    fun removeItem(position: Int) {
//        items.removeAt(position)
//        notifyItemRemoved(position)
//    }

    @SuppressLint("NotifyDataSetChanged")
    override fun removeItem(item: ExternalActivityData?) {
        val position = items.indexOf(item)
//        items.remove(item)
        notifyItemRemoved(position)
//        notifyDataSetChanged()
    }

    internal inner class SingleViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val itemBinding: ItemAdminQuestionRowBinding
        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            val externalActivityData = items[position]

            itemBinding.tvQuestionText.text = externalActivityData!!.summary
           
            itemBinding.txtQuestionFor.text = "[${externalActivityData.id}] " + if (externalActivityData.status.equals(
                    "Y",
                    ignoreCase = true
                )
            ) "Active" else "Inactive"

            itemBinding.txtQuestionFor.setTextColor(
                if (externalActivityData.status.equals(
                        "Y",
                        ignoreCase = true
                    )
                ) ContextCompat.getColor(
                    itemBinding.root.context,
                    R.color.good_green_color
                ) else ContextCompat.getColor(itemBinding.root.context, R.color.color_red)
            )

//            itemBinding.cardUpdate.setCardBackgroundColor(
//                if (externalActivityData.status.equals(
//                        "Y",
//                        ignoreCase = true
//                    )
//                ) ContextCompat.getColor(
//                    itemBinding.root.context,
//                    R.color.white
//                ) else ContextCompat.getColor(itemBinding.root.context, R.color.white)
//            )
//
//            itemBinding.cardDate.setCardBackgroundColor(
//                if (externalActivityData.status.equals(
//                        "Y",
//                        ignoreCase = true
//                    )
//                ) ContextCompat.getColor(
//                    itemBinding.root.context,
//                    R.color.white
//                ) else ContextCompat.getColor(itemBinding.root.context, R.color.white)
//            )

            itemBinding.txtDate.text = if (externalActivityData.activityTriggered) {
                "Triggered On ${Events.getServerTimeConversation(externalActivityData.scheduledTime!!)}"
            } else if (externalActivityData.activityScheduled) {
                "Scheduled For ${Events.getServerTimeConversation(externalActivityData.scheduledTime!!)}"
            } else {
                "Created On  ${Events.getServerTimeConversation(externalActivityData.createdDate!!)}"
            }
//
//            if (!TextUtils.isEmpty(externalActivityData.videoUrl) || !TextUtils.isEmpty(externalActivityData.youtubeLink)) {
//                itemBinding.ivPlayVideo.visibility = View.VISIBLE
//            } else {
//                itemBinding.ivPlayVideo.visibility = View.GONE
//            }
//
//            if (!TextUtils.isEmpty(externalActivityData.imageUrl)) {
//                itemBinding.ivQuestionImage.visibility = View.VISIBLE
//            } else {
//                itemBinding.ivQuestionImage.visibility = View.GONE
//            }
//
//            Glide.with(itemBinding.root.context)
//                .load(externalActivityData.imageUrl)
//                .apply(options)
//                .into(itemBinding.ivQuestionImage)
            itemBinding.ivQuestionImage.tag = bindingAdapterPosition

            if (!TextUtils.isEmpty(externalActivityData.imageUrl)) {
                itemBinding.ivQuestionImage.visibility = View.VISIBLE
                Glide.with(itemBinding.root.context)
                    .load(externalActivityData.imageUrl)
                    .apply(options)
                    .into(itemBinding.ivQuestionImage)

                if (externalActivityData.youtubeLink == "Y") {
                    itemBinding.ivPlayVideo.visibility = View.VISIBLE
                } else {
                    itemBinding.ivPlayVideo.visibility = View.GONE
                }
            } else {
                itemBinding.ivQuestionImage.visibility = View.GONE
            }

            itemBinding.txtSubText.text = if (externalActivityData.activityTriggered) {
                "Triggered"
            } else if (externalActivityData.activityScheduled) {
                "Scheduled"
            } else {
                "Schedule"
            }

            if (externalActivityData.activityTriggered) {
                itemBinding.txtDelete.text = "Reschedule"
            } else if (externalActivityData.activityScheduled){
                itemBinding.txtDelete.text = "Edit"
            }else {
                itemBinding.txtDelete.text = "Delete"
            }

            itemBinding.txtNotify.text = "Update"
            itemBinding.txtNotify.visibility = View.VISIBLE


//            if (selectedRadioButton.equals("Unscheduled", ignoreCase = true)) {
//                itemBinding.txtNotify.visibility = View.GONE
//            } else {
//                itemBinding.txtNotify.visibility = View.VISIBLE
//            }

            itemBinding.txtSubText.setBackgroundColor(
                if (externalActivityData.activityTriggered) {
                    ContextCompat.getColor(itemView.context, R.color.good_green_color)
                } else if (externalActivityData.activityScheduled) {
                    ContextCompat.getColor(itemView.context, R.color.good_red_color)
                } else {
                    ContextCompat.getColor(itemView.context, R.color.colorPrimary)
                }
            )

        }

        init {
            itemBinding = ItemAdminQuestionRowBinding.bind(itemView)

            itemBinding.ivQuestionImage.setOnClickListener(ViewClickListener {
                val item = items[bindingAdapterPosition]
                onItemClickListener.onItemClick(Triple(CLICK_MEDIA, item, bindingAdapterPosition))
            })

            itemBinding.ivPlayVideo.setOnClickListener(ViewClickListener {
                val item = items[bindingAdapterPosition]
                onItemClickListener.onItemClick(Triple(CLICK_MEDIA, item, bindingAdapterPosition))
            })

            itemBinding.tvQuestionText.setOnClickListener(ViewClickListener {
                val item = items[bindingAdapterPosition]
                onItemClickListener.onItemClick(Triple(CLICK_MEDIA, item, bindingAdapterPosition))
            })

            itemBinding.txtNotify.setOnClickListener(ViewClickListener {
                val item = items[bindingAdapterPosition]
                onItemClickListener.onItemClick(Triple(CLICK_EDIT, item, bindingAdapterPosition))

//                when {
//                    selectedRadioButton.equals("Inactive", ignoreCase = true) -> {
//                        onItemClickListener.onItemClick(Triple(CLICK_EDIT, item, bindingAdapterPosition))
//                    }
//                    selectedRadioButton.equals("Active", ignoreCase = true) -> {
//                        onItemClickListener.onItemClick(Triple(CLICK_EDIT, item, bindingAdapterPosition))
//                    }
//                    selectedRadioButton.equals("Scheduled", ignoreCase = true) -> {
//                        onItemClickListener.onItemClick(Triple(CLICK_NOTIFY, item, bindingAdapterPosition))
//                    }
//                    else -> {
//                        onItemClickListener.onItemClick(Triple(CLICK_NOTIFY, item, bindingAdapterPosition))
//                    }
//                }

            })

//            itemBinding.cardActivityRemove.setOnClickListener(ViewClickListener {
//                val item = items[bindingAdapterPosition]
//                onItemClickListener.onItemClick(Triple(CLICK_DELETE, item, bindingAdapterPosition))
//            })

            itemBinding.txtSubText.setOnClickListener(ViewClickListener { v1: View? ->
                val notificationItem = items[bindingAdapterPosition]
                if (!notificationItem!!.activityTriggered && !notificationItem.activityScheduled) {
                    onItemClickListener.onItemClick(Triple(CLICK_UN_SCHEDULE, notificationItem, bindingAdapterPosition))
                }
            })
            itemBinding.txtDelete.setOnClickListener(ViewClickListener { view: View? ->
                val notificationItem = items[bindingAdapterPosition]
                if (!notificationItem!!.activityTriggered && !notificationItem.activityScheduled) {
                    onItemClickListener.onItemClick(Triple(CLICK_DELETE, notificationItem, bindingAdapterPosition))
                } else {
                    onItemClickListener.onItemClick(Triple(CLICK_UN_SCHEDULE, notificationItem, bindingAdapterPosition))
                }
            })
        }
    }

    fun getServerTimeConversation(serverTime: String): String {
        val inputFormat: DateFormat = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.ENGLISH
        )
        val outputFormat: DateFormat = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.ENGLISH
        )
        val dateInput = inputFormat.parse(serverTime)

        return outputFormat.format(dateInput!!)
    }

    companion object {
        var CLICK_EDIT = 4
        var CLICK_MEDIA = 3
        var CLICK_DELETE = 2
        var CLICK_WHOLE = 1
        var CLICK_UN_SCHEDULE = 5
        var CLICK_NOTIFY = 6
    }
}