package com.onourem.android.activity.ui.dashboard

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.databinding.FragmentMoodsTodayBinding
import com.onourem.android.activity.models.GetWatchListResponse
import com.onourem.android.activity.models.UserWatchList
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.dashboard.watchlist.OnWatchListItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject

class MoodsTodayFragment :
    AbstractBaseBottomSheetBindingDialogFragment<DashboardViewModel, FragmentMoodsTodayBinding>(),
    OnWatchListItemClickListener<Pair<Int, UserWatchList>> {
    var viewPagerAdapter: ViewPagerAdapter? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var userWatchLists: ArrayList<UserWatchList>? = null
    override fun isCancelable(): Boolean {
        return false
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_moods_today
    }

    public override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as View).setBackgroundColor(Color.TRANSPARENT)
        val moodsTodayFragmentArgs = MoodsTodayFragmentArgs.fromBundle(requireArguments())
        setAdapter(moodsTodayFragmentArgs.userWatchlistResponse)
        isCancelable = false
        binding.cvClose.setOnClickListener(ViewClickListener { v: View? -> dismiss() })
    }

    private fun setAdapter(userWatchlistResponse: GetWatchListResponse) {
        userWatchLists = ArrayList()
        for (userWatchList in userWatchlistResponse.userWatchList!!) {
            if (userWatchList.status.equals("Watching", ignoreCase = true)) {
                userWatchLists!!.add(userWatchList)
            }
        }
        //userWatchLists = (ArrayList<UserWatchList>) userWatchlistResponse.getUserWatchList();
        viewPagerAdapter =
            ViewPagerAdapter(fragmentContext, userWatchLists!!, navController, preferenceHelper!!)
        binding.rvWatchlist.adapter = viewPagerAdapter
        binding.indicator.setViewPager(binding.rvWatchlist)
        binding.btnNext.setOnClickListener(ViewClickListener { view: View? ->
            binding.rvWatchlist.setCurrentItem(
                binding.rvWatchlist.currentItem + 1, true
            )
        })
        binding.btnPrev.setOnClickListener(ViewClickListener { view: View? ->
            binding.rvWatchlist.setCurrentItem(
                binding.rvWatchlist.currentItem - 1, true
            )
        })
        binding.rvWatchlist.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.btnPrev.visibility = View.INVISIBLE
                } else {
                    binding.btnPrev.visibility = View.VISIBLE
                }
                if (viewPagerAdapter != null) {
                    if (position < viewPagerAdapter!!.count - 1) {
                        binding.btnNext.visibility = View.VISIBLE
                    } else {
                        binding.btnNext.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })
    }

    override fun onItemClick(item: Pair<Int, UserWatchList>, position: Int) {

    }

}