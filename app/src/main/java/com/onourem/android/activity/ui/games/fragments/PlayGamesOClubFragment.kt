package com.onourem.android.activity.ui.games.fragments

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentPlayGamesOclubBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.adapters.PlaySoloGamesAdapter
import com.onourem.android.activity.ui.games.dialogs.SendNowOrSendLaterDialogFragment
import com.onourem.android.activity.ui.games.dialogs.SendNowOrSendLaterDialogFragment.Companion.getInstance
import com.onourem.android.activity.ui.games.fragments.AnswerQuestionDialogFragment.Companion.getInstance
import com.onourem.android.activity.ui.games.viewmodel.GamesReceiverViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class PlayGamesOClubFragment :
    AbstractBaseViewModelBindingFragment<QuestionGamesViewModel, FragmentPlayGamesOclubBinding>(),
    OnItemClickListener<Triple<LoginDayActivityInfoList?, Int?, String?>?> {
    private val activityStatusList: MutableList<ActivityStatusList>? = ArrayList()
    private val gameResIdList: MutableList<String> = ArrayList()
    private val loginDayActivityInfoList: MutableList<LoginDayActivityInfoList>? = ArrayList()
    private var playGroup: PlayGroup? = null
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var soloGamesAdapter: PlaySoloGamesAdapter? = null
    private var gamesReceiverViewModel: GamesReceiverViewModel? = null
    private var layoutManager: LinearLayoutManager? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun viewModelType(): Class<QuestionGamesViewModel> {
        return QuestionGamesViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Log.d("###", "onCreate: called");
        gamesReceiverViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            GamesReceiverViewModel::class.java
        )
        viewModel.reloadUI().observe(this, Observer({ reload: Boolean ->
            if (reload) {
                refreshUI()
            }
        }))
        viewModel.getGameActivityUpdateStatus().observe(this, { item ->
            if (item != null) {
                viewModel.setGameActivityUpdateStatus(null)
                if (!(playGroup!!.playGroupId
                        .equals("AAA", ignoreCase = true) || playGroup!!.playGroupId
                        .equals("ZZZ", ignoreCase = true)) && playGroup!!.playGroupId
                        .equals(item.first, ignoreCase = true)
                ) {
                    if (loginDayActivityInfoList!!.isEmpty()) {
                        activityStatusList!!.clear()
                        loadData(false, true)
                    } else if (!loginDayActivityInfoList.contains(item.second)) {
//                        soloGamesAdapter = null;
//                        loginDayActivityInfoList.clear();
//                        loadData(false, false);
//                    } else {
//                        loginDayActivityInfoList.add(0, item.second);
//
//                        if (soloGamesAdapter != null) {
//                            soloGamesAdapter.notifyDataSetChanged();
//                        }
                        loginDayActivityInfoList.clear()
                        activityStatusList!!.clear()
                        loadData(false, true)
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
        })
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_play_games_oclub
    }

    private fun setTextViewDrawableColor(textView: TextView, color: Int) {
        for (drawable: Drawable? in textView.compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(
                        textView.context,
                        color
                    ), PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playGamesFragmentArgs: PlayGamesFragmentArgs =
            PlayGamesFragmentArgs.fromBundle(requireArguments())
        //        Log.d("###", "onViewCreated: called ");
        playGroup = playGamesFragmentArgs.selectedGroup
        layoutManager = LinearLayoutManager(requireActivity())
        binding.rvPlayGames.layoutManager = layoutManager
        gamesReceiverViewModel!!.getGameActivityUpdateResponseLiveData()
            .observe(viewLifecycleOwner, { activityResponse ->
                if (activityResponse != null) {
                    gamesReceiverViewModel!!.setGameActivityUpdateResponseLiveData(null)
                    if (soloGamesAdapter != null) {
                        soloGamesAdapter!!.notifyDataSetChanged()
                    }
                }
            })
        binding.rvPlayGames.addOnScrollListener(object : PaginationListener(layoutManager!!) {
            override fun loadMoreItems() {
                this@PlayGamesOClubFragment.isLoading = true
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener({ refreshUI() }))
        binding.rvPlayGames.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0) binding.fab.extend() else if (dy > 0) binding.fab.shrink()
            }
        })
        binding.fab.setOnClickListener(ViewClickListener(View.OnClickListener({ v: View? ->
            navController.navigate(
                PlayGamesFragmentDirections.actionNavPlayGamesToNavCreateOwnQuestion(
                    (playGroup)!!
                )
            )
        })))
        loadData(true, false)
    }

    private fun refreshUI() {
        isLastPage = false
        isLoading = false
        if (soloGamesAdapter != null) soloGamesAdapter!!.removeFooter()
        activityStatusList!!.clear()
        gameResIdList.clear()
        binding.swipeRefreshLayout.isRefreshing = true
        loadData(false, true)
    }

    private fun loadData(showProgress: Boolean, refreshing: Boolean) {
        isLoading = false
        isLastPage = false
        activityStatusList!!.clear()
        if (playGroup != null) {
            if (playGroup!!.newMemeberNumber > 0) {
                if (playGroup!!.newMemeberNumber == 1) {
                    binding.tvSeeMembers.text = String.format(
                        "%s New Member",
                        playGroup!!.newMemeberNumber
                    )
                } else if (playGroup!!.newMemeberNumber > 1) {
                    binding.tvSeeMembers.text = String.format(
                        "%s New Members",
                        playGroup!!.newMemeberNumber
                    )
                }
                binding.tvSeeMembers.setTextColor(
                    ContextCompat.getColor(
                        fragmentContext,
                        R.color.color_white
                    )
                )
                binding.tvSeeMembers.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                        fragmentContext,
                        R.drawable.shape_oval_rounded_corner
                    )
                )
                setTextViewDrawableColor(
                    binding.tvSeeMembers,
                    ContextCompat.getColor(fragmentContext, R.color.color_blue)
                )
            } else {
                binding.tvSeeMembers.text = getString(R.string.action_label_see_members)
            }
        }
        if (playGroup != null && !((playGroup!!.playGroupId.equals("AAA", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("FFF", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("YYY", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("ZZZ", ignoreCase = true)))
        ) {
            binding.tvSeeMembers.visibility = View.VISIBLE
            binding.tvSeeMembers.setOnClickListener(ViewClickListener(View.OnClickListener({ v: View? ->
                viewModel.setPlayGroup(playGroup!!)
                navController.navigate(PlayGamesFragmentDirections.actionNavPlayGamesToNavPlayGroupMember())
            })))
            if (refreshing || (loginDayActivityInfoList == null) || loginDayActivityInfoList.isEmpty()) {
                viewModel.getUserActivityGroupInfo(playGroup!!.playGroupId).observe(
                    viewLifecycleOwner,
                    Observer({ responseApiResponse: ApiResponse<UserActivityInfoResponse> ->
                        handleResponse(
                            responseApiResponse,
                            false,
                            showProgress
                        )
                    })
                )
            } else {
                setAdapter()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getActionMutableLiveData().observe(viewLifecycleOwner) { action ->
            if (action == null) return@observe
            val actionType: ActionType? = action.actionType
            if ((actionType == null) || (actionType == ActionType.NONE) || (viewModel == null) || ((navController == null) || (navController.currentDestination == null
                        ) || (navController.currentDestination!!.id != R.id.nav_play_games))
            ) return@observe
            viewModel.actionConsumed()
            if (actionType == ActionType.CREATE_QUESTION) {
                navController.navigate(
                    PlayGamesFragmentDirections.actionNavPlayGamesToNavCreateOwnQuestion(
                        (playGroup)!!
                    )
                )
            }
        }
    }

    private fun handleResponse(
        apiResponse: ApiResponse<UserActivityInfoResponse>,
        isSolo: Boolean,
        showProgress: Boolean
    ) {
        if (apiResponse.loading) {
            if (showProgress) showProgress()
        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            hideProgress()
            if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing =
                false
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                if (apiResponse.body.loginDayActivityInfoList != null) {
                    loginDayActivityInfoList!!.clear()
                    loginDayActivityInfoList.addAll(apiResponse.body.loginDayActivityInfoList)
                    if (!isSolo) viewModel.updateActivityMemberNumber(playGroup!!.playGroupId)
                        .observe(
                            viewLifecycleOwner,
                            Observer({ userActivityInfoResponseApiResponse: ApiResponse<UserActivityInfoResponse>? -> })
                        )
                    setAdapter()
                    if (apiResponse.body.activityStatusList != null && !apiResponse.body.activityStatusList
                            .isEmpty()
                    ) {
                        activityStatusList!!.clear()
                        activityStatusList.addAll(apiResponse.body.activityStatusList)
                    }
                    if (apiResponse.body.gameResIdList != null && !apiResponse.body.gameResIdList
                            .isEmpty()
                    ) {
                        gameResIdList.clear()
                        gameResIdList.addAll(apiResponse.body.gameResIdList)
                    }
                } else {
                    val footerMessage: String
                    if (playGroup!!.playGroupId.equals("FFF", ignoreCase = true)) {
                        footerMessage =
                            "Looks like you do not have any questions for today. Please check back tomorrow."
                    } else if (playGroup!!.playGroupId.equals("AAA", ignoreCase = true)) {
                        footerMessage =
                            "Looks like you do not have any questions for today. Please check back tomorrow."
                    } else if (playGroup!!.playGroupId.equals("ZZZ", ignoreCase = true)) {
                        footerMessage = "You have not answered any question so far"
                    } else if (playGroup!!.playGroupId.equals("YYY", ignoreCase = true)) {
                        footerMessage = "You have not created any question so far"
                    } else {
                        footerMessage =
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
            if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing =
                false
            hideProgress()
            showAlert(apiResponse.errorMessage)
            if ((apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                        || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
            ) {
                if (BuildConfig.DEBUG) {
                    AppUtilities.showLog("Network Error", "getAppDemoForUser")
                }
                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                    "createGameActivity",
                    apiResponse.code.toString()
                )
            }
        }
    }

    private fun setAdapter() {
        if (loginDayActivityInfoList != null && loginDayActivityInfoList.isNotEmpty()) {
            if (soloGamesAdapter == null) soloGamesAdapter =
                PlaySoloGamesAdapter(playGroup!!, loginDayActivityInfoList, preferenceHelper!!, this)
            binding.rvPlayGames.adapter = soloGamesAdapter
            binding.tvMessage.visibility = View.GONE
            binding.rvPlayGames.visibility = View.VISIBLE
        } else {
            var footerMessage: String? = ""
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
    }

    private fun loadMoreGames() {
        if (playGroup != null && !((playGroup!!.playGroupId.equals("FFF", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("AAA", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("YYY", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("ZZZ", ignoreCase = true)))
        ) {
            if (activityStatusList == null || activityStatusList.isEmpty()) {
                isLastPage = true
                isLoading = false
                return
            }
            val start: Int = soloGamesAdapter!!.itemCount
            val end: Int = activityStatusList.size
            if (start >= end) {
                isLastPage = true
                isLoading = false
                setFooterMessage()
                return
            }
            val ids: MutableList<Int> = ArrayList()
            val tags: MutableList<String> = ArrayList()
            val status: MutableList<String> = ArrayList()
            val reason: MutableList<String> = ArrayList()
            val friends: MutableList<Int> = ArrayList()
            val responseIds: MutableList<Int> = ArrayList()
            for (i in start until end) {
                val item: ActivityStatusList = activityStatusList.get(i)
                ids.add(item.activityId)
                friends.add(item.friendCount)
                tags.add(item.activityTag)
                status.add(item.userParticipationStatus)
                reason.add(item.activityReason)
                responseIds.add(item.activityResponseId)
            }
            val userActivityRequest: UserActivityRequest = UserActivityRequest()
            userActivityRequest.activityIds = Utilities.getTokenSeparatedString(ids, ",")
            userActivityRequest.activityTags = Utilities.getTokenSeparatedString(tags, ",")
            userActivityRequest.userParticipationStatus = Utilities.getTokenSeparatedString(
                status,
                ","
            )
            userActivityRequest.activityReason = Utilities.getTokenSeparatedString(reason, "@")
            userActivityRequest.friendCount = Utilities.getTokenSeparatedString(friends, ",")
            userActivityRequest.activityGameResponseId = Utilities.getTokenSeparatedString(
                responseIds,
                ","
            )
            viewModel.getNextUserActivityGroup(userActivityRequest).observe(
                viewLifecycleOwner,
                Observer { apiResponse: ApiResponse<UserActivityResponse> ->
                    if (apiResponse.loading) {
                        if (soloGamesAdapter != null) {
                            soloGamesAdapter!!.addLoading()
                        }
                    }
                    else if (apiResponse.isSuccess && apiResponse.body != null) {
                        if (soloGamesAdapter != null) {
                            soloGamesAdapter!!.removeLoading()
                        }
                        isLoading = false
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                            if (apiResponse.body.loginDayActivityInfoList == null || apiResponse.body.loginDayActivityInfoList!!
                                    .isEmpty()
                            ) {
                                isLastPage = true
                                setFooterMessage()
                            }
                            else {
                                soloGamesAdapter!!.addItems(apiResponse.body.loginDayActivityInfoList)
                                if (apiResponse.body.loginDayActivityInfoList!!.size < PaginationListener.PAGE_ITEM_SIZE) {
                                    isLastPage = true
                                    setFooterMessage()
                                }
                                // Log.e("####", String.format("server: %d", apiResponse.body.getLoginDayActivityInfoList().size()));
                            }
                        }
                        else {
                            isLastPage = true
                            setFooterMessage()
                            showAlert(apiResponse.body.errorMessage)
                        }
                    }
                    else {
                        isLoading = false
                        if (soloGamesAdapter != null) {
                            soloGamesAdapter!!.removeLoading()
                        }
                        showAlert(apiResponse.errorMessage)
                        if ((apiResponse.errorMessage != null
                                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                    || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
                        ) {
                            if (BuildConfig.DEBUG) {
                                AppUtilities.showLog("Network Error", "getAppDemoForUser")
                            }
                            (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                "createGameActivity",
                                apiResponse.code.toString()
                            )
                        }
                    }
                }
            )
        }
    }

    private fun setFooterMessage() {
        isLoading = false
        val footerMessage: String
        if (soloGamesAdapter != null) {
            if (soloGamesAdapter!!.itemCount == 0) {
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
            } else {
                if (playGroup!!.playGroupId.equals("FFF", ignoreCase = true)) {
                    footerMessage =
                        "No one has asked any question in this O-Club so far. Ask a question to start having fun."
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
            }
            binding.rvPlayGames.postDelayed(
                Runnable({ soloGamesAdapter!!.addFooter(footerMessage) }),
                200
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //        state = null;
        loginDayActivityInfoList!!.clear()
        viewModel.actionConsumed()
        //Glide.get(requireActivity()).clearMemory()
    }

    override fun onItemClick(item: Triple<LoginDayActivityInfoList?, Int?, String?>?) {
        when (item!!.second) {
            PlaySoloGamesAdapter.CLICK_ACTIVITY -> {
                var title: String? = ""
                when (ActivityType.getActivityType(item.first!!.activityType)) {
                    ActivityType.ONE_TO_MANY -> title = "Group Bonding Question"
                    ActivityType.ONE_TO_ONE -> title = "One-on-One Question"
                    ActivityType.D_TO_ONE -> title = "Know Someone"
                    ActivityType.TASK -> title = "Wellbeing Activity"
                    ActivityType.MESSAGE -> title = "Heartfelt Message"
                }
                showAlert(title, item.first!!.activityTypeHint)
            }
            PlaySoloGamesAdapter.CLICK_BUTTON -> if ((getString(R.string.click_action_answer) == item.third)) {
//                        a.	Answer:  If this icon appear then User need to answer first then only user can see who else have answered or can ask anyone else. This icon appear on following type questions:
//                        i.	One to One
//                        ii.	One to Many
//                        iii.	Ask Directly
                if ((ActivityType.getActivityType(item.first!!.activityType)
                            == ActivityType.ONE_TO_ONE)
                ) {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavOneToOneQuestionDetails(
                            item.first!!,
                            (getPlayGroup())!!,
                            null
                        )
                    )
                } else if (ActivityType.getActivityType(item.first!!.activityType)
                    == ActivityType.ONE_TO_MANY || ActivityType.getActivityType(item.first!!.activityType)
                    == ActivityType.D_TO_ONE
                ) {
                    if ((ActivityType.getActivityType(item.first!!.activityType)
                                == ActivityType.D_TO_ONE)
                    ) {
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalDToOneQuestionDetailsFragment(
                                item.first!!,
                                (getPlayGroup())!!,
                                null
                            )
                        )
                    } else {
                        getInstance(
                            item.first!!,
                            getPlayGroup(),
                            OnItemClickListener({ apiResponse: UploadPostResponse? ->
                                if (!TextUtils.isEmpty(apiResponse!!.participationStatus)) item.first!!.userParticipationStatus =
                                    apiResponse.participationStatus
                                if (!TextUtils.isEmpty(apiResponse.activityTagStatus)) item.first!!.activityTag =
                                    apiResponse.activityTagStatus
                                if ((ActivityType.getActivityType(item.first!!.activityType)
                                            == ActivityType.ONE_TO_MANY)
                                ) {
                                    navController.navigate(
                                        MobileNavigationDirections.actionGlobalOneToManyQuestionDetailsFragment(
                                            item.first!!,
                                            (getPlayGroup())!!,
                                            null,
                                            null,
                                            true
                                        )
                                    )
                                }
                            })
                        ).show(requireActivity().supportFragmentManager, "Answer")
                    }
                } else {
                    showAlert("Unknown Action " + item.first!!.activityType)
                }
            } else if ((getString(R.string.click_action_start_game) == item.third)) {
//                        b.	Start Game: Icon appear only on One to One questions. It works in following way :
//                        i.	When use clicks on Start Game icon then user can ask this to any other Specific user.
//                                ii.	User can ask more the on user in one shot but NO other two users can see each other answer.  For Ex: If User A asked this to B , C & D users. Then user A can see answers of B,C & D but User B can not see answer of C & D. Same applicable on User C & D.
                if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.ONE_TO_ONE) {
                    askQuestionToFriends(item)
                }
            } else if ((getString(R.string.click_action_send_now) == item.third)) {
                if ((ActivityType.getActivityType(item.first!!.activityType)
                            == ActivityType.MESSAGE)
                ) {
                    TaskAndMessageComposeDialogFragment.Companion.getInstance(
                        item.first!!,
                        OnItemClickListener({ item1: Pair<LoginDayActivityInfoList?, UploadPostResponse?>? ->
                            navController.navigate(
                                MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                    item.first!!,
                                    (playGroup)!!
                                )
                            )
                        })
                    ).show(requireActivity().supportFragmentManager, "TaskMessageCompose")
                } else {
                    showAlert("Unknown Action " + item.first!!.activityType)
                }
            } else if ((getString(R.string.click_action_ask_friends) == item.third) || (getString(R.string.click_action_ask_question) == item.third)) {
//                        This icon appear on following type of questions:
//                        i.	One to One
//                        ii.	One to Many
                if (((ActivityType.getActivityType(item.first!!.activityType)
                            == ActivityType.D_TO_ONE)) || ((ActivityType.getActivityType(item.first!!.activityType)
                            == ActivityType.ONE_TO_ONE)) || ((ActivityType.getActivityType(item.first!!.activityType)
                            == ActivityType.ONE_TO_MANY))
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
//                    if (ActivityType.getActivityType(item.first!!.getActivityType())
//                            == ActivityType.D_TO_ONE) {
//                        navController.navigate(PlayGamesFragmentDirections.actionNavPlayGamesToNavAnswerQuestionDialog(item.first!!));
//                    } else {
//                        showAlert("Unknown Action " + item.first!!.getActivityType());
//                    }
            } else if (((getString(R.string.click_action_read_messages) == item.third) || (getString(
                    R.string.click_action_read_experience
                ) == item.third))
            ) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                        item.first!!,
                        (getPlayGroup())!!
                    )
                )
            } else if ((getString(R.string.click_action_i_want_to_send) == item.third)) {
//              f.	I Want To Send & Send Now: Icon appear on :
//                    i.	Appreciate type messages
//                        ii.	It work in following way:
//                    1.	User click on “I want to Send” Icon – then it show info pop-up(Screen f3) with “Send Now” & “Send Later” button- then icon convert in “Send Now” (Screen f4)
//                        2.	If user click any where on question screen (other then “I want to Send” icon) then  pop up appear (Screen f2)
//                        3.	Info pop up text :
//                        a.	“First commit to sending the message. You will then have 15days to send the message”
//                        b.	“You have 15 days from now to send this message” – Clickable Buttons: “Send Now”    “Send Later” – User click on ”Send Now” to compose message. User clicks on “Send Later” will close the pop up.
                showSendNowSendLater(item.first!!)
            } else if ((getString(R.string.click_action_share_experience) == item.third)) {
//                    navController.navigate(PlayGamesFragmentDirections.actionNavPlayGamesToNavTaskMessageCompose(item.first!!));
                TaskAndMessageComposeDialogFragment.Companion.getInstance(
                    item.first!!,
                    OnItemClickListener({ item1: Pair<LoginDayActivityInfoList?, UploadPostResponse?> ->
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                (item1.first)!!, (playGroup)!!
                            )
                        )
                    })
                ).show(requireActivity().supportFragmentManager, "TaskMessageCompose")
            } else if ((getString(R.string.click_action_i_will_do_it) == item.third)) {
                if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.TASK) {
                    val createGameActivityRequest: CreateGameActivityRequest =
                        CreateGameActivityRequest()
                    createGameActivityRequest.serviceName = "createGameActivity"
                    createGameActivityRequest.screenId = "12"
                    createGameActivityRequest.loginDay = item.first!!.loginDay
                    createGameActivityRequest.activityId = item.first!!.activityId
                    createGameActivityRequest.activityTypeId = item.first!!.activityTypeId
                    viewModel.createGameActivity(createGameActivityRequest).observe(
                        viewLifecycleOwner,
                        Observer({ apiResponse: ApiResponse<GameActivityUpdateResponse> ->
                            if (apiResponse.loading) {
                                showProgress()
                            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                                hideProgress()
                                if (apiResponse.body.errorCode
                                        .equals("000", ignoreCase = true)
                                ) {
                                    showAlert(
                                        apiResponse.body.message,
                                        View.OnClickListener({ v: View? ->
                                            if (!TextUtils.isEmpty(apiResponse.body.participationStatus)) item.first!!.userParticipationStatus =
                                                apiResponse.body.participationStatus
                                            if (!TextUtils.isEmpty(apiResponse.body.activityTagStatus)) item.first!!.activityTag =
                                                apiResponse.body.activityTagStatus
                                            soloGamesAdapter!!.notifyDataSetChanged()
                                        })
                                    )
                                } else {
                                    showAlert(apiResponse.body.errorMessage)
                                }
                            } else {
                                hideProgress()
                                showAlert(apiResponse.errorMessage)
                                if (apiResponse.errorMessage != null && ((apiResponse.errorMessage.contains(
                                        getString(R.string.unable_to_connect_host_message)
                                    ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
                                ) {
                                    if (BuildConfig.DEBUG) {
                                        AppUtilities.showLog("Network Error", "getAppDemoForUser")
                                    }
                                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                        "createGameActivity",
                                        apiResponse.code.toString()
                                    )
                                }
                            }
                        })
                    )
                } else {
                    showAlert("Unknown Action " + item.first!!.activityType)
                }
            } else if ((getString(R.string.click_action_all_answers) == item.third)) {
                if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.ONE_TO_MANY) {
                    if ((getPlayGroup()!!.playGroupId.equals("AAA", ignoreCase = true)
                                || getPlayGroup()!!.playGroupId
                            .equals("ZZZ", ignoreCase = true)
                                || getPlayGroup()!!.playGroupId
                            .equals("YYY", ignoreCase = true))
                    ) {
                        viewModel.getActivityGroups((item.first!!.activityId)!!).observe(
                            viewLifecycleOwner,
                            Observer({ apiResponse: ApiResponse<GetActivityGroupsResponse> ->
                                if (apiResponse.loading) {
                                    showProgress()
                                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                                    hideProgress()
                                    if (apiResponse.body.errorCode
                                            .equals("000", ignoreCase = true)
                                    ) {
                                        if (apiResponse.body.activityGroupList != null && !apiResponse.body.activityGroupList!!
                                                .isEmpty()
                                        ) {
                                            if (apiResponse.body.activityGroupList!!.size == 1) {
                                                val activityGroupList: ActivityGroupList =
                                                    apiResponse.body.activityGroupList!!.get(0)
                                                val group: PlayGroup = PlayGroup()
                                                group.playGroupId = if ("0".equals(
                                                        activityGroupList.groupId,
                                                        ignoreCase = true
                                                    )
                                                ) "AAA" else activityGroupList.groupId
                                                if (!TextUtils.isEmpty(activityGroupList.activityGameResponseId)) {
                                                    item.first!!.activityGameResponseId =
                                                        activityGroupList.activityGameResponseId
                                                }
                                                if (!TextUtils.isEmpty(activityGroupList.groupId) && !activityGroupList.groupId
                                                        .equals("0", ignoreCase = true)
                                                ) {
                                                    group.playGroupName = activityGroupList.groupName
                                                    group.isDummyGroup = false
                                                } else {
                                                    group.playGroupName = playGroup!!.playGroupName
                                                    group.isDummyGroup = playGroup!!.isDummyGroup
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
                                    if (apiResponse.errorMessage != null && ((apiResponse.errorMessage.contains(
                                            getString(R.string.unable_to_connect_host_message)
                                        ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
                                    ) {
                                        if (BuildConfig.DEBUG) {
                                            AppUtilities.showLog(
                                                "Network Error",
                                                "getAppDemoForUser"
                                            )
                                        }
                                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                            "createGameActivity",
                                            apiResponse.code.toString()
                                        )
                                    }
                                }
                            })
                        )
                    } else {
                        navigateToDetails(item.first!!, getPlayGroup())
                    }
                } else if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.D_TO_ONE) {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalDToOneQuestionDetailsFragment(
                            item.first!!,
                            (getPlayGroup())!!,
                            null
                        )
                    )
                } else if (ActivityType.getActivityType(item.first!!.activityType) == ActivityType.ONE_TO_ONE) {
//                        Log.d("###", "onItemClick: " + item.first!!.hashCode());
//                        Log.d("###", "onItemClick: " + item.first!!);
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavOneToOneQuestionDetails(
                            item.first!!,
                            (getPlayGroup())!!,
                            null
                        )
                    )
                } else {
                    showAlert("Unknown Action " + item.first!!.activityType)
                }
            }
            PlaySoloGamesAdapter.CLICK_WHOLE_ITEM -> if (ActivityType.getActivityType(item.first!!.activityType)
                == ActivityType.MESSAGE && (item.first!!.userParticipationStatus == "None")
            ) {
                showAlert("First commit to sending the message. You will then have 15 days to send the message")
            }
        }
    }

    private fun getPlayGroup(): PlayGroup? {
        val playGroupTemp: PlayGroup?
        if ((playGroup!!.playGroupId.equals("AAA", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("FFF", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("YYY", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("ZZZ", ignoreCase = true))
        ) {
            playGroupTemp = playGroup
            playGroupTemp!!.playGroupId = "AAA"
            return playGroupTemp
        } else {
            return playGroup
        }
    }

    private fun showGroupSelectionPopup(
        playGroupList: List<ActivityGroupList?>,
        first: LoginDayActivityInfoList
    ) {
        SelectPlayGroupDialogFragment.Companion.getInstance(
            playGroupList,
            OnItemClickListener({ activityGroupList: ActivityGroupList ->
                val group: PlayGroup = PlayGroup()
                group.playGroupId = activityGroupList.groupId
                if (!TextUtils.isEmpty(activityGroupList.activityGameResponseId)) {
                    first.activityGameResponseId = activityGroupList.activityGameResponseId
                }
                if (!TextUtils.isEmpty(activityGroupList.groupId) && !activityGroupList.groupId
                        .equals("0", ignoreCase = true)
                ) {
                    group.playGroupName = activityGroupList.groupName
                } else {
                    group.playGroupName = playGroup!!.playGroupName
                }
                group.isDummyGroup = playGroup!!.isDummyGroup
                navigateToDetails(first, group)
            })
        ).show(requireActivity().supportFragmentManager, "SelectPlayGroupDialogFragment")
    }

    private fun navigateToDetails(item: LoginDayActivityInfoList, playGroup: PlayGroup?) {
        navController.navigate(
            MobileNavigationDirections.actionGlobalOneToManyQuestionDetailsFragment(
                item,
                (playGroup)!!,
                null,
                null,
                true
            )
        )
    }

    private fun askQuestionToFriends(item: Triple<LoginDayActivityInfoList?, Int?, String?>) {
        val isActivityForNewQuestion: Boolean =
            playGroup!!.playGroupId.equals("YYY", ignoreCase = true)
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavAskQuestionsSheet(
                item.first!!,
                isActivityForNewQuestion,
                playGroup!!.playGroupId!!
            )
        )
    }

    private fun showSendNowSendLater(activityInfoList: LoginDayActivityInfoList) {
        val createGameActivityRequest: CreateGameActivityRequest = CreateGameActivityRequest()
        createGameActivityRequest.serviceName = "createGameActivity"
        createGameActivityRequest.screenId = "12"
        createGameActivityRequest.loginDay = activityInfoList.loginDay
        createGameActivityRequest.activityId = activityInfoList.activityId
        createGameActivityRequest.activityTypeId = activityInfoList.activityTypeId
        viewModel.createGameActivity(createGameActivityRequest).observe(
            viewLifecycleOwner,
            Observer({ apiResponse: ApiResponse<GameActivityUpdateResponse> ->
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
                        val fragment: SendNowOrSendLaterDialogFragment = getInstance(
                            apiResponse.body.message,
                            activityInfoList,
                            OnItemClickListener({ pair: android.util.Pair<LoginDayActivityInfoList?, Int> ->
                                if (pair.second == 1) {
                                    TaskAndMessageComposeDialogFragment.Companion.getInstance(
                                        pair.first,
                                        OnItemClickListener({ item1: Pair<LoginDayActivityInfoList?, UploadPostResponse?>? ->
                                            navController.navigate(
                                                MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                                    (pair.first)!!, (playGroup)!!
                                                )
                                            )
                                        })
                                    ).show(
                                        requireActivity().supportFragmentManager,
                                        "TaskMessageCompose"
                                    )
                                    //                            navController.navigate(PlayGamesFragmentDirections.actionNavPlayGamesToNavTaskMessageCompose(pair));
                                }
                            })
                        )
                        fragment.show(
                            parentFragmentManager,
                            "SendNowOrSendLaterDialogFragment"
                        )
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null && ((apiResponse.errorMessage.contains(
                            getString(R.string.unable_to_connect_host_message)
                        ) || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "getAppDemoForUser")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "createGameActivity",
                            apiResponse.code.toString()
                        )
                    }
                }
            })
        )
    }
}