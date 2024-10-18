package com.onourem.android.activity.ui.games.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.VisibilityGroupList
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class EditPrivacyAdapter(
    private val privacyGroupList: List<VisibilityGroupList>,
    defaultSelected: List<VisibilityGroupList>?
) : RecyclerView.Adapter<EditPrivacyAdapter.PrivacyViewHolder>() {
    //    private OnItemClickListener<List<VisibilityGroupList>> onItemClickListener;
    internal val selected: MutableList<VisibilityGroupList> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivacyViewHolder {
        return PrivacyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_edit_privacy, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PrivacyViewHolder, position: Int) {
        val privacyGroup = privacyGroupList[position]
        holder.bind(privacyGroup)
    }

    override fun getItemCount(): Int {
        return privacyGroupList.size
    }

    fun getSelected(): List<VisibilityGroupList> {
        return selected
    }

    inner class PrivacyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: AppCompatImageView
        private val ivCheck: AppCompatImageView
        private val textView: AppCompatTextView
        fun bind(privacyGroup: VisibilityGroupList) {
            textView.text = privacyGroup.groupName
            //            Drawable drawable = AppCompatResources.getDrawable(imageView.getContext(), R.drawable.ic_custom_signup_eye_image);
            if (selected.isEmpty()) {
//                imageView.setImageDrawable(AppCompatResources.getDrawable(imageView.getContext(), R.drawable.ic_custom_signup_eye_image));
//                DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.gray_color));
                ivCheck.visibility = View.INVISIBLE
            } else {
                if (selected.contains(privacyGroup)) {
//                    DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.colorAccent));
                    ivCheck.visibility = View.VISIBLE
                } else {
//                    DrawableCompat.setTint(drawable, itemView.getResources().getColor(R.color.gray_color));
//                    imageView.setImageDrawable(AppCompatResources.getDrawable(imageView.getContext(), R.drawable.ic_custom_signup_eye_image));
                    ivCheck.visibility = View.INVISIBLE
                }
            }
            //            imageView.setImageDrawable(drawable);
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
                if (selected.contains(clickedItem) && selected.size != 1) selected.remove(
                    clickedItem
                ) else selected.add(clickedItem)
                notifyDataSetChanged()
            })
            textView = view.findViewById(R.id.textView)
            ivCheck = view.findViewById(R.id.ivCheck)
            imageView = view.findViewById(R.id.imageView)
        }
    }

    init {
        if (defaultSelected != null && !defaultSelected.isEmpty()) selected.addAll(defaultSelected)

//        this.onItemClickListener = onItemClickListener;
    }
}