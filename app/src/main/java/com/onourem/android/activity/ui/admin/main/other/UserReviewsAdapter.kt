package com.onourem.android.activity.ui.admin.main.other

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemAppReviewRowBinding
import com.onourem.android.activity.models.CsvRow
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class UserReviewsAdapter(
    private val settingsItems: ArrayList<CsvRow>,
    private var onItemClickListener: OnItemClickListener<Triple<Int, CsvRow, Int>>
) : RecyclerView.Adapter<UserReviewsAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_app_review_row, parent, false)
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
        private val rowBinding: ItemAppReviewRowBinding? = DataBindingUtil.bind(itemView)
        var options = RequestOptions()
            .fitCenter()

        @SuppressLint("SetTextI18n")
        fun bind(item: CsvRow) {
            rowBinding!!.tvUserName.text = item.column12
            rowBinding.tvName.text = item.column10
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