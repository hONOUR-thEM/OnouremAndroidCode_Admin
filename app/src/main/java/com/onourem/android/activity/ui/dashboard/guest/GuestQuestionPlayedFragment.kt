package com.onourem.android.activity.ui.dashboard.guest

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
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
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.dashboard.DashboardFragmentDirections
import com.onourem.android.activity.ui.games.adapters.PlaySoloGamesAdapter
import com.onourem.android.activity.ui.games.dialogs.SendNowOrSendLaterDialogFragment
import com.onourem.android.activity.ui.games.fragments.AnswerQuestionDialogFragment
import com.onourem.android.activity.ui.games.fragments.SelectPlayGroupDialogFragment
import com.onourem.android.activity.ui.games.fragments.TaskAndMessageComposeDialogFragment
import com.onourem.android.activity.ui.games.viewmodel.GamesReceiverViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.ui.utils.listners.RVScrollListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject

class GuestQuestionPlayedFragment(val rvScrollListener: RVScrollListener) :
    AbstractBaseViewModelBindingFragment<DashboardViewModel, FragmentDashboardPlayGamesGuestBinding>(),
    OnItemClickListener<Triple<LoginDayActivityInfoList?, Int?, String?>?> {

    private var activityStatusList: MutableList<String>? = ArrayList()
    private var remainingSurveyIds: String? = ""
    private var remainingCardIds: String? = ""
    private var gameResIdList: MutableList<String>? = ArrayList()

    private val selectedSearch = false

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var loginDayActivityInfoList: MutableList<LoginDayActivityInfoList>? = ArrayList()
    private var linkUserId = ""
    private var isLinkVerified = ""
    private var adviceUpdateDialog: Dialog? = null
    private var questionGamesViewModel: QuestionGamesViewModel? = null
    private lateinit var playGroup: PlayGroup
    private var soloGamesAdapter: PlaySoloGamesAdapter? = null
    private var gamesReceiverViewModel: GamesReceiverViewModel? = null

    private var filterItems: ArrayList<FilterItem>? = null
    private var selectedFilterIndex = 0
    private var userActionViewModel: UserActionViewModel? = null
    private var isDataLoading = false
    private var isFrom = ""
    private var displayNumberOfActivity: Long? = 10L

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(parentJob + Dispatchers.Default)

    private var layoutManager: LinearLayoutManager? = null

    companion object {

        private var isLoading = false
        private var isLastPage = false

        fun create(rvScrollListener: RVScrollListener): GuestQuestionPlayedFragment {
            return GuestQuestionPlayedFragment(rvScrollListener)
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionGamesViewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[QuestionGamesViewModel::class.java]
        gamesReceiverViewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[GamesReceiverViewModel::class.java]
        userActionViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceHelper!!.putValue(Constants.KEY_SELECTED_FILTER_INDEX, selectedFilterIndex)

        layoutManager = LinearLayoutManager(requireActivity())
        binding.rvPlayGames.layoutManager = layoutManager

        playGroup = PlayGroup()
        playGroup.playGroupId = "ZZZ"
        playGroup.playGroupName = "Played"
        playGroup.isDummyGroup = true

        //binding.fabOClub.visibility = View.VISIBLE

        binding.rvPlayGames.addOnScrollListener(object : PaginationListener(layoutManager!!) {
            override fun loadMoreItems() {
                GuestQuestionPlayedFragment.isLoading = true
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return GuestQuestionPlayedFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return GuestQuestionPlayedFragment.isLoading
            }
        })


        binding.rvPlayGames.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
//                if (dy < 0) {
//                    binding.fab.extend()
//                    binding.fabOClub.extend()
//                } else if (dy > 0) {
//                    binding.fab.shrink()
//                    binding.fabOClub.shrink()
//                }
                rvScrollListener.onScroll(dx, dy)
            }
        })

        binding.fab.setOnClickListener(ViewClickListener {
            (fragmentContext as DashboardActivity).showGuestPopup("Create Question", "")
        })
        binding.fabOClub.setOnClickListener(ViewClickListener {
            (fragmentContext as DashboardActivity).showGuestPopup("My O'club", "")
        })

    }

    private fun loadData(showProgress: Boolean, refreshing: Boolean) {

        binding.swipeRefreshLayout.isRefreshing = refreshing
        isLoading = false
        isLastPage = false

        playGroup.playGroupId = "ZZZ"
        preferenceHelper!!.putValue(Constants.KEY_SELECTED_FILTER_INDEX, 2)
        if (loginDayActivityInfoList == null || loginDayActivityInfoList!!.isEmpty()) {
            questionGamesViewModel!!.userAnsweredActivityInfo().observe(
                viewLifecycleOwner
            ) { responseApiResponse: ApiResponse<UserActivityInfoResponse> ->
                handleResponse(
                    responseApiResponse,
                    true,
                    showProgress,
                    "getUserAnsweredActivityInfo"
                )
            }
        } else {
            setAdapter()
        }

    }

    private fun editQuestion(action: Action<Any?>?) {
        val first = action!!.data as LoginDayActivityInfoList
        navController.navigate(DashboardFragmentDirections.actionNavHomeToNavEditQuestion(first))
    }

    private fun deleteQuestion(action: Action<Any?>?) {
        val first = action!!.data as LoginDayActivityInfoList
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
                titleText,
                bundle, ""
            )
        )
    }

    private fun ignoreQuestion(action: Action<Any?>?) {
        val first = action!!.data as LoginDayActivityInfoList
        val titleText = "Do you want to ignore this question?"
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
                titleText,
                bundle, ""
            )
        )
    }

    private fun deleteQuestionConfirmed(action: Action<Any?>?) {
        val first = action!!.data as LoginDayActivityInfoList
        questionGamesViewModel!!.deleteQuestion(first.activityId!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<EditQuestionResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        if (apiResponse.body.message != null && !apiResponse.body.message.equals(
                                "",
                                ignoreCase = true
                            )
                        ) {
                            showAlert(apiResponse.body.message)
                        } else {
                            if (soloGamesAdapter != null) soloGamesAdapter!!.removeItem(first)
                        }
                    } else {
                        hideProgress()
                        showAlert(apiResponse.body.errorMessage)
                        if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(
                                getString(R.string.unable_to_connect_host_message)
                            ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                    || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                        ) {
                            if (com.onourem.android.activity.BuildConfig.DEBUG) {
                                AppUtilities.showLog("Network Error", "deleteQuestion")
                            }
                            (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                "deleteQuestion",
                                apiResponse.code.toString()
                            )
                        }
                    }
                }
            }
    }

    private fun ignoreQuestionConfirmed(action: Action<Any?>?) {
        val first = action!!.data as LoginDayActivityInfoList
        if (first.activityType.equals("1toM", ignoreCase = true)) {
            if (selectedFilterIndex == 0 || selectedFilterIndex == 1) {
                ignoreGame(
                    null,
                    null,
                    null,
                    first.activityId,
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
        gameId: String?,
        activityGameResId: String?,
        playGroupId: String?,
        activityId: String?,
        first: LoginDayActivityInfoList?
    ) {
        questionGamesViewModel!!.ignoreOneToManyGameActivity(
            gameId,
            activityGameResId,
            playGroupId,
            activityId
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
                        soloGamesAdapter!!.removeItem(first)
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
                    ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (com.onourem.android.activity.BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "ignoreGame")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "ignoreGame",
                        apiResponse.code.toString()
                    )
                }
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
        if (!isDataLoading) loadData(false, true)

    }


    private fun checkForceUpgradeData(
        showProgress: Boolean,
        refreshing: Boolean,
        getAppUpgradeInfoResponse: GetAppUpgradeInfoResponse?
    ) {
        var forceAndAdviceUpgrade: ForceAndAdviceUpgrade? = null
        if (getAppUpgradeInfoResponse != null) forceAndAdviceUpgrade =
            getAppUpgradeInfoResponse.forceAndAdviceUpgrade
        if (getAppUpgradeInfoResponse != null && forceAndAdviceUpgrade != null && AppUtilities.getAppVersion() >= java.lang.Double.valueOf(
                forceAndAdviceUpgrade.androidForceUpgradeVersion
            ).toInt()
        ) {

            //AppUtilities.showLog("PlayGamesFragment", "loadData " + counterLoadData++);

        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun handleResponse(
        apiResponse: ApiResponse<UserActivityInfoResponse>,
        isSolo: Boolean,
        showProgress: Boolean,
        serviceName: String
    ) {
        if (apiResponse.loading) {
            isDataLoading = true
            if (showProgress) showProgress()

            if (binding.swipeRefreshLayout.isRefreshing) {
                binding.tvMessage.text = "Loading Data, Please Wait...!"
                binding.tvMessage.visibility = View.VISIBLE
            } else {
                binding.tvMessage.text = ""
                binding.tvMessage.visibility = View.GONE
            }

        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            isDataLoading = false
            hideProgress()
            if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing =
                false
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                displayNumberOfActivity = apiResponse.body.displayNumberOfActivity
                //                AppUtilities.showLog("PlayGamesFragment", "LoginDayActivityInfoList " + apiResponse.body.getLoginDayActivityInfoList().size() + "");
//                AppUtilities.showLog("PlayGamesFragment", "ActivityStatusList " + (apiResponse.body.getActivityStatusList() != null ? apiResponse.body.getActivityStatusList().size() : "null") + "");
//                AppUtilities.showLog("PlayGamesFragment", "GameResIdList " + (apiResponse.body.getGameResIdList() != null ? apiResponse.body.getGameResIdList().size() : "null") + "");
                showFiltersAndBadges(selectedSearch)
                binding.tvMessage.visibility = View.GONE
                if (!isSolo) {
                    questionGamesViewModel!!.updateActivityMemberNumber(playGroup.playGroupId)
                        .observe(
                            viewLifecycleOwner
                        ) { userActivityInfoResponseApiResponse: ApiResponse<UserActivityInfoResponse>? -> }
                }
//                    if (apiResponse.body.activityStatusList != null && !apiResponse.body.activityStatusList.isEmpty()) {
//                        activityStatusList!!.clear()
//                        activityStatusList!!.addAll(apiResponse.body.activityStatusList)
//                    }
                if (apiResponse.body.gameResIdList != null && !apiResponse.body.gameResIdList.isEmpty()) {
                    gameResIdList!!.clear()
                    gameResIdList!!.addAll(apiResponse.body.gameResIdList)
                }
                loginDayActivityInfoList!!.clear()
                if (soloGamesAdapter != null) soloGamesAdapter!!.notifyDataSetChanged()
                loginDayActivityInfoList!!.addAll(apiResponse.body.loginDayActivityInfoList)
                if (soloGamesAdapter != null) soloGamesAdapter!!.notifyDataSetChanged()

                setAdapter()

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

        if (loginDayActivityInfoList != null && loginDayActivityInfoList!!.isNotEmpty()) {
            if (soloGamesAdapter == null) {
                soloGamesAdapter = PlaySoloGamesAdapter(
                    playGroup,
                    loginDayActivityInfoList,
                    preferenceHelper!!,
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
        } else {
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

        if (!TextUtils.isEmpty(linkUserId)) {
            sendPushNotificationToLinkUser()
        }
    }

    // print twenty element from list from the start index specified
    private fun getDisplayGameIdListElements(myList: List<String>, startIndex: Int): List<String> {
        var sub: List<String> = ArrayList()
        sub = myList.subList(
            startIndex, Math.min(myList.size.toLong(), startIndex + displayNumberOfActivity!!)
                .toInt()
        )
        return sub
    }

    private fun loadMoreGames() {
        var start = 0
        var end = 0
        val activityIds: MutableList<Int>

        if (gameResIdList == null || gameResIdList!!.isEmpty()) {
            isLastPage = true
            isLoading = false
            setFooterMessage()
            return
        }
        start = soloGamesAdapter!!.itemCount
        end = gameResIdList!!.size
        if (start >= end) {
            isLastPage = true
            isLoading = false
            setFooterMessage()
            return
        }
        activityIds = ArrayList()
        val gameIdList2 = getDisplayGameIdListElements(
            gameResIdList!!, start
        )
        AppUtilities.showLog("**gameIdList2:", gameIdList2.size.toString())
        var i = 0
        while (i < gameIdList2.size) {
            val item = gameIdList2[i]
            activityIds.add(item.toInt())
            i++
        }

        questionGamesViewModel!!.getZZZNextUserAnsweredActivityInfo(
            Utilities.getTokenSeparatedString(
                activityIds,
                ","
            ), "getUserAnsweredActivityInfo"
        ).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<UserActivityInfoResponse> ->
            if (apiResponse.loading) {
                if (soloGamesAdapter != null) {
                    soloGamesAdapter!!.addLoading()
                }
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                if (soloGamesAdapter != null) {
                    soloGamesAdapter!!.removeLoading()
                }
                isLoading = false
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    //AppUtilities.showLog("PlayGamesFragment", "LoginDayActivityInfoList getZZZNextUserAnsweredActivityInfo" + apiResponse.body.getLoginDayActivityInfoList().size() + "");
                    if (apiResponse.body.loginDayActivityInfoList == null || apiResponse.body.loginDayActivityInfoList.isEmpty()) {
                        // isLastPage = true;
                        setFooterMessage()
                    } else {
                        if (soloGamesAdapter != null) {
                            soloGamesAdapter!!.addItems(apiResponse.body.loginDayActivityInfoList)
                            loginDayActivityInfoList!!.addAll(apiResponse.body.loginDayActivityInfoList)
                            //setDashboardDataToActivity()
                        }
                        if (apiResponse.body.loginDayActivityInfoList.size < PaginationListener.PAGE_ITEM_SIZE) {
                            // isLastPage = true;
                            setFooterMessage()
                        } else {
                            isLastPage = false
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
                    // isLastPage = true;
                    setFooterMessage()
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                isLoading = false
                if (soloGamesAdapter != null) {
                    soloGamesAdapter!!.removeLoading()
                }
                showAlert(apiResponse.errorMessage)
            }
        }
    }


    override fun layoutResource(): Int {
        return R.layout.fragment_dashboard_play_games_guest
    }

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    private fun setFooterMessage() {
        isLoading = false
        val footerMessage: String
        if (soloGamesAdapter != null) {
            footerMessage =
                if (soloGamesAdapter!!.itemCount > 0 /*&& !soloGamesAdapter.hasValidItems()*/) {
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
                    soloGamesAdapter!!.addFooter(footerMessage)
                    soloGamesAdapter!!.notifyItemChanged(soloGamesAdapter!!.itemCount - 1)
                }
            }, 200)
        }
    }

    private fun ignoreQuestion(first: LoginDayActivityInfoList) {
        val titleText = "Do you wish to remove this question from your list?"
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
                titleText,
                bundle,
                ""
            )
        )
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
        //Hiding Keyboard
//        if (binding.cardSearch.getVisibility() == View.VISIBLE) {
//            AppUtilities.hideKeyboard(requireActivity());
//            binding.edtSearchQuery.clearFocus();
//        }
        if (item != null) {
            if (item.first != null) {
                when (item.second) {

                    PlaySoloGamesAdapter.CLICK_REMOVE -> {
                        ignoreQuestion(item.first!!)
                    }
                    PlaySoloGamesAdapter.CLICK_MENU -> {
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
                                "None",
                                ignoreCase = true
                            )
                            || item.first!!.userParticipationStatus.equals(
                                "Pending",
                                ignoreCase = true
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
                        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                                titleText,
                                bundle, ""
                            )
                        )
                    }
                    PlaySoloGamesAdapter.CLICK_ACTIVITY -> {
                        var title = ""
                        when (ActivityType.getActivityType(item.first!!.activityType)) {
                            ActivityType.ONE_TO_MANY -> title = "Group Bonding Question"
                            ActivityType.ONE_TO_ONE -> title = "One-on-One Question"
                            ActivityType.D_TO_ONE -> title = "Know Someone"
                            ActivityType.TASK -> title = "Wellbeing Activity"
                            ActivityType.MESSAGE -> title = "Heartfelt Message"
                        }
                        showAlert(title, item.first!!.activityTypeHint)
                    }
                    PlaySoloGamesAdapter.CLICK_BUTTON -> if (getString(R.string.click_action_answer) == item.third) {
                        //                        a.	Answer:  If this icon appear then User need to answer first then only user can see who else have answered or can ask anyone else. This icon appear on following type questions:
                        //                        i.	One to One
                        //                        ii.	One to Many
                        //                        iii.	Ask Directly
                        if (ActivityType.getActivityType(item.first!!.activityType)
                            == ActivityType.ONE_TO_ONE
                        ) {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavOneToOneQuestionDetails(
                                    item.first!!,
                                    getPlayGroup(),
                                    null
                                )
                            )
                        } else if (ActivityType.getActivityType(item.first!!.activityType)
                            == ActivityType.ONE_TO_MANY || ActivityType.getActivityType(item.first!!.activityType)
                            == ActivityType.D_TO_ONE
                        ) {
                            if (ActivityType.getActivityType(item.first!!.activityType)
                                == ActivityType.D_TO_ONE
                            ) {
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalDToOneQuestionDetailsFragment(
                                        item.first!!,
                                        getPlayGroup(),
                                        null
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
                        if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.ONE_TO_ONE) {
                            askQuestionToFriends(item)
                        }
                    } else if (getString(R.string.click_action_send_now) == item.third) {
                        if (ActivityType.getActivityType(item.first!!.activityType)
                            == ActivityType.MESSAGE
                        ) {
                            TaskAndMessageComposeDialogFragment.getInstance(item.first!!) { item1: Pair<LoginDayActivityInfoList?, UploadPostResponse?>? ->
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                        item.first!!,
                                        playGroup
                                    )
                                )
                            }
                                .show(
                                    requireActivity().supportFragmentManager,
                                    "TaskMessageCompose"
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
                        if ((ActivityType.getActivityType(item.first!!.activityType)
                                    == ActivityType.D_TO_ONE) || (ActivityType.getActivityType(item.first!!.activityType)
                                    == ActivityType.ONE_TO_ONE) || (ActivityType.getActivityType(
                                item.first!!.activityType
                            )
                                    == ActivityType.ONE_TO_MANY)
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
                                item.first!!,
                                getPlayGroup()
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
                                    item1.first!!,
                                    playGroup
                                )
                            )
                        }
                            .show(requireActivity().supportFragmentManager, "TaskMessageCompose")
                    } else if (getString(R.string.click_action_i_will_do_it) == item.third) {
                        if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.TASK) {
                            val createGameActivityRequest = CreateGameActivityRequest()
                            createGameActivityRequest.serviceName = "createGameActivity"
                            createGameActivityRequest.screenId = "12"
                            createGameActivityRequest.loginDay = item.first!!.loginDay
                            createGameActivityRequest.activityId = item.first!!.activityId
                            createGameActivityRequest.activityTypeId = item.first!!.activityTypeId
                            questionGamesViewModel!!.createGameActivity(createGameActivityRequest)
                                .observe(
                                    viewLifecycleOwner
                                ) { apiResponse: ApiResponse<GameActivityUpdateResponse> ->
                                    if (apiResponse.loading) {
                                        showProgress()
                                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                                        hideProgress()
                                        if (apiResponse.body.errorCode.equals(
                                                "000",
                                                ignoreCase = true
                                            )
                                        ) {
                                            showAlert(apiResponse.body.message) {
                                                if (!TextUtils.isEmpty(apiResponse.body.participationStatus)) item.first!!.userParticipationStatus =
                                                    apiResponse.body.participationStatus
                                                if (!TextUtils.isEmpty(apiResponse.body.activityTagStatus)) item.first!!.activityTag =
                                                    apiResponse.body.activityTagStatus
                                                soloGamesAdapter!!.notifyDataSetChanged()
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
                            showAlert("Unknown Action " + item.first!!.activityType)
                        }
                    } else if (getString(R.string.click_action_all_answers) == item.third) {
                        if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.ONE_TO_MANY) {
                            if (getPlayGroup().playGroupId.equals("AAA", ignoreCase = true)
                                || getPlayGroup().playGroupId.equals("ZZZ", ignoreCase = true)
                                || getPlayGroup().playGroupId.equals("YYY", ignoreCase = true)
                            ) {
                                questionGamesViewModel!!.getActivityGroups(item.first!!.activityId!!)
                                    .observe(
                                        viewLifecycleOwner
                                    ) { apiResponse: ApiResponse<GetActivityGroupsResponse> ->
                                        if (apiResponse.loading) {
                                            showProgress()
                                        } else if (apiResponse.isSuccess && apiResponse.body != null) {
                                            hideProgress()
                                            if (apiResponse.body.errorCode.equals(
                                                    "000",
                                                    ignoreCase = true
                                                )
                                            ) {
                                                if (apiResponse.body.activityGroupList != null && apiResponse.body.activityGroupList!!.isNotEmpty()) {
                                                    if (apiResponse.body.activityGroupList!!.size == 1) {
                                                        val activityGroupList =
                                                            apiResponse.body.activityGroupList!![0]
                                                        val group = PlayGroup()
                                                        group.playGroupId = if ("0".equals(
                                                                activityGroupList.groupId,
                                                                ignoreCase = true
                                                            )
                                                        ) "AAA" else activityGroupList.groupId
                                                        if (!TextUtils.isEmpty(activityGroupList.activityGameResponseId)) {
                                                            item.first!!.activityGameResponseId =
                                                                activityGroupList.activityGameResponseId
                                                        }
                                                        if (!TextUtils.isEmpty(activityGroupList.groupId) && !activityGroupList.groupId.equals(
                                                                "0",
                                                                ignoreCase = true
                                                            )
                                                        ) {
                                                            group.playGroupName =
                                                                activityGroupList.groupName
                                                            group.isDummyGroup = false
                                                        } else {
                                                            group.playGroupName =
                                                                playGroup.playGroupName
                                                            group.isDummyGroup =
                                                                playGroup.isDummyGroup
                                                        }
                                                        navigateToDetails(item.first!!, group)
                                                    } else {
                                                        showGroupSelectionPopup(
                                                            apiResponse.body.activityGroupList!!,
                                                            item.first!!
                                                        )
                                                    }
                                                } else {
                                                    navigateToDetails(item.first!!, getPlayGroup())
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
                        } else if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.D_TO_ONE) {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalDToOneQuestionDetailsFragment(
                                    item.first!!,
                                    getPlayGroup(),
                                    null
                                )
                            )
                        } else if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.ONE_TO_ONE) {
                            //                        AppUtilities.showLog("###", "onItemClick: " + item.first.hashCode());
                            //                        AppUtilities.showLog("###", "onItemClick: " + item.first);
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavOneToOneQuestionDetails(
                                    item.first!!,
                                    getPlayGroup(),
                                    null
                                )
                            )
                        } else {
                            showAlert("Unknown Action " + item.first!!.activityType)
                        }
                    } else if (getString(R.string.click_action_send_to_friends) == item.third) {
                        if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.CARD) {
                            inviteFriends(item.first!!)
                        } else {
                            showAlert("Unknown Action " + item.first!!.activityType)
                        }
                    } else if (getString(R.string.click_action_all_cards) == item.third) {
                        if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.CARD) {
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavFunCards(
                                    linkUserId
                                )
                            )
                        } else {
                            showAlert("Unknown Action " + item.first!!.activityType)
                        }
                    } else if (getString(R.string.click_action_see_stats) == item.third) {
                        if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.SURVEY) {
                            navController.navigate(
                                DashboardFragmentDirections.actionNavHomeToNavAnonymousSurvey(
                                    item.first!!.activityId!!,
                                )
                            )
                        } else {
                            showAlert("Unknown Action " + item.first!!.activityType)
                        }
                    }
                    PlaySoloGamesAdapter.CLICK_WHOLE_ITEM -> if (ActivityType.getActivityType(item.first!!.activityType)
                        == ActivityType.MESSAGE && item.first!!.userParticipationStatus == "None"
                    ) {
                        showAlert("First commit to sending the message. You will then have 15 days to send the message")
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
                    PlaySoloGamesAdapter.CLICK_SHARE -> if (item.first!!.activityType.equals(
                            "Card",
                            ignoreCase = true
                        )
                    ) {
                        inviteFriends(item.first!!)
                    }
                }
            }
        }
    }

    private fun navigateToAnswer(first: LoginDayActivityInfoList, playGroup: PlayGroup) {

        AnswerQuestionDialogFragment.getInstance(
            first,
            playGroup
        ) { apiResponse: UploadPostResponse? ->
            if (!TextUtils.isEmpty(apiResponse!!.participationStatus)) first.userParticipationStatus =
                apiResponse.participationStatus
            if (!TextUtils.isEmpty(apiResponse.activityTagStatus)) first.activityTag =
                apiResponse.activityTagStatus
            if (ActivityType.getActivityType(first.activityType)
                == ActivityType.ONE_TO_MANY
            ) {
                navigateToDetails(first, playGroup)
            }
        }.show(requireActivity().supportFragmentManager, "Answer")

    }

    private fun getPlayGroup(): PlayGroup {
        val playGroupTemp: PlayGroup
        return if (playGroup.playGroupId.equals("AAA", ignoreCase = true)
            || playGroup.playGroupId.equals("FFF", ignoreCase = true)
            || playGroup.playGroupId.equals("YYY", ignoreCase = true)
            || playGroup.playGroupId.equals("ZZZ", ignoreCase = true)
        ) {
            playGroupTemp = playGroup
            playGroupTemp.playGroupId = "AAA"
            playGroupTemp
        } else {
            playGroup
        }
    }

    private fun showGroupSelectionPopup(
        playGroupList: List<ActivityGroupList>,
        first: LoginDayActivityInfoList?
    ) {
        SelectPlayGroupDialogFragment.getInstance(playGroupList) { activityGroupList: ActivityGroupList ->
            val group = PlayGroup()
            group.playGroupId = activityGroupList.groupId
            if (!TextUtils.isEmpty(activityGroupList.activityGameResponseId)) {
                first!!.activityGameResponseId = activityGroupList.activityGameResponseId
            }
            if (!TextUtils.isEmpty(activityGroupList.groupId) && !activityGroupList.groupId.equals(
                    "0",
                    ignoreCase = true
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

    private fun askQuestionToFriends(item: Triple<LoginDayActivityInfoList?, Int?, String?>?) {
        var isActivityForNewQuestion = false
        if (playGroup.playGroupId.equals("YYY", ignoreCase = true)) {
            isActivityForNewQuestion = true
        }
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavAskQuestionsSheet(
                item!!.first!!,
                isActivityForNewQuestion,
                playGroup.playGroupId?:""?:""
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
                    soloGamesAdapter!!.notifyDataSetChanged()
                    val fragment = SendNowOrSendLaterDialogFragment.getInstance(
                        apiResponse.body.message,
                        activityInfoList
                    ) { pair: android.util.Pair<LoginDayActivityInfoList?, Int> ->
                        if (pair.second == 1) {
                            TaskAndMessageComposeDialogFragment.getInstance(pair.first) { item1: Pair<LoginDayActivityInfoList?, UploadPostResponse?>? ->
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                        pair.first!!,
                                        playGroup
                                    )
                                )
                            }
                                .show(
                                    requireActivity().supportFragmentManager,
                                    "TaskMessageCompose"
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
            binding.fab.visibility = View.GONE
            binding.fabOClub.visibility = View.GONE
        } else {
            if (playGroup.playGroupId.equals("FFF", ignoreCase = true)
                || playGroup.playGroupId.equals("AAA", ignoreCase = true)
                || playGroup.playGroupId.equals("ZZZ", ignoreCase = true)
                || playGroup.playGroupId.equals("YYY", ignoreCase = true)
            ) {
                binding.fab.visibility = View.VISIBLE
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
                questionGamesViewModel!!.questionFilterInfo().observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetQuestionFilterInfoResponse> ->
                    if (apiResponse.isSuccess && apiResponse.body != null) {
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
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
                        }
                    }
//            } else {
//                binding.ibSearch.setVisibility(View.GONE);
//                binding.cardSearch.setVisibility(View.GONE);
                }
            }
        }
    }


    private fun inviteFriends(first: LoginDayActivityInfoList?) {
        viewModel.getUserLinkInfo(
            first!!.activityType,
            first.activityId!!,
            "48",
            first.activityText
        ).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetUserLinkInfoResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
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
                        share(
                            apiResponse.body.title,
                            apiResponse.body.shortLink,
                            apiResponse.body.activityImageUrl, "2"
                        )
                    } else if (!TextUtils.isEmpty(first.activityImageLargeUrl)) {
                        share(
                            apiResponse.body.title,
                            apiResponse.body.shortLink,
                            apiResponse.body.activityImageUrl,
                            "1"
                        )
                    } else {
                        share(
                            apiResponse.body.title,
                            apiResponse.body.shortLink, "", "-1"
                        )
                    }
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (com.onourem.android.activity.BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getUserLinkInfo")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getUserLinkInfo",
                        apiResponse.code.toString()
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
//    private fun shortLink(
//        title: String,
//        linkUserId: String,
//        linkMsg: String,
//        first: LoginDayActivityInfoList?,
//        linkType: String,
//        activityImageUrl: String,
//        activityText: String
//    ) {
//        showProgress()
//        val titleSocial: String
//        val description: String
//        var imageUrl: String? = ""
//        description = if (linkType.equals("Card", ignoreCase = true)) {
//            "Fun cards to share with friends"
//        } else if (linkType.equals("1toM", ignoreCase = true)) {
//            "Fun questions to ask friends"
//        } else {
//            "A place for good friends"
//        }
//        if (!TextUtils.isEmpty(first!!.activityImageUrl)) {
//            imageUrl = first.activityImageUrl
//        }
//        titleSocial = first.activityText!!
//        val builderSocialMeta = DynamicLink.SocialMetaTagParameters.Builder()
//            .setTitle(titleSocial)
//            .setImageUrl(Uri.parse(imageUrl))
//            .setDescription(description)
//            .build()
//        val navigationInfoParameters = DynamicLink.NavigationInfoParameters.Builder()
//        navigationInfoParameters.setForcedRedirectEnabled(true)
//        FirebaseDynamicLinks.getInstance().createDynamicLink()
//            .setLink(Uri.parse("https://www.onourem.com/?linkUserId=$linkUserId&linkType=$linkType"))
//            .setDomainUriPrefix("https://e859h.app.goo.gl")
//            .setAndroidParameters(
//                DynamicLink.AndroidParameters.Builder("com.onourem.android.activity") //                                .setMinimumVersion(125)
//                    .build()
//            )
//            .setSocialMetaTagParameters(builderSocialMeta)
//            .setIosParameters(
//                DynamicLink.IosParameters.Builder("com.onourem.onoureminternet")
//                    .setAppStoreId("1218240628") //                                .setMinimumVersion("1.0.1")
//                    .build()
//            ) // Set parameters
//            // ...
//            .setNavigationInfoParameters(navigationInfoParameters.build())
//            .buildShortDynamicLink()
//            .addOnCompleteListener(requireActivity()) { task: Task<ShortDynamicLink> ->
//                if (task.isSuccessful) {
//                    // Short link created
//                    val shortLink = Objects.requireNonNull(task.result)!!.shortLink
//                    //Log.d("shortLink", shortLink.toString())
//                    viewModel.updateAppShortLink(linkUserId, shortLink.toString()).observe(
//                        viewLifecycleOwner
//                    ) { response: ApiResponse<StandardResponse> ->
//                        if (response.loading) {
//                            showProgress()
//                        } else if (response.isSuccess && response.body != null) {
//                            hideProgress()
//                            if (response.body.errorCode.equals("000", ignoreCase = true)) {
////                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
////                                    shareIntent.setType("text/plain");
////                                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem");
////                                    String shareMessage = linkMsg + " \n" + shortLink.toString();
////                                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
////                                    startActivityForResult(Intent.createChooser(shareIntent, title), 1212);
////
//                                if (!TextUtils.isEmpty(first.questionVideoUrl)) {
//                                    share(title, linkMsg, shortLink!!, first.questionVideoUrl!!, 2)
//                                } else if (!TextUtils.isEmpty(first.activityImageLargeUrl)) {
//                                    share(
//                                        title,
//                                        linkMsg,
//                                        shortLink!!,
//                                        first.activityImageLargeUrl!!,
//                                        1
//                                    )
//                                } else {
//                                    share(title, linkMsg, shortLink!!, "", -1)
//                                }
//                            } else {
//                                showAlert(response.body.errorMessage)
//                            }
//                        } else {
//                            hideProgress()
//                            showAlert(response.errorMessage)
//                            if (response.errorMessage != null
//                                && (response.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
//                                        || response.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
//                            ) {
//                                if (com.onourem.android.activity.BuildConfig.DEBUG) {
//                                    AppUtilities.showLog("Network Error", "updateAppShortLink")
//                                }
//                                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
//                                    "updateAppShortLink",
//                                    response.code.toString()
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//    }

    private fun share(
        title: String?,
        linkMsg: String?,
        mediaUrl: String?,
        mediaType: String?,
    ) {
//        if (!mediaUrl.equals("", ignoreCase = true)) {
//            navController.navigate(
//                MobileNavigationDirections.actionGlobalNavMediaShare(
//                    title?:"",
//                    linkMsg?:"",
//                    mediaUrl?:"",
//                    mediaType?:""
//                )
//            )
//        } else {
//            val shareIntent = Intent(Intent.ACTION_SEND)
//            shareIntent.type = "text/plain"
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
//            shareIntent.putExtra(Intent.EXTRA_TEXT, linkMsg)
//            try {
//                fragmentContext.startActivity(Intent.createChooser(shareIntent,title));
//            } catch (ex : ActivityNotFoundException) {
//                Toast.makeText(fragmentContext,"NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
//            }
//        }

        navController.navigate(
            MobileNavigationDirections.actionGlobalNavShareContent(
                title ?: "",
                linkMsg ?: "",
                mediaUrl ?: "",
                mediaType ?: ""
            )
        )
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

    private fun sendPushNotificationToLinkUser() {
        viewModel.sendPushNotificationToLinkUser(linkUserId, "8")
            .observe(viewLifecycleOwner) { apiResponse1: ApiResponse<SendPushNotificationToLinkUserResponse> ->
                if (apiResponse1.loading) {
                    //  showProgress();
                } else if (apiResponse1.isSuccess && apiResponse1.body != null) {
                    // hideProgress();
                    if (apiResponse1.body.errorCode.equals("000", ignoreCase = true)) {
                        linkUserId = ""
                        isLinkVerified = "false"
                        preferenceHelper!!.putValue(Constants.KEY_LINK_USER_ID, linkUserId)
                        preferenceHelper!!.putValue(Constants.KEY_IS_LINK_VERIFIED, isLinkVerified)
                        if (apiResponse1.body.message != null && !apiResponse1.body.message.equals(
                                "",
                                ignoreCase = true
                            )
                        ) {
                            showAlert(apiResponse1.body.message)
                        }
                    } else {
                        showAlert(apiResponse1.body.errorMessage)
                    }
                } else {
                    // hideProgress();
                    showAlert(apiResponse1.errorMessage)
                    if (apiResponse1.errorMessage != null
                        && (apiResponse1.errorMessage.contains(getString(R.string.unable_to_connect_host_message))
                                || apiResponse1.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse1.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (com.onourem.android.activity.BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "sendPushNotificationToLinkUser")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "sendPushNotificationToLinkUser",
                            apiResponse1.code.toString()
                        )
                    }
                }
            }
    }


}