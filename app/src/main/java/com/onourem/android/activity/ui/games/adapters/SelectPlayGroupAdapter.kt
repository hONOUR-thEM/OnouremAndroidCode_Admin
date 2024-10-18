package com.onourem.android.activity.ui.games.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.ActivityGroupList
import com.onourem.android.activity.ui.games.adapters.SelectPlayGroupAdapter.PlayGroupViewHolder
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class SelectPlayGroupAdapter(
    private val playGroupList: List<ActivityGroupList?>?,
    private val onItemClickListener: OnItemClickListener<ActivityGroupList>?
) : RecyclerView.Adapter<PlayGroupViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayGroupViewHolder {
        return PlayGroupViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_select_privacy, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlayGroupViewHolder, position: Int) {
        val playGroup = playGroupList?.get(position)
        if (playGroup != null) {
            holder.bind(playGroup)
        }
    }

    override fun getItemCount(): Int {
        return playGroupList?.size ?: 0
    }

    inner class PlayGroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: AppCompatImageView
        private val textView: AppCompatTextView
        fun bind(playGroup: ActivityGroupList) {
            textView.text = playGroup.groupName
            //            if (selected.isEmpty()) {
////                imageView.setImageDrawable(AppCompatResources.getDrawable(imageView.getContext(), R.drawable.ic_custom_signup_eye_image));
//                DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.gray_color));
//            } else {
//                if (selected.contains(playGroup)) {
//                    DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.colorAccent));
//                } else {
//                    DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.gray_color));
//                    imageView.setImageDrawable(AppCompatResources.getDrawable(imageView.getContext(), R.drawable.ic_custom_signup_eye_image));
//                }
//            }
//            if (playGroup.isSelected()) {
//                Drawable drawable = AppCompatResources.getDrawable(imageView.getContext(), R.drawable.ic_checkbox_on_background);
//                imageView.setImageDrawable(drawable);
//            } else {
//                imageView.setImageDrawable(null);
//            }
        }

        init {
            view.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener?.onItemClick(
                    (playGroupList?.get(bindingAdapterPosition) ?: 0) as ActivityGroupList?
                )
            })
            textView = view.findViewById(R.id.textView)
            imageView = view.findViewById(R.id.imageView)
        }
    }
}