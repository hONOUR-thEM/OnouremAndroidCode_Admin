package com.onourem.android.activity.ui.bottomsheet

import com.onourem.android.activity.ui.bottomsheet.ActionBottomSheetDialogAdapter.ActionViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.onourem.android.activity.R
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.databinding.ItemActionViewBinding
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

internal class ActionBottomSheetDialogAdapter(
    private val actions: MutableList<Action<Any?>>,
    private val actionOnItemClickListener: OnItemClickListener<Action<Any?>>
) : RecyclerView.Adapter<ActionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        return ActionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_action_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.bind(actions[position])
    }

    override fun getItemCount(): Int {
        return actions.size
    }

    internal inner class ActionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemBinding: ItemActionViewBinding
        fun bind(action: Action<Any?>) {
            itemBinding.tvActionName.text = action.label
            itemBinding.tvActionName.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    action.textColor
                )
            )
            itemBinding.tvActionName.setOnClickListener(ViewClickListener { v: View? ->
                actionOnItemClickListener.onItemClick(
                    action
                )
            })
        }

        init {
            itemBinding = ItemActionViewBinding.bind(itemView)
        }
    }
}