package com.onourem.android.activity.ui.dashboard.landing

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.gson.Gson
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentDashboardPlayGamesBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.YoutubeActivity
import com.onourem.android.activity.ui.audio.models.AudioResponse
import com.onourem.android.activity.ui.audio.models.GetAudioInfoResponse
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.audio.playback.SongProvider
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.circle.FriendCircleGameViewModel
import com.onourem.android.activity.ui.dashboard.DashboardFragmentDirections
import com.onourem.android.activity.ui.dashboard.watchlist.OnWatchListItemClickListener
import com.onourem.android.activity.ui.data.DashboardData
import com.onourem.android.activity.ui.games.adapters.FriendsPlayingWatchListAdapter
import com.onourem.android.activity.ui.games.adapters.activity.ActivityGamesAdapter
import com.onourem.android.activity.ui.games.dialogs.SendNowOrSendLaterDialogFragment
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.games.fragments.AnswerQuestionDialogFragment
import com.onourem.android.activity.ui.games.fragments.SelectPlayGroupDialogFragment
import com.onourem.android.activity.ui.games.fragments.TaskAndMessageComposeDialogFragment
import com.onourem.android.activity.ui.games.fragments.WriteCommentDialogFragment
import com.onourem.android.activity.ui.games.viewmodel.GamesReceiverViewModel
import com.onourem.android.activity.ui.games.viewmodel.MediaOperationViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.profile.ProfileViewModel
import com.onourem.android.activity.ui.survey.viewmodel.SurveyViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.listners.*
import com.onourem.android.activity.viewmodel.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.*
import javax.inject.Inject


