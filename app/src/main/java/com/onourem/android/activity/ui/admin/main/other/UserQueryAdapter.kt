package com.onourem.android.activity.ui.admin.main.other

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemReportRowBinding
import com.onourem.android.activity.databinding.ItemUserQueryBinding
import com.onourem.android.activity.models.ReportItem
import com.onourem.android.activity.models.UserQueryItem
import com.onourem.android.activity.ui.utils.TimeUtil
import com.onourem.android.activity.ui.utils.TimeUtilBackward
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class UserQueryAdapter(
    private val settingsItems: ArrayList<UserQueryItem>,
    private var onItemClickListener: OnItemClickListener<Triple<Int, UserQueryItem, Int>>
) : RecyclerView.Adapter<UserQueryAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_query, parent, false)
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
        private val rowBinding: ItemUserQueryBinding? = DataBindingUtil.bind(itemView)
        var options = RequestOptions()
            .fitCenter()

        @SuppressLint("SetTextI18n")
        fun bind(item: UserQueryItem) {
            rowBinding!!.tvUserName.text = "${item.userName}-[${item.userId}]"
            rowBinding.tvName.text = item.description

            if (item.queryType == "ContactUs"){
                rowBinding.parentCard.setCardBackgroundColor(ContextCompat.getColor(rowBinding.root.context, R.color.good_green_color_as))
            }else{
                rowBinding.parentCard.setCardBackgroundColor(ContextCompat.getColor(rowBinding.root.context, R.color.good_red_color_as))
            }

//            item.setActionDateTime("2023-11-30 12:46:11.0");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                rowBinding.tvDate.text = TimeUtil.getRelatedTime(item.createdOn) + " • " + item.queryType
            } else {
                rowBinding.tvDate.text = TimeUtilBackward.getRelatedTime(item.createdOn) + " • " + item.queryType
            }

        }

        init {
            rowBinding?.parent?.setOnClickListener(ViewClickListener { v1: View? ->
                val oClubSettingsItem = settingsItems[bindingAdapterPosition]
                onItemClickListener.onItemClick(Triple(CLICK_COMMENTS, oClubSettingsItem, bindingAdapterPosition))
            })
        }

    }

    companion object {
        var CLICK_LINKS = 6
        var CLICK_COMMENTS = 7
    }
}