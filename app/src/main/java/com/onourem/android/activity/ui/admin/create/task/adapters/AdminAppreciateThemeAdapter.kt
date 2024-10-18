package com.onourem.android.activity.ui.admin.create.task.adapters

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.CategoryList
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class AdminAppreciateThemeAdapter(
    private val themeList: List<CategoryList>,
    private val onItemClickListener: OnItemClickListener<CategoryList>
) : RecyclerView.Adapter<AdminAppreciateThemeAdapter.ThemeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        return ThemeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_admin_theme_category_adapter, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        holder.bind(themeList[position])
    }

    override fun getItemCount(): Int {
        return themeList.size
    }

    inner class ThemeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTheme: AppCompatTextView
        private val tvReceiverRequired: AppCompatTextView
        private val tvThemeDescription: AppCompatTextView
        private val linearLayout: LinearLayout
        @SuppressLint("SetTextI18n")
        fun bind(theme: CategoryList) {
            tvTheme.text = theme.catCode

            if (theme.receiverRequired == "Y"){
                tvReceiverRequired.setTextColor(ContextCompat.getColorStateList(itemView.context, R.color.good_green_color))
            }else{
                tvReceiverRequired.setTextColor(ContextCompat.getColorStateList(itemView.context, R.color.color_6))
            }

            tvReceiverRequired.text = "Receiver Required : ${theme.receiverRequired}"

            if (TextUtils.isEmpty(theme.description)){
                tvThemeDescription.visibility = View.GONE
            }else{
                tvThemeDescription.text = theme.description
                tvThemeDescription.visibility = View.VISIBLE
            }

        }

        init {
            linearLayout = itemView.findViewById(R.id.linearLayout)
            tvTheme = itemView.findViewById(R.id.tvTheme)
            tvReceiverRequired = itemView.findViewById(R.id.tvReceiverRequired)
            tvThemeDescription = itemView.findViewById(R.id.tvThemeDescription)
            linearLayout.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    themeList[bindingAdapterPosition]
                )
            })
        }
    }
}