package com.onourem.android.activity.ui.games.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.databinding.ItemQuesGamesFilterBinding
import com.onourem.android.activity.models.FilterItem
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class QuestionGamesFilterAdapter(
    private val filterItems: ArrayList<FilterItem>,
    private val onItemClickListener: OnItemClickListener<FilterItem>?
) : RecyclerView.Adapter<QuestionGamesFilterAdapter.SettingsViewHolder>() {
    // if checkedPosition = -1, there is no default selection
    // if checkedPosition = 0, 1st item is selected by default
    private var checkedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ques_games_filter, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val options = filterItems[position]
        holder.bind(options)
    }

    override fun getItemCount(): Int {
        return filterItems.size
    }

    val selected: FilterItem?
        get() = if (checkedPosition != -1) {
            filterItems[checkedPosition]
        } else null
    var selectedPos: Int
        get() = checkedPosition
        set(pos) {
            checkedPosition = pos
            notifyItemChanged(pos)
        }

    fun modifyItem(position: Int, model: FilterItem) {
        filterItems[position] = model
        notifyItemChanged(position)
    }

    inner class SettingsViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        private val rowBinding: ItemQuesGamesFilterBinding?
        var options = RequestOptions()
            .fitCenter()

        fun bind(item: FilterItem) {
            if (checkedPosition == -1) {
                if (bindingAdapterPosition == 0) {
                    rowBinding!!.textView.isSelected = true
                    rowBinding!!.textView.typeface =
                        ResourcesCompat.getFont(itemView.context, R.font.montserrat_semibold)
                } else {
                    rowBinding!!.textView.isSelected = false
                    rowBinding!!.textView.typeface =
                        ResourcesCompat.getFont(itemView.context, R.font.montserrat_regular)
                }
            } else {
                if (checkedPosition == bindingAdapterPosition) {
                    rowBinding!!.textView.isSelected = true
                    rowBinding!!.textView.typeface =
                        ResourcesCompat.getFont(itemView.context, R.font.montserrat_semibold)
                } else {
                    rowBinding!!.textView.isSelected = false
                    rowBinding!!.textView.typeface =
                        ResourcesCompat.getFont(itemView.context, R.font.montserrat_regular)
                }
            }
            if (item.isVisible) {
                rowBinding!!.tvBubble.visibility = View.VISIBLE
            } else {
                rowBinding!!.tvBubble.visibility = View.INVISIBLE
            }
            rowBinding!!.textView.text = item.title
        }

        init {
            rowBinding = DataBindingUtil.bind(itemView)
            itemView.setOnClickListener(ViewClickListener { v: View? ->
                if (checkedPosition != bindingAdapterPosition) {
                    if (onItemClickListener != null) {
                        val FilterItem = filterItems[bindingAdapterPosition]
                        onItemClickListener.onItemClick(FilterItem)
                        if (checkedPosition != bindingAdapterPosition) {
                            checkedPosition = bindingAdapterPosition
                            notifyDataSetChanged()
                        }
                    }
                }
            })
        }
    }
}