class FriendsPlayingFragment : AbstractBaseViewModelBindingFragment<DashboardViewModel, FragmentDashboardPlayGamesBinding>(),
    OnItemClickListener<Triple<LoginDayActivityInfoList?, Int?, String?>?>,
    OnWatchListItemClickListener<Pair<Int, UserWatchList>> {

    private var forceRefreshData: Boolean = false
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false

    private var recyclerViewState: Parcelable? = null

    private lateinit var loginUserId: String
    private var rvScrollListener: RVScrollListener? = null
    private lateinit var inviteWatchListItem: UserWatchList
    private lateinit var drawableItem: UserWatchList
    private lateinit var editWatchListItem: UserWatchList
    private var lastVisiblePosForWatchlist: Int = 0
    private var lastVisiblePosForVocal: Int = 0
    private var totalActivities: Int = 0
    private var json: String? = ""
    private lateinit var gson: Gson
    private var watchListItemClickListener: OnWatchListItemClickListener<android.util.Pair<Int, UserWatchList>>? = null
    private var songsFromServer = mutableListOf<Song>()

    //    private var userWatchLists = mutableListOf<UserWatchList>()
    private var dashboardData: DashboardData? = null
    private var counterFriendsPlaying: Int = 0
    private var recyclerViewPosition: Int = 0
    private var linkType: String? = ""
    private var linkUserId: String? = ""
    private var isMoodDialogShowing: Boolean = false
    private lateinit var surveyViewModel: SurveyViewModel
    private lateinit var mediaViewModel: MediaOperationViewModel

    //    private var activityStatusList: MutableList<String>? = ArrayList()
    private var filterActivities: String = ""
    private var remainingExternalActIds: String? = ""
    private var remainingSurveyIds: String? = ""
    private var remainingPostIds: String? = ""
    private var remainingCardIds: String? = ""
    private var gameResIdList: MutableList<String>? = ArrayList()
    private val selectedSearch = false

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var loginDayActivityInfoList: MutableList<LoginDayActivityInfoList>? = ArrayList()

    private var isLinkVerified = ""
    private var adviceUpdateDialog: Dialog? = null
    private var questionGamesViewModel: QuestionGamesViewModel? = null
    private var friendCircleGameViewModel: FriendCircleGameViewModel? = null
    private lateinit var playGroup: PlayGroup
    private var soloGamesAdapter: ActivityGamesAdapter? = null
    private var gamesReceiverViewModel: GamesReceiverViewModel? = null

    private var filterItems: ArrayList<FilterItem>? = null
    private var selectedFilterIndex = 0
    private var userActionViewModel: UserActionViewModel? = null
    private var profileViewModel: ProfileViewModel? = null
    private var isDataLoading = false
    private var isFrom = ""
    private var displayNumberOfActivity: Long? = 10L

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(parentJob + Dispatchers.Default)

    private var layoutManager: LinearLayoutManager? = null

    companion object {

        const val youTubeUrlRegEx = "^(https?)?(://)?(www.)?(m.)?((adap.com)|(youtu.be))/"
        val videoIdRegex = arrayListOf<String>(
            "\\?vi?=([^&]*)", "watch\\?.*v=([^&]*)", "(?:embed|vi?)/([^/?]*)", "^([A-Za-z0-9\\-]*)"
        )

        fun create(
            linkUserId: String,
            linkType: String,
        ): FriendsPlayingFragment {
            val fragment = FriendsPlayingFragment()
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

        questionGamesViewModel = ViewModelProvider(
            requireActivity(), viewModelFactory
        )[QuestionGamesViewModel::class.java]

        gamesReceiverViewModel = ViewModelProvider(
            requireActivity(), viewModelFactory
        )[GamesReceiverViewModel::class.java]

        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]

        surveyViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[SurveyViewModel::class.java]

        friendCircleGameViewModel = ViewModelProvider(
            requireActivity(), viewModelFactory
        )[FriendCircleGameViewModel::class.java]

        mediaViewModel = ViewModelProvider(
            requireActivity(), viewModelFactory
        )[MediaOperationViewModel::class.java]

        profileViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[ProfileViewModel::class.java]

        questionGamesViewModel!!.reloadUI().observe(this) { reload: Boolean ->
            if (reload) {
                refreshUI()
            }
        }

        viewModel.getTrendingDataRefresh.observe(this) {
            if (it) {
                refreshUI()
            }
        }
        questionGamesViewModel!!.gameActivityUpdateStatus.observe(this) { item: Pair<String?, LoginDayActivityInfoList>? ->
            if (item != null) {
                questionGamesViewModel!!.setGameActivityUpdateStatus(null)
                if (!(playGroup.playGroupId.equals(
                        "AAA", ignoreCase = true
                    ) || playGroup.playGroupId.equals(
                        "ZZZ", ignoreCase = true
                    )) && playGroup.playGroupId.equals(item.first, ignoreCase = true)
                ) {
                    if (loginDayActivityInfoList!!.isEmpty()) {
                        if (!isDataLoading) loadData()
                    } else if (!loginDayActivityInfoList!!.contains(item.second)) {
                        loginDayActivityInfoList?.clear()
//                        activityStatusList!!.clear()
                        soloGamesAdapter?.clearData()
                        refreshUI()
                    } else {
                        if (soloGamesAdapter != null) {
                            soloGamesAdapter?.notifyDataSetChanged()
                        }
                    }
                } else {
                    if (soloGamesAdapter != null) {
                        soloGamesAdapter?.notifyDataSetChanged()
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
//                for (question in loginDayActivityInfoList?) {
//                    if (item.surveyIdFromQuestionListing == question.activityId) {
//                        item.userAnserForSurvey = "Y"
//                        question.surveySeeStats = "answered"
//                        question.surveyCO = item
//                        soloGamesAdapter?.updateItem(question)
//                        break
//                    }
//                }
//            }
//        }

//        viewModel.removeSurveyItem.observe(this) {
//            if (it != null) {
//                if (soloGamesAdapter != null) {
//                    loginDayActivityInfoList!!.forEachIndexed { index, loginDayActivityInfoList ->
//                        if (it == loginDayActivityInfoList.activityId) {
//                            viewModel.removeSurveyItemConsumed()
//                            soloGamesAdapter?.removeItem(index)
//                            soloGamesAdapter?.notifyItemChanged(index)
//                        }
//                    }
//                }
//            }
//        }

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.getLoginDayActivityInfoList.observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                forceRefreshData = true
            }
        }

        loginUserId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)

        rvScrollListener = (fragmentContext as DashboardActivity).getRVScrollListenerReference()

        editWatchListItem = UserWatchList()
        editWatchListItem.userId = "editWatchlist"
        editWatchListItem.status = "editWatchlist"
        editWatchListItem.subText = "Edit\nWatchList"
        editWatchListItem.viewType = FriendsPlayingWatchListAdapter.DRAWABLE_WITH_TEXT_VIEW
        editWatchListItem.drawable = ContextCompat.getDrawable(fragmentContext, R.drawable.edit_watchlist_icon)

        drawableItem = UserWatchList()
        drawableItem.userId = "drawableWatchlist"
        editWatchListItem.status = "drawableWatchlist"
        drawableItem.viewType = FriendsPlayingWatchListAdapter.DRAWABLE_VIEW
        drawableItem.subText =
            "Take care of our closest people. Sometimes we don't realise when they need us. \nAdd closest friends in your Onourem Watch List. You will see their day to day mood here and they will see yours. Reach out to them when they need you."
        drawableItem.drawable = ContextCompat.getDrawable(fragmentContext, R.drawable.honeycomb)

        inviteWatchListItem = UserWatchList()
        editWatchListItem.status = "inviteWatchlist"
        inviteWatchListItem.userId = "inviteWatchlist"
        inviteWatchListItem.subText = "InviteWatchlist"
        inviteWatchListItem.viewType = FriendsPlayingWatchListAdapter.DRAWABLE_INVITE_VIEW
        inviteWatchListItem.drawable = ContextCompat.getDrawable(
            fragmentContext, R.drawable.invite_friend_icon
        )


        linkType = requireArguments().getString("linkType")
        linkUserId = requireArguments().getString("linkUserId")
        viewModel.isMoodsDialogShowing.observe(viewLifecycleOwner) { show: Boolean ->
            isMoodDialogShowing = show
        }

        preferenceHelper!!.putValue(Constants.KEY_SELECTED_FILTER_INDEX, selectedFilterIndex)

//        linkUserId = preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID)
//        linkType = preferenceHelper!!.getString(Constants.KEY_LINK_TYPE)
//        isLinkVerified = preferenceHelper!!.getString(Constants.KEY_IS_LINK_VERIFIED)


        layoutManager = LinearLayoutManager(requireActivity())

        playGroup = PlayGroup()
        playGroup.playGroupId = "FFF"
        playGroup.playGroupName = "Friends Playing"
        playGroup.isDummyGroup = true

        setWatchlistListener()

        dashboardData = (fragmentContext as DashboardActivity).getDashboardData()
        if (dashboardData != null) {

//            activityStatusList = (dashboardData!!.activityStatusList as MutableList<String>?)
            gameResIdList = (dashboardData!!.gameResIdList as MutableList<String>?)
            loginDayActivityInfoList = (dashboardData!!.loginDayActivityInfoList as MutableList<LoginDayActivityInfoList>?)
            playGroup = dashboardData!!.playGroup!!
//            filterItems = dashboardData.filterItems
            selectedFilterIndex = dashboardData!!.selectedFilterIndex!!
            displayNumberOfActivity = dashboardData!!.displayNumberOfActivity ?: 10L
            counterFriendsPlaying = dashboardData!!.counterFriendsPlaying ?: 0
            recyclerViewPosition = dashboardData!!.recyclerViewPosition ?: 0
            totalActivities = dashboardData!!.totalActivities ?: 0
            filterActivities = dashboardData!!.filterActivities ?: ""

//            if (loginDayActivityInfoList?.size <= 2) {
//                refreshUI()
//            }

            setAdapter()

            if (soloGamesAdapter != null) {
                getAudioShorts(Constants.TRENDING_VOCALS)
            }

        } else {
            loadData()
        }

        //binding.fabOClub.visibility = View.VISIBLE

        showFiltersAndBadges(selectedSearch)

        questionGamesViewModel!!.refreshShowBadges.observe(viewLifecycleOwner) {
            showFiltersAndBadges(
                selectedSearch
            )
        }
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

        gamesReceiverViewModel!!.gameActivityUpdateResponseLiveData.observe(viewLifecycleOwner) { activityResponse: GameActivityUpdateResponse? ->
            if (activityResponse != null) {
                gamesReceiverViewModel!!.setGameActivityUpdateResponseLiveData(null)
                if (soloGamesAdapter != null) {
                    soloGamesAdapter?.notifyDataSetChanged()
                }
            }
        }

        binding.rvPlayGames.addOnScrollListener(object : FriendsPlayingPaginationListener(layoutManager!!) {
            override fun loadMoreItems() {
                this@FriendsPlayingFragment.isLoading = true
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@FriendsPlayingFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@FriendsPlayingFragment.isLoading
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            preferenceHelper!!.putValue(Constants.WATCHLIST, "")
            preferenceHelper!!.putValue(Constants.VOCALS, "")
            preferenceHelper!!.putValue(Constants.WATCHLIST_POSITION_INDEX, 0)
            (fragmentContext as DashboardActivity).setWatchListData(null)
            (fragmentContext as DashboardActivity).setFriendsPlayingRecyclerViewPosition(
                -1, -1
            )
            isDataLoading = false
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
                    val currentVisibleItemPosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (lastPosition != currentVisibleItemPosition && currentVisibleItemPosition != RecyclerView.NO_POSITION) {
                        lastPosition = currentVisibleItemPosition

//                        AppUtilities.showLog(
//                            "###",
//                            "lastPosition : $lastPosition"
//                        )
                    }
                }

                rvScrollListener?.onScroll(dx, dy)

//                if (dy < 0) {
//                    binding.fab.extend()
//                    binding.fabOClub.extend()
//                } else if (dy > 0) {
//                    binding.fab.shrink()
//                    binding.fabOClub.shrink()
//                }

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

//        binding.fab.setOnClickListener(ViewClickListener {
//
////            navController.navigate(
////                DashboardFragmentDirections.actionNavHomeToNavCreateOwnQuestion(playGroup)
////            )
//
//            navController.navigate(MobileNavigationDirections.actionGlobalNavShareSomething())
//
//        })

        binding.fabOClub.setOnClickListener(ViewClickListener {

            navController.navigate(
                DashboardFragmentDirections.actionNavHomeToNavQuestionGames(
                    null, "solo"
                )
            )

        })
        userActionViewModel?.actionMutableLiveData?.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel?.actionConsumed()
                when (action.actionType) {
                    ActionType.EDIT_QUESTION -> {
                        if (action.data is LoginDayActivityInfoList) {
                            editQuestion(action)
                        }
                    }

                    ActionType.DELETE_QUESTION -> {
                        if (action.data is LoginDayActivityInfoList) {
                            deleteQuestion(action)
                        }
                    }

                    ActionType.IGNORE_QUESTION -> {
                        if (action.data is LoginDayActivityInfoList) {
                            ignoreQuestion(action)
                        }
                    }

                    ActionType.DELETE_QUESTION_CONFIRMATION -> {
                        if (action.data is LoginDayActivityInfoList) {
                            deleteQuestionConfirmed(action)
                        }
                    }

                    ActionType.IGNORE_QUESTION_CONFIRMATION -> {
                        if (action.data is LoginDayActivityInfoList) {
                            ignoreQuestionConfirmed(action)
                        }
                    }

                    ActionType.EDIT_GAME_VISIBILITY -> {
                        if (action.data is LoginDayActivityInfoList) {
                            editGamePrivacy(action)
                        }
                    }

                    ActionType.DELETE_GAME -> {
                        if (action.data is LoginDayActivityInfoList) {
                            deleteThisPost(action)
                        }
                    }

                    ActionType.REPORT_ABUSE -> {
                        if (action.data is LoginDayActivityInfoList) {
                            reportAbuse(action)
                        }

                    }

                    else -> {

                    }
                }
            }
        }

        questionGamesViewModel!!.refreshEditedItem.observe(viewLifecycleOwner) { item: LoginDayActivityInfoList? ->
            if (item != null && soloGamesAdapter != null) {
                soloGamesAdapter?.notifyDataUpdated(item)
            }
        }
        questionGamesViewModel!!.updateItem().observe(viewLifecycleOwner) { item: LoginDayActivityInfoList? ->
            if (item != null && soloGamesAdapter != null) {
                soloGamesAdapter?.removeItem(item)
            }
        }

        viewModel.getLoginDayActivityInfo.observe(viewLifecycleOwner) { item: LoginDayActivityInfoList? ->
            if (item != null && soloGamesAdapter != null) {
                soloGamesAdapter?.addItemAtPosition(0, item)
                binding.rvPlayGames.scrollToPosition(0)
            }
        }

        viewModel.updateWatchlistResponse.observe(viewLifecycleOwner) { update ->
            if (update != null) {
                updateFromEditWatchlist(update)
            }
        }

    }

    private fun reportAbuse(action: Action<Any?>?) {
        val item = action?.data as LoginDayActivityInfoList
        TwoActionAlertDialog.showAlert(
            requireActivity(),
            getString(R.string.label_confirm),
            getString(R.string.message_report_post_abuse),
            item,
            "Cancel",
            "Yes"
        ) { item1: Pair<Int?, LoginDayActivityInfoList?>? ->
            if (item1?.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                profileViewModel!!.reportAbuse(item.feedsInfo!!.postId)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<PostsActionResponse> ->
                        actionButtonResponse(
                            apiResponse, item, "reportAbuse"
                        )
                    }
            }
        }
    }

    private fun editGamePrivacy(action: Action<Any?>?) {
        val item = action?.data as LoginDayActivityInfoList
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavEditPrivacyDialog(
                item.feedsInfo!!.activityId!!, item.feedsInfo!!.postId!!
            )
        )
    }

    private fun deleteThisPost(action: Action<Any?>?) {
        val item = action?.data as LoginDayActivityInfoList
        TwoActionAlertDialog.showAlert(
            requireActivity(), getString(R.string.label_confirm), getString(R.string.message_delete_post), item, "Cancel", "Yes"
        ) { item1: Pair<Int?, LoginDayActivityInfoList?>? ->
            if (item1?.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                profileViewModel!!.deletePosts(item.feedsInfo!!.postId)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<PostsActionResponse> ->
                        actionButtonResponse(
                            apiResponse, item, "deletePosts"
                        )
                    }
            }
        }
    }

    private fun actionButtonResponse(
        apiResponse: ApiResponse<PostsActionResponse>, item: LoginDayActivityInfoList, serviceName: String
    ) {
        if (apiResponse.loading) {
            showProgress()
        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            hideProgress()
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                if (soloGamesAdapter != null) soloGamesAdapter?.removeItem(item)
            } else {
                showAlert(apiResponse.body.errorMessage)
            }
        } else {
            hideProgress()
            showAlert(apiResponse.errorMessage)
            if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1)) || apiResponse.errorMessage.contains(
                    getString(R.string.unable_to_connect_host_message3)
                ))
            ) {
                if (BuildConfig.DEBUG) {
                    AppUtilities.showLog("Network Error", serviceName)
                }
                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                    serviceName, apiResponse.code.toString()
                )
            }
        }
    }


    private fun loadData() {
        val getAppUpgradeInfoResponse = Gson().fromJson(
            preferenceHelper!!.getString(Constants.KEY_GET_APP_UPDATE_RESPONSE), GetAppUpgradeInfoResponse::class.java
        )
        if (getAppUpgradeInfoResponse != null) {
            checkForceUpgradeData(getAppUpgradeInfoResponse)
        } else {
            getAppForceUpgradeInfo()
        }

        binding.swipeRefreshLayout.isRefreshing = false
        this@FriendsPlayingFragment.isLoading = false
        this@FriendsPlayingFragment.isLastPage = false

        if (loginDayActivityInfoList == null || loginDayActivityInfoList!!.isEmpty() && !isDataLoading || linkUserId != "") {
            isDataLoading = true
//                        questionGamesViewModel!!.getUserFriendAnsweredActivityInfo(linkUserId)
//                            .observe(
//                                viewLifecycleOwner
//                            ) { responseApiResponse: ApiResponse<UserActivityInfoResponse> ->
//                                handleResponse(
//                                    responseApiResponse,
//                                    true,
//                                    showProgress,
//                                    "getUserFriendAnsweredActivityInfo"
//                                )
//                            }

            val id =
                if (linkType == "Card" || linkType == "1toM" || linkType == "External" || linkType == "Bond003Game" || linkType == "Post") {
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
            viewModel.setShowCreateFab(false)
            questionGamesViewModel!!.getTopPriorityActivityInfo(id)
                .observe(viewLifecycleOwner) { responseApiResponse: ApiResponse<NewActivityInfoResponse> ->
                    handleNewResponse(
                        responseApiResponse, true, "getTopPriorityActivityInfo"
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

    private fun getAppForceUpgradeInfo() {
        viewModel.appUpgradeInfo().observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetAppUpgradeInfoResponse> ->
            if (apiResponse.loading) {
                //showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                //hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    preferenceHelper!!.putValue(
                        Constants.KEY_GET_APP_UPDATE_RESPONSE, Gson().toJson(apiResponse.body)
                    )
                    checkForceUpgradeData(apiResponse.body)
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
//                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(
                        getString(
                            R.string.unable_to_connect_host_message
                        )
                    ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1)) || apiResponse.errorMessage.contains(
                        getString(R.string.unable_to_connect_host_message3)
                    ))
                ) {
                    if (com.onourem.android.activity.BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getAppUpgradeInfo")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getAppUpgradeInfo", apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun editQuestion(action: Action<Any?>?) {
        val first = action?.data as LoginDayActivityInfoList
        navController.navigate(DashboardFragmentDirections.actionNavHomeToNavEditQuestion(first))
    }

    private fun deleteQuestion(action: Action<Any?>?) {
        val first = action?.data as LoginDayActivityInfoList
        val titleText = "Do you want to delete this question?"
        val actions = ArrayList<Action<*>>()
        actions.add(
            Action(
                getString(R.string.action_label_delete_confirm),
                R.color.color_black,
                ActionType.DELETE_QUESTION_CONFIRMATION,
                first
            )
        )
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                titleText, bundle, ""
            )
        )
    }

    private fun ignoreQuestion(action: Action<Any?>?) {
        val first = action?.data as LoginDayActivityInfoList

        var isSurvey = ""
        isSurvey = if (first.activityType == "Survey") {
            "survey"
        } else {
            "question"
        }
        val titleText = "Do you want to ignore this $isSurvey?"
        val actions = ArrayList<Action<*>>()
        actions.add(
            Action(
                getString(R.string.action_label_ignore_confirm),
                R.color.color_black,
                ActionType.IGNORE_QUESTION_CONFIRMATION,
                first
            )
        )
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                titleText, bundle, ""
            )
        )
    }

    private fun deleteQuestionConfirmed(action: Action<Any?>?) {
        val first = action?.data as LoginDayActivityInfoList
        questionGamesViewModel!!.deleteQuestion(first.activityId!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<EditQuestionResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        if (apiResponse.body.message != null && !apiResponse.body.message.equals(
                                "", ignoreCase = true
                            )
                        ) {
                            showAlert(apiResponse.body.message)
                        } else {
                            if (soloGamesAdapter != null) soloGamesAdapter?.removeItem(first)
                        }
                    } else {
                        hideProgress()
                        showAlert(apiResponse.body.errorMessage)
                        if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(
                                getString(R.string.unable_to_connect_host_message)
                            ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1)) || apiResponse.errorMessage.contains(
                                getString(R.string.unable_to_connect_host_message3)
                            ))
                        ) {
                            if (com.onourem.android.activity.BuildConfig.DEBUG) {
                                AppUtilities.showLog("Network Error", "deleteQuestion")
                            }
                            (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                "deleteQuestion", apiResponse.code.toString()
                            )
                        }
                    }
                }
            }
    }

    private fun ignoreQuestionConfirmed(action: Action<Any?>?) {
        val first = action?.data as LoginDayActivityInfoList
        if (first.activityType.equals("1toM", ignoreCase = true)) {
            if (selectedFilterIndex == 0 || selectedFilterIndex == 1) {
                ignoreGame(
                    null, null, null, first.activityId, first
                )
            }
        } else if (first.activityType.equals("Survey", ignoreCase = true)) {
            if (selectedFilterIndex == 0 || selectedFilterIndex == 1) {
                ignoreSurvey(
                    first.activityId, first
                )
            }
        } else if (first.activityType.equals("FriendCircleGame", ignoreCase = true)) {
            if (selectedFilterIndex == 0 || selectedFilterIndex == 1) {
                addQualityQuestionVisibility(
                    first
                )
            }
        }
    }


    private fun activityPlayGroupId(): String? {
        var activityPlayGroupId: String? = null
        if (playGroup != null) {
            val playGroupId = playGroup.playGroupId
            activityPlayGroupId = if (playGroupId.equals("FFF", ignoreCase = true)) {
                "0"
            } else if (playGroupId.equals("AAA", ignoreCase = true)) {
                "1"
            } else if (playGroupId.equals("YYY", ignoreCase = true)) {
                "2"
            } else if (playGroupId.equals("ZZZ", ignoreCase = true)) {
                "3"
            } else {
                playGroupId
            }
        }
        return activityPlayGroupId
    }

    private fun ignoreGame(
        gameId: String?, activityGameResId: String?, playGroupId: String?, activityId: String?, first: LoginDayActivityInfoList?
    ) {
        questionGamesViewModel!!.ignoreOneToManyGameActivity(
            gameId, activityGameResId, playGroupId, activityId
        ).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GameActivityUpdateResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (first != null) {
                        if (!TextUtils.isEmpty(apiResponse.body.participationStatus)) first.userParticipationStatus =
                            apiResponse.body.participationStatus
                        if (!TextUtils.isEmpty(apiResponse.body.activityTagStatus)) first.userParticipationStatus =
                            apiResponse.body.activityTagStatus
                    }
                    if (soloGamesAdapter != null) {
                        soloGamesAdapter?.removeItem(first)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(
                        getString(
                            R.string.unable_to_connect_host_message
                        )
                    ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1)) || apiResponse.errorMessage.contains(
                        getString(R.string.unable_to_connect_host_message3)
                    ))
                ) {
                    if (com.onourem.android.activity.BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "ignoreGame")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "ignoreGame", apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun ignoreSurvey(
        activityId: String?, first: LoginDayActivityInfoList?
    ) {
        questionGamesViewModel!!.ignoreSurvey(
            activityId!!
        ).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (soloGamesAdapter != null) {
                        soloGamesAdapter?.removeItem(first)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(
                        getString(
                            R.string.unable_to_connect_host_message
                        )
                    ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1)) || apiResponse.errorMessage.contains(
                        getString(R.string.unable_to_connect_host_message3)
                    ))
                ) {
                    if (com.onourem.android.activity.BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "ignoreSurvey")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "ignoreGame", apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun addQualityQuestionVisibility(
        first: LoginDayActivityInfoList?
    ) {
        friendCircleGameViewModel!!.addQualityQuestionVisibility().observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (soloGamesAdapter != null) {
                        soloGamesAdapter?.removeItem(first)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun refreshUI() {

        (fragmentContext as DashboardActivity).setDashboardData(null)
        this@FriendsPlayingFragment.isLastPage = false
        this@FriendsPlayingFragment.isLoading = false
        if (soloGamesAdapter != null) soloGamesAdapter?.removeFooter()
        if (soloGamesAdapter != null) {
            soloGamesAdapter?.clearData()
            soloGamesAdapter = null
        }
//        activityStatusList!!.clear()
        gameResIdList!!.clear()
        loginDayActivityInfoList?.clear()
        if (!isDataLoading) loadData()


//        if ((fragmentContext as DashboardActivity).loginDayActivityInfoList != null && (fragmentContext as DashboardActivity).loginDayActivityInfoList?.size > 0 && linkUserId == "") {
//            //setDashboardData()
//        } else {
//
//        }
    }


    private fun checkForceUpgradeData(getAppUpgradeInfoResponse: GetAppUpgradeInfoResponse?) {
        var forceAndAdviceUpgrade: ForceAndAdviceUpgrade? = null
        if (getAppUpgradeInfoResponse != null) forceAndAdviceUpgrade = getAppUpgradeInfoResponse.forceAndAdviceUpgrade
        if (getAppUpgradeInfoResponse != null && forceAndAdviceUpgrade != null && AppUtilities.getAppVersion() >= java.lang.Double.valueOf(
                forceAndAdviceUpgrade.androidForceUpgradeVersion!!
            ).toInt()
        ) {

            //AppUtilities.showLog("PlayGamesFragment", "loadData " + counterLoadData++);

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleNewResponse(
        apiResponse: ApiResponse<NewActivityInfoResponse>, isSolo: Boolean, serviceName: String
    ) {
        if (apiResponse.loading) {
            showProgress()
            isDataLoading = true
            if (binding.swipeRefreshLayout.isRefreshing) {
//                binding.tvMessage.text = "Loading Data, Please Wait...!"
//                binding.tvMessage.visibility = View.VISIBLE
            } else {
                binding.tvMessage.text = ""
                binding.tvMessage.visibility = View.GONE
            }

        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            isDataLoading = false
//            hideProgress()
            if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing = false
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                linkType = ""
                linkUserId = ""

                (fragmentContext as DashboardActivity).setHasUserPlayedGame(apiResponse.body.isUserPlayedGame)
                (fragmentContext as DashboardActivity).setHasBond003AvailableForUsers(apiResponse.body.isBond003AvailableForUsers)
                (fragmentContext as DashboardActivity).hideShowBondGameMenu()

                displayNumberOfActivity = apiResponse.body.displayNumberOfActivity
                totalActivities = apiResponse.body.totalActivities
                filterActivities = apiResponse.body.filterActivities!!
                //                AppUtilities.showLog("PlayGamesFragment", "LoginDayActivityInfoList " + apiResponse.body.getLoginDayActivityInfoList().size() + "");
//                AppUtilities.showLog("PlayGamesFragment", "ActivityStatusList " + (apiResponse.body.getActivityStatusList() != null ? apiResponse.body.getActivityStatusList().size() : "null") + "");
//                AppUtilities.showLog("PlayGamesFragment", "GameResIdList " + (apiResponse.body.getGameResIdList() != null ? apiResponse.body.getGameResIdList().size() : "null") + "");
                //showFiltersAndBadges(selectedSearch)
                if (apiResponse.body.loginDayActivityMoodInfoList != null && apiResponse.body.loginDayActivityMoodInfoList!!.isNotEmpty()) {
                    loginDayActivityInfoList?.addAll(apiResponse.body.loginDayActivityMoodInfoList!!)
                }

                if (apiResponse.body.loginDayActivityInfoList != null) {
                    binding.tvMessage.visibility = View.GONE
                    if (!isSolo) {
                        questionGamesViewModel!!.updateActivityMemberNumber(playGroup.playGroupId).observe(
                            viewLifecycleOwner
                        ) { userActivityInfoResponseApiResponse: ApiResponse<UserActivityInfoResponse>? -> }
                    }
//                    if (apiResponse.body.activityStatusList != null && !apiResponse.body.activityStatusList.isEmpty()) {
//                        activityStatusList!!.clear()
//                        activityStatusList!!.addAll(apiResponse.body.activityStatusList)
//                    }

//                    if (apiResponse.body.gameResIdList != null && !apiResponse.body.gameResIdList.isEmpty()) {
//                        gameResIdList!!.clear()
//                        gameResIdList!!.addAll(apiResponse.body.gameResIdList)
//                    }
//                    loginDayActivityInfoList?.clear()
//                    if (soloGamesAdapter != null) soloGamesAdapter?.clearData()

                    loginDayActivityInfoList?.addAll(apiResponse.body.loginDayActivityInfoList!!)
//                    if (soloGamesAdapter != null) soloGamesAdapter?.notifyDataSetChanged()

                    val dashboardData = DashboardData(
                        emptyList(),
                        gameResIdList!!,
                        loginDayActivityInfoList,
                        playGroup,
                        filterItems,
                        selectedFilterIndex,
                        displayNumberOfActivity,
                        counterFriendsPlaying,
                        recyclerViewPosition,
                        totalActivities,
                        filterActivities,
                        layoutManager!!
                    )
                    (fragmentContext as DashboardActivity).setDashboardData(dashboardData)

                    setAdapter()

                    getTopPriorityActivityList()

                    Log.e("ApiResponse", Gson().toJson(apiResponse.body))
//                    preferenceHelper!!.putValue(Constants.JSON_1, Gson().toJson(apiResponse.body))

                } else {
                    val footerMessage: String = if (playGroup.playGroupId.equals("FFF", ignoreCase = true)) {
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
            if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing = false
            hideProgress()
            showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
            if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message)) || apiResponse.errorMessage.contains(
                    getString(R.string.unable_to_connect_host_message1)
                ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
            ) {
                if (com.onourem.android.activity.BuildConfig.DEBUG) {
                    AppUtilities.showLog("Network Error", serviceName)
                }
                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                    serviceName, apiResponse.code.toString()
                )
            }
        }
    }

    private fun getTopPriorityActivityList() {
//        if (activityStatusList == null || activityStatusList!!.isEmpty()) {
//            this@FriendsPlayingFragment.isLastPage = true
//            this@FriendsPlayingFragment.isLoading = false
//            return
//        }
//        val start = soloGamesAdapter?.itemCount
//        val end = activityStatusList!!.size
//        if (start >= end) {
//            this@FriendsPlayingFragment.isLastPage = true
//            this@FriendsPlayingFragment.isLoading = false
//            setFooterMessage()
//            return
//        }
//        val ids: MutableList<Int> = ArrayList()
//
//        val statusLists = getDisplayElements(
//            activityStatusList!!, start
//        )
//        AppUtilities.showLog("**newstatusLists:", statusLists.size.toString())
//        var i = 0
//        while (i < activityStatusList!!.size) {
//            val item = activityStatusList!![i]
//            ids.add(item.toInt())
//            i++
//        }

//        val ids = Utilities.getTokenSeparatedString(activityStatusList!!, ",")
//        val ids = filterActivities
        questionGamesViewModel!!.getTopPriorityActivityList(filterActivities)
            .observe(viewLifecycleOwner) { responseApiResponse: ApiResponse<NewActivityInfoResponse> ->
                handleNewSecondResponse(
                    responseApiResponse, true, false, "getTopPriorityActivityList"
                )
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleNewSecondResponse(
        apiResponse: ApiResponse<NewActivityInfoResponse>, isSolo: Boolean, showProgress: Boolean, serviceName: String
    ) {
        if (apiResponse.loading) {
            isDataLoading = true
            //if (showProgress) showProgress()
        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            isDataLoading = false
            if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing = false
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                //displayNumberOfActivity = apiResponse.body.displayNumberOfActivity
                //                AppUtilities.showLog("PlayGamesFragment", "LoginDayActivityInfoList " + apiResponse.body.getLoginDayActivityInfoList().size() + "");
//                AppUtilities.showLog("PlayGamesFragment", "ActivityStatusList " + (apiResponse.body.getActivityStatusList() != null ? apiResponse.body.getActivityStatusList().size() : "null") + "");
//                AppUtilities.showLog("PlayGamesFragment", "GameResIdList " + (apiResponse.body.getGameResIdList() != null ? apiResponse.body.getGameResIdList().size() : "null") + "");
                //showFiltersAndBadges(selectedSearch)
                totalActivities = apiResponse.body.totalActivities
                if (apiResponse.body.loginDayActivityInfoList != null) {
                    if (!isSolo) {
                        questionGamesViewModel!!.updateActivityMemberNumber(playGroup.playGroupId).observe(
                            viewLifecycleOwner
                        ) { userActivityInfoResponseApiResponse: ApiResponse<UserActivityInfoResponse>? -> }
                    }
//                    if (apiResponse.body.activityStatusList != null && apiResponse.body.activityStatusList.isNotEmpty()) {
//                        //activityStatusList!!.clear()
//                        activityStatusList!!.addAll(apiResponse.body.activityStatusList)
//                    }

//                    if (apiResponse.body.remainingSurveyIds != null && apiResponse.body.remainingSurveyIds.isNotEmpty()) {
//                        remainingSurveyIds = (apiResponse.body.remainingSurveyIds)
//                    }
//
//                    if (apiResponse.body.remainingPostIds != null && apiResponse.body.remainingPostIds.isNotEmpty()) {
//                        remainingPostIds = (apiResponse.body.remainingPostIds)
//                    }
//
//                    if (apiResponse.body.remainingExternalActIds != null && apiResponse.body.remainingExternalActIds.isNotEmpty()) {
//                        remainingExternalActIds = (apiResponse.body.remainingExternalActIds)
//                    }
//
//                    if (apiResponse.body.remainingCardIds != null && apiResponse.body.remainingCardIds.isNotEmpty()) {
//                        remainingCardIds = (apiResponse.body.remainingCardIds)
//                    }

//                    AppUtilities.showLog("***", "remainingSurveyIds : $remainingSurveyIds")
//                    AppUtilities.showLog(
//                        "***",
//                        "remainingExternalActIds : $remainingExternalActIds"
//                    )
//                    AppUtilities.showLog("***", "remainingCardIds : $remainingCardIds")
//                    AppUtilities.showLog("***", "remainingPostIds : $remainingPostIds")
                    //loginDayActivityInfoList?.clear()
//                    if (soloGamesAdapter != null) soloGamesAdapter?.notifyDataSetChanged()
//                    loginDayActivityInfoList?.addAll(apiResponse.body.loginDayActivityInfoList?)
//                    if (soloGamesAdapter != null) soloGamesAdapter?.notifyDataSetChanged()

                    if (soloGamesAdapter != null) {
                        soloGamesAdapter?.addItems(apiResponse.body.loginDayActivityInfoList)
                        loginDayActivityInfoList?.addAll(apiResponse.body.loginDayActivityInfoList!!)
                    }

                    val dashboardData = DashboardData(
                        emptyList(),
                        gameResIdList!!,
                        loginDayActivityInfoList,
                        playGroup,
                        filterItems,
                        selectedFilterIndex,
                        displayNumberOfActivity,
                        counterFriendsPlaying,
                        recyclerViewPosition,
                        totalActivities,
                        filterActivities,
                        layoutManager!!
                    )
                    (fragmentContext as DashboardActivity).setDashboardData(dashboardData)

                    setAdapter()

                    if (soloGamesAdapter != null) {
                        getAudioShorts(Constants.TRENDING_VOCALS)
                    }

                    //getUserWatchList()

                    //getRemainingTopPriorityActivityIdList()

                    hideProgress()
                    Log.e("ApiResponse", Gson().toJson(apiResponse.body))
//                    preferenceHelper!!.putValue(Constants.JSON_2, Gson().toJson(apiResponse.body))

                } else {
                    val footerMessage: String = if (playGroup.playGroupId.equals("FFF", ignoreCase = true)) {
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
            if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing = false
            hideProgress()
            showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
            if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message)) || apiResponse.errorMessage.contains(
                    getString(R.string.unable_to_connect_host_message1)
                ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
            ) {
                if (com.onourem.android.activity.BuildConfig.DEBUG) {
                    AppUtilities.showLog("Network Error", serviceName)
                }
                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                    serviceName, apiResponse.code.toString()
                )
            }
        }
    }

    private fun getRemainingTopPriorityActivityIdList() {
        if (remainingCardIds!!.isNotEmpty() || remainingSurveyIds!!.isNotEmpty() || remainingExternalActIds!!.isNotEmpty() || remainingPostIds!!.isNotEmpty()) {

            questionGamesViewModel!!.getRemainingTopPriorityActivityIdList(
                remainingCardIds!!, remainingSurveyIds!!, remainingExternalActIds!!, remainingPostIds!!
            ).observe(viewLifecycleOwner) { responseApiResponse: ApiResponse<RemainingActivityIdsResponse> ->
                if (responseApiResponse.body != null && responseApiResponse.body.errorCode.equals(
                        "000", ignoreCase = true
                    )
                ) {
                    if (responseApiResponse.body.activityStatusList.isNotEmpty()) {
                        //activityStatusList!!.clear()
//                            activityStatusList!!.addAll(responseApiResponse.body.activityStatusList)

                        val dashboardData = DashboardData(
                            emptyList(),
                            gameResIdList!!,
                            loginDayActivityInfoList,
                            playGroup,
                            filterItems,
                            selectedFilterIndex,
                            displayNumberOfActivity,
                            counterFriendsPlaying,
                            recyclerViewPosition,
                            totalActivities,
                            filterActivities,
                            layoutManager!!
                        )
                        (fragmentContext as DashboardActivity).setDashboardData(dashboardData)
                    }
                }
            }
        }
    }

    private fun getAudioShorts(service: String) {
        val gson = Gson()
        var json = preferenceHelper!!.getString(Constants.VOCALS)
        val response = gson.fromJson(json, GetAudioInfoResponse::class.java)

        if (response != null) {
            songsFromServer.clear()
            val arrayList = ArrayList<AudioResponse>()

            response.audioResponseList.forEachIndexed { index, audioResponse ->
                if (index <= 6) {
                    arrayList.add(audioResponse)
                }
            }


            songsFromServer.addAll(SongProvider.getAllServerSongs(arrayList))

            addVocalItemToQuestionListing(songsFromServer)

            getUserWatchList()

        } else {
            mediaViewModel.getAudioInfo(service, "", "").observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<GetAudioInfoResponse> ->
                if (apiResponse.loading) {
//                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {

                    hideProgress()
                    binding.swipeRefreshLayout.isRefreshing = false
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        if (service == "getAudioInfo") {
                            if (apiResponse.body.message != null && apiResponse.body.message.isNotEmpty()) {
                                showAlert(apiResponse.body.message)
                            }
                            preferenceHelper!!.putValue(
                                Constants.KEY_AUDIO_LIMIT, apiResponse.body.audioDuration
                            )
                            preferenceHelper!!.putValue(
                                Constants.KEY_AUDIO_VIEW, apiResponse.body.audioViewDuration
                            )
                        }

                        json = gson.toJson(apiResponse.body)
                        preferenceHelper!!.putValue(Constants.VOCALS, json)

                        songsFromServer.clear()
                        val arrayList = ArrayList<AudioResponse>()

                        apiResponse.body.audioResponseList.forEachIndexed { index, audioResponse ->
                            if (index <= 6) {
                                arrayList.add(audioResponse)
                            }
                        }

                        songsFromServer.addAll(SongProvider.getAllServerSongs(arrayList))

                        addVocalItemToQuestionListing(songsFromServer)

                        getUserWatchList()
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                }
            }
        }

    }

    private fun addVocalItemToQuestionListing(songsFromServer: MutableList<Song>) {

        if (soloGamesAdapter != null) {
            var hasVocalItem = false
            loginDayActivityInfoList?.forEachIndexed { index, loginDayActivityInfoList ->
                if (loginDayActivityInfoList.activityId == "Audio0000") {
                    hasVocalItem = true
                    lastVisiblePosForVocal = index
                    loginDayActivityInfoList.songsFromServer = songsFromServer as ArrayList<Song>
                    soloGamesAdapter?.notifyItemChanged(lastVisiblePosForVocal)
                    return@forEachIndexed
                }
            }

            if (!hasVocalItem) {
                val item = LoginDayActivityInfoList()
                item.activityId = "Audio0000"
                item.activityType = "Audio"
                item.songsFromServer = songsFromServer as ArrayList<Song>

                val lastPos = layoutManager!!.findLastVisibleItemPosition()
                val position = if (lastPos <= 4) {
                    4
                } else {
                    lastPos
                }
                if (soloGamesAdapter!!.itemCount > position) {
                    soloGamesAdapter?.addItemAtPosition(
                        position, item
                    )
                }

                lastVisiblePosForVocal = position
            }

        }

        val dashboardData = DashboardData(
            emptyList(),
            gameResIdList!!,
            loginDayActivityInfoList,
            playGroup,
            filterItems,
            selectedFilterIndex,
            displayNumberOfActivity,
            counterFriendsPlaying,
            recyclerViewPosition,
            totalActivities,
            filterActivities,
            layoutManager!!
        )
        (fragmentContext as DashboardActivity).setDashboardData(dashboardData)

    }

    private fun updateFromEditWatchlist(watchListResponse: GetWatchListResponse) {

        //soloGamesAdapter?.removeItem(lastVisiblePosForWatchlist)
        if (watchListResponse.isFromEditAdd) {
            setAddWatchListData(watchListResponse)
        } else {
            setWatchListData(watchListResponse)
        }
//        setWatchlistListener()
    }

    private fun setWatchlistListener() {
        watchListItemClickListener = object : OnWatchListItemClickListener<android.util.Pair<Int, UserWatchList>> {
            override fun onItemClick(
                item: android.util.Pair<Int, UserWatchList>, position: Int
            ) {
                if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                    if (item.first == WatchListActions.ADD_TO_WATCH_LIST) {

                        val watchListResponse = (fragmentContext as DashboardActivity).getWatchListData()
                        if (watchListResponse != null) {
                            watchListResponse.userWatchList!!.forEach {
                                if (it.userId == item.second.userId) {
                                    it.status = "CancelInvitation"
                                }
                            }
                            (fragmentContext as DashboardActivity).setWatchListData(watchListResponse)
                        }

                    } else if (item.first == WatchListActions.WATCHING) {
                        if (item.second.status.equals("Watching", ignoreCase = true)) {
                            val shareMessage = String.format(
                                "Hi %s, I just saw you felt" + " " + item.second.expressionName!!.lowercase(
                                    Locale.getDefault()
                                ) + ". What made you feel that way?", item.second.firstName
                            )
                            val conversation = Conversation()
                            conversation.id = "EMPTY"
                            conversation.userName = item.second.firstName + " " + item.second.lastName
                            conversation.userOne = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
                            conversation.userTwo = item.second.userId
                            conversation.profilePicture = item.second.profilePictureUrl
                            conversation.userTypeId = item.second.userTypeId
                            conversation.userMessageFromWatchlist = shareMessage
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavConversations(
                                    conversation
                                )
                            )
                        }
                    } else if (item.first == WatchListActions.ACCEPT_INVITATION) {
                        val watchListResponse = (fragmentContext as DashboardActivity).getWatchListData()
                        if (watchListResponse != null) {
                            watchListResponse.userWatchList!!.forEach {
                                if (it.userId == item.second.userId) {
                                    it.status = "Watching"
                                }
                            }
                            (fragmentContext as DashboardActivity).setWatchListData(watchListResponse)
                        }

                    } else if (item.first == WatchListActions.REJECT_INVITATION) {
                        val watchListResponse = (fragmentContext as DashboardActivity).getWatchListData()

                        if (watchListResponse != null) {
                            val userWatchLists = ArrayList<UserWatchList>()
                            watchListResponse.userWatchList!!.forEach {
                                if (it.userId != item.second.userId) {
                                    userWatchLists.add(it)
                                }
                            }
                            watchListResponse.userWatchList = userWatchLists
                            (fragmentContext as DashboardActivity).setWatchListData(watchListResponse)
                        }


                    } else if (item.first == WatchListActions.CANCEL_INVITATION) {
                        val watchListResponse = (fragmentContext as DashboardActivity).getWatchListData()

                        if (watchListResponse != null) {
                            val userWatchLists = ArrayList<UserWatchList>()
                            watchListResponse.userWatchList!!.forEach {
                                if (it.userId != null && item.second.userId != null && it.userId != item.second.userId) {
                                    userWatchLists.add(it)
                                }
                            }
                            watchListResponse.userWatchList = userWatchLists
                            (fragmentContext as DashboardActivity).setWatchListData(watchListResponse)
                        }


                    } else if (item.first == WatchListActions.WATCH_LIST_EDIT) {
                        navController.navigate(
                            DashboardFragmentDirections.actionNavHomeToNavEditAddWatchList(
                                false
                            )
                        )
                        //            navController.popBackStack();
                    } else if (item.first == WatchListActions.WATCH_LIST_INVITE_FRIENDS) {
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavInviteFriends()
                        )
                        //            navController.popBackStack();
                    } else if (item.first == WatchListActions.WATCH_LIST_ADD) {
                        navController.navigate(
                            DashboardFragmentDirections.actionNavHomeToNavEditAddWatchList(
                                true
                            )
                        )
                        //            navController.popBackStack();
                    } else if (item.first == WatchListActions.WHOLE_ITEM) {
                        if (item.second.userId == "drawableWatchlist" || item.second.userId == "inviteWatchlist") {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavInviteFriends()
                            )
                        }
                    } else {
                        throw IllegalStateException("Unexpected value: " + item.first)
                    }
                } else {
                    (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
                }
            }

        }

    }

    private fun getUserWatchList() {
        val watchListResponse = (fragmentContext as DashboardActivity).getWatchListData()
        if (watchListResponse != null) {

            if (watchListResponse.isFromEditAdd) {
                setAddWatchListData(watchListResponse)
            } else {
                setWatchListData(watchListResponse)
            }

        } else {
            viewModel.getUserWatchList("").observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetWatchListResponse> ->
                if (apiResponse.loading) {
                    // showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        preferenceHelper!!.putValue(
                            Constants.KEY_GAME_POINTS, apiResponse.body.gamePoint
                        )
//                            json = gson.toJson(apiResponse.body)
//                            preferenceHelper!!.putValue(Constants.WATCHLIST, json)
                        preferenceHelper!!.putValue(Constants.WATCHLIST_POSITION_INDEX, 0)

                        val response = apiResponse.body
                        response.isFromEditAdd = false

                        (fragmentContext as DashboardActivity).setWatchListData(response)

                        setWatchListData(response)

                    } else {
                        hideProgress()
                        showAlert(apiResponse.errorMessage)
                        if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1)) || apiResponse.errorMessage.contains(
                                getString(R.string.unable_to_connect_host_message3)
                            ))
                        ) {
                            if (BuildConfig.DEBUG) {
                                AppUtilities.showLog("Network Error", "getUserWatchList")
                            }
                            (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                "getUserWatchList", apiResponse.code.toString()
                            )
                        }
                    }
                }
            }

        }

    }

    private fun setWatchListData(watchListResponse: GetWatchListResponse) {
        if (watchListResponse.userWatchList!!.size > 0) {
            val userWatchLists = ArrayList<UserWatchList>()

            for (item in watchListResponse.userWatchList!!) {
                item.centerText = ""
                item.viewType = FriendsPlayingWatchListAdapter.WATCHLIST_VIEW
                userWatchLists.add(item)
            }

            userWatchLists.add(editWatchListItem)

            userWatchLists.add(drawableItem)

            userWatchLists.add(inviteWatchListItem)

            addWatchListItemToQuestionListing(userWatchLists)

        } else {
            addToWatchFriendList()
        }
    }


    private fun setAddWatchListData(watchListResponse: GetWatchListResponse) {
        if (watchListResponse.userWatchList != null && watchListResponse.userWatchList!!.size > 0) {
            val userWatchLists = ArrayList<UserWatchList>()

            userWatchLists.add(drawableItem)

            for (item in watchListResponse.userWatchList!!) {
                item.centerText = ""
                item.viewType = FriendsPlayingWatchListAdapter.WATCHLIST_VIEW
                userWatchLists.add(item)
            }

            userWatchLists.add(inviteWatchListItem)

            addWatchListItemToQuestionListing(userWatchLists)
        } else {
            addZeroFriendsWatchListItems()
        }
    }

    private fun addZeroFriendsWatchListItems() {
        val userWatchLists = ArrayList<UserWatchList>()
        userWatchLists.add(drawableItem)
        userWatchLists.add(inviteWatchListItem)
        addWatchListItemToQuestionListing(userWatchLists)
    }

    private fun addWatchListItemToQuestionListing(userWatchLists: ArrayList<UserWatchList>) {

        if (soloGamesAdapter != null) {

            var hasVocalItem = false
            soloGamesAdapter?.getItems()?.forEachIndexed { index, loginDayActivityInfoList ->
                if (loginDayActivityInfoList?.activityId == "Watchlist0001") {
                    hasVocalItem = true
                    lastVisiblePosForWatchlist = index
                    loginDayActivityInfoList.watchListResponse = userWatchLists
                    soloGamesAdapter?.notifyItemChanged(lastVisiblePosForWatchlist)
                    return@forEachIndexed
                }
            }

            if (!hasVocalItem) {
                val item = LoginDayActivityInfoList()
                item.activityId = "Watchlist0001"
                item.activityType = "Watchlist"
                item.userParticipationStatus = "EditWatchlist"
                item.watchListResponse = userWatchLists

                val lastPos = layoutManager!!.findLastVisibleItemPosition()
                val position = if (lastPos <= 4) {
                    4
                } else {
                    lastPos
                }
                if (soloGamesAdapter!!.itemCount > position) {
                    soloGamesAdapter?.addItemAtPosition(
                        position, item
                    )
                }

                lastVisiblePosForWatchlist = position

            }


        }

        val dashboardData = DashboardData(
            emptyList(),
            gameResIdList!!,
            loginDayActivityInfoList,
            playGroup,
            filterItems,
            selectedFilterIndex,
            displayNumberOfActivity,
            counterFriendsPlaying,
            recyclerViewPosition,
            totalActivities,
            filterActivities,
            layoutManager!!
        )
        (fragmentContext as DashboardActivity).setDashboardData(dashboardData)

    }

    private fun addToWatchFriendList() {

        viewModel.watchFriendList().observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetWatchFriendListResponse> ->
            if (apiResponse.loading) {
                //showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                preferenceHelper!!.putValue(Constants.WATCHLIST_POSITION_INDEX, 0)

                val response = GetWatchListResponse()
                if (apiResponse.body.userWatchFriendList != null) {
                    response.userWatchList = apiResponse.body.userWatchFriendList as MutableList<UserWatchList>
                }
                response.isFromEditAdd = true

                setAddWatchListData(response)

                (fragmentContext as DashboardActivity).setWatchListData(response)


            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1)) || apiResponse.errorMessage.contains(
                        getString(R.string.unable_to_connect_host_message3)
                    ))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getWatchFriendList")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getWatchFriendList", apiResponse.code.toString()
                    )
                }
            }
        }

    }

    private fun setAdapter() {
        viewModel.updateTrendingData(false)
        viewModel.setShowCreateFab(true)
        binding.swipeRefreshLayout.isRefreshing = false
        binding.rvPlayGames.layoutManager = layoutManager
        if (loginDayActivityInfoList != null && loginDayActivityInfoList!!.isNotEmpty()) {
            if (soloGamesAdapter == null) {
                soloGamesAdapter = ActivityGamesAdapter(
                    playGroup,
                    loginDayActivityInfoList,
                    preferenceHelper!!,
                    this,
                    watchListItemClickListener,
                    viewModel,
                    viewLifecycleOwner,
                    navController,
                    alertDialog
                )
                binding.rvPlayGames.adapter = soloGamesAdapter
            } else {

                if (forceRefreshData) {
                    forceRefreshData = false
                    soloGamesAdapter?.updateItems(loginDayActivityInfoList)
                    binding.rvPlayGames.adapter = soloGamesAdapter
                } else {
                    //soloGamesAdapter?.addItems(loginDayActivityInfoList)
                    soloGamesAdapter?.notifyDataSetChanged()
                }

            }
            //setDashboardDataToActivity()
            binding.tvMessage.visibility = View.GONE
            binding.rvPlayGames.visibility = View.VISIBLE
            //            soloGamesAdapter.updateItems(loginDayActivityInfoList);

            loginDayActivityInfoList?.forEach {

                if (!TextUtils.isEmpty(it.activityImageLargeUrl)) {
                    Glide.with(requireActivity()).load(it.activityImageLargeUrl)
                }

                if (it.feedsInfo != null && !TextUtils.isEmpty(it.feedsInfo!!.postLargeImageURL)) {
                    Glide.with(requireActivity()).load(it.feedsInfo!!.postLargeImageURL)
                }
            }
//            hideProgress()
        } else {
//            hideProgress()
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

        binding.rvPlayGames.adapter = soloGamesAdapter

        if ((fragmentContext as DashboardActivity).getFriendsPlayingRecyclerViewPosition() != -1) layoutManager?.scrollToPositionWithOffset(
            (fragmentContext as DashboardActivity).getFriendsPlayingRecyclerViewPosition(),
            (fragmentContext as DashboardActivity).getFriendsPlayingRecyclerViewPositionTopView()
        )


//        if (recyclerViewState != null) {
//            layoutManager?.onRestoreInstanceState(recyclerViewState)
//        }
    }

    // print twenty element from list from the start index specified
    private fun getDisplayElements(
        myList: List<String>, startIndex: Int
    ): List<String> {
        var sub: List<String> = ArrayList()
        sub = myList.subList(
            startIndex, myList.size.toLong().coerceAtMost(startIndex + displayNumberOfActivity!!).toInt()
        )
        return sub
    }

    // print twenty element from list from the start index specified
    private fun getDisplayGameIdListElements(
        myList: List<String>, startIndex: Int
    ): List<String> {
        var sub: List<String> = ArrayList()
        sub = myList.subList(
            startIndex, myList.size.toLong().coerceAtMost(startIndex + displayNumberOfActivity!!).toInt()
        )
        return sub
    }

    private fun loadMoreGames() {
        var start = 0
        var end = 0
        val activityIds: MutableList<Int>

//        if (activityStatusList == null || activityStatusList!!.isEmpty()) {
//            this@FriendsPlayingFragment.isLastPage = true
//            this@FriendsPlayingFragment.isLoading = false
//            return
//        }
        start = soloGamesAdapter!!.itemCount
        end = totalActivities
//                AppUtilities.showLog("**activityStatusList:", activityStatusList!!.size.toString())
//                AppUtilities.showLog("**start:", soloGamesAdapter?.itemCount.toString())
        if (start >= end) {
            this@FriendsPlayingFragment.isLastPage = true
            this@FriendsPlayingFragment.isLoading = false
            setFooterMessage()
            return
        }
////        val ids: MutableList<String> = ArrayList()
////        val statusLists = getDisplayElements(
////            activityStatusList!!, start
////        )
////        AppUtilities.showLog("**newstatusLists:", statusLists.size.toString())
//        var i = 0
//        while (i < statusLists.size) {
//            val item = statusLists[i]
//            ids.add(item)
//            i++
//        }
//        val idsChunk = Utilities.getTokenSeparatedString(ids, ",")

        questionGamesViewModel!!.getNextTopPriorityActivityInfo("").observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<NewActivityInfoResponse> ->
            if (apiResponse.loading) {
                if (soloGamesAdapter != null) {
                    soloGamesAdapter?.addLoading()
                }
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                if (soloGamesAdapter != null) {
                    soloGamesAdapter?.removeLoading()
                }
                this@FriendsPlayingFragment.isLoading = false
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    //AppUtilities.showLog("PlayGamesFragment", "LoginDayActivityInfoList getNextUserActivityGroup" + apiResponse.body.getLoginDayActivityInfoList().size() + "");
                    if (apiResponse.body.loginDayActivityInfoList == null || apiResponse.body.loginDayActivityInfoList!!.isEmpty()) {
                        this@FriendsPlayingFragment.isLastPage = true
                        this@FriendsPlayingFragment.isLoading = false
                        setFooterMessage()
                    } else {
                        if (soloGamesAdapter != null) {
                            soloGamesAdapter?.addItems(apiResponse.body.loginDayActivityInfoList)
                            loginDayActivityInfoList?.addAll(apiResponse.body.loginDayActivityInfoList!!)
//                            preferenceHelper!!.putValue(Constants.JSON_3, Gson().toJson(apiResponse.body))

                            //setDashboardDataToActivity()
                        }

                        if (apiResponse.body.loginDayActivityInfoList!!.size < PaginationListener.PAGE_ITEM_SIZE) {
                            // this@FriendsPlayingFragment.isLastPage = true;
                            setFooterMessage()
                        } else {
                            this@FriendsPlayingFragment.isLastPage = false
                        }
//                                Log.e(
//                                    "####",
//                                    String.format(
//                                        "server: %d",
//                                        apiResponse.body.loginDayActivityInfoList.size
//                                    )
//                                )
                    }
                } else {
                    // this@FriendsPlayingFragment.isLastPage = true;
                    setFooterMessage()
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                this@FriendsPlayingFragment.isLoading = false
                if (soloGamesAdapter != null) {
                    soloGamesAdapter?.removeLoading()
                }
                showAlert(apiResponse.errorMessage)
            }
        }

    }


    override fun layoutResource(): Int {
        return R.layout.fragment_dashboard_play_games
    }

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    private fun setFooterMessage() {
        this@FriendsPlayingFragment.isLoading = false
        val footerMessage: String
        if (soloGamesAdapter != null) {
            footerMessage = if (soloGamesAdapter!!.itemCount > 0 /*&& !soloGamesAdapter.hasValidItems()*/) {
                if (playGroup.playGroupId.equals("FFF", ignoreCase = true)) {
                    "We appreciate you for using Onourem. Remember to keep smiling and check in with your loved ones regularly."
                } else if (playGroup.playGroupId.equals("AAA", ignoreCase = true)) {
                    "These are the questions you have not answered. We add a few new questions every day in this section. Answer questions you like and forward them to friends."
                } else if (playGroup.playGroupId.equals("ZZZ", ignoreCase = true)) {
                    "These are the questions you have answered before."
                } else if (playGroup.playGroupId.equals("YYY", ignoreCase = true)) {
                    "These are the questions you have created yourself."
                } else {
                    "That's all for the games played in this O-Club so far. Want to play more? Try creating a new question with the bottom right button."
                }
            } else {
                if (playGroup.playGroupId.equals("FFF", ignoreCase = true)) {
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
            }
            binding.rvPlayGames.postDelayed({
                if (soloGamesAdapter != null) {
                    soloGamesAdapter?.addFooter(footerMessage)
                    soloGamesAdapter?.notifyItemChanged(soloGamesAdapter!!.itemCount - 1)
                }
            }, 200)
        }
    }

    private fun ignoreQuestion(first: LoginDayActivityInfoList) {

        var titleText = ""
        if (first.activityType == "FriendCircleGame") {
            titleText = "Bond 003 game will be removed from your list for a few days."
        } else {
            var isSurvey = ""
            isSurvey = if (first.activityType == "Survey") {
                "survey"
            } else {
                "question"
            }
            titleText = "Do you wish to remove this $isSurvey from your list?"
        }

        val actions = ArrayList<Action<*>>()
        actions.add(
            Action(
                getString(R.string.action_label_ignore_confirm),
                R.color.color_black,
                ActionType.IGNORE_QUESTION_CONFIRMATION,
                first
            )
        )
        val bundle = Bundle()
        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                titleText, bundle, ""
            )
        )
    }

    override fun onPause() {
        super.onPause()
        //        state = layoutManager.onSaveInstanceState();
//        counter++;
        val positionIndex = layoutManager?.findFirstVisibleItemPosition() ?: 0
        val startView = binding.rvPlayGames.getChildAt(0)
        val topView = if (startView == null) 0 else startView.top - binding.rvPlayGames.paddingTop
        (fragmentContext as DashboardActivity).setFriendsPlayingRecyclerViewPosition(positionIndex, topView)

        (fragmentContext as DashboardActivity).setDashboardSelectedFilter(selectedFilterIndex)
        isDataLoading = false

        viewModel.setLoginDayActivityInfoList(loginDayActivityInfoList as MutableList<LoginDayActivityInfoList>)

//        recyclerViewState = layoutManager?.onSaveInstanceState()


    }

    override fun onItemClick(item: Triple<LoginDayActivityInfoList?, Int?, String?>?) {
        //Hiding Keyboard
//        if (binding.cardSearch.getVisibility() == View.VISIBLE) {
//            AppUtilities.hideKeyboard(requireActivity());
//            binding.edtSearchQuery.clearFocus();
//        }
        if (item != null) {
            if (item.first != null) {

                if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                    when (item.second) {

                        ActivityGamesAdapter.CLICK_MORE -> {
                            val title = "What would you like to do?"
                            val actions = ArrayList<Action<*>>()
                            if (loginUserId == item.first!!.feedsInfo!!.postCreatedId) {
                                actions.add(
                                    Action(
                                        "Delete This Message", R.color.color_red, ActionType.DELETE_GAME, item.first
                                    )
                                )
                                actions.add(
                                    Action(
                                        "Edit Post Visibility", R.color.color_black, ActionType.EDIT_GAME_VISIBILITY, item.first
                                    )
                                )
                            } else {
                                actions.add(
                                    Action(
                                        "Report Abuse", R.color.color_black, ActionType.REPORT_ABUSE, item.first
                                    )
                                )

                            }
                            val bundle = Bundle()
                            bundle.putParcelableArrayList(
                                Constants.KEY_BOTTOM_SHEET_ACTIONS, actions
                            )
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                                    title, bundle, ""
                                )
                            )
                        }

                        ActivityGamesAdapter.CLICK_COMMENT -> {
                            val receiverIds = StringBuilder()
                            var i = 0
                            while (i < item.first!!.feedsInfo!!.receiverId!!.size) {
                                receiverIds.append(item.first!!.feedsInfo!!.receiverId!![i])
                                if (i < item.first!!.feedsInfo!!.receiverId!!.size) receiverIds.append(",")
                                i += 4
                            }
                            WriteCommentDialogFragment.getInstance(
                                item.first!!.feedsInfo!!.activityId,
                                receiverIds.toString(),
                                "",
                                item.first!!.feedsInfo!!.postId,
                                ""
                            ) {
                                if (TextUtils.isEmpty(item.first!!.feedsInfo!!.commentCount)) {
                                    item.first!!.feedsInfo!!.commentCount = "1"
                                } else {
                                    val count = item.first!!.feedsInfo!!.commentCount!!.toInt() + 1
                                    item.first!!.feedsInfo!!.commentCount = count.toString()
                                }
                                if (soloGamesAdapter != null) {
                                    val updatedItemPosition = soloGamesAdapter?.updateItem(item.first)
                                    //scrollToTop(updatedItemPosition)
                                }
                            }.show(requireActivity().supportFragmentManager, "Comment")
                        }

                        ActivityGamesAdapter.CLICK_SENDER_PROFILE -> {
                            if (item.first!!.feedsInfo!!.anonymousOnOff.equals(
                                    "Y", ignoreCase = true
                                ) && loginUserId != item.first!!.feedsInfo!!.postCreatedId
                            ) {
                                showAlert("Identity of the friend who sent this message will be revealed after 48 hrs from the time the message was sent")
                            } else if (loginUserId == item.first!!.feedsInfo!!.postCreatedId) {
                                // scrollToTop(0)
                            } else {

                                if (!item.first!!.feedsInfo!!.postCreatedId.equals(
                                        "4264", ignoreCase = true
                                    )
                                ) {
                                    navController.navigate(
                                        MobileNavigationDirections.actionGlobalNavProfile(
                                            item.first!!.feedsInfo!!.activityId, item.first!!.feedsInfo!!.postCreatedId
                                        )
                                    )
                                } else {
                                    showAlert("You can't access profile of this admin user")
                                }
                            }
                        }

                        ActivityGamesAdapter.CLICK_ATTACHMENT -> {

                            if (!TextUtils.isEmpty(item.first!!.youTubeVideoId)) {
                                val intent = Intent(context, YoutubeActivity::class.java)
                                intent.putExtra(
                                    "youtubeId", item.first!!.youTubeVideoId!!
                                )
                                (fragmentContext as DashboardActivity).exoPlayerPause(true)
                                fragmentContext.startActivity(intent)
                            } else {
                                var url: String? = ""
                                val media: Int
                                if (!TextUtils.isEmpty(item.first!!.feedsInfo!!.videoURL)) {
                                    url = item.first!!.feedsInfo!!.videoURL
                                    media = 2
                                } else {
                                    url = item.first!!.feedsInfo!!.postLargeImageURL
                                    media = 1
                                }

                                if (url != null) {
                                    navController.navigate(
                                        MobileNavigationDirections.actionGlobalNavMediaView(
                                            media, url
                                        )
                                    )
                                }
                            }

                        }

                        ActivityGamesAdapter.CLICK_COMMENT_COUNT -> {
                            val receiverArray = ArrayList<String>()
                            val senderArray = ArrayList<String>()
                            val receiverIds = StringBuilder()
                            var i = 0
                            while (i < item.first!!.feedsInfo!!.receiverId!!.size) {
                                receiverArray.add(item.first!!.feedsInfo!!.receiverId!![i])
                                receiverIds.append(item.first!!.feedsInfo!!.receiverId!![i])
                                if (i < item.first!!.feedsInfo!!.receiverId!!.size) receiverIds.append(",")
                                i += 4
                            }
                            val userId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)

                            navController.navigate(MobileNavigationDirections.actionGlobalNavPostCommentList("",
                                item.first!!.feedsInfo!!.activityId!!,
                                item.first!!.feedsInfo!!.postId!!,
                                receiverIds.toString(),
                                "",
                                item.first!!.feedsInfo!!.activityId!!,
                                item.first!!.feedsInfo!!.postCreatedId!!,
                                "Post",
                                receiverArray.any { it == userId } || item.first!!.feedsInfo!!.postCreatedId == userId))
                        }

                        ActivityGamesAdapter.CLICK_REMOVE -> {
                            ignoreQuestion(item.first!!)
                        }

                        ActivityGamesAdapter.CLICK_AUDIO_ITEM -> {

                            navController.navigate(
                                (MobileNavigationDirections.actionGlobalNavVocalsMain(
                                    "", "", "", item.first!!.gameId!!
                                ))
                            )
                        }

                        ActivityGamesAdapter.CLICK_MENU -> {
                            val titleText = "What do you want to do with this question?"
                            val actions = ArrayList<Action<*>>()
                            if (item.first!!.isQuestionEditable) {
                                actions.add(
                                    Action(
                                        getString(R.string.action_label_edit),
                                        R.color.color_black,
                                        ActionType.EDIT_QUESTION,
                                        item.first
                                    )
                                )
                                actions.add(
                                    Action(
                                        getString(R.string.action_label_delete),
                                        R.color.color_black,
                                        ActionType.DELETE_QUESTION,
                                        item.first
                                    )
                                )
                            } else if (item.first!!.userParticipationStatus.equals(
                                    "None", ignoreCase = true
                                ) || item.first!!.userParticipationStatus.equals(
                                    "Pending", ignoreCase = true
                                )
                            ) {
                                if (selectedFilterIndex == 0) {
                                    actions.add(
                                        Action(
                                            getString(R.string.action_label_ignore),
                                            R.color.color_black,
                                            ActionType.IGNORE_QUESTION,
                                            item.first
                                        )
                                    )
                                }
                            }
                            val bundle = Bundle()
                            bundle.putParcelableArrayList(
                                Constants.KEY_BOTTOM_SHEET_ACTIONS, actions
                            )
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                                    titleText, bundle, ""
                                )
                            )
                        }

                        ActivityGamesAdapter.CLICK_ACTIVITY -> {
                            var title = ""
                            when (ActivityType.getActivityType(item.first!!.activityType!!)) {
                                ActivityType.ONE_TO_MANY -> title = "Group Bonding Question"
                                ActivityType.ONE_TO_ONE -> title = "One-on-One Question"
                                ActivityType.D_TO_ONE -> title = "Know Someone"
                                ActivityType.TASK -> title = "Wellbeing Activity"
                                ActivityType.MESSAGE -> title = "Heartfelt Message"
                                ActivityType.SURVEY -> title = "Survey"
                                ActivityType.EXTERNAL -> title = "Gems of Internet"
                                ActivityType.USER_MOOD_CARD -> title = "Gems of Internet"
                                ActivityType.CARD -> title = "Cards That Bring Smile"
                            }
                            showAlert(title, item.first!!.activityTypeHint)
                        }

                        ActivityGamesAdapter.CLICK_BUTTON -> if (getString(R.string.click_action_answer) == item.third) {
                            //                        a.	Answer:  If this icon appear then User need to answer first then only user can see who else have answered or can ask anyone else. This icon appear on following type questions:
                            //                        i.	One to One
                            //                        ii.	One to Many
                            //                        iii.	Ask Directly
                            if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.ONE_TO_ONE) {
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavOneToOneQuestionDetails(
                                        item.first!!, getPlayGroup(), null
                                    )
                                )
                            } else if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.ONE_TO_MANY || ActivityType.getActivityType(
                                    item.first!!.activityType!!
                                ) == ActivityType.D_TO_ONE
                            ) {
                                if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.D_TO_ONE) {
                                    navController.navigate(
                                        MobileNavigationDirections.actionGlobalDToOneQuestionDetailsFragment(
                                            item.first!!, getPlayGroup(), null
                                        )
                                    )
                                } else {
                                    navigateToAnswer(item.first!!, getPlayGroup())
                                }
                            } else {
                                showAlert("Unknown Action " + item.first!!.activityType)
                            }
                        } else if (getString(R.string.click_action_start_game) == item.third) {
                            //                        b.	Start Game: Icon appear only on One to One questions. It works in following way :
                            //                        i.	When use clicks on Start Game icon then user can ask this to any other Specific user.
                            //                                ii.	User can ask more the on user in one shot but NO other two users can see each other answer.  For Ex: If User A asked this to B , C & D users. Then user A can see answers of B,C & D but User B can not see answer of C & D. Same applicable on User C & D.
                            if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.ONE_TO_ONE) {
                                askQuestionToFriends(item)
                            }
                        } else if (getString(R.string.click_action_send_now) == item.third) {
                            if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.MESSAGE) {
                                TaskAndMessageComposeDialogFragment.getInstance(item.first!!) { item1: Pair<LoginDayActivityInfoList?, UploadPostResponse?>? ->
                                    navController.navigate(
                                        MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                            item.first!!, playGroup
                                        )
                                    )
                                }.show(
                                    requireActivity().supportFragmentManager, "TaskMessageCompose"
                                )
                            } else {
                                showAlert("Unknown Action " + item.first!!.activityType)
                            }
                        } else if (getString(R.string.click_action_ask_friends) == item.third || getString(
                                R.string.click_action_ask_question
                            ) == item.third
                        ) {
                            //                        This icon appear on following type of questions:
                            //                        i.	One to One
                            //                        ii.	One to Many
                            if ((ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.D_TO_ONE) || (ActivityType.getActivityType(
                                    item.first!!.activityType!!
                                ) == ActivityType.ONE_TO_ONE) || (ActivityType.getActivityType(
                                    item.first!!.activityType!!
                                ) == ActivityType.ONE_TO_MANY)
                            ) {
                                askQuestionToFriends(item)
                            } else {
                                showAlert("Unknown Action " + item.first!!.activityType)
                            }
                            //                    ask someone is now changed to Ask Friends
                            //                } else if (getString(R.string.click_action_ask_someone).equals(item.third)) {
                            ////                        This icon appear on following type of questions
                            ////                        i.	“Ask Directly”
                            ////                        ii.	It work in: This is 1-Way Question: User can ask this to any one and user need not to answer it.
                            //                    if (ActivityType.getActivityType(item.first.getActivityType())
                            //                            == ActivityType.D_TO_ONE) {
                            //                        navController.navigate(DashboardFragmentDirections.actionNavPlayGamesToNavAnswerQuestionDialog(item.first));
                            //                    } else {
                            //                        showAlert("Unknown Action " + item.first.getActivityType());
                            //                    }
                        } else if (getString(R.string.click_action_read_messages) == item.third || getString(
                                R.string.click_action_read_experience
                            ) == item.third
                        ) {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                    item.first!!, getPlayGroup()
                                )
                            )
                        } else if (getString(R.string.click_action_i_want_to_send) == item.third) {
                            //              f.	I Want To Send & Send Now: Icon appear on :
                            //                    i.	Appreciate type messages
                            //                        ii.	It work in following way:
                            //                    1.	User click on “I want to Send” Icon – then it show info pop-up(Screen f3) with “Send Now” & “Send Later” button- then icon convert in “Send Now” (Screen f4)
                            //                        2.	If user click any where on question screen (other then “I want to Send” icon) then  pop up appear (Screen f2)
                            //                        3.	Info pop up text :
                            //                        a.	“First commit to sending the message. You will then have 15days to send the message”
                            //                        b.	“You have 15 days from now to send this message” – Clickable Buttons: “Send Now”    “Send Later” – User click on ”Send Now” to compose message. User clicks on “Send Later” will close the pop up.
                            showSendNowSendLater(item.first!!)
                        } else if (getString(R.string.click_action_share_experience) == item.third) {
                            //                    navController.navigate(DashboardFragmentDirections.actionNavPlayGamesToNavTaskMessageCompose(item.first));
                            TaskAndMessageComposeDialogFragment.getInstance(item.first!!) { item1: Pair<LoginDayActivityInfoList?, UploadPostResponse?> ->
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                        item1.first!!, playGroup
                                    )
                                )
                            }.show(
                                requireActivity().supportFragmentManager, "TaskMessageCompose"
                            )
                        } else if (getString(R.string.click_action_i_will_do_it) == item.third) {
                            if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.TASK) {
                                val createGameActivityRequest = CreateGameActivityRequest()
                                createGameActivityRequest.serviceName = "createGameActivity"
                                createGameActivityRequest.screenId = "12"
                                createGameActivityRequest.loginDay = item.first!!.loginDay
                                createGameActivityRequest.activityId = item.first!!.activityId
                                createGameActivityRequest.activityTypeId = item.first!!.activityTypeId
                                questionGamesViewModel!!.createGameActivity(
                                    createGameActivityRequest
                                ).observe(
                                    viewLifecycleOwner
                                ) { apiResponse: ApiResponse<GameActivityUpdateResponse> ->
                                    if (apiResponse.loading) {
                                        showProgress()
                                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                                        hideProgress()
                                        if (apiResponse.body.errorCode.equals(
                                                "000", ignoreCase = true
                                            )
                                        ) {
                                            showAlert(apiResponse.body.message) {
                                                if (!TextUtils.isEmpty(apiResponse.body.participationStatus)) item.first!!.userParticipationStatus =
                                                    apiResponse.body.participationStatus
                                                if (!TextUtils.isEmpty(apiResponse.body.activityTagStatus)) item.first!!.activityTag =
                                                    apiResponse.body.activityTagStatus
                                                soloGamesAdapter?.notifyDataSetChanged()
                                            }
                                        } else {
                                            showAlert(apiResponse.body.errorMessage)
                                        }
                                    } else {
                                        hideProgress()
                                        showAlert(apiResponse.errorMessage)
                                    }
                                }
                            } else {
                                showAlert("Unknown Action " + item.first!!.activityType!!)
                            }
                        } else if (getString(R.string.click_action_all_answers) == item.third) {
                            if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.ONE_TO_MANY) {
                                if (getPlayGroup().playGroupId.equals(
                                        "AAA",
                                        ignoreCase = true
                                    ) || getPlayGroup().playGroupId.equals(
                                        "ZZZ",
                                        ignoreCase = true
                                    ) || getPlayGroup().playGroupId.equals("YYY", ignoreCase = true)
                                ) {
                                    questionGamesViewModel!!.getActivityGroups(item.first!!.activityId!!).observe(
                                        viewLifecycleOwner
                                    ) { apiResponse: ApiResponse<GetActivityGroupsResponse> ->
                                        if (apiResponse.loading) {
                                            showProgress()
                                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                                            hideProgress()
                                            if (apiResponse.body.errorCode.equals(
                                                    "000", ignoreCase = true
                                                )
                                            ) {
                                                if (apiResponse.body.activityGroupList != null && apiResponse.body.activityGroupList!!.isNotEmpty()) {
                                                    if (apiResponse.body.activityGroupList!!.size == 1) {
                                                        val activityGroupList = apiResponse.body.activityGroupList!![0]
                                                        val group = PlayGroup()
                                                        group.playGroupId = if ("0".equals(
                                                                activityGroupList.groupId, ignoreCase = true
                                                            )
                                                        ) "AAA"
                                                        else activityGroupList.groupId
                                                        if (!TextUtils.isEmpty(activityGroupList.activityGameResponseId)) {
                                                            item.first!!.activityGameResponseId =
                                                                activityGroupList.activityGameResponseId
                                                        }
                                                        if (!TextUtils.isEmpty(activityGroupList.groupId) && !activityGroupList.groupId.equals(
                                                                "0", ignoreCase = true
                                                            )
                                                        ) {
                                                            group.playGroupName = activityGroupList.groupName
                                                            group.isDummyGroup = false
                                                        } else {
                                                            group.playGroupName = playGroup.playGroupName
                                                            group.isDummyGroup = playGroup.isDummyGroup
                                                        }
                                                        navigateToDetails(item.first!!, group)
                                                    } else {
                                                        showGroupSelectionPopup(
                                                            apiResponse.body.activityGroupList!!, item.first!!
                                                        )
                                                    }
                                                } else {
                                                    navigateToDetails(
                                                        item.first!!, getPlayGroup()
                                                    )
                                                }
                                            } else {
                                                showAlert(apiResponse.body.errorMessage)
                                            }
                                        } else {
                                            hideProgress()
                                            showAlert(apiResponse.errorMessage)
                                        }
                                    }
                                } else {
                                    navigateToDetails(item.first!!, getPlayGroup())
                                }
                            } else if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.D_TO_ONE) {
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalDToOneQuestionDetailsFragment(
                                        item.first!!, getPlayGroup(), null
                                    )
                                )
                            } else if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.ONE_TO_ONE) {
                                //                        AppUtilities.showLog("###", "onItemClick: " + item.first.hashCode());
                                //                        AppUtilities.showLog("###", "onItemClick: " + item.first);
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavOneToOneQuestionDetails(
                                        item.first!!, getPlayGroup(), null
                                    )
                                )
                            } else {
                                showAlert("Unknown Action " + item.first!!.activityType!!)
                            }
                        } else if (getString(R.string.click_action_send_to_friends) == item.third) {
                            if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.CARD) {
                                inviteFriends(item.first!!)
                            } else if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.EXTERNAL) {
                                inviteFriends(item.first!!)
                                (fragmentContext as DashboardActivity).updateExternalActivityInfo(
                                    item.first!!
                                )
                            } else {
                                showAlert("Unknown Action " + item.first!!.activityType!!)
                            }
                        } else if (getString(R.string.click_action_all_cards) == item.third) {
                            if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.CARD) {
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavFunCards(
                                        linkUserId!!
                                    )
                                )
                            } else {
                                showAlert("Unknown Action " + item.first!!.activityType!!)
                            }
                        }
