package com.onourem.android.activity.ui.games.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onourem.android.activity.R

class HeaderAdapter(val title: String, val titleSub: String, val drawable: Drawable) :
    RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder>() {

    /* ViewHolder for displaying header. */
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtSubTitle: TextView = itemView.findViewById(R.id.txtSubTitle)
        private val rivProfile: AppCompatImageView = itemView.findViewById(R.id.rivProfile)

        fun bind(pos: Int, title: String, titleSub: String, drawable: Drawable) {
            txtTitle.text = title
            txtSubTitle.text = titleSub
            Glide.with(itemView.context)
                .load(drawable)
                .into(rivProfile)
        }
    }

    /* Inflates view and returns HeaderViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.header_item, parent, false)
        return HeaderViewHolder(view)
    }

    /* Binds number of flowers to the header. */
    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(position, title, titleSub, drawable)
    }

    /* Returns number of items, since there is only one item in the header return one  */
    override fun getItemCount(): Int {
        return 1
    }
}