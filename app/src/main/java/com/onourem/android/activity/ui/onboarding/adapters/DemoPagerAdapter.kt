package com.onourem.android.activity.ui.onboarding.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.onourem.android.activity.R

class DemoPagerAdapter(private val context: Context, private val demoPages: ArrayList<String>) :
    PagerAdapter() {
    private val options = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.ic_logo)
        .error(R.drawable.ic_logo)

    override fun getCount(): Int {
        return demoPages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.demo_screens_pager_item, container, false)
        Glide.with(context)
            .load(demoPages[position])
            .apply(options)
            .into((itemView.findViewById<View>(R.id.img_pager_item) as ImageView))
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}