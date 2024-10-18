package com.onourem.android.activity.ui.games.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentPlayGamesBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.YoutubeActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.adapters.activity.ActivityGamesAdapter
import com.onourem.android.activity.ui.games.dialogs.SendNowOrSendLaterDialogFragment
import com.onourem.android.activity.ui.games.viewmodel.GamesReceiverViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.survey.viewmodel.SurveyViewModel
import com.onourem.android.activity.ui.utils.*
import com.onourem.android.activity.ui.utils.listners.FriendsPlayingPaginationListener
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlayGamesFragment :
    AbstractBaseViewModelBindingFragment<QuestionGamesViewModel, FragmentPlayGamesBinding>(),
    OnItemClickListener<Triple<LoginDayActivityInfoList?, Int?, String?>?> {

    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false

    private var recyclerViewState: Parcelable? = null
    private lateinit var loginUserId: String
    private val activityStatusList: MutableList<ActivityStatusList>? = ArrayList()
    private val gameResIdList: MutableList<String>? = ArrayList()
    private val loginDayActivityInfoList: MutableList<LoginDayActivityInfoList>? = ArrayList()

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var playGroup: PlayGroup? = null
    private var soloGamesAdapter: ActivityGamesAdapter? = null
    private var gamesReceiverViewModel: GamesReceiverViewModel? = null
    private var layoutManager: LinearLayoutManager? = null
    private var userActionViewModel: UserActionViewModel? = null
    private var isDataLoading = false
    private var isFrom = ""
    private var dashboardViewModel: DashboardViewModel? = null
    private var surveyViewModel: SurveyViewModel? = null
    private var selectedFilterIndex = 0


    override fun viewModelType(): Class<QuestionGamesViewModel> {
        return QuestionGamesViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        AppUtilities.showLog("###", "onCreate: called");
        gamesReceiverViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            GamesReceiverViewModel::class.java
        )
        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            UserActionViewModel::class.java
        )
        dashboardViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            DashboardViewModel::class.java
        )

        surveyViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            SurveyViewModel::class.java
        )
        viewModel.reloadUI().observe(this) { reload: Boolean ->
            if (reload) {
                //AppUtilities.showLog("PlayGamesFragment", "reloadUI" + counterRefreshUI++);
                refreshUI()
            }
        }
        viewModel.getGameActivityUpdateStatus().observe(this) { item ->
            if (item != null) {
                viewModel.setGameActivityUpdateStatus(null)
                if (!(playGroup!!.playGroupId
                        .equals("AAA", ignoreCase = true) || playGroup!!.playGroupId
                        .equals("ZZZ", ignoreCase = true)) && playGroup!!.playGroupId
                        .equals(item.first, ignoreCase = true)
                ) {
                    if (loginDayActivityInfoList!!.isEmpty()) {
//                        activityStatusList.clear();
                        if (!isDataLoading) loadData(false, true)
                    } else if (!loginDayActivityInfoList.contains(item.second)) {
                        loginDayActivityInfoList.clear()
                        activityStatusList!!.clear()
                        soloGamesAdapter!!.clearData()
                        refreshUI()
                        //                        if (!isDataLoading)
//                            loadData(false, true);
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
        viewModel.inAppReviewPopup.observe(this) { show: Boolean ->
            if (show) {
                val todayAnswerCount: Int =
                    preferenceHelper!!.getInt(Constants.KEY_TOTAL_QUES_ANSWERED_TODAY)
                val totalAnswerCount: Int =
                    preferenceHelper!!.getInt(Constants.KEY_TOTAL_QUES_ANSWERED_BEFORE_TODAY)
                val noOfTimeRequestReviewRaised: Int =
                    preferenceHelper!!.getInt(Constants.KEY_NUMBER_OF_TIME_APP_REVIEW_REQUESTS_RAISED)
                var noOfDaysAfterShownReviewLastTime: Long = 0L
                val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                var lastTimeReviewShownDate: String =
                    preferenceHelper!!.getString(Constants.KEY_LAST_TIME_WHEN_REVIEW_POPUP_WAS_SHOWN)
                if (lastTimeReviewShownDate.equals("", ignoreCase = true)) {
                    lastTimeReviewShownDate = "2000-01-01 01:01:01"
                }
                try {
                    val date: Date? = sdf.parse(lastTimeReviewShownDate)
                    if (date != null) {
                        noOfDaysAfterShownReviewLastTime = getDaysBetweenDates(Date(), date)
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                if ((todayAnswerCount >= 3) && (totalAnswerCount > 20) && (noOfDaysAfterShownReviewLastTime > 15) && (noOfTimeRequestReviewRaised < 3)) {
                    dashboardViewModel!!.setShowInAppReview(true)
                }
            }
        }
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_play_games
    }

    private fun setTextViewDrawableColor(textView: TextView, color: Int) {
        for (drawable: Drawable? in textView.compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(textView.context, color),
                    PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var playGamesFragmentArgs: PlayGamesFragmentArgs? = null
        playGamesFragmentArgs = PlayGamesFragmentArgs.fromBundle(requireArguments())
        isFrom = playGamesFragmentArgs.isFrom
        layoutManager = LinearLayoutManager(requireActivity())

//        AppUtilities.showLog("###", "onViewCreated: called ");

        binding.fabOClub.visibility = View.GONE

        viewModel.getActionMutableLiveData().observe(viewLifecycleOwner) { action ->
            if (action == null) return@observe
            val actionType: ActionType? = action.actionType
            if ((actionType == null) || (actionType == ActionType.NONE) || ((navController.currentDestination == null) || (navController.currentDestination!!.id != R.id.nav_play_games))
            ) return@observe
            viewModel.actionConsumed()
            if (actionType == ActionType.CREATE_QUESTION) {
                navController.navigate(
                    PlayGamesFragmentDirections.actionNavPlayGamesToNavCreateOwnQuestion(
                        playGroup!!
                    )
                )
            }
        }

        viewModel.refreshEditedItem.observe(viewLifecycleOwner) { item: LoginDayActivityInfoList? ->
            if (item != null && soloGamesAdapter != null) {
                soloGamesAdapter!!.notifyDataUpdated(item)
            }
        }

        viewModel.updateItem().observe(viewLifecycleOwner) { item: LoginDayActivityInfoList? ->
            if (item != null && soloGamesAdapter != null) {
                soloGamesAdapter!!.removeItem(item)
            }
        }

        playGroup = playGamesFragmentArgs.selectedGroup
        loginUserId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)

        if (soloGamesAdapter == null) {
            this@PlayGamesFragment.isLoading = false;
            this@PlayGamesFragment.isLastPage = false;
            soloGamesAdapter = ActivityGamesAdapter(
                playGroup!!,
                loginDayActivityInfoList,
                preferenceHelper!!,
                this,
                null,
                dashboardViewModel!!,
                viewLifecycleOwner,
                navController,
                alertDialog
            )
        }
        binding.rvPlayGames.layoutManager = layoutManager
        binding.rvPlayGames.adapter = soloGamesAdapter

        gamesReceiverViewModel!!.getGameActivityUpdateResponseLiveData().observe(
            viewLifecycleOwner
        ) { activityResponse ->
            if (activityResponse != null) {
                gamesReceiverViewModel!!.setGameActivityUpdateResponseLiveData(null)
                if (soloGamesAdapter != null) {
                    soloGamesAdapter!!.notifyDataSetChanged()
                }
            }
        }

//        binding.rvPlayGames.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (dy < 0) binding.fab.extend() else if (dy > 0) binding.fab.shrink()
//            }
//        })
//        binding.rvPlayGames.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (dy < 0) binding.fabOClub.extend() else if (dy > 0) binding.fabOClub.shrink()
//            }
//        })
        binding.rvPlayGames.addOnScrollListener(object : FriendsPlayingPaginationListener(layoutManager!!) {
            override fun loadMoreItems() {

                //TODO change
//                val layoutManager =  binding.rvPlayGames.layoutManager as LinearLayoutManager;
//                var totalItemCount = 0
//                totalItemCount = layoutManager.itemCount;
//                val lastVisible = layoutManager.findLastVisibleItemPosition();
//
//                val endHasBeenReached = lastVisible + 2 >= totalItemCount;
//                if (totalItemCount > 0 && endHasBeenReached) {
//                    //you have reached to the bottom of your recycler view
//                    this@PlayGamesFragment.isLoading = true;
//                    loadMoreGames();
//                }
                this@PlayGamesFragment.isLoading = true;
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@PlayGamesFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@PlayGamesFragment.isLoading
            }
        })

        binding.swipeRefreshLayout.setOnRefreshListener {
            isDataLoading = false
            refreshUI()
        }

        binding.fab.setOnClickListener(ViewClickListener { v: View? ->
            navController.navigate(
                PlayGamesFragmentDirections.actionNavPlayGamesToNavCreateOwnQuestion(
                    (playGroup)!!
                )
            )
        })

        loadData(true, false)

        binding.fabOClub.setOnClickListener(ViewClickListener { v: View? ->
            // viewModel.actionCameFromDashboard(false);
            navController.navigate(
                PlayGamesFragmentDirections.actionNavPlayGamesToNavQuestionGames(
                    null,
                    "solo"
                )
            )
        })

        userActionViewModel!!.getActionMutableLiveData().observe(viewLifecycleOwner) { action ->
            if (action != null && action.actionType !== ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()
                if (action.actionType === ActionType.EDIT_QUESTION) {
                    editQuestion(action)
                }
                if (action.actionType === ActionType.DELETE_QUESTION) {
                    deleteQuestion(action)
                }
                if (action.actionType === ActionType.IGNORE_QUESTION) {
                    ignoreQuestion(action)
                }
                if (action.actionType === ActionType.DELETE_QUESTION_CONFIRMATION) {
                    deleteQuestionConfirmed(action)
                }
                if (action.actionType === ActionType.IGNORE_QUESTION_CONFIRMATION) {
                    ignoreOClubQuestionConfirmed(action)
                }
            }
        }

//        viewModel.getActionCameFromDashboard().observe(getViewLifecycleOwner(), action -> {
//            isCameFromDashboard = action;
//        });
    }

    private fun getDaysBetweenDates(d1: Date, d2: Date): Long {
        return TimeUnit.MILLISECONDS.toDays(d1.time - d2.time)
    }

    private fun editQuestion(action: Action<Any?>?) {
        val first = action!!.data as LoginDayActivityInfoList
        navController.navigate(PlayGamesFragmentDirections.actionNavPlayGamesToNavEditQuestion(first))
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
                bundle,
                ""
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
                bundle,
                ""
            )
        )
    }

    private fun ignoreQuestion(first: LoginDayActivityInfoList?) {
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

    private fun deleteQuestionConfirmed(action: Action<Any?>?) {
        val first = action!!.data as LoginDayActivityInfoList
        viewModel.deleteQuestion((first.activityId)!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<EditQuestionResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        if (apiResponse.body.message != null && !apiResponse.body.message
                                .equals("", ignoreCase = true)
                        ) {
                            showAlert(apiResponse.body.message)
                        } else {
                            if (soloGamesAdapter != null) soloGamesAdapter!!.removeItem(first)
                        }
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                    if ((apiResponse.errorMessage != null
                                && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
                    ) {
                        if (BuildConfig.DEBUG) {
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

    private fun ignoreQuestionConfirmed(action: Action<Any?>?) {
        val first = action!!.data as LoginDayActivityInfoList
        if (first.activityType.equals("1toM", ignoreCase = true)) {
            ignoreGame(
                first.gameId,
                first.activityGameResponseId,
                first.activityId,
                first.activityId,
                first
            )
        }
    }

    private fun ignoreOClubQuestionConfirmed(action: Action<Any?>?) {
        val first = action!!.data as LoginDayActivityInfoList
        ignoreGame(
            first.gameId,
            first.activityGameResponseId,
            first.playgroupId,
            first.activityId,
            first
        )
    }

    private fun activityPlayGroupId(): String? {
        var activityPlayGroupId: String? = null
        if (playGroup != null) {
            val playGroupId = playGroup!!.playGroupId
            if (playGroupId.equals("FFF", ignoreCase = true)) {
                activityPlayGroupId = "0"
            } else if (playGroupId.equals("AAA", ignoreCase = true)) {
                activityPlayGroupId = "1"
            } else if (playGroupId.equals("YYY", ignoreCase = true)) {
                activityPlayGroupId = "2"
            } else if (playGroupId.equals("ZZZ", ignoreCase = true)) {
                activityPlayGroupId = "3"
            } else {
                activityPlayGroupId = playGroupId
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
        viewModel.ignoreOClubActivityForPlaygroup(gameId, activityGameResId, playGroupId, activityId, first?.oClubActivityId)
            .observe(
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
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                    if ((apiResponse.errorMessage != null
                                && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "ignoreOneToManyGameActivity")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "ignoreOneToManyGameActivity",
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

    private fun loadData(showProgress: Boolean, refreshing: Boolean) {
        isDataLoading = true
        //AppUtilities.showLog("PlayGamesFragment", "loadData " + counterLoadData++);
        binding.swipeRefreshLayout.isRefreshing = refreshing
//        this@PlayGamesFragment.isLastPage = false
//        this@PlayGamesFragment.isLoading = false
        if (playGroup != null) {
            if (playGroup!!.newMemeberNumber > 0) {
                if (playGroup!!.newMemeberNumber == 1) {
                    binding.tvSeeMembers.text =
                        String.format("%s New Member", playGroup!!.newMemeberNumber)
                } else if (playGroup!!.newMemeberNumber > 1) {
                    binding.tvSeeMembers.text =
                        String.format("%s New Members", playGroup!!.newMemeberNumber)
                }
                binding.tvSeeMembers.setTextColor(
                    ContextCompat.getColor(
                        fragmentContext,
                        R.color.color_white
                    )
                )
                binding.tvSeeMembers.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                        fragmentContext, R.drawable.shape_oval_rounded_corner
                    )
                )
                setTextViewDrawableColor(
                    binding.tvSeeMembers, ContextCompat.getColor(
                        fragmentContext, R.color.color_blue
                    )
                )
            } else {
                if (playGroup!!.memberCount != null && !playGroup!!.memberCount.equals(
                        "",
                        ignoreCase = true
                    )
                ) {
                    if (playGroup!!.memberCount!!.toInt() == 1) {
                        binding.tvSeeMembers.text =
                            String.format("%s Member", playGroup!!.memberCount)
                    } else if (playGroup!!.memberCount != null && playGroup!!.memberCount!!.toInt() > 1) {
                        binding.tvSeeMembers.text =
                            String.format("%s Members", playGroup!!.memberCount)
                    }
                }
                //binding.tvSeeMembers.setText(getString(R.string.action_label_see_members));
            }
        }
        binding.tvSeeMembers.visibility = View.VISIBLE
        binding.tvSeeMembers.setOnClickListener(ViewClickListener { v: View? ->
            viewModel.setPlayGroup(playGroup!!)
            navController.navigate(PlayGamesFragmentDirections.actionNavPlayGamesToNavPlayGroupMember())
        })
        if (refreshing || (loginDayActivityInfoList == null) || loginDayActivityInfoList.isEmpty()) {
            AppUtilities.showLog(
                "***PlayGames",
                "getUserActivityGroupInfo" + playGroup!!.playGroupId
            )
            viewModel.getUserActivityGroupInfo(playGroup!!.playGroupId).observe(
                viewLifecycleOwner
            ) { responseApiResponse: ApiResponse<UserActivityInfoResponse> ->
                handleResponse(
                    responseApiResponse,
                    false,
                    showProgress,
                    "getUserActivityGroupInfo"
                )
            }
        } else {
            setAdapter()
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
        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            isDataLoading = false
            hideProgress()
            if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing =
                false
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                if (!isSolo) {
                    viewModel.updateActivityMemberNumber(playGroup!!.playGroupId).observe(
                        viewLifecycleOwner
                    ) { }
                }
                if (apiResponse.body.activityStatusList.isNotEmpty()) {
                    activityStatusList!!.clear()
                    activityStatusList.addAll(apiResponse.body.activityStatusList)
                }
                if (apiResponse.body.gameResIdList.isNotEmpty()) {
                    gameResIdList!!.clear()
                    gameResIdList.addAll(apiResponse.body.gameResIdList)
                }
                loginDayActivityInfoList!!.clear()
                if (soloGamesAdapter != null) soloGamesAdapter!!.notifyDataSetChanged()

                loginDayActivityInfoList.addAll(apiResponse.body.loginDayActivityInfoList)
                if (soloGamesAdapter != null) soloGamesAdapter?.notifyDataSetChanged()
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
            if ((apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                        || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
            ) {
                if (BuildConfig.DEBUG) {
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
        if (loginDayActivityInfoList != null && loginDayActivityInfoList.isNotEmpty()) {
            if (soloGamesAdapter == null) {
                soloGamesAdapter = ActivityGamesAdapter(
                    playGroup!!,
                    loginDayActivityInfoList,
                    preferenceHelper!!,
                    this,
                    null,
                    dashboardViewModel!!,
                    viewLifecycleOwner,
                    navController,
                    alertDialog
                )
                binding.rvPlayGames.adapter = soloGamesAdapter
            } else {
                soloGamesAdapter!!.updateItems(loginDayActivityInfoList)
            }
            binding.tvMessage.visibility = View.GONE
            binding.rvPlayGames.visibility = View.VISIBLE
            //            soloGamesAdapter.updateItems(loginDayActivityInfoList);
        } else {
            var footerMessage: String = ""
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
            if (playGroup!!.playGroupId.equals("FFF", ignoreCase = true)) {
                footerMessage =
                    "You do not have questions in this section today. Here you see questions that you or your friends are playing."
            } else if (playGroup!!.playGroupId.equals("AAA", ignoreCase = true)) {
                footerMessage =
                    "No new questions for today. Try other filters or create your question and ask friends."
            } else if (playGroup!!.playGroupId.equals("ZZZ", ignoreCase = true)) {
                footerMessage =
                    "This section shows the questions you have answered. You have not answered any so far."
            } else if (playGroup!!.playGroupId.equals("YYY", ignoreCase = true)) {
                footerMessage =
                    "You have not created any questions. Use the bottom left pen icon to create a question and ask friends."
            } else {
                footerMessage =
                    "No one has asked any question in this O-Club so far. Ask a question to start having fun."
            }
            binding.tvMessage.text = footerMessage
            binding.tvMessage.visibility = View.VISIBLE
            binding.rvPlayGames.visibility = View.GONE
        }


//        if ((fragmentContext as DashboardActivity).getPlayGamesRecyclerViewPosition() != -1) layoutManager!!.scrollToPositionWithOffset(
//            (fragmentContext as DashboardActivity).getPlayGamesRecyclerViewPosition(),
//            (fragmentContext as DashboardActivity).getPlayGamesRecyclerViewPositionTopView()
//        )

        if (recyclerViewState != null) {
            binding.rvPlayGames.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }

    }

    private fun loadMoreGames() {
        if (playGroup != null) {
            if (gameResIdList == null || gameResIdList.isEmpty()) {
                this@PlayGamesFragment.isLastPage = true
                this@PlayGamesFragment.isLoading = false
                setFooterMessage()
                return
            }
            val start = soloGamesAdapter!!.itemCount
            val end = gameResIdList.size
            if (start >= end) {
                this@PlayGamesFragment.isLastPage = true
                this@PlayGamesFragment.isLoading = false
                setFooterMessage()
                return
            }

//            List<Integer> ids = new ArrayList<>();
//            List<String> tags = new ArrayList<>();
//            List<String> status = new ArrayList<>();
//            List<String> reason = new ArrayList<>();
//            List<Integer> friends = new ArrayList<>();
            val responseIds: MutableList<String> = ArrayList()
            //            for (int i = start; i < end; i++) {
//                ActivityStatusList item = activityStatusList.get(i);
//                ids.add(item.getActivityId());
//                friends.add(item.getFriendCount());
//                tags.add(item.getActivityTag());
//                status.add(item.getUserParticipationStatus());
//                reason.add(item.getActivityReason());
//                responseIds.add(item.getActivityResponseId());
//
//            }
            for (i in start until end) {
                val item = gameResIdList[i]
                responseIds.add(item)
            }
            val userActivityRequest = UserActivityRequest()
            //            userActivityRequest.setActivityIds(Utilities.getTokenSeparatedString(ids, ","));
//            userActivityRequest.setActivityTags(Utilities.getTokenSeparatedString(tags, ","));
//            userActivityRequest.setUserParticipationStatus(Utilities.getTokenSeparatedString(status, ","));
//            userActivityRequest.setActivityReason(Utilities.getTokenSeparatedString(reason, "@"));
            userActivityRequest.playGroupId = activityPlayGroupId()
            userActivityRequest.activityGameResponseId = Utilities.getTokenSeparatedString(responseIds, ",")
            //Utilities.getTokenSeparatedString(responseIds, ",")
            viewModel.getNextUserActivityGroup(userActivityRequest)
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserActivityResponse> ->
                    if (apiResponse.loading) {
                        if (soloGamesAdapter != null) {
                            soloGamesAdapter!!.addLoading()
                        }
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        if (soloGamesAdapter != null) {
                            soloGamesAdapter!!.removeLoading()
                        }
                        this@PlayGamesFragment.isLoading = false
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                        AppUtilities.showLog("PlayGamesFragment", "LoginDayActivityInfoList getNextUserActivityGroup" + apiResponse.body.getLoginDayActivityInfoList().size() + "");
                            if (apiResponse.body.loginDayActivityInfoList == null || apiResponse.body.loginDayActivityInfoList!!
                                    .isEmpty()
                            ) {
                                this@PlayGamesFragment.isLastPage = true;
                                this@PlayGamesFragment.isLoading = false;
                                setFooterMessage()
                            } else {
                                soloGamesAdapter!!.addItems(apiResponse.body.loginDayActivityInfoList)
                                loginDayActivityInfoList!!.addAll(apiResponse.body.loginDayActivityInfoList!!)
                                if (apiResponse.body.loginDayActivityInfoList!!.size < PaginationListener.PAGE_ITEM_SIZE) {
                                    // isLastPage = true;
                                    setFooterMessage()
                                } else {
                                    this@PlayGamesFragment.isLastPage = false
                                }
                                // Log.e("####", String.format("server: %d", apiResponse.body.getLoginDayActivityInfoList().size()));
                            }
                        } else {
                            // isLastPage = true;
                            setFooterMessage()
                            showAlert(apiResponse.body.errorMessage)
                        }
                    } else {
                        this@PlayGamesFragment.isLoading = false
                        if (soloGamesAdapter != null) {
                            soloGamesAdapter!!.removeLoading()
                        }
                        showAlert(apiResponse.errorMessage)
                        if ((apiResponse.errorMessage != null
                                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                    || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
                        ) {
                            if (BuildConfig.DEBUG) {
                                AppUtilities.showLog("Network Error", "getNextUserActivityGroup")
                            }
                            (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                "getNextUserActivityGroup",
                                apiResponse.code.toString()
                            )
                        }
                    }
                }
        }
    }

    private fun setFooterMessage() {

//        if (selectedFilterIndex == invokedServiceIndex) {
        this@PlayGamesFragment.isLoading = false
        val footerMessage: String
        if (soloGamesAdapter != null) {
//            switch (selectedFilterIndex) {
//                case 0:
//                    break;
//                case 1:
//                    break;
//                case 2:
//                    break;
//                case 3:
//                    break;
//            }
            if (soloGamesAdapter!!.itemCount > 0 /*&& !soloGamesAdapter.hasValidItems()*/) {
                if (playGroup!!.playGroupId.equals("FFF", ignoreCase = true)) {
                    footerMessage =
                        "We appreciate you for using Onourem. Remember to keep smiling and check in with your loved ones regularly."
                } else if (playGroup!!.playGroupId.equals("AAA", ignoreCase = true)) {
                    footerMessage =
                        "These are the questions you have not answered. We add a few new questions every day in this section. Answer questions you like and forward them to friends."
                } else if (playGroup!!.playGroupId.equals("ZZZ", ignoreCase = true)) {
                    footerMessage = "These are the questions you have answered before."
                } else if (playGroup!!.playGroupId.equals("YYY", ignoreCase = true)) {
                    footerMessage = "These are the questions you have created yourself."
                } else {
                    footerMessage =
                        "That's all for the games played in this O-Club so far. Want to play more? Try creating a new question with the bottom right button."
                }
            } else {
                if (playGroup!!.playGroupId.equals("FFF", ignoreCase = true)) {
                    footerMessage =
                        "You do not have questions in this section today. Here you see questions that you or your friends are playing."
                } else if (playGroup!!.playGroupId.equals("AAA", ignoreCase = true)) {
                    footerMessage =
                        "No new questions for today. Try other filters or create your question and ask friends."
                } else if (playGroup!!.playGroupId.equals("ZZZ", ignoreCase = true)) {
                    footerMessage =
                        "This section shows the questions you have answered. You have not answered any so far."
                } else if (playGroup!!.playGroupId.equals("YYY", ignoreCase = true)) {
                    footerMessage =
                        "You have not created any questions. Use the bottom left pen icon to create a question and ask friends."
                } else {
                    footerMessage =
                        "No one has asked any question in this O-Club so far. Ask a question to start having fun."
                }
            }
            binding.rvPlayGames.postDelayed({
                soloGamesAdapter!!.addFooter(footerMessage)
                soloGamesAdapter!!.notifyItemChanged(soloGamesAdapter!!.itemCount - 1)
            }, 200)
        }
        //        }
    }

    override fun onPause() {
        super.onPause()
        //        state = layoutManager.onSaveInstanceState();
//        counter++;
//        val positionIndex = layoutManager!!.findLastVisibleItemPosition()
//        val startView = binding.rvPlayGames.getChildAt(0)
//        val topView = if (startView == null) 0 else startView.top - binding.rvPlayGames.paddingTop
////        val topView = binding.rvPlayGames.computeVerticalScrollOffset()
//
//
//        (fragmentContext as DashboardActivity).setPlayGamesRecyclerViewPosition(positionIndex, topView)

        isDataLoading = false

//            viewModel.setLoginDayActivityInfoList(loginDayActivityInfoList as MutableList<LoginDayActivityInfoList>)

        recyclerViewState = binding.rvPlayGames.layoutManager?.onSaveInstanceState()

    }

    override fun onDestroy() {
        super.onDestroy()
        //        state = null;
        loginDayActivityInfoList!!.clear()
        activityStatusList?.clear()
        gameResIdList?.clear()
        viewModel.actionConsumed()
        //Glide.get(requireActivity()).clearMemory()
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
                                        "Delete This Message",
                                        R.color.color_red,
                                        ActionType.DELETE_GAME,
                                        item.first
                                    )
                                )
                                actions.add(
                                    Action(
                                        "Edit Post Visibility",
                                        R.color.color_black,
                                        ActionType.EDIT_GAME_VISIBILITY,
                                        item.first
                                    )
                                )
                            } else {
                                actions.add(
                                    Action(
                                        "Report Abuse",
                                        R.color.color_black,
                                        ActionType.REPORT_ABUSE,
                                        item.first
                                    )
                                )

                            }
                            val bundle = Bundle()
                            bundle.putParcelableArrayList(
                                Constants.KEY_BOTTOM_SHEET_ACTIONS,
                                actions
                            )
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                                    title,
                                    bundle,
                                    ""
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
                                    val updatedItemPosition =
                                        soloGamesAdapter!!.updateItem(item.first)
                                    //scrollToTop(updatedItemPosition)
                                }
                            }.show(requireActivity().supportFragmentManager, "Comment")
                        }
                        ActivityGamesAdapter.CLICK_SENDER_PROFILE -> {
                            if (item.first!!.feedsInfo!!.anonymousOnOff.equals(
                                    "Y",
                                    ignoreCase = true
                                ) && loginUserId != item.first!!.feedsInfo!!.postCreatedId
                            ) {
                                showAlert("Identity of the friend who sent this message will be revealed after 48 hrs from the time the message was sent")
                            } else if (loginUserId == item.first!!.feedsInfo!!.postCreatedId) {
                                // scrollToTop(0)
                            } else {

                                if (!item.first!!.feedsInfo!!.postCreatedId.equals(
                                        "4264",
                                        ignoreCase = true
                                    )
                                ) {
                                    navController.navigate(
                                        MobileNavigationDirections.actionGlobalNavProfile(
                                            item.first!!.feedsInfo!!.activityId,
                                            item.first!!.feedsInfo!!.postCreatedId
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
                                    "youtubeId",
                                    item.first!!.youTubeVideoId!!
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
                                            media,
                                            url
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

                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavPostCommentList(
                                    "",
                                    item.first!!.feedsInfo!!.activityId!!,
                                    item.first!!.feedsInfo!!.postId!!,
                                    receiverIds.toString(),
                                    "",
                                    item.first!!.feedsInfo!!.activityId!!,
                                    item.first!!.feedsInfo!!.postCreatedId!!,
                                    "Post",
                                    receiverArray.any { it == userId } || item.first!!.feedsInfo!!.postCreatedId == userId
                                )
                            )
                        }

                        ActivityGamesAdapter.CLICK_REMOVE -> {
                            ignoreQuestion(item.first!!)
                        }

                        ActivityGamesAdapter.CLICK_AUDIO_ITEM -> {

                            navController.navigate(
                                (MobileNavigationDirections.actionGlobalNavVocalsMain(
                                    "",
                                    "", "", item.first!!.gameId!!
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
                            bundle.putParcelableArrayList(
                                Constants.KEY_BOTTOM_SHEET_ACTIONS,
                                actions
                            )
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                                    titleText,
                                    bundle, ""
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
                            if (ActivityType.getActivityType(item.first!!.activityType!!)
                                == ActivityType.ONE_TO_ONE
                            ) {
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavOneToOneQuestionDetails(
                                        item.first!!,
                                        getPlayGroup(),
                                        null
                                    )
                                )
                            } else if (ActivityType.getActivityType(item.first!!.activityType!!)
                                == ActivityType.ONE_TO_MANY || ActivityType.getActivityType(item.first!!.activityType!!)
                                == ActivityType.D_TO_ONE
                            ) {
                                if (ActivityType.getActivityType(item.first!!.activityType!!)
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
                            if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.ONE_TO_ONE) {
                                askQuestionToFriends(item)
                            }
                        } else if (getString(R.string.click_action_send_now) == item.third) {
                            if (ActivityType.getActivityType(item.first!!.activityType!!)
                                == ActivityType.MESSAGE
                            ) {
                                TaskAndMessageComposeDialogFragment.getInstance(item.first!!) { item1: Pair<LoginDayActivityInfoList?, UploadPostResponse?>? ->
                                    item.first!!.friendCount = 0
                                    viewModel.setRefreshEditedItem(item.first!!)
                                    navController.navigate(
                                        MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                            item.first!!,
                                            playGroup!!
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
                            if ((ActivityType.getActivityType(item.first!!.activityType!!)
                                        == ActivityType.D_TO_ONE) || (ActivityType.getActivityType(
                                    item.first!!.activityType!!
                                )
                                        == ActivityType.ONE_TO_ONE) || (ActivityType.getActivityType(
                                    item.first!!.activityType!!
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
                            item.first!!.friendCount = 0
                            viewModel.setRefreshEditedItem(item.first!!)
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
                                item.first!!.friendCount = 0
                                viewModel.setRefreshEditedItem(item.first!!)
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                        item1.first!!,
                                        playGroup!!
                                    )
                                )
                            }
                                .show(
                                    requireActivity().supportFragmentManager,
                                    "TaskMessageCompose"
                                )
                        } else if (getString(R.string.click_action_i_will_do_it) == item.third) {
                            if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.TASK) {
                                val createGameActivityRequest = CreateGameActivityRequest()
                                createGameActivityRequest.serviceName = "createGameActivity"
                                createGameActivityRequest.screenId = "12"
                                createGameActivityRequest.loginDay = item.first!!.loginDay
                                createGameActivityRequest.activityId = item.first!!.activityId
                                createGameActivityRequest.activityTypeId =
                                    item.first!!.activityTypeId
                                viewModel.createGameActivity(
                                    createGameActivityRequest
                                )
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
                                showAlert("Unknown Action " + item.first!!.activityType!!)
                            }
                        } else if (getString(R.string.click_action_all_answers) == item.third) {
                            if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.ONE_TO_MANY) {
                                if (getPlayGroup().playGroupId.equals("AAA", ignoreCase = true)
                                    || getPlayGroup().playGroupId.equals("ZZZ", ignoreCase = true)
                                    || getPlayGroup().playGroupId.equals("YYY", ignoreCase = true)
                                ) {
                                    viewModel.getActivityGroups(item.first!!.activityId!!)
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
                                                            ) "AAA"
                                                            else activityGroupList.groupId
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
                                                                    playGroup?.playGroupName
                                                                group.isDummyGroup =
                                                                    playGroup!!.isDummyGroup
                                                            }
                                                            navigateToDetails(item.first!!, group)
                                                        } else {
                                                            showGroupSelectionPopup(
                                                                apiResponse.body.activityGroupList!!,
                                                                item.first!!
                                                            )
                                                        }
                                                    } else {
                                                        navigateToDetails(
                                                            item.first!!,
                                                            getPlayGroup()
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
                                        item.first!!,
                                        getPlayGroup(),
                                        null
                                    )
                                )
                            } else if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.ONE_TO_ONE) {
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
                                        ""
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
                            } else {
                                showAlert("Unknown Action " + item.first!!.activityType!!)
                            }
                        } else if (getString(R.string.click_action_see_stats) == item.third) {

                            if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_play_games) {
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavAnonymousSurvey(
                                        item.first!!.activityId!!,
                                    )
                                )
                            }
//                            getSurveyData(item)

                        }
                        ActivityGamesAdapter.CLICK_WHOLE_ITEM -> if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.MESSAGE
                            || ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.TASK
                        ) {
                            item.first!!.friendCount = 0
                            viewModel.setRefreshEditedItem(item.first!!)
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                    item.first!!,
                                    getPlayGroup()
                                )
                            )
                        } else if (ActivityType.getActivityType(
                                item.first!!.activityType!!
                            )
                            == ActivityType.FRIEND_CIRCLE_GAME
                        ) {
                            navigateToFriendCircleGame()
                        }
                        ActivityGamesAdapter.CLICK_MEDIA -> {
                            if (item.first!!.activityType!! == "External") {
                                (fragmentContext as DashboardActivity).updateExternalActivityInfo(
                                    item.first!!
                                )
                                if (!TextUtils.isEmpty(item.first!!.youTubeVideoId)) {
                                    val intent = Intent(context, YoutubeActivity::class.java)
                                    intent.putExtra(
                                        "youtubeId",
                                        item.first!!.youTubeVideoId!!
                                    )
                                    (fragmentContext as DashboardActivity).exoPlayerPause(true)
                                    fragmentContext.startActivity(intent)
                                } else {
                                    if (!TextUtils.isEmpty(item.first!!.questionVideoUrl)) {
                                        navController.navigate(
                                            MobileNavigationDirections.actionGlobalNavWebExternalContent(
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
                            } else if (!TextUtils.isEmpty(item.first!!.youTubeVideoId)) {
                                val intent = Intent(context, YoutubeActivity::class.java)
                                intent.putExtra(
                                    "youtubeId",
                                    item.first!!.youTubeVideoId!!
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
                        ActivityGamesAdapter.CLICK_SHARE -> if (item.first!!.activityType!!.equals(
                                "Card",
                                ignoreCase = true
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

    private fun getSurveyData(item: Triple<LoginDayActivityInfoList?, Int?, String?>) {
        surveyViewModel!!.getSurveyData(item.first!!.activityId!!)
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
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "getSurveyData")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getSurveyData",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

//
//    private fun showGroupSelectionPopup(
//        playGroupList: List<ActivityGroupList?>,
//        first: LoginDayActivityInfoList?
//    ) {
//        SelectPlayGroupDialogFragment.this@PlayGamesFragment.getInstance(
//            playGroupList,
//            OnItemClickListener { activityGroupList: ActivityGroupList ->
//                val group: PlayGroup = PlayGroup()
//                group.playGroupId = activityGroupList.groupId
//                if (!TextUtils.isEmpty(activityGroupList.activityGameResponseId)) {
//                    first!!.activityGameResponseId = activityGroupList.activityGameResponseId
//                }
//                if (!TextUtils.isEmpty(activityGroupList.groupId) && !activityGroupList.groupId
//                        .equals("0", ignoreCase = true)
//                ) {
//                    group.playGroupName = activityGroupList.groupName
//                } else {
//                    group.playGroupName = playGroup!!.playGroupName
//                }
//                group.isDummyGroup = playGroup!!.isDummyGroup
//                navigateToDetails(first, group)
//            }).show(requireActivity().supportFragmentManager, "SelectPlayGroupDialogFragment")
//    }


//    private fun askQuestionToFriends(item: Triple<LoginDayActivityInfoList?, Int?, String?>) {
//        val isActivityForNewQuestion = playGroup!!.playGroupId.equals("YYY", ignoreCase = true)
//        navController.navigate(
//            MobileNavigationDirections.actionGlobalNavAskQuestionsSheet(
//                (item.first)!!,
//                isActivityForNewQuestion,
//                playGroup!!.playGroupId!!
//            )
//        )
//    }


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

    private fun navigateToAnswer(first: LoginDayActivityInfoList, playGroup: PlayGroup) {

        AnswerQuestionDialogFragment.getInstance(
            first,
            playGroup
        ) { apiResponse: UploadPostResponse? ->
            if (!TextUtils.isEmpty(apiResponse!!.participationStatus)) first.userParticipationStatus =
                apiResponse.participationStatus
            if (!TextUtils.isEmpty(apiResponse.activityTagStatus)) first.activityTag =
                apiResponse.activityTagStatus
            if (ActivityType.getActivityType(first.activityType!!)
                == ActivityType.ONE_TO_MANY
            ) {
                navigateToDetails(first, playGroup)
            }
        }.show(requireActivity().supportFragmentManager, "Answer")

    }

    private fun getPlayGroup(): PlayGroup {
        val playGroupTemp: PlayGroup
        return if (playGroup?.playGroupId.equals("AAA", ignoreCase = true)
            || playGroup?.playGroupId.equals("FFF", ignoreCase = true)
            || playGroup?.playGroupId.equals("YYY", ignoreCase = true)
            || playGroup?.playGroupId.equals("ZZZ", ignoreCase = true)
        ) {
            playGroupTemp = playGroup!!
            playGroupTemp.playGroupId = "AAA"
            playGroupTemp
        } else {
            playGroup!!
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
                group.playGroupName = playGroup?.playGroupName
            }
            group.isDummyGroup = playGroup!!.isDummyGroup
            navigateToDetails(first, group)
        }.show(requireActivity().supportFragmentManager, "SelectPlayGroupDialogFragment")
    }

    private fun navigateToDetails(item: LoginDayActivityInfoList?, playGroup: PlayGroup?) {

        if (item != null) {
            item.friendCount = 0
            viewModel.setRefreshEditedItem(item)
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
        if (playGroup?.playGroupId.equals("YYY", ignoreCase = true)) {
            isActivityForNewQuestion = true
        }
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavAskQuestionsSheet(
                item!!.first!!,
                isActivityForNewQuestion,
                playGroup?.playGroupId ?: ""
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
        viewModel.createGameActivity(createGameActivityRequest).observe(
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
                                item1!!.first!!.friendCount = 0
                                viewModel.setRefreshEditedItem(item1.first!!)
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                        pair.first!!,
                                        playGroup!!
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

    private fun share(
        title: String?,
        linkMsg: String?,
        mediaUrl: String?,
        mediaType: String?,
    ) {
//        if (!mediaUrl.equals("", ignoreCase = true)) {
//            navController.navigate(
//                MobileNavigationDirections.actionGlobalNavMediaShare(
//                    title ?: "",
//                    linkMsg ?: "",
//                    mediaUrl ?: "",
//                    mediaType ?: ""
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
                        if (first.activityType == "External") {
                            share(
                                apiResponse.body.title,
                                apiResponse.body.shortLink,
                                apiResponse.body.activityImageUrl,
                                "1"
                            )
                        } else {
                            share(
                                apiResponse.body.title,
                                apiResponse.body.shortLink,
                                first.questionVideoUrl!!,
                                "2"
                            )
                        }

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
                            apiResponse.body.shortLink,
                            "", "-1"
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

    override fun onDestroyView() {
        super.onDestroyView()
        if ((!activityPlayGroupId().equals("0", ignoreCase = true)
                    && !activityPlayGroupId().equals("1", ignoreCase = true)
                    && !activityPlayGroupId().equals("2", ignoreCase = true)
                    && !activityPlayGroupId().equals("3", ignoreCase = true))
        ) {
            viewModel.actionOpenPlayGroupListingPerformed(true)
        }
    }
}