package com.onourem.android.activity.ui.admin.main.other

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemReportRowBinding
import com.onourem.android.activity.models.ReportItem
import com.onourem.android.activity.ui.utils.Base64CommonsCodec
import com.onourem.android.activity.ui.utils.Base64Utility
import com.onourem.android.activity.ui.utils.TimeUtil
import com.onourem.android.activity.ui.utils.TimeUtilBackward
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class ReportsAdapter(
    private val settingsItems: ArrayList<ReportItem>,
    private var onItemClickListener: OnItemClickListener<Triple<Int, ReportItem, Int>>
) : RecyclerView.Adapter<ReportsAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_report_row, parent, false)
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
        private val rowBinding: ItemReportRowBinding? = DataBindingUtil.bind(itemView)
        var options = RequestOptions()
            .fitCenter()

        @SuppressLint("SetTextI18n")
        fun bind(item: ReportItem) {
            rowBinding!!.tvUserName.text = "${item.userName}-[${item.userId}]"

            val text = ""

            rowBinding.tvName.text = if (Base64CommonsCodec.isBase64(item.description)) {
                Base64Utility.decode(item.description)
            } else {
                item.description
            }
            rowBinding.tvContentUserName.text = item.contentUserName + " [${item.contentUserId}] "
            rowBinding.tvContentId.text = "Id : ${item.activityId}"
            rowBinding.tvContentType.text = "Type : ${item.activityType}"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                rowBinding.tvDate.text = TimeUtil.getRelatedTime(item.createdOn)
            } else {
                rowBinding.tvDate.text = TimeUtilBackward.getRelatedTime(item.createdOn)
            }

//            if (cClubSettingsItem.isOpen){
//                rowBinding.card.visibility = View.VISIBLE
//            }else{
//                rowBinding.card.visibility = View.GONE
//            }

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