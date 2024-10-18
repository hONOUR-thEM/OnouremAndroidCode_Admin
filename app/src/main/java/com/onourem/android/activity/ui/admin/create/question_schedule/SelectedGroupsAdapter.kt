package com.onourem.android.activity.ui.admin.create.question_schedule

import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemGroupIdRowBinding
import com.onourem.android.activity.databinding.ItemSettingsRowBinding
import com.onourem.android.activity.models.GroupItem
import com.onourem.android.activity.models.PlayGroup
import com.onourem.android.activity.models.SettingsItem
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class SelectedGroupsAdapter(
    private val settingsItems: ArrayList<PlayGroup>,
    private val onItemClickListener: OnItemClickListener<Pair<Int, PlayGroup>>?
) : RecyclerView.Adapter<SelectedGroupsAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_group_id_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val options = settingsItems[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return settingsItems.size
    }

    val selected: List<PlayGroup>
        get() {
            return settingsItems
        }

    inner class SettingsViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        private val rowBinding: ItemGroupIdRowBinding? = DataBindingUtil.bind(itemView)
        var options = RequestOptions()
            .fitCenter()

        fun bind(settingsItem: PlayGroup) {

            rowBinding!!.tvSelectedCount.text = settingsItem.playGroupName
        }

        init {
            itemView.setOnClickListener(ViewClickListener { v: View? ->
                if (onItemClickListener != null) {
                    val settingsItem = settingsItems[bindingAdapterPosition]
                    onItemClickListener.onItemClick(Pair(bindingAdapterPosition, settingsItem))
                }
            })
        }
    }
}