package com.onourem.android.activity.ui.games.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onourem.android.activity.R
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class MoodHeaderAdapter(val drawable: Int, private val onItemClickListener: OnItemClickListener<Int>?) :
    RecyclerView.Adapter<MoodHeaderAdapter.HeaderViewHolder>() {

    /* ViewHolder for displaying header. */
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val rivProfile: AppCompatImageView = itemView.findViewById(R.id.ivMood)

        fun bind(drawable: Int, onItemClickListener: OnItemClickListener<Int>?) {
            Glide.with(itemView.context)
                .load(drawable)
                .into(rivProfile)

            itemView.setOnClickListener(ViewClickListener {
                onItemClickListener?.onItemClick(1)
            })
        }

        init {

        }
    }

    /* Inflates view and returns HeaderViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.header_mood_item, parent, false)
        return HeaderViewHolder(view)
    }

    /* Binds number of flowers to the header. */
    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(drawable, onItemClickListener)
    }

    /* Returns number of items, since there is only one item in the header return one  */
    override fun getItemCount(): Int {
        return 1
    }
}