//                    else if (getString(R.string.click_action_share) == item.third) {
//                        if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.EXTERNAL) {
//                            inviteFriends(item.first!!)
//                            (fragmentContext as DashboardActivity).updateExternalActivityInfo(
//                                item.first!!
//                            )
//                        }
//                        else {
//                            showAlert("Unknown Action " + item.first!!.activityType!!)
//                        }
//                    }
                        else if (getString(R.string.click_action_show_more) == item.third) {
                            if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.EXTERNAL) {
                                (fragmentContext as DashboardActivity).updateExternalActivityInfo(
                                    item.first!!
                                )
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavWebExternalContent(
                                        item.first!!.questionVideoUrl!!
                                    )
                                )
                            } else if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.USER_MOOD_CARD) {

                                if (!TextUtils.isEmpty(item.first!!.youTubeVideoId)) {
                                    val intent = Intent(context, YoutubeActivity::class.java)
                                    intent.putExtra(
                                        "youtubeId", item.first!!.youTubeVideoId!!
                                    )
                                    (fragmentContext as DashboardActivity).exoPlayerPause(true)
                                    fragmentContext.startActivity(intent)
                                } else {
                                    if (item.first!!.youTubeLink == "V") {
                                        navController.navigate(
                                            MobileNavigationDirections.actionGlobalNavMediaView(
                                                2, item.first!!.questionVideoUrl!!
                                            )
                                        )
                                    } else if (!TextUtils.isEmpty(item.first!!.questionVideoUrl)) {
                                        navController.navigate(
                                            MobileNavigationDirections.actionGlobalNavWebExternalContent(
                                                item.first!!.questionVideoUrl!!
                                            )
                                        )
                                    } else if (!TextUtils.isEmpty(item.first!!.activityImageLargeUrl)) {
                                        navController.navigate(
                                            MobileNavigationDirections.actionGlobalNavMediaView(
                                                1, item.first!!.activityImageLargeUrl!!
                                            )
                                        )
                                    }

                                }
                            } else {
                                showAlert("Unknown Action " + item.first!!.activityType!!)
                            }
                        } else if (getString(R.string.click_action_see_stats) == item.third) {
                            if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_home) {
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavAnonymousSurvey(
                                        item.first!!.activityId!!,
                                    )
                                )
                            }
