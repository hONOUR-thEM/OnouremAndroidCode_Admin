package com.onourem.android.activity.ui.circle.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R

class ViewPagerHeaderAdapter(val title: String, val drawable: String?) :
    RecyclerView.Adapter<ViewPagerHeaderAdapter.HeaderViewHolder>() {


    /* ViewHolder for displaying header. */
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val rivProfile: AppCompatImageView = itemView.findViewById(R.id.ivImage)
        private val cvImage: ConstraintLayout = itemView.findViewById(R.id.cvImage)

        private val options = RequestOptions()
            .fitCenter()
            .transform(FitCenter(), RoundedCorners(30)) //15
            .placeholder(R.drawable.default_place_holder)
            .error(R.drawable.default_place_holder)

        fun bind(pos: Int, title: String, drawable: String?) {
            txtTitle.text = title
            if (drawable != null) {
                Glide.with(itemView.context)
                    .load(drawable)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .apply(options)
                    .into(rivProfile)
            } else {
                rivProfile.visibility = View.GONE
                cvImage.visibility = View.GONE
            }


        }
    }

    /* Inflates view and returns HeaderViewHolder. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_header_view_pager_item, parent, false)
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