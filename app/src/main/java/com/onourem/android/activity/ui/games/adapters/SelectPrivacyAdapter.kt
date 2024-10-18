package com.onourem.android.activity.ui.games.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.PrivacyGroup
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class SelectPrivacyAdapter(
    private val privacyGroupList: List<PrivacyGroup>,
    defaultSelected: List<PrivacyGroup>,
    onItemClickListener: OnItemClickListener<List<PrivacyGroup>>
) : RecyclerView.Adapter<SelectPrivacyAdapter.PrivacyViewHolder>() {
    private val onItemClickListener: OnItemClickListener<List<PrivacyGroup>>
    internal val selected: MutableList<PrivacyGroup> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivacyViewHolder {
        return PrivacyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_select_privacy, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PrivacyViewHolder, position: Int) {
        val privacyGroup = privacyGroupList[position]
        holder.bind(privacyGroup)
    }

    override fun getItemCount(): Int {
        return privacyGroupList.size
    }

    fun getSelected(): List<PrivacyGroup> {
        return selected
    }

    inner class PrivacyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: AppCompatImageView
        private val textView: AppCompatTextView
        fun bind(privacyGroup: PrivacyGroup) {
            textView.text = privacyGroup.groupName
            val drawable = AppCompatResources.getDrawable(
                imageView.context,
                R.drawable.ic_custom_signup_eye_image
            )
            if (selected.isEmpty()) {
//                imageView.setImageDrawable(AppCompatResources.getDrawable(imageView.getContext(), R.drawable.ic_custom_signup_eye_image));
                if (drawable != null) {
                    DrawableCompat.setTint(
                        drawable,
                        ContextCompat.getColor(imageView.context, R.color.gray_color)
                    )
                }
            } else {
                if (selected.contains(privacyGroup)) {
                    if (drawable != null) {
                        DrawableCompat.setTint(
                            drawable,
                            ContextCompat.getColor(imageView.context, R.color.colorAccent)
                        )
                    }
                } else {
                    if (drawable != null) {
                        DrawableCompat.setTint(
                            drawable,
                            ContextCompat.getColor(imageView.context, R.color.gray_color)
                        )
                    }
                    imageView.setImageDrawable(
                        AppCompatResources.getDrawable(
                            imageView.context,
                            R.drawable.ic_custom_signup_eye_image
                        )
                    )
                }
            }
            imageView.setImageDrawable(drawable)
        }

        init {
            view.setOnClickListener(ViewClickListener { v: View? ->
                var hasDefaultGroup = false
                for (privacyGroup in selected) {
                    if (privacyGroup.groupId <= 4) {
                        hasDefaultGroup = true
                        break
                    }
                }
                val clickedItem = privacyGroupList[bindingAdapterPosition]
                if (hasDefaultGroup || clickedItem.groupId <= 4) {
                    selected.clear()
                }
                if (selected.contains(clickedItem) && selected.size != 1) {
                    selected.remove(clickedItem)
                } else if (selected.contains(clickedItem) && selected.size == 1) {
                    selected.remove(clickedItem)
                    selected.add(privacyGroupList[0])
                } else selected.add(clickedItem)
                notifyDataSetChanged()
                onItemClickListener?.onItemClick(selected)
            })
            textView = view.findViewById(R.id.textView)
            imageView = view.findViewById(R.id.imageView)
        }
    }

    init {
        if (defaultSelected != null && !defaultSelected.isEmpty()) selected.addAll(defaultSelected)
        this.onItemClickListener = onItemClickListener
    }
}