//                            getSurveyData(item)
//                            if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.SURVEY && item.first!!.surveySeeStats != "answered") {
//                                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_home) {
//                                    navController.navigate(
//                                        DashboardFragmentDirections.actionNavHomeToNavAnonymousSurvey(
//                                            item.first!!.activityId!!, "FriendsPlayingFragment"
//                                        )
//                                    )
//                                }
//
//                            } else if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.SURVEY && item.first!!.surveySeeStats == "answered") {
//                                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_home) {
//                                    navController.navigate(
//                                        DashboardFragmentDirections.actionNavHomeToNavStatisticsSurvey(
//                                            item.first!!.surveyCO!!
//                                        )
//                                    )
//                                }
//
//                            } else {
//                                showAlert("Unknown Action " + item.first!!.activityType!!)
//                            }
                        }

                        ActivityGamesAdapter.CLICK_WHOLE_ITEM -> if (ActivityType.getActivityType(
                                item.first!!.activityType!!
                            ) == ActivityType.MESSAGE && item.first!!.userParticipationStatus == "None"
                        ) {
                            showAlert("First commit to sending the message. You will then have 15 days to send the message")
                        } else if (ActivityType.getActivityType(
                                item.first!!.activityType!!
                            ) == ActivityType.FRIEND_CIRCLE_GAME
                        ) {
                            navigateToFriendCircleGame()
                        }

                        ActivityGamesAdapter.CLICK_MEDIA -> {
                            if (item.first!!.activityType!! == "External" || item.first!!.activityType!! == "UserMoodCard") {
                                (fragmentContext as DashboardActivity).updateExternalActivityInfo(
                                    item.first!!
                                )
                                if (!TextUtils.isEmpty(item.first!!.youTubeVideoId)) {
                                    val intent = Intent(context, YoutubeActivity::class.java)
                                    intent.putExtra(
                                        "youtubeId", item.first!!.youTubeVideoId!!
                                    )
                                    (fragmentContext as DashboardActivity).exoPlayerPause(true)
                                    fragmentContext.startActivity(intent)
                                } else {
                                    if (item.first!!.youTubeLink == "V") {
                                        navController.navigate(
                                            MobileNavigationDirections.actionGlobalNavMediaView(
                                                2, item.first!!.questionVideoUrl!!
                                            )
                                        )
                                    } else if (!TextUtils.isEmpty(item.first!!.questionVideoUrl)) {
                                        navController.navigate(
                                            MobileNavigationDirections.actionGlobalNavWebExternalContent(
                                                item.first!!.questionVideoUrl!!
                                            )
                                        )
                                    } else if (!TextUtils.isEmpty(item.first!!.activityImageLargeUrl)) {
                                        navController.navigate(
                                            MobileNavigationDirections.actionGlobalNavMediaView(
                                                1, item.first!!.activityImageLargeUrl!!
                                            )
                                        )
                                    }

                                }
                            } else if (!TextUtils.isEmpty(item.first!!.youTubeVideoId)) {
                                val intent = Intent(context, YoutubeActivity::class.java)
                                intent.putExtra(
                                    "youtubeId", item.first!!.youTubeVideoId!!
                                )
                                (fragmentContext as DashboardActivity).exoPlayerPause(true)
                                fragmentContext.startActivity(intent)
                            } else {
                                if (item.first!!.activityType!! == "FriendCircleGame") {
                                    navigateToFriendCircleGame()
                                } else {
                                    if (!TextUtils.isEmpty(item.first!!.questionVideoUrl)) {
                                        navController.navigate(
                                            MobileNavigationDirections.actionGlobalNavMediaView(
                                                2, item.first!!.questionVideoUrl!!
                                            )
                                        )
                                    } else if (!TextUtils.isEmpty(item.first!!.activityImageLargeUrl)) {
                                        navController.navigate(
                                            MobileNavigationDirections.actionGlobalNavMediaView(
                                                1, item.first!!.activityImageLargeUrl!!
                                            )
                                        )
                                    }
                                }

                            }

                        }

                        ActivityGamesAdapter.CLICK_SHARE -> if (item.first!!.activityType!!.equals(
                                "Card", ignoreCase = true
                            )
                        ) {
                            inviteFriends(item.first!!)
                        }
                    }
                } else {
                    (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
                }

            }
        }
    }

    private fun navigateToFriendCircleGame() {

        if ((fragmentContext as DashboardActivity).isUserPlayedGame() > 0) {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavFriendsThoughts(true)
            )
        } else {
            if ((fragmentContext as DashboardActivity).isUserPlayedGame() == -1) {
                Toast.makeText(fragmentContext, "Fetching Game data, please retry in few seconds", Toast.LENGTH_SHORT).show()
            } else {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavFriendsCircleMain()
                )
            }
        }
    }

    private fun getSurveyData(item: Triple<LoginDayActivityInfoList?, Int?, String?>) {
        surveyViewModel.getSurveyData(item.first!!.activityId!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<AnonymousSurveyResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        val surveyCOList = apiResponse.body.surveyCOList[0]
                        if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.SURVEY && surveyCOList.userAnserForSurvey == "N") {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavAnonymousSurvey(
                                    item.first!!.activityId!!,
                                )
                            )
                        } else if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.SURVEY && surveyCOList.userAnserForSurvey == "Y") {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavStatisticsSurvey(
                                    surveyCOList
                                )
                            )
                        }
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1)) || apiResponse.errorMessage.contains(
                            getString(R.string.unable_to_connect_host_message3)
                        ))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "getSurveyData")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getSurveyData", apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun navigateToAnswer(first: LoginDayActivityInfoList, playGroup: PlayGroup) {

        AnswerQuestionDialogFragment.getInstance(
            first, playGroup
        ) { apiResponse: UploadPostResponse? ->
            if (!TextUtils.isEmpty(apiResponse!!.participationStatus)) first.userParticipationStatus =
                apiResponse.participationStatus
            if (!TextUtils.isEmpty(apiResponse.activityTagStatus)) first.activityTag = apiResponse.activityTagStatus
            if (ActivityType.getActivityType(first.activityType!!) == ActivityType.ONE_TO_MANY) {
                navigateToDetails(first, playGroup)
            }
        }.show(requireActivity().supportFragmentManager, "Answer")

    }

    private fun getPlayGroup(): PlayGroup {
        val playGroupTemp: PlayGroup
        return if (playGroup.playGroupId.equals("AAA", ignoreCase = true) || playGroup.playGroupId.equals(
                "FFF",
                ignoreCase = true
            ) || playGroup.playGroupId.equals("YYY", ignoreCase = true) || playGroup.playGroupId.equals("ZZZ", ignoreCase = true)
        ) {
            playGroupTemp = playGroup
            playGroupTemp.playGroupId = "AAA"
            playGroupTemp
        } else {
            playGroup
        }
    }

    private fun showGroupSelectionPopup(
        playGroupList: List<ActivityGroupList>, first: LoginDayActivityInfoList?
    ) {
        SelectPlayGroupDialogFragment.getInstance(playGroupList) { activityGroupList: ActivityGroupList ->
            val group = PlayGroup()
            group.playGroupId = activityGroupList.groupId
            if (!TextUtils.isEmpty(activityGroupList.activityGameResponseId)) {
                first!!.activityGameResponseId = activityGroupList.activityGameResponseId
            }
            if (!TextUtils.isEmpty(activityGroupList.groupId) && !activityGroupList.groupId.equals(
                    "0", ignoreCase = true
                )
            ) {
                group.playGroupName = activityGroupList.groupName
            } else {
                group.playGroupName = playGroup.playGroupName
            }
            group.isDummyGroup = playGroup.isDummyGroup
            navigateToDetails(first, group)
        }.show(requireActivity().supportFragmentManager, "SelectPlayGroupDialogFragment")
    }

    private fun navigateToDetails(item: LoginDayActivityInfoList?, playGroup: PlayGroup?) {

        if (navController.currentDestination!!.id == R.id.nav_home && item != null) {
            navController.navigate(
                MobileNavigationDirections.actionGlobalOneToManyQuestionDetailsFragment(
                    item, playGroup!!, null, null, true
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

    override fun onResume() {
        super.onResume()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun askQuestionToFriends(item: Triple<LoginDayActivityInfoList?, Int?, String?>?) {
        var isActivityForNewQuestion = false
        if (playGroup.playGroupId.equals("YYY", ignoreCase = true)) {
            isActivityForNewQuestion = true
        }
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavAskQuestionsSheet(
                item!!.first!!, isActivityForNewQuestion, playGroup.playGroupId ?: ""
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showSendNowSendLater(activityInfoList: LoginDayActivityInfoList?) {
        val createGameActivityRequest = CreateGameActivityRequest()
        createGameActivityRequest.serviceName = "createGameActivity"
        createGameActivityRequest.screenId = "12"
        createGameActivityRequest.loginDay = activityInfoList!!.loginDay
        createGameActivityRequest.activityId = activityInfoList.activityId
        createGameActivityRequest.activityTypeId = activityInfoList.activityTypeId
        questionGamesViewModel!!.createGameActivity(createGameActivityRequest).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GameActivityUpdateResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (!TextUtils.isEmpty(apiResponse.body.activityTagStatus)) activityInfoList.activityTag =
                        apiResponse.body.activityTagStatus
                    if (!TextUtils.isEmpty(apiResponse.body.participationStatus)) activityInfoList.userParticipationStatus =
                        apiResponse.body.participationStatus
                    soloGamesAdapter?.notifyDataSetChanged()
                    val fragment = SendNowOrSendLaterDialogFragment.getInstance(
                        apiResponse.body.message, activityInfoList
                    ) { pair: android.util.Pair<LoginDayActivityInfoList?, Int> ->
                        if (pair.second == 1) {
                            TaskAndMessageComposeDialogFragment.getInstance(pair.first) { item1: Pair<LoginDayActivityInfoList?, UploadPostResponse?>? ->
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                        pair.first!!, playGroup
                                    )
                                )
                            }.show(
                                requireActivity().supportFragmentManager, "TaskMessageCompose"
                            )
                            //                            navController.navigate(DashboardFragmentDirections.actionNavPlayGamesToNavTaskMessageCompose(pair));
                        }
                    }
                    fragment.show(parentFragmentManager, "SendNowOrSendLaterDialogFragment")
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }


    private fun showFiltersAndBadges(show: Boolean) {
        if (show) {

//            binding.rvQuesGamesFilter.setVisibility(View.GONE);
            //TODO Search Visibility GONE
//            binding.cardSearch.setVisibility(View.VISIBLE);
//            binding.ibSearch.setVisibility(View.VISIBLE);
//            binding.fab.visibility = View.GONE
            binding.fabOClub.visibility = View.GONE
        } else {
            if (playGroup.playGroupId.equals("FFF", ignoreCase = true) || playGroup.playGroupId.equals(
                    "AAA",
                    ignoreCase = true
                ) || playGroup.playGroupId.equals("ZZZ", ignoreCase = true) || playGroup.playGroupId.equals(
                    "YYY",
                    ignoreCase = true
                )
            ) {
//                binding.fab.visibility = View.VISIBLE
                //binding.fabOClub.visibility = View.VISIBLE

//                if (selectedSearch) {
//                    Drawable close = binding.getRoot().getResources().getDrawable(R.drawable.ic_close_black_18dp);
//                    binding.ibSearch.setImageDrawable(close);
//                } else {
//                    Drawable search = binding.getRoot().getResources().getDrawable(R.drawable.ic_search_24_dp);
//                    binding.ibSearch.setImageDrawable(search);
//                }

//                Drawable search = binding.getRoot().getResources().getDrawable(R.drawable.ic_search_24_dp);
//                binding.ibSearch.setImageDrawable(search);
                AppUtilities.hideKeyboard(requireActivity())
                //binding.edtSearchQuery.clearFocus();
//                binding.edtSearchQuery.setText("");
                //               binding.cardSearch.setVisibility(View.GONE);
                questionGamesViewModel!!.questionFilterInfo()
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetQuestionFilterInfoResponse> ->
                        if (apiResponse.isSuccess && apiResponse.body != null) {
                            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                hideProgress()
//                            if (apiResponse.body.oClubBubbleTag.equals("Y", ignoreCase = true)) {
//                                binding.oclubBubble.visibility = View.VISIBLE
//                            } else {
//                                binding.oclubBubble.visibility = View.GONE
//                            }
//                            if (apiResponse.body.friendsPlayingBubbleTag.equals(
//                                    "Y",
//                                    ignoreCase = true
//                                )
//                            ) {
//                                val filterItem = filterItems!![0]
//                                filterItem.isVisible = true
//                            } else {
//                                val filterItem = filterItems!![0]
//                                filterItem.isVisible = false
//                            }
//                            if (apiResponse.body.newQuestionsBubbleTag.equals(
//                                    "Y",
//                                    ignoreCase = true
//                                )
//                            ) {
//                                val filterItem = filterItems!![1]
//                                filterItem.isVisible = true
//                            } else {
//                                val filterItem = filterItems!![1]
//                                filterItem.isVisible = false
//                            }
//                            if (apiResponse.body.answeredBubbleTag.equals("Y", ignoreCase = true)) {
//                                val filterItem = filterItems!![2]
//                                filterItem.isVisible = true
//                            } else {
//                                val filterItem = filterItems!![2]
//                                filterItem.isVisible = false
//                            }
//                            (fragmentContext as DashboardActivity).loadMessageBadgeFromFilter(
//                                apiResponse
//                            )
//
//                            //quesGamesFilterAdapter.notifyDataSetChanged();
//                        } else {
//                            showAlert(apiResponse.body.errorMessage)
//                        }
                                hideProgress()
                            }
                        }
//            } else {
//                binding.ibSearch.setVisibility(View.GONE);
//                binding.cardSearch.setVisibility(View.GONE);
                    }
            }
        }
    }

    private fun share(
        title: String?,
        linkMsg: String?,
        mediaUrl: String?,
        mediaType: String?,
    ) {

        navController.navigate(
            MobileNavigationDirections.actionGlobalNavShareContent(
                title ?: "",
                linkMsg ?: "",
                mediaUrl ?: "",
                mediaType ?: ""
            )
        )

//        if (!mediaUrl.equals("", ignoreCase = true)) {
//            navController.navigate(
//                MobileNavigationDirections.actionGlobalNavMediaShare(
//                    title ?: "", linkMsg ?: "", mediaUrl ?: "", mediaType ?: ""
//                )
//            )
//        } else {
//            val shareIntent = Intent(Intent.ACTION_SEND)
//            shareIntent.type = "text/plain"
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
//            shareIntent.putExtra(Intent.EXTRA_TEXT, linkMsg)
//            try {
//                fragmentContext.startActivity(Intent.createChooser(shareIntent, title));
//            } catch (ex: ActivityNotFoundException) {
//                Toast.makeText(fragmentContext, "NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
//            }
//        }
    }

    private fun inviteFriends(first: LoginDayActivityInfoList?) {
        viewModel.getUserLinkInfo(
            first!!.activityType, first.activityId!!, "48", first.activityText
        ).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetUserLinkInfoResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    hideProgress()
//                    shortLink(
//                        apiResponse.body.title,
//                        apiResponse.body.linkUserId,
//                        apiResponse.body.userLink,
//                        first,
//                        apiResponse.body.linkType,
//                        apiResponse.body.activityImageUrl,
//                        apiResponse.body.activityText
//                    )

                    if (!TextUtils.isEmpty(first.questionVideoUrl)) {
                        if (first.activityType == "External") {
                            share(
                                apiResponse.body.title, apiResponse.body.shortLink, apiResponse.body.activityImageUrl, "1"
                            )
                        } else {
                            share(
                                apiResponse.body.title, apiResponse.body.shortLink, first.questionVideoUrl!!, "2"
                            )
                        }

                    } else if (!TextUtils.isEmpty(first.activityImageLargeUrl)) {
                        share(
                            apiResponse.body.title, apiResponse.body.shortLink, apiResponse.body.activityImageUrl, "1"
                        )
                    } else {
                        share(
                            apiResponse.body.title, apiResponse.body.shortLink, "", "-1"
                        )
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1)) || apiResponse.errorMessage.contains(
                        getString(R.string.unable_to_connect_host_message3)
                    ))
                ) {
                    if (com.onourem.android.activity.BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getUserLinkInfo")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getUserLinkInfo", apiResponse.code.toString()
                    )
                }
            }
        }
    }

    //if activityImageUrl != nil && activityImageUrl != ""
