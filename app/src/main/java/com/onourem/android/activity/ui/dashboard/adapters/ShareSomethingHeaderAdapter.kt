package com.onourem.android.activity.ui.dashboard.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.R

class ShareSomethingHeaderAdapter(val title: String, val subTitle: String) :
    RecyclerView.Adapter<ShareSomethingHeaderAdapter.HeaderViewHolder>() {

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtSubTitle: TextView = itemView.findViewById(R.id.txtSubTitle)

        fun bind(title: String, subTitle: String) {
            txtTitle.text = title
            txtSubTitle.text = subTitle
        }
    }

    /* Inflates view and returns HeaderViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_header_share_something_item, parent, false)
        return HeaderViewHolder(view)
    }

    /* Binds number of flowers to the header. */
    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bind(title, subTitle)
    }

    /* Returns number of items, since there is only one item in the header return one  */
    override fun getItemCount(): Int {
        return 1
    }
}