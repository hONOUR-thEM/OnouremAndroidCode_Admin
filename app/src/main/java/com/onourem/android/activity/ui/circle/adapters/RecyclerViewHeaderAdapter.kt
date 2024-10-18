package com.onourem.android.activity.ui.circle.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onourem.android.activity.R

class RecyclerViewHeaderAdapter(val title: String, val drawable: Drawable?) :
    RecyclerView.Adapter<RecyclerViewHeaderAdapter.HeaderViewHolder>() {

    /* ViewHolder for displaying header. */
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val rivProfile: AppCompatImageView = itemView.findViewById(R.id.rivProfile)
        private val cvProfile: ConstraintLayout = itemView.findViewById(R.id.cvProfile)

        fun bind(pos: Int, title: String, drawable: Drawable?) {
            txtTitle.text = title
            if (drawable != null) {
                Glide.with(itemView.context)
                    .load(drawable)
                    .into(rivProfile)
            } else {
                rivProfile.visibility = View.GONE
                cvProfile.visibility = View.GONE
            }


        }
    }

    /* Inflates view and returns HeaderViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_header_item, parent, false)
        return HeaderViewHolder(view)
    }

    /* Binds number of flowers to the header. */
    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(position, title, drawable)
    }

    /* Returns number of items, since there is only one item in the header return one  */
    override fun getItemCount(): Int {
        return 1
    }
}