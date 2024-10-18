package com.onourem.android.activity.ui.admin.main.mood

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
import com.onourem.android.activity.databinding.ItemMoodInfoBinding
import com.onourem.android.activity.models.MoodInfoData
import com.onourem.android.activity.models.UserExpressionList
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MoodInfoAdapter(
    moodInfoDataMutableList: MutableList<MoodInfoData>,
    var selectedRadioButton: String,
    val localMoods: HashMap<String, UserExpressionList>?,
    private var onItemClickListener: OnItemClickListener<Triple<Int, MoodInfoData, Int>>
) : PaginationRVAdapter<MoodInfoData?>(
    ArrayList(
        moodInfoDataMutableList
    )
) {

    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(1)) //15
        .placeholder(R.drawable.shape_image_video_placeholder)
        .error(R.drawable.shape_image_video_placeholder)


    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return SingleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_mood_info, parent, false)
        )
    }

    override fun emptyLoadingItem(): MoodInfoData {
        return MoodInfoData()
    }

    override fun emptyFooterItem(): MoodInfoData {
        return MoodInfoData()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<Triple<Int, MoodInfoData, Int>>) {
        this.onItemClickListener = onItemClickListener
    }

    fun modifyItem(position: Int, model: MoodInfoData) {
        items[position] = model
        notifyItemChanged(position)
    }

//    fun removeItem(position: Int) {
//        items.removeAt(position)
//        notifyItemRemoved(position)
//    }

    @SuppressLint("NotifyDataSetChanged")
    override fun removeItem(item: MoodInfoData?) {
        val position = items.indexOf(item)
//        items.remove(item)
        notifyItemRemoved(position)
//        notifyDataSetChanged()
    }

    internal inner class SingleViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val itemBinding: ItemMoodInfoBinding

        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            val externalActivityData = items[position]

            itemBinding.tvQuestionText.text = externalActivityData!!.summary

            val mood = localMoods!![externalActivityData.expressionId]
            itemBinding.txtMoodName.text = mood!!.expressionText
            itemBinding.ivMood.setImageResource(mood.moodImage)

            itemBinding.txtQuestionFor.text =
                String.format(
                    "MoodID: %s | Status: %s",
                    externalActivityData.expressionId,
                    if (externalActivityData.status == "0") "Active" else "Inactive"
                )

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

//            itemBinding.txtDate.text = "Updated On  ${Events.getServerTimeConversation(externalActivityData.createdDate!!)}"

            if (!TextUtils.isEmpty(externalActivityData.imageUrl)) {
                itemBinding.ivQuestionImage.visibility = View.VISIBLE
            } else {
                itemBinding.ivQuestionImage.visibility = View.GONE
            }

            if (!TextUtils.isEmpty(externalActivityData.videoUrl) && externalActivityData.youtubeLink == "Y") {
                itemBinding.ivPlayVideo.visibility = View.VISIBLE
            } else {
                itemBinding.ivPlayVideo.visibility = View.GONE
            }


////
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

                if (externalActivityData.youtubeLink == "Y" || externalActivityData.youtubeLink == "V") {
                    itemBinding.ivPlayVideo.visibility = View.VISIBLE
                } else {
                    itemBinding.ivPlayVideo.visibility = View.GONE
                }
            } else {
                itemBinding.ivQuestionImage.visibility = View.GONE
            }



//            itemBinding.txtSubText.setBackgroundColor(
//                if (externalActivityData.status == "Y") {
//                    ContextCompat.getColor(itemView.context, R.color.good_green_color)
//                } else {
//                    ContextCompat.getColor(itemView.context, R.color.good_red_color)
//                }
//            )

        }

        init {
            itemBinding = ItemMoodInfoBinding.bind(itemView)

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

            itemBinding.txtEdit.setOnClickListener(ViewClickListener {
                val item = items[bindingAdapterPosition]
                onItemClickListener.onItemClick(Triple(CLICK_EDIT, item, bindingAdapterPosition))

            })

            itemBinding.txtDelete.setOnClickListener(ViewClickListener {
                val item = items[bindingAdapterPosition]
                onItemClickListener.onItemClick(Triple(CLICK_DELETE, item, bindingAdapterPosition))
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