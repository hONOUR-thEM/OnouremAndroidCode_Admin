package com.onourem.android.activity.ui.dashboard.subscription

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemSubscriptionPackageBinding
import com.onourem.android.activity.models.PackageInfo
import com.onourem.android.activity.models.SubscriptionPackageRes
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class SubscriptionPackageAdapter(
    private val items: ArrayList<SubscriptionPackageRes>,
    private val onItemClickListener: OnItemClickListener<SubscriptionPackageRes>?
) : RecyclerView.Adapter<SubscriptionPackageAdapter.SettingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_subscription_package, parent, false))
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val options = items[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getSelectedPackage() : SubscriptionPackageRes? {
        return items.find { it.selectionStatus == "Y" }
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class SettingsViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        private val rowBinding: ItemSubscriptionPackageBinding = ItemSubscriptionPackageBinding.bind(itemView)
        private val options = RequestOptions()
            .fitCenter()
            .transform(FitCenter(), RoundedCorners(20)) //15

        @SuppressLint("SetTextI18n")
        fun bind(item: SubscriptionPackageRes) {

            Glide.with(rowBinding.imageView)
                    .load(item.imageUrl)
                    .apply(options)
                    .into(rowBinding.imageView);

            Glide.with(rowBinding.ivCheck)
                    .load(R.drawable.check)
                    .apply(options)
                    .into(rowBinding.ivCheck);

            if (item.selectionStatus == "Y"){
                rowBinding.parent.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#F9A70F")))
                rowBinding.parent.strokeWidth = 12
                rowBinding.ivCheck.visibility = View.VISIBLE
                rowBinding.imageView.invalidate()
            }else{
                rowBinding.parent.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
                rowBinding.parent.strokeWidth = 0
                rowBinding.ivCheck.visibility = View.GONE
                rowBinding.imageView.invalidate()
            }
        }

        init {
            itemView.setOnClickListener(ViewClickListener { v: View? ->
                if (onItemClickListener != null) {
                    val settingsItem = items[bindingAdapterPosition]
                    items.forEach {
                        it.selectionStatus = "N"
                    }
                    settingsItem.selectionStatus = "Y"
                    notifyDataSetChanged()
                    onItemClickListener.onItemClick(settingsItem)
                }
            })
        }
    }
}