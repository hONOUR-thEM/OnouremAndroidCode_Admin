package com.onourem.android.activity.ui.games.adapters

import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.PrivacyGroup
import com.onourem.android.activity.ui.games.adapters.PrivacyGroupAdapter.PrivacyGroupViewHolder
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class PrivacyGroupAdapter(
    private val privacyGroupList: MutableList<PrivacyGroup>,
    private val onItemClickListener: OnItemClickListener<Pair<String, PrivacyGroup>>?
) : RecyclerView.Adapter<PrivacyGroupViewHolder>() {
    fun resetData(privacyGroupArrayList: ArrayList<PrivacyGroup>?) {
        privacyGroupList.clear()
        privacyGroupList.addAll(privacyGroupArrayList!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrivacyGroupViewHolder {
        return PrivacyGroupViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_question_games_group, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PrivacyGroupViewHolder, position: Int) {
        val options = privacyGroupList[position]
        holder.tvGroupName.text = options.groupName
        holder.clGroups.visibility = View.VISIBLE
        holder.clSection.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return privacyGroupList.size
    }

    inner class PrivacyGroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvGroupName: AppCompatTextView
        val clGroups: ConstraintLayout
        val clSection: ConstraintLayout

        init {
            view.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener?.onItemClick(
                    Pair.create(ACTION_ROW, privacyGroupList[bindingAdapterPosition])
                )
            })
            tvGroupName = view.findViewById(R.id.tvTitle)
            clGroups = view.findViewById(R.id.clGroups)
            clSection = view.findViewById(R.id.clSection)
            val ivMoreMenu = view.findViewById<AppCompatImageView>(R.id.ivMore)
            ivMoreMenu.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener?.onItemClick(
                    Pair.create(ACTION_MORE, privacyGroupList[bindingAdapterPosition])
                )
            })
        }
    }

    companion object {
        const val ACTION_MORE = "MORE"
        const val ACTION_ROW = "ROW"
    }
}