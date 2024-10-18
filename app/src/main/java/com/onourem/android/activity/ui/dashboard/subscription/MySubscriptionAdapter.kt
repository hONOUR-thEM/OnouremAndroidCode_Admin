package com.onourem.android.activity.ui.dashboard.subscription

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemMySubscriptionBinding
import com.onourem.android.activity.models.UserCurrentSubscriptionInfo
import com.onourem.android.activity.ui.utils.Events
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*

class MySubscriptionAdapter(
    private val settingsItems: ArrayList<UserCurrentSubscriptionInfo>,
    private val onItemClickListener: OnItemClickListener<UserCurrentSubscriptionInfo>?
) : RecyclerView.Adapter<MySubscriptionAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_my_subscription, parent, false)
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
        private val rowBinding: ItemMySubscriptionBinding = ItemMySubscriptionBinding.bind(itemView)
        private val options = RequestOptions()
            .fitCenter()
            .transform(FitCenter(), RoundedCorners(20)) //15

        @SuppressLint("SetTextI18n")
        fun bind(item: UserCurrentSubscriptionInfo) {

            Glide.with(rowBinding.imageView)
                .load(item.packageImageUrl)
                .apply(options)
                .into(rowBinding.imageView);


            val strDate = Events.getServerTimeConversationSubscription(item.startDate!!)
            val endDate = Events.getServerTimeConversationSubscription(item.validTill!!)

            rowBinding.tvStartDateText.text = strDate
            rowBinding.tvValidTillText.text = endDate
            rowBinding.tvSubCountText.text = item.freeInviteNumber.toString()

            rowBinding.tvSubCodeStatusUnused.visibility = View.VISIBLE
            rowBinding.tvSubCodeStatusTextUnused.visibility = View.VISIBLE

            if (item.freeInviteNumber > 0){
                rowBinding.tvSubCodeStatusTextUnused.text = item.unusedInviteNumber.toString()
                rowBinding.btnShare.visibility = View.VISIBLE
            }else{
                rowBinding.tvSubCodeStatusTextUnused.text = "0"
                rowBinding.btnShare.visibility = View.GONE
            }
        }

        init {

            rowBinding.btnShare.setOnClickListener(ViewClickListener { v: View? ->
                if (onItemClickListener != null) {
                    val settingsItem = settingsItems[bindingAdapterPosition]
                    onItemClickListener.onItemClick(settingsItem)
                }
            })
        }
    }
}