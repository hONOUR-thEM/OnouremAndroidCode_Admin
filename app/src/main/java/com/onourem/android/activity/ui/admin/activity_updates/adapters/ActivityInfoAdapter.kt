package com.onourem.android.activity.ui.admin.activity_updates.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemActivityInfoBinding
import com.onourem.android.activity.ui.admin.activity_updates.models.ActivityInfoResponse
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener

internal class ActivityInfoAdapter(
    infoList: MutableList<ActivityInfoResponse>,
    private var onItemClickListener: OnItemClickListener<Triple<Int, ActivityInfoResponse, Int>>
) : PaginationRVAdapter<ActivityInfoResponse?>(
    ArrayList(
        infoList
    )
) {

    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(1)) //15
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)


    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return SingleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_activity_info, parent, false)
        )
    }

    override fun emptyLoadingItem(): ActivityInfoResponse {
        return ActivityInfoResponse()
    }

    override fun emptyFooterItem(): ActivityInfoResponse {
        return ActivityInfoResponse()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<Triple<Int, ActivityInfoResponse, Int>>) {
        this.onItemClickListener = onItemClickListener
    }

    fun modifyItem(position: Int, model: ActivityInfoResponse) {
        items[position] = model
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    @SuppressLint("SetTextI18n")
    internal inner class SingleViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val itemBinding: ItemActivityInfoBinding

        init {
            itemBinding = ItemActivityInfoBinding.bind(itemView)
        }

        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {

            val adminActivityResponse = items[position]
            itemBinding.tvStatus.text = adminActivityResponse!!.activityTriggered.toString()
            itemBinding.tvUserAnsweredCount.text = adminActivityResponse.noOfAnswers
            itemBinding.txtQuestionFor.setTextColor(
                ContextCompat.getColor(
                    itemBinding.root.context,
                    R.color.good_green_color
                )
            )
            val isSentForAll = if (adminActivityResponse.activityFor != "Solo"){
                if (adminActivityResponse.isSentForAll == "Y"){
                    "- All"
                }else{
                    "- Specific"
                }
            }else{
                ""
            }


            itemBinding.txtQuestionFor.text = "${adminActivityResponse.activityFor} - ${adminActivityResponse.activityCategory} $isSentForAll"

            itemBinding.tvStatus.text = if (adminActivityResponse.activityTriggered) {
                "Triggered"
            } else if (adminActivityResponse.activityScheduled) {
                "Scheduled"
            } else {
                "Created"
            }


        }
    }

    companion object {
        var CLICK_UN_SCHEDULE = 3
        var CLICK_DELETE = 2
        var CLICK_WHOLE = 1
        var CLICK_NOTIFY = 6
        var CLICK_INFO = 7
    }
}