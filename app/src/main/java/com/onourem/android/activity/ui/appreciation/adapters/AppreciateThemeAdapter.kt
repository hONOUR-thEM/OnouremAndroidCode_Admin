package com.onourem.android.activity.ui.appreciation.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.CategoryList
import com.onourem.android.activity.ui.appreciation.adapters.AppreciateThemeAdapter.ThemeViewHolder
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class AppreciateThemeAdapter(
    private val themeList: List<CategoryList>,
    private val onItemClickListener: OnItemClickListener<CategoryList>
) : RecyclerView.Adapter<ThemeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        return ThemeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_theme_category_adapter, parent, false)
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
        private val tvThemeDescription: AppCompatTextView
        private val linearLayout: LinearLayout
        fun bind(theme: CategoryList) {
            tvTheme.text = theme.catCode
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
            tvThemeDescription = itemView.findViewById(R.id.tvThemeDescription)
            linearLayout.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    themeList[bindingAdapterPosition]
                )
            })
        }
    }
}