//        {
//            linkBuilder!.socialMetaTagParameters = DynamicLinkSocialMetaTagParameters()
//            linkBuilder!.socialMetaTagParameters!.title = activityText
//
//            if linkType == "Card" {
//                linkBuilder!.socialMetaTagParameters?.descriptionText = "Fun cards to share with friends"
//            }
//            else if linkType == "1toM" {
//                linkBuilder!.socialMetaTagParameters?.descriptionText = "Fun questions to ask friends"
//            }
//            else {
//                linkBuilder!.socialMetaTagParameters?.descriptionText = "A place for good friends"
//            }
//
//            linkBuilder!.socialMetaTagParameters!.imageURL = URL(string: activityImageUrl  );
//
//        }
    private fun shortLink(
        title: String,
        linkUserId: String,
        linkMsg: String,
        first: LoginDayActivityInfoList?,
        linkType: String,
        activityImageUrl: String,
        activityText: String
    ) {
        first!!.activityImageUrl = activityImageUrl
        first.activityText = activityText
        showProgress()
        val titleSocial: String
        val description: String
        var imageUrl: String? = ""
        description = if (linkType.equals("Card", ignoreCase = true)) {
            "Fun cards to share with friends"
        } else if (linkType.equals("1toM", ignoreCase = true)) {
            "Fun questions to ask friends"
        } else if (linkType.equals("External", ignoreCase = true)) {
            "Came across this interesting post on Onourem."
        } else {
            "A place for good friends"
        }


        if (!TextUtils.isEmpty(first.activityImageUrl)) {
            imageUrl = first.activityImageUrl
        }
        titleSocial = first.activityText!!
        val builderSocialMeta =
            DynamicLink.SocialMetaTagParameters.Builder().setTitle(titleSocial).setImageUrl(Uri.parse(imageUrl))
                .setDescription(description).build()
        val navigationInfoParameters = DynamicLink.NavigationInfoParameters.Builder()
        navigationInfoParameters.setForcedRedirectEnabled(true)
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://www.onourem.com/?linkUserId=$linkUserId&linkType=$linkType"))
            .setDomainUriPrefix("https://e859h.app.goo.gl").setAndroidParameters(
                DynamicLink.AndroidParameters.Builder("com.onourem.android.activity") //                                .setMinimumVersion(125)
                    .build()
            ).setSocialMetaTagParameters(builderSocialMeta).setIosParameters(
                DynamicLink.IosParameters.Builder("com.onourem.onoureminternet")
                    .setAppStoreId("1218240628") //                                .setMinimumVersion("1.0.1")
                    .build()
            ) // Set parameters
            // ...
            .setNavigationInfoParameters(navigationInfoParameters.build()).buildShortDynamicLink()
            .addOnCompleteListener(requireActivity()) { task: Task<ShortDynamicLink> ->
                if (task.isSuccessful) {
                    // Short link created
                    val shortLink = Objects.requireNonNull(task.result!!).shortLink
                    //Log.d("shortLink", shortLink.toString())
                    viewModel.updateAppShortLink(linkUserId, shortLink.toString()).observe(
                        viewLifecycleOwner
                    ) { response: ApiResponse<StandardResponse> ->
                        if (response.loading) {
                            showProgress()
                        } else if (response.isSuccess && response.body != null) {
                            hideProgress()
                            if (response.body.errorCode.equals("000", ignoreCase = true)) {
//                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                                    shareIntent.setType("text/plain");
//                                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem");
//                                    String shareMessage = linkMsg + " \n" + shortLink.toString();
//                                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
//                                    startActivityForResult(Intent.createChooser(shareIntent, title), 1212);
//

                            } else {
                                showAlert(response.body.errorMessage)
                            }
                        } else {
                            hideProgress()
                            showAlert(response.errorMessage)
                            if (response.errorMessage != null && (response.errorMessage.contains(getString(R.string.unable_to_connect_host_message1)) || response.errorMessage.contains(
                                    getString(R.string.unable_to_connect_host_message3)
                                ))
                            ) {
                                if (com.onourem.android.activity.BuildConfig.DEBUG) {
                                    AppUtilities.showLog("Network Error", "updateAppShortLink")
                                }
                                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                    "updateAppShortLink", response.code.toString()
                                )
                            }
                        }
                    }
                }
            }
    }

    private fun share(
        title: String, linkMsg: String, shortLink: Uri, mediaUrl: String, mediaType: String
    ) {
//        if (!mediaUrl.equals("", ignoreCase = true)) {
//            navController.navigate(
//                MobileNavigationDirections.actionGlobalNavMediaShare(
//                    title, linkMsg, mediaUrl, mediaType
//                )
//            )
//        } else {
//            val shareIntent = Intent(Intent.ACTION_SEND)
//            shareIntent.type = "text/plain"
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
//            val shareMessage = "$linkMsg \n$shortLink"
//            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
//            try {
//                fragmentContext.startActivity(Intent.createChooser(shareIntent, title));
//            } catch (ex: ActivityNotFoundException) {
//                Toast.makeText(fragmentContext, "NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
//            }
//        }

        navController.navigate(
            MobileNavigationDirections.actionGlobalNavShareContent(
                title,
                linkMsg,
                mediaUrl,
                mediaType
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        //        state = null;
        loginDayActivityInfoList?.clear()
//        if (activityStatusList != null) {
//            activityStatusList!!.clear()
//        }
        if (gameResIdList != null) {
            gameResIdList!!.clear()
        }
        questionGamesViewModel!!.actionConsumed()
        //Glide.get(requireActivity()).clearMemory()
        parentJob.cancel()
    }

    override fun onItemClick(item: Pair<Int, UserWatchList>, position: Int) {

    }

}