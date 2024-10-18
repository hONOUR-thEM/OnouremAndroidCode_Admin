package com.onourem.android.activity.ui.settings.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemPushSettingsRowBinding
import com.onourem.android.activity.models.PushSettingsItem
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class PushSettingsAdapter(
    private val settingsItems: ArrayList<PushSettingsItem>,
    private val onItemClickListener: OnItemClickListener<PushSettingsItem>?
) : RecyclerView.Adapter<PushSettingsAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_push_settings_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val options = settingsItems[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return settingsItems.size
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class SettingsViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        private val rowBinding: ItemPushSettingsRowBinding?
        var isTouched = false
        fun bind(settingsItem: PushSettingsItem) {
            if (settingsItem.isSwitchVisible) {
                rowBinding!!.imageButton.visibility = View.GONE
                rowBinding!!.switchCompat.visibility = View.VISIBLE
                rowBinding!!.tvName.text = settingsItem.title
                rowBinding!!.switchCompat.isChecked = settingsItem.isValueOfAlert
            } else {
                rowBinding!!.imageButton.visibility = View.VISIBLE
                rowBinding!!.switchCompat.visibility = View.GONE
                rowBinding!!.tvName.text = settingsItem.title
                rowBinding!!.imageButton.text = settingsItem.notificationPreferredTime
            }
        }

        init {
            rowBinding = DataBindingUtil.bind(itemView)
            if (rowBinding != null) {
                rowBinding!!.switchCompat.setOnTouchListener { view1: View?, motionEvent: MotionEvent? ->
                    isTouched = true
                    false
                }
                rowBinding!!.switchCompat.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
                    if (isTouched) {
                        isTouched = false
                        if (onItemClickListener != null) {
                            val settingsItem = settingsItems[bindingAdapterPosition]
                            settingsItem.isValueOfAlert = isChecked
                            onItemClickListener.onItemClick(settingsItem)
                        }
                    }
                }
                rowBinding!!.imageButton.setOnClickListener(ViewClickListener { v: View? ->
                    if (onItemClickListener != null) {
                        val settingsItem = settingsItems[bindingAdapterPosition]
                        onItemClickListener.onItemClick(settingsItem)
                    }
                })
            }
        }
    }
}