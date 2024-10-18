package com.onourem.android.activity.ui.onboarding.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.navigation.NavController
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textview.MaterialTextView
import com.onourem.android.activity.R
import com.onourem.android.activity.models.OnboardingPage
import com.onourem.android.activity.ui.onboarding.fragments.OnboardingFragmentDirections
import com.onourem.android.activity.ui.utils.listners.ViewClickListener

class OnboardingPagerAdapter     // new OnboardingPage(R.string.label_page4_title, R.string.label_page4_desc, R.drawable.page4)
    (
    private val context: Context,
    private val onboardingPages: ArrayList<OnboardingPage>,
    private val navController: NavController
) : PagerAdapter() {
    private val options = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)

    override fun getCount(): Int {
        return onboardingPages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.onboarding_pager_item, container, false)
        val onboardingPage = onboardingPages[position]
        val imageView = itemView.findViewById<AppCompatImageView>(R.id.img_pager_item)
        val tvPlay = itemView.findViewById<MaterialTextView>(R.id.tvPlay)
        imageView.setImageResource(onboardingPages[position].image)
        tvPlay.isEnabled = onboardingPage.isVideoAvailable
        tvPlay.setOnClickListener(getListener(onboardingPage.videoLink2))
        container.addView(itemView)
        return itemView
    }

    private fun getListener(s: String): ViewClickListener {
        return ViewClickListener { view: View? ->
            navController.navigate(
                OnboardingFragmentDirections.actionGlobalNavMediaView(2, s)
            )
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}