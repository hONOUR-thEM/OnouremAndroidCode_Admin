package com.onourem.android.activity.ui.games.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R

class SecondHeaderWatchlistAdapter(
    val title: String,
    val titleSub: String,
    val drawable: Drawable
) :
    RecyclerView.Adapter<SecondHeaderWatchlistAdapter.HeaderViewHolder>() {


    /* ViewHolder for displaying header. */
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtSubTitle: TextView = itemView.findViewById(R.id.txtSubTitle)
        private val rivProfile: AppCompatImageView = itemView.findViewById(R.id.rivProfile)

        val options = RequestOptions()
            .fitCenter()
            .transform(FitCenter(), RoundedCorners(30)) //15
            .placeholder(R.drawable.default_place_holder)
            .error(R.drawable.default_place_holder)

        fun bind(pos: Int, title: String, titleSub: String, drawable: Drawable) {
            txtSubTitle.text = titleSub
            Glide.with(itemView.context)
                .load(drawable)
                .apply(options)
                .into(rivProfile)
        }
    }

    /* Inflates view and returns HeaderViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.watchlist_second_header_item, parent, false)
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