package com.onourem.android.activity.ui.games.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemSearchItemRowBinding
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class SearchQuestionAdapter(
    private val questionItems: ArrayList<LoginDayActivityInfoList>,
    private val onItemClickListener: OnItemClickListener<LoginDayActivityInfoList>?
) : RecyclerView.Adapter<SearchQuestionAdapter.SettingsViewHolder>() {
    private val options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .placeholder(R.drawable.ic_logo)
        .error(R.drawable.ic_logo)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_item_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val options = questionItems[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return questionItems.size
    }

    inner class SettingsViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        private val rowBinding: ItemSearchItemRowBinding?
        fun bind(item: LoginDayActivityInfoList?) {
            Glide.with(rowBinding!!.rivProfile)
                .load("https://dtv39ga8f8jxc.cloudfront.net/images/appimages/activity/large/12b45c31-617a-43c2-af0b-ed3a67724bdf1597828248422.jpeg")
                .apply(options)
                .into(rowBinding!!.rivProfile)
            //
//            rowBinding!!.tvName.setText(item.getActivityText());
        }

        init {
            rowBinding = DataBindingUtil.bind(itemView)
            itemView.setOnClickListener(ViewClickListener { v: View? ->
                if (onItemClickListener != null) {
                    val settingsItem = questionItems[bindingAdapterPosition]
                    onItemClickListener.onItemClick(settingsItem)
                }
            })
        }
    }
}