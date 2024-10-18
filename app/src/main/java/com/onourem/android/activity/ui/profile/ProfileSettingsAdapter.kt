package com.onourem.android.activity.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemProfileRowBinding
import com.onourem.android.activity.models.ProfileItem
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class ProfileSettingsAdapter(
    private val profileItems: ArrayList<ProfileItem>,
    private val onItemClickListener: OnItemClickListener<ProfileItem>?
) : RecyclerView.Adapter<ProfileSettingsAdapter.SettingsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_profile_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val options = profileItems[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return profileItems.size
    }

    inner class SettingsViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        private val rowBinding: ItemProfileRowBinding? = DataBindingUtil.bind(itemView)
        fun bind(item: ProfileItem) {

            rowBinding!!.tvName.text = item.title
            rowBinding.tvDesc.text = item.details

            val drawable = AppCompatResources.getDrawable(itemView.context, R.drawable.ic_edit_icon)
            rowBinding.imageButton.setImageDrawable(drawable)
            if (item.position == 2) {
                if (drawable != null) {
                    DrawableCompat.setTint(
                        drawable,
                        ContextCompat.getColor(itemView.context, R.color.color_gray_cross)
                    )
                }
            } else {
                if (drawable != null) {
                    DrawableCompat.setTint(
                        drawable,
                        ContextCompat.getColor(itemView.context, R.color.colorAccent)
                    )
                }
            }
        }

        init {

//            itemView.setOnClickListener(new ViewClickListener(v -> {
//
////                if (onItemClickListener != null) {
////                    ProfileItem item = profileItems.get(getBindingAdapterPosition());
////                    onItemClickListener.onItemClick(item);
////                }
//
//            }));
            rowBinding!!.imageButton.setOnClickListener(ViewClickListener { v: View? ->
                if (onItemClickListener != null) {
                    val item = profileItems[bindingAdapterPosition]
                    onItemClickListener.onItemClick(item)
                }
            })
        }
    }
}