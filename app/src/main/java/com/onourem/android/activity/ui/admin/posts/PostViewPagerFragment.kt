package com.onourem.android.activity.ui.admin.posts

import android.Manifest
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentPostViewPagerBinding
import com.onourem.android.activity.databinding.FragmentVocalsMainBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.audio_exo.internal.AudioPlayerService
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.dashboard.guest.audio.GuestFavouriteVocalsFragment
import com.onourem.android.activity.ui.dashboard.guest.audio.GuestFriendsVocalsFragment
import com.onourem.android.activity.ui.dashboard.guest.audio.GuestMyVocalsFragment
import com.onourem.android.activity.ui.dashboard.guest.audio.GuestTrendingVocalsFragment
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import kotlinx.coroutines.*
import javax.inject.Inject


class PostViewPagerFragment :
    AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentPostViewPagerBinding>() {

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(parentJob + Dispatchers.Default)

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var userActionViewModel: UserActionViewModel

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    override fun layoutResource(): Int {
        return R.layout.fragment_post_view_pager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[DashboardViewModel::class.java]

        userActionViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]

        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
//        val item = menu.findItem(R.id.search_nav)
//        if (item != null) item.isVisible = false
        menu.findItem(R.id.search_nav).isVisible = false
        menu.findItem(R.id.phone_nav).isVisible = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderViewPager()
        renderTabLayout()
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    private fun renderViewPager() {
        binding.viewpager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> PendingPostsFragment.create()
                    1 -> LivePostsFragment.create()
                    2 -> RejectedPostsFragment.create()
                    else -> {
                        PendingPostsFragment.create()
                    }
                }
            }

            override fun getItemCount(): Int {
                return tabList.size
            }
        }

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

//                if ((fragmentContext as DashboardActivity).getPlayingItem() != null) {
//                    dashboardViewModel.setUpdateUI((fragmentContext as DashboardActivity).getPlayingItem())
//                }
                super.onPageSelected(position)
            }
        })


    }

    fun setStyleForTab(tab: TabLayout.Tab, style: Int) {
        tab.view.children.find { it is TextView }?.let { tv ->
            (tv as TextView).post {
                tv.setTypeface(tv.typeface, style)
            }
        }
    }

    private fun renderTabLayout() {

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    setStyleForTab(it, Typeface.NORMAL)
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    setStyleForTab(it, Typeface.BOLD)
                }
            }


        })


        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = tabList[position]
            //tab.icon = ContextCompat.getDrawable(fragmentContext, R.drawable.ic_heart)
//            setStyleForTab(tab, Typeface.NORMAL )
            // val tabs = binding.tabs.getChildAt(0) as ViewGroup

            for (i in 0 until (binding.tabs.getChildAt(0) as ViewGroup).childCount) {
                val tabView = (binding.tabs.getChildAt(0) as ViewGroup).getChildAt(i)
                val p = tabView.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(0, 0, 20, 0)
                tabView.requestLayout()
            }
        }.attach()


//        binding.cvSearch.setOnClickListener(ViewClickListener {
//
//            if ((fragmentContext as DashboardActivity).isGuestUser()) {
//                (fragmentContext as DashboardActivity).showGuestPopup(
//                    "Search Vocals",
//                    "You can find Vocals by title or creator name after you login."
//                )
//            } else {
//                val titleText = "Choose Option"
//                val actions = ArrayList<Action<*>>()
//
//                actions.add(
//                    Action(
//                        getString(R.string.action_label_search_by_user),
//                        R.color.color_black,
//                        ActionType.VOCALS_BY_USER,
//                        "VOCALS_BY_USER"
//                    )
//                )
//                actions.add(
//                    Action(
//                        getString(R.string.action_label_search_by_title),
//                        R.color.color_black,
//                        ActionType.VOCALS_BY_TITLE,
//                        "VOCALS_BY_TITLE"
//                    )
//                )
//
//                val bundle = Bundle()
//                bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
//                navController.navigate(
//                    MobileNavigationDirections.actionGlobalNavSearchAction(
//                        titleText,
//                        bundle,
//                        ""
//                    )
//                )
//            }
//        })

//        if (vocalUserId != "" || linkUserId != "" || audioIdFromNotification != "") {
//
//            navController.navigate(
//                MobileNavigationDirections.actionGlobalNavOthersVocals(
//                    vocalUserId,
//                    userName,
//                    linkUserId,
//                    audioIdFromNotification,
//                )
//            )
//        }

    }

    companion object {
        val tabList = listOf(
            "Pending",
            "Live On Sky",
            "Rejected"
        )
    }

}
