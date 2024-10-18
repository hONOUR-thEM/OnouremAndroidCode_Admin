package com.onourem.android.activity.ui.onboarding.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R
import com.onourem.android.activity.ui.onboarding.adapters.DemoScreensAdapter.DemoScreensViewHolder

class DemoScreensAdapter(private val context: Context, private val stringList: List<String>) :
    RecyclerView.Adapter<DemoScreensViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DemoScreensViewHolder {
        return DemoScreensViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.demo_screens_pager_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DemoScreensViewHolder, position: Int) {
        val options = RequestOptions()
            .fitCenter()
            .placeholder(R.drawable.ic_logo)
            .error(R.drawable.ic_logo)
        Glide.with(context)
            .load(stringList[position])
            .apply(options)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return stringList.size
    }

    class DemoScreensViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        val imageView: AppCompatImageView

        init {
            imageView = itemView.findViewById(R.id.img_pager_item)
        }
    }
}