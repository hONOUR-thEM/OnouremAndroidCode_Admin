package com.onourem.android.activity.ui.dashboard.subscription

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemShareCodeBinding
import com.onourem.android.activity.models.FreeSubscriptionRes
import com.onourem.android.activity.models.ShareCouponInfo
import com.onourem.android.activity.ui.utils.Events
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class ShareCouponsAdapter(
    private val settingsItems: ArrayList<FreeSubscriptionRes>,
    private val onItemClickListener: OnItemClickListener<FreeSubscriptionRes>?
) : RecyclerView.Adapter<ShareCouponsAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_share_code, parent, false)
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
        private val rowBinding: ItemShareCodeBinding = ItemShareCodeBinding.bind(itemView)
        private val options = RequestOptions()
            .fitCenter()
            .transform(FitCenter(), RoundedCorners(20)) //15

        @SuppressLint("SetTextI18n")
        fun bind(item: FreeSubscriptionRes) {

            Glide.with(rowBinding.imageView)
                .load(item.packageImageUrl)
                .apply(options)
                .into(rowBinding.imageView);

            rowBinding.tvValidTillText.text = Events.getServerTimeConversationSubscription(item.codeValidTill)
            rowBinding.tvSubCodeStatusText.text = item.status
            rowBinding.tvSubscriptionCodeText.text = item.discountCoupon

            if (item.status == "Available") {
                rowBinding.btnShare.visibility = View.VISIBLE
                rowBinding.tvSubCodeStatusText.setTextColor(ColorStateList.valueOf(Color.parseColor("#00B832")))
            } else {
                rowBinding.btnShare.visibility = View.GONE
                rowBinding.tvSubCodeStatusText.setTextColor(ColorStateList.valueOf(Color.parseColor("#A3A2A2")))
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