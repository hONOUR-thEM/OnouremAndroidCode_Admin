package com.onourem.android.activity.ui.dashboard.guest

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentDashboardPlayGamesGuestBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.adapters.PlaySoloGamesAdapter
import com.onourem.android.activity.ui.games.viewmodel.GamesReceiverViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.survey.viewmodel.SurveyViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.ui.utils.listners.RVScrollListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

class GuestFriendsPlayingFragment() :
    AbstractBaseViewModelBindingFragment<DashboardViewModel, FragmentDashboardPlayGamesGuestBinding>(),
    OnItemClickListener<Triple<LoginDayActivityInfoList?, Int?, String?>?> {

    private var rvScrollListener: RVScrollListener? = null

    private var linkType: String? = ""
    private var linkUserId: String? = ""
    private var isMoodDialogShowing: Boolean = false
    private lateinit var surveyViewModel: SurveyViewModel
    private var activityStatusList: MutableList<String>? = ArrayList()
    private var gameResIdList: MutableList<String>? = ArrayList()

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var loginDayActivityInfoList: MutableList<LoginDayActivityInfoList>? = ArrayList()

    private var isLinkVerified = ""
    private var questionGamesViewModel: QuestionGamesViewModel? = null
    private lateinit var playGroup: PlayGroup
    private var soloGamesAdapter: GuestPlaySoloGamesAdapter? = null
    private var gamesReceiverViewModel: GamesReceiverViewModel? = null

    private var filterItems: ArrayList<FilterItem>? = null
    private var selectedFilterIndex = 0
    private var userActionViewModel: UserActionViewModel? = null
    private var isDataLoading = false
    private var isFrom = ""
    private var displayNumberOfActivity: Long? = 10L

    private val parentJob = Job()

    private var layoutManager: LinearLayoutManager? = null

    companion object {

        private var isLoading = false
        private var isLastPage = false

        fun create(linkUserId: String, linkType: String,): GuestFriendsPlayingFragment {
            val fragment = GuestFriendsPlayingFragment()
            val args = Bundle()
            args.putString("linkUserId", linkUserId)
            args.putString("linkType", linkType)
            fragment.arguments = args
            return fragment
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        questionGamesViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            QuestionGamesViewModel::class.java
        )
        gamesReceiverViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            GamesReceiverViewModel::class.java
        )
        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            UserActionViewModel::class.java
        )

        surveyViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            SurveyViewModel::class.java
        )
        questionGamesViewModel!!.reloadUI().observe(this) { reload: Boolean ->
            if (reload) {
                refreshUI()
            }
        }

        questionGamesViewModel!!.gameActivityUpdateStatus.observe(this) { item: Pair<String?, LoginDayActivityInfoList>? ->
            if (item != null) {
                questionGamesViewModel!!.setGameActivityUpdateStatus(null)
                if (!(playGroup.playGroupId.equals(
                        "AAA",
                        ignoreCase = true
                    ) || playGroup.playGroupId.equals(
                        "ZZZ",
                        ignoreCase = true
                    )) && playGroup.playGroupId.equals(item.first, ignoreCase = true)
                ) {
                    if (loginDayActivityInfoList!!.isEmpty()) {
                        if (!isDataLoading) loadData()
                    } else if (!loginDayActivityInfoList!!.contains(item.second)) {
                        loginDayActivityInfoList!!.clear()
                        activityStatusList!!.clear()
                        soloGamesAdapter!!.clearData()
                        refreshUI()
                    } else {
                        if (soloGamesAdapter != null) {
                            soloGamesAdapter!!.notifyDataSetChanged()
                        }
                    }
                } else {
                    if (soloGamesAdapter != null) {
                        soloGamesAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        }

        questionGamesViewModel!!.actionOpenPlayGroupListing.observe(this) { action: Boolean ->
            if (action && isFrom.equals("solo", ignoreCase = true)) {
                val id = navController.currentDestination!!.id
                if (id == R.id.nav_home) {
                    questionGamesViewModel!!.actionOpenPlayGroupListingConsumed()
                    questionGamesViewModel!!.actionCameFromDashboardConsumed()
                    binding.fabOClub.performClick()
                }
            }
        }

//        surveyViewModel.surveyAnswered.observe(this) { item: SurveyCOList? ->
//            if (item != null) {
//                surveyViewModel.actionConsumed()
//                for (question in loginDayActivityInfoList!!) {
//                    if (item.surveyIdFromQuestionListing == question.activityId) {
//                        item.userAnserForSurvey = "Y"
//                        question.surveySeeStats = "answered"
//                        question.surveyCO = item
//                        soloGamesAdapter!!.updateItem(question)
//                        break
//                    }
//                }
//            }
//        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linkType = requireArguments().getString("linkType")
        linkUserId = requireArguments().getString("linkUserId")
        viewModel.isMoodsDialogShowing.observe(viewLifecycleOwner) { show: Boolean ->
            isMoodDialogShowing = show
        }

        rvScrollListener = (fragmentContext as DashboardActivity).getRVScrollListenerReference()


        preferenceHelper!!.putValue(Constants.KEY_SELECTED_FILTER_INDEX, selectedFilterIndex)

//        linkUserId = preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID)
//        linkType = preferenceHelper!!.getString(Constants.KEY_LINK_TYPE)
//        isLinkVerified = preferenceHelper!!.getString(Constants.KEY_IS_LINK_VERIFIED)


        layoutManager = LinearLayoutManager(requireActivity())
        binding.rvPlayGames.layoutManager = layoutManager

        playGroup = PlayGroup()
        playGroup.playGroupId = "FFF"
        playGroup.playGroupName = "Friends Playing"
        playGroup.isDummyGroup = true


//        if ((fragmentContext as DashboardActivity).loginDayActivityInfoList != null && (fragmentContext as DashboardActivity).loginDayActivityInfoList!!.size > 0 && linkUserId == "") {
//            //setDashboardData()
//        } else {
//            isFrom = "Solo"
//            when (selectedFilterIndex) {
//                0 -> {
//                    playGroup = PlayGroup()
//                    playGroup.playGroupId = "FFF"
//                    playGroup.playGroupName = "Friends Playing"
//                    playGroup.isDummyGroup = true
//                }
////                1 -> {
////                    playGroup = PlayGroup()
////                    playGroup.playGroupId = "AAA"
////                    playGroup.playGroupName = "New"
////                    playGroup.isDummyGroup = true
////                }
//                2 -> {
//                    playGroup = PlayGroup()
//                    playGroup.playGroupId = "ZZZ"
//                    playGroup.playGroupName = "Played"
//                    playGroup.isDummyGroup = true
//                }
//                3 -> {
//                    playGroup = PlayGroup()
//                    playGroup.playGroupId = "YYY"
//                    playGroup.playGroupName = "My Qs"
//                    playGroup.isDummyGroup = true
//                }
//            }
//
//        }

        loadData()

        //binding.fabOClub.visibility = View.VISIBLE

//        if (playGroup.playGroupId.equals("FFF", ignoreCase = true)
//            || playGroup.playGroupId.equals("AAA", ignoreCase = true)
//            || playGroup.playGroupId.equals("ZZZ", ignoreCase = true)
//            || playGroup.playGroupId.equals("YYY", ignoreCase = true)
//        ) {
//
//
//        } else {
//            binding.fabOClub.visibility = View.GONE
//        }

//        if (playGroup.playGroupId.equals("FFF", ignoreCase = true)
//            || playGroup.playGroupId.equals("AAA", ignoreCase = true)
//            || playGroup.playGroupId.equals("ZZZ", ignoreCase = true)
//            || playGroup.playGroupId.equals("YYY", ignoreCase = true)
//        ) {
//
//        }


        binding.rvPlayGames.addOnScrollListener(object : PaginationListener(layoutManager!!) {
            override fun loadMoreItems() {
                GuestFriendsPlayingFragment.isLoading = true
                //loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return GuestFriendsPlayingFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return GuestFriendsPlayingFragment.isLoading
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            isDataLoading = false
//            if ((fragmentContext as DashboardActivity).loginDayActivityInfoList != null && (fragmentContext as DashboardActivity).loginDayActivityInfoList!!.size > 0) {
//                (fragmentContext as DashboardActivity).loginDayActivityInfoList!!.clear()
//            }
            refreshUI()
        }
        binding.rvPlayGames.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var lastPosition = 0

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val manager = (recyclerView.layoutManager as LinearLayoutManager?)!!
                val visiblePosition = manager.findFirstVisibleItemPosition()
//                println("++++: $visiblePosition $isShownRewards")
//                if (visiblePosition > -1 && visiblePosition == 5 && !isShownRewards) {
//                    isShownRewards = true
//                    (fragmentContext as DashboardActivity).showCampaignRewards()
//                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (layoutManager is LinearLayoutManager) {
                    val currentVisibleItemPosition =
                        (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (lastPosition != currentVisibleItemPosition && currentVisibleItemPosition != RecyclerView.NO_POSITION) {
                        lastPosition = currentVisibleItemPosition

//                        AppUtilities.showLog(
//                            "###",
//                            "lastPosition : $lastPosition"
//                        )
                    }
                }

//                if (dy < 0) {
//                    binding.fab.extend()
//                    binding.fabOClub.extend()
//                } else if (dy > 0) {
//                    binding.fab.shrink()
//                    binding.fabOClub.shrink()
//                }

                rvScrollListener?.onScroll(dx,dy)

//                val manager = (recyclerView.layoutManager as LinearLayoutManager?)!!
//                val totalCount = manager.itemCount
//
//
//                if (totalCount == activityStatusList!!.size && totalCount >= 20
//                    && !recyclerView.canScrollVertically(1)
//                ) {
////                    AppUtilities.showLog(
////                        "###",
////                        "Total Count : " + totalCount + "---ActivityStatusList : " + activityStatusList!!.size
////                    )
//                    if (manager.findLastVisibleItemPosition() == manager.itemCount - 1
//                        && !recyclerView.canScrollVertically(1)
//                    ) {
//                        getRemainingTopPriorityActivityIdList()
//                    }
//                }
            }


        })


    }

    private fun loadData() {
//        val getAppUpgradeInfoResponse = Gson().fromJson(
//            preferenceHelper!!.getString(Constants.KEY_GET_APP_UPDATE_RESPONSE),
//            GetAppUpgradeInfoResponse::class.java
//        )
//        if (getAppUpgradeInfoResponse != null) {
//            checkForceUpgradeData(getAppUpgradeInfoResponse)
//        } else {
//            getAppForceUpgradeInfo()
//        }

//        binding.swipeRefreshLayout.isRefreshing = true
        isLoading = false
        isLastPage = false

        if (loginDayActivityInfoList == null || loginDayActivityInfoList!!.isEmpty() && !isDataLoading || linkUserId != "") {
            isDataLoading = true
//                        questionGamesViewModel!!.getUserFriendAnsweredActivityInfo(linkUserId)
//                            .observe(
//                                viewLifecycleOwner
//                            ) { responseApiResponse: ApiResponse<UserActivityInfoResponse?> ->
//                                handleResponse(
//                                    responseApiResponse,
//                                    true,
//                                    showProgress,
//                                    "getUserFriendAnsweredActivityInfo"
//                                )
//                            }

            val id = if (linkType == "Card" || linkType == "1toM") {
                linkUserId
            } else {
                ""
            }

            AppUtilities.showLog(
                "Link", "LinkType :${
                    if (linkType != "") {
                        linkType
                    } else "Api getting called without LinkUserId"
                } API: getTopPriorityActivityInfo | sent LinkUserId = $id"
            )

            questionGamesViewModel!!.questionsForGuestUser()
                .observe(viewLifecycleOwner) { responseApiResponse: ApiResponse<NewActivityInfoResponse> ->
                    handleNewResponse(
                        responseApiResponse,
                        true,
                        "getQuestionsForGuestUser"
                    )
                }
        } else {
            setAdapter()
        }

        when (selectedFilterIndex) {
            0 -> {
                playGroup.playGroupId = "FFF"
                //binding.rvPlayGames.scrollToPosition(0);
                preferenceHelper!!.putValue(Constants.KEY_SELECTED_FILTER_INDEX, 0)

                //AppUtilities.showLog("PlayGamesFragment", "getUserFriendAnsweredActivityInfo");

            }
        }
    }

    private fun refreshUI() {

        isLastPage = false
        isLoading = false
        if (soloGamesAdapter != null) soloGamesAdapter!!.removeFooter()
        if (soloGamesAdapter != null) {
            soloGamesAdapter!!.clearData()
            soloGamesAdapter = null
        }
        activityStatusList!!.clear()
        gameResIdList!!.clear()
        loginDayActivityInfoList!!.clear()
        if (!isDataLoading) loadData()

//        if ((fragmentContext as DashboardActivity).loginDayActivityInfoList != null && (fragmentContext as DashboardActivity).loginDayActivityInfoList!!.size > 0 && linkUserId == "") {
//            //setDashboardData()
//        } else {
//
//        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleNewResponse(
        apiResponse: ApiResponse<NewActivityInfoResponse>,
        isSolo: Boolean,
        serviceName: String
    ) {
        if (apiResponse.loading) {
            isDataLoading = true
            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.tvMessage.text = "Loading Data, Please Wait...!"
                binding.tvMessage.visibility = View.VISIBLE
            } else {
                binding.tvMessage.text = ""
                binding.tvMessage.visibility = View.GONE
            }

        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            isDataLoading = false

            if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing =
                false
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                linkType = ""
                linkUserId = ""

                displayNumberOfActivity = apiResponse.body.displayNumberOfActivity
                //                AppUtilities.showLog("PlayGamesFragment", "LoginDayActivityInfoList " + apiResponse.body.getLoginDayActivityInfoList().size() + "");
//                AppUtilities.showLog("PlayGamesFragment", "ActivityStatusList " + (apiResponse.body.getActivityStatusList() != null ? apiResponse.body.getActivityStatusList().size() : "null") + "");
//                AppUtilities.showLog("PlayGamesFragment", "GameResIdList " + (apiResponse.body.getGameResIdList() != null ? apiResponse.body.getGameResIdList().size() : "null") + "");
                //showFiltersAndBadges(selectedSearch)
                if (apiResponse.body.loginDayActivityInfoList != null) {
                    binding.tvMessage.visibility = View.GONE

//                    if (apiResponse.body.activityStatusList != null && apiResponse.body.activityStatusList.isNotEmpty()) {
//                        activityStatusList!!.clear()
//                        activityStatusList!!.addAll(apiResponse.body.activityStatusList)
//                    }
//                    if (apiResponse.body.gameResIdList != null && apiResponse.body.gameResIdList.isNotEmpty()) {
//                        gameResIdList!!.clear()
//                        gameResIdList!!.addAll(apiResponse.body.gameResIdList)
//                    }
                    loginDayActivityInfoList!!.clear()
                    if (soloGamesAdapter != null) soloGamesAdapter!!.clearData()
                    loginDayActivityInfoList!!.addAll(apiResponse.body.loginDayActivityInfoList!!)
//                    if (soloGamesAdapter != null) soloGamesAdapter!!.notifyDataSetChanged()

                    setAdapter()

                } else {
                    val footerMessage: String =
                        if (playGroup.playGroupId.equals("FFF", ignoreCase = true)) {
                            "Looks like you do not have any questions for today. Please check back tomorrow."
                        } else if (playGroup.playGroupId.equals("AAA", ignoreCase = true)) {
                            "Looks like you do not have any questions for today. Please check back tomorrow."
                        } else if (playGroup.playGroupId.equals("ZZZ", ignoreCase = true)) {
                            "You have not answered any question so far"
                        } else if (playGroup.playGroupId.equals("YYY", ignoreCase = true)) {
                            "You have not created any question so far"
                        } else {
                            "No one has asked any question in this O-Club so far. Ask a question to start having fun."
                        }
                    binding.tvMessage.text = footerMessage
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.rvPlayGames.visibility = View.GONE
                }

            } else {
                showAlert(apiResponse.body.errorMessage)
            }
        } else {
            isDataLoading = false
            if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing =
                false
            hideProgress()
            showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
            if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message)) || apiResponse.errorMessage.contains(
                    getString(R.string.unable_to_connect_host_message1)
                )
                        || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
            ) {
                if (com.onourem.android.activity.BuildConfig.DEBUG) {
                    AppUtilities.showLog("Network Error", serviceName)
                }
                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                    serviceName,
                    apiResponse.code.toString()
                )
            }
        }
    }


    private fun setAdapter() {
        binding.swipeRefreshLayout.isRefreshing = false
        hideProgress()
        if (loginDayActivityInfoList != null && loginDayActivityInfoList!!.isNotEmpty()) {
            if (soloGamesAdapter == null) {
                soloGamesAdapter = GuestPlaySoloGamesAdapter(
                    playGroup,
                    loginDayActivityInfoList,
                    preferenceHelper,
                    this
                )
                binding.rvPlayGames.adapter = soloGamesAdapter
            } else {
                soloGamesAdapter!!.updateItems(loginDayActivityInfoList)
                binding.rvPlayGames.adapter = soloGamesAdapter
            }
            //setDashboardDataToActivity()
            binding.tvMessage.visibility = View.GONE
            binding.rvPlayGames.visibility = View.VISIBLE
            //            soloGamesAdapter.updateItems(loginDayActivityInfoList);
            hideProgress()
        } else {
            hideProgress()
            var footerMessage = ""
            //            if (playGroup.getPlayGroupId().equalsIgnoreCase("AAA")) {
//                footerMessage = "Looks like you do not have any questions for today. Please check back tomorrow.";
//            } else if (playGroup.getPlayGroupId().equalsIgnoreCase("FFF")) {
//                footerMessage = "You have no Friends so far";
//            } else if (playGroup.getPlayGroupId().equalsIgnoreCase("ZZZ")) {
//                footerMessage = "You have not answered any question so far";
//            } else if (playGroup.getPlayGroupId().equalsIgnoreCase("YYY")) {
//                footerMessage = "You have not created any question so far";
//            } else {
//                footerMessage = "No one has asked any question in this O-Club so far. Ask a question to start having fun.";
//            }
            footerMessage = if (playGroup.playGroupId.equals("FFF", ignoreCase = true)) {
                "You do not have questions in this section today. Here you see questions that you or your friends are playing."
            } else if (playGroup.playGroupId.equals("AAA", ignoreCase = true)) {
                "No new questions for today. Try other filters or create your question and ask friends."
            } else if (playGroup.playGroupId.equals("ZZZ", ignoreCase = true)) {
                "This section shows the questions you have answered. You have not answered any so far."
            } else if (playGroup.playGroupId.equals("YYY", ignoreCase = true)) {
                "You have not created any questions. Use the bottom left pen icon to create a question and ask friends."
            } else {
                "No one has asked any question in this O-Club so far. Ask a question to start having fun."
            }
            binding.tvMessage.text = footerMessage
            binding.tvMessage.visibility = View.VISIBLE
            binding.rvPlayGames.visibility = View.GONE
        }

        linkUserId = ""
        linkType = ""
        isLinkVerified = "false"
        preferenceHelper!!.putValue(Constants.KEY_LINK_USER_ID, linkUserId)
        preferenceHelper!!.putValue(Constants.KEY_LINK_TYPE, linkType)
        preferenceHelper!!.putValue(Constants.KEY_IS_LINK_VERIFIED, isLinkVerified)
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_dashboard_play_games_guest
    }

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun onPause() {
        super.onPause()
        //        state = layoutManager.onSaveInstanceState();
//        counter++;
        val positionIndex = layoutManager!!.findFirstVisibleItemPosition()
        val startView = binding.rvPlayGames.getChildAt(0)
        val topView = if (startView == null) 0 else startView.top - binding.rvPlayGames.paddingTop
        (fragmentContext as DashboardActivity).setDashboardRecyclerViewPosition(
            positionIndex,
            topView
        )
        isDataLoading = false
    }

    override fun onItemClick(item: Triple<LoginDayActivityInfoList?, Int?, String?>?) {
        if (item != null) {
            if (item.first != null) {
                when (item.second) {

                    PlaySoloGamesAdapter.CLICK_ACTIVITY -> {
                        var title = ""
                        when (ActivityType.getActivityType(item.first!!.activityType)) {
                            ActivityType.ONE_TO_MANY -> title = "Group Bonding Question"
                            ActivityType.ONE_TO_ONE -> title = "One-on-One Question"
                            ActivityType.D_TO_ONE -> title = "Know Someone"
                            ActivityType.TASK -> title = "Wellbeing Activity"
                            ActivityType.MESSAGE -> title = "Heartfelt Message"
                            ActivityType.SURVEY -> title = "Survey"
                            ActivityType.EXTERNAL -> title = "Gems of Internet"
                            ActivityType.USER_MOOD_CARD -> title = "Gems of Internet"
                        }
                        showAlert(title, item.first!!.activityTypeHint)
                    }

                    PlaySoloGamesAdapter.CLICK_BUTTON -> if (getString(R.string.click_action_all_answers) == item.third) {
                        if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.ONE_TO_MANY) {
                            navigateToDetails(item.first, playGroup)
                        }
                    } else if (getString(R.string.click_action_see_stats) == item.third) {
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavGuestStatisticsSurvey(
                                item.first!!.activityId!!
                            )
                        )
                    } else if (getString(R.string.click_action_ask_friends) == item.third || getString(
                            R.string.click_action_ask_question
                        ) == item.third
                    ) {
                        (fragmentContext as DashboardActivity).showGuestPopup(
                            "Ask Friends",
                            "You can ask questions to friends after you login."
                        )
                    } else if (getString(R.string.click_action_send_to_friends) == item.third) {
                        if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.CARD) {
                            (fragmentContext as DashboardActivity).showGuestPopup(
                                "Fun Cards",
                                "You can share these fun cards with friends after you login."
                            )
                        }
                    } else if (getString(R.string.click_action_all_cards) == item.third) {
                        if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.CARD) {
                            (fragmentContext as DashboardActivity).showGuestPopup(
                                "Fun Cards",
                                "You can check out more fun cards after you login."
                            )
                        }
                    }

                    PlaySoloGamesAdapter.CLICK_MEDIA -> if (!TextUtils.isEmpty(item.first!!.questionVideoUrl)) {
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavMediaView(
                                2,
                                item.first!!.questionVideoUrl!!
                            )
                        )
                    } else if (!TextUtils.isEmpty(item.first!!.activityImageLargeUrl)) {
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavMediaView(
                                1,
                                item.first!!.activityImageLargeUrl!!
                            )
                        )
                    }

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.swipeRefreshLayout.isRefreshing = isDataLoading
    }

    override fun onDestroy() {
        super.onDestroy()
        //        state = null;
        loginDayActivityInfoList!!.clear()
        if (activityStatusList != null) {
            activityStatusList!!.clear()
        }
        if (gameResIdList != null) {
            gameResIdList!!.clear()
        }
        questionGamesViewModel!!.actionConsumed()
        //Glide.get(requireActivity()).clearMemory()
        parentJob.cancel()
    }


    private fun navigateToDetails(item: LoginDayActivityInfoList?, playGroup: PlayGroup?) {

        if (navController.currentDestination!!.id == R.id.nav_home && item != null) {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavGuestOneToManyGames(
                    item,
                    playGroup!!,
                    null,
                    null,
                    true
                )
            )
        }
//        coroutineScope.launch(Dispatchers.Main) {
//            handlePermissionResultForOneToMany(
//                PermissionManager.requestPermissions(
//                    this@DashboardFragment,
//                    4,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.CAMERA,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.RECORD_AUDIO
//                ), item!!, playGroup!!
//            )
//        }

    }

}