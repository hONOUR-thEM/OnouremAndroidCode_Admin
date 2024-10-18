package com.onourem.android.activity.ui.audio.fragments

import android.Manifest
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
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
import com.onourem.android.activity.databinding.FragmentVocalsMainBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
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


class VocalsFragment :
    AbstractBaseViewModelBindingFragment<MediaOperationViewModel, FragmentVocalsMainBinding>() {

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(parentJob + Dispatchers.Default)

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var userActionViewModel: UserActionViewModel

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    private var vocalUserId: String = ""
    private var userName: String = ""
    private var linkUserId: String = ""
    private var audioIdFromNotification: String = ""


    override fun layoutResource(): Int {
        return R.layout.fragment_vocals_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[DashboardViewModel::class.java]

        userActionViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]

        setHasOptionsMenu(true)
    }

//    override fun onPrepareOptionsMenu(menu: Menu) {
////        val item = menu.findItem(R.id.search_nav)
////        if (item != null) item.isVisible = false
//        menu.findItem(R.id.search_nav).isVisible = false
//        menu.findItem(R.id.profile_nav).isVisible = false
//        menu.findItem(R.id.phone_nav).isVisible = false
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}
            override fun onPrepareMenu(menu: Menu) {
                menu.findItem(R.id.search_nav)?.setVisible(false)
                menu.findItem(R.id.profile_nav)?.setVisible(false)
                menu.findItem(R.id.phone_nav)?.setVisible(false)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        vocalUserId = VocalsFragmentArgs.fromBundle(requireArguments()).vocalUserId
        userName = VocalsFragmentArgs.fromBundle(requireArguments()).userName
        linkUserId = VocalsFragmentArgs.fromBundle(requireArguments()).linkUserId
        audioIdFromNotification =
            VocalsFragmentArgs.fromBundle(requireArguments()).audioIdFromNotification

        if ((fragmentContext as DashboardActivity).isGuestUser()) {
            binding.tabs.visibility = View.INVISIBLE
            binding.btnLogin.visibility = View.VISIBLE
            binding.ivMood.setColorFilter(
                ContextCompat.getColor(
                    fragmentContext,
                    R.color.color_gray_cross
                ), android.graphics.PorterDuff.Mode.SRC_IN
            )

            binding.btnLogin.setOnClickListener(ViewClickListener {
                (fragmentContext as DashboardActivity).onLogout()
            })
        }
        else {
            binding.tabs.visibility = View.VISIBLE
            binding.btnLogin.visibility = View.INVISIBLE
        }

        if ((fragmentContext as DashboardActivity).isGuestUser()) {
            renderGuestViewPager()
            renderGuestTabLayout()
        }
        else {
            renderViewPager()
            renderTabLayout()
        }
    }

    override fun viewModelType(): Class<MediaOperationViewModel> {
        return MediaOperationViewModel::class.java
    }

    private fun renderViewPager() {
        binding.viewpager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0    -> if ((fragmentContext as DashboardActivity).isGuestUser()) {
                        GuestTrendingVocalsFragment.create()
                    }
                    else {
                        TrendingVocalsFragment.create(
                            vocalUserId,
                            linkUserId,
                            audioIdFromNotification
                        )
                    }
                    1    -> if ((fragmentContext as DashboardActivity).isGuestUser()) {
                        GuestFriendsVocalsFragment.create()
                    }
                    else {
                        FriendsVocalsFragment.create()
                    }
                    2    -> if ((fragmentContext as DashboardActivity).isGuestUser()) {
                        GuestMyVocalsFragment.create()
                    }
                    else {
                        MyVocalsFragment.create()
                    }
                    3    -> if ((fragmentContext as DashboardActivity).isGuestUser()) {
                        GuestFavouriteVocalsFragment.create()
                    }
                    else {
                        FavoriteVocalsFragment.create()
                    }
                    else -> if ((fragmentContext as DashboardActivity).isGuestUser()) {
                        TrendingVocalsFragment.create(
                            vocalUserId,
                            linkUserId,
                            audioIdFromNotification
                        )
                    }
                    else {
                        GuestTrendingVocalsFragment.create()
                    }
                }
            }

            override fun getItemCount(): Int {
                return vocalsTabList.size
            }
        }

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        })


    }


    private fun renderGuestViewPager() {
        binding.viewpager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return GuestTrendingVocalsFragment.create()
            }

            override fun getItemCount(): Int {
                return guestVocalsTabList.size
            }
        }

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
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
            tab.text = getString(vocalsTabList[position])

            for (i in 0 until (binding.tabs.getChildAt(0) as ViewGroup).childCount) {
                val tabView = (binding.tabs.getChildAt(0) as ViewGroup).getChildAt(i)
                val p = tabView.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(0, 0, 20, 0)
                tabView.requestLayout()
            }

        }.attach()

        binding.fab.setOnClickListener(ViewClickListener {

            if (preferenceHelper.getInt(Constants.KEY_AUDIO_LIMIT) == 0){
                preferenceHelper.putValue(Constants.KEY_AUDIO_LIMIT, 180)
            }

            if ((fragmentContext as DashboardActivity).canUserAccessApp) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    coroutineScope.launch {
                        withContext(Dispatchers.Main) {
                            handlePermissionResult(
                                PermissionManager.requestPermissions(
                                    this@VocalsFragment, 5,
                                    Manifest.permission.READ_MEDIA_AUDIO,
                                    Manifest.permission.RECORD_AUDIO,
                                )
                            )
                        }
                    }
                } else {
                    coroutineScope.launch {
                        withContext(Dispatchers.Main) {
                            handlePermissionResult(
                                PermissionManager.requestPermissions(
                                    this@VocalsFragment, 5,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO,
//                            Manifest.permission.READ_CONTACTS,
                                )
                            )
                        }
                    }
                }

            }else{
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }

        })

        binding.cvSearch.setOnClickListener(ViewClickListener {

            if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                if ((fragmentContext as DashboardActivity).isGuestUser()) {
                    (fragmentContext as DashboardActivity).showGuestPopup(
                        "Search Vocals",
                        "You can find Vocals by title or creator name after you login."
                    )
                }
                else {
                    val titleText = "Choose Option"
                    val actions = ArrayList<Action<*>>()

                    actions.add(
                        Action(
                            getString(R.string.action_label_search_by_user),
                            R.color.color_black,
                            ActionType.VOCALS_BY_USER,
                            "VOCALS_BY_USER"
                        )
                    )
                    actions.add(
                        Action(
                            getString(R.string.action_label_search_by_title),
                            R.color.color_black,
                            ActionType.VOCALS_BY_TITLE,
                            "VOCALS_BY_TITLE"
                        )
                    )

                    val bundle = Bundle()
                    bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavSearchAction(
                            titleText,
                            bundle,
                            ""
                        )
                    )
                }
            }else{
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }

        })

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

        userActionViewModel!!.searchActionMutableLiveData.observe(
            viewLifecycleOwner
        ) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.searchActionConsumed()
                when (action.actionType) {
                    ActionType.VOCALS_BY_USER  -> goToUserSearch()
                    ActionType.VOCALS_BY_TITLE -> goToVocalsSearch()
                    else                       -> {}
                }
            }
        }

        binding.viewpager.postDelayed({
            binding.viewpager.currentItem =
                (fragmentContext as DashboardActivity).getVocalsSelectedFilter()
        }, 10)

    }


    private fun renderGuestTabLayout() {

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
            tab.text = getString(vocalsTabList[position])
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


        //set the badge
//        val badgeDrawable: BadgeDrawable = binding.tabs.getTabAt(1)!!.orCreateBadge
//        badgeDrawable.isVisible = true
//        badgeDrawable.number = 5

//        binding.viewpager.currentItem =
//            if (preferenceHelper.getString(Constants.KEY_SELECTED_FILTER_INT) != "") {
//                preferenceHelper.getString(Constants.KEY_SELECTED_FILTER_INT).toInt()
//            } else {
//                0
//            }

        binding.fab.setOnClickListener(ViewClickListener {
//            mPlayerAdapter!!.pause()

            if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                viewModel.setPlayerOperation(AudioPlayerService.KEY_SHOULD_NOT_PLAY)
                (fragmentContext as DashboardActivity).setMusicPlayerVisibility(false)
                val navBuilder = NavOptions.Builder()
                val navOptions = navBuilder.setPopUpTo(R.id.nav_vocals_main, false).build()
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavRecorder(
                        preferenceHelper.getInt(Constants.KEY_AUDIO_LIMIT).toLong()
                    ), navOptions
                )
            }else{
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }

        })


        binding.cvSearch.setOnClickListener(ViewClickListener {

            if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                if ((fragmentContext as DashboardActivity).isGuestUser()) {
                    (fragmentContext as DashboardActivity).showGuestPopup(
                        "Search Vocals",
                        "You can find Vocals by title or creator name after you login."
                    )
                }
                else {
                    val titleText = "Choose Option"
                    val actions = ArrayList<Action<*>>()

                    actions.add(
                        Action(
                            getString(R.string.action_label_search_by_user),
                            R.color.color_black,
                            ActionType.VOCALS_BY_USER,
                            "VOCALS_BY_USER"
                        )
                    )
                    actions.add(
                        Action(
                            getString(R.string.action_label_search_by_title),
                            R.color.color_black,
                            ActionType.VOCALS_BY_TITLE,
                            "VOCALS_BY_TITLE"
                        )
                    )

                    val bundle = Bundle()
                    bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavSearchAction(
                            titleText,
                            bundle,
                            ""
                        )
                    )
                }
            }else{
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }

        })

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

        userActionViewModel!!.searchActionMutableLiveData.observe(
            viewLifecycleOwner
        ) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.searchActionConsumed()
                when (action.actionType) {
                    ActionType.VOCALS_BY_USER  -> goToUserSearch()
                    ActionType.VOCALS_BY_TITLE -> goToVocalsSearch()
                    else                       -> {}
                }
            }
        }

    }


    private fun goToVocalsSearch() {
        navController.navigate(MobileNavigationDirections.actionGlobalNavSearchVocalsByTitle())
    }

    private fun goToUserSearch() {
        navController.navigate(MobileNavigationDirections.actionGlobalNavSearchVocal())
    }

    companion object {
        val vocalsTabList = listOf(
            R.string.tabVocalsTrending,
            R.string.tabVocalsFriends,
            R.string.tabMyVocals,
            R.string.tabVocalsFavorite
        )

        val guestVocalsTabList = listOf(
            R.string.tabVocalsTrending,
        )
    }

    private fun handlePermissionResult(permissionResult: PermissionResult) {
        when (permissionResult) {
            is PermissionResult.PermissionGranted           -> {
                viewModel.setPlayerOperation(AudioPlayerService.KEY_SHOULD_NOT_PLAY)
                (fragmentContext as DashboardActivity).setMusicPlayerVisibility(false)
                val navBuilder = NavOptions.Builder()
                val navOptions = navBuilder.setPopUpTo(R.id.nav_vocals_main, false).build()
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavRecorder(
                        preferenceHelper.getInt(Constants.KEY_AUDIO_LIMIT).toLong()
                    ), navOptions
                )
            }
            is PermissionResult.PermissionDenied            -> {
                Toast.makeText(fragmentContext, "Denied", Toast.LENGTH_SHORT).show()
            }
            is PermissionResult.ShowRational                -> {
                showAlert(
                    "Permissions Needed",
                    "We need permissions to work record audio functionality.",
                    ViewClickListener {
                        when (permissionResult.requestCode) {

                            5 -> {
                                coroutineScope.launch(Dispatchers.Main) {
                                    handlePermissionResult(
                                        PermissionManager.requestPermissions(
                                            this@VocalsFragment,
                                            5,
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.RECORD_AUDIO,
//                                            Manifest.permission.READ_CONTACTS,
                                        )
                                    )
                                }
                            }
                        }
                    })

            }
            is PermissionResult.PermissionDeniedPermanently -> {
                showAlert(
                    "Permissions Needed",
                    "You have denied app permissions permanently, We need permissions to work record audio functionality. Please go to Settings and give Onourem App required permissions to use the app.",
                    ViewClickListener {
                        fragmentContext.openAppSystemSettings()
                    })
            }
        }
    }

}
