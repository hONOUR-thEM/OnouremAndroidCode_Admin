package com.onourem.android.activity.ui.notifications

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentNotificationsBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.data.NotificationData
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.notifications.adapters.NotificationAdapter
import com.onourem.android.activity.ui.utils.*
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationsFragment :
    AbstractBaseViewModelBindingFragment<NotificationsViewModel, FragmentNotificationsBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var notificationAdapter: NotificationAdapter? = null
    private var userActionViewModel: UserActionViewModel? = null
    private var dashboardViewModel: DashboardViewModel? = null
    private var questionGamesViewModel: QuestionGamesViewModel? = null
    private var isReadyToMove = true
    private var isLastPage = false
    private var isLoading = false
    private var notificationIdList: MutableList<Int>? = null

    //    private List<UserList> blockedUserIds;
    private var blockedUserCount = 0
    private var loginUserId: String? = null
    private var list: MutableList<NotificationInfoList>? = null
    private var notificationData: NotificationData? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            UserActionViewModel::class.java
        )
        dashboardViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            DashboardViewModel::class.java
        )
        questionGamesViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            QuestionGamesViewModel::class.java
        )
        setHasOptionsMenu(true)
    }

    override fun viewModelType(): Class<NotificationsViewModel> {
        return NotificationsViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_notifications
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

        blockedUserCount = 0
        notificationData = (fragmentContext as DashboardActivity).getNotificationData()
        NotificationManagerCompat.from(requireActivity()).cancelAll()
        showBadgeOnHome()
        loginUserId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
        binding.swipeRefreshLayout.setOnRefreshListener {
            blockedUserCount = 0
            isLastPage = false
            isLoading = false
            if (notificationIdList != null) notificationIdList!!.clear()
            binding.swipeRefreshLayout.isRefreshing = true
            (fragmentContext as DashboardActivity).setNotificationRecyclerViewPosition(
                0,
                0
            )
            loadNotificationData(false)
        }
        linearLayoutManager = object : LinearLayoutManager(requireActivity(), VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }
        binding.rvNotification.addItemDecoration(VerticalSpaceItemDecoration(20))
        binding.rvNotification.layoutManager = linearLayoutManager
        enableSwipeToDeleteAndUndo()
        binding.rvNotification.addOnScrollListener(object :
            PaginationListener(linearLayoutManager!!) {
            override fun loadMoreItems() {
                this@NotificationsFragment.isLoading = true
                loadMoreNotifications()
            }

            override fun isLastPage(): Boolean {
                return this@NotificationsFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@NotificationsFragment.isLoading
            }
        })
        if (notificationData != null) {
            list = ArrayList()
            notificationIdList = ArrayList()
            if (!notificationData!!.list.isEmpty()) {
                for (item in notificationData!!.list) {
                    if (item != null) {
                        list!!.add(item)
                    }
                }
                notificationIdList!!.addAll(notificationData!!.notificationIdList)
                isLastPage = notificationData!!.isLastPage
                isLoading = notificationData!!.isLoading
                isReadyToMove = notificationData!!.isReadyToMove
                setAdapter(list)
            }
        } else {
            loadNotificationData(true)
        }

//        if (notificationAdapter == null) {
//            loadNotificationData(true);
//        } else {
//            binding.rvNotification.setAdapter(notificationAdapter);
//        }
        userActionViewModel!!.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()
                if (action.actionType == ActionType.REJECT_FRIEND_REQUEST) {
                    ignoreFriendRequest(action)
                }
                if (action.actionType == ActionType.REJECT_WATCHLIST_FRIEND_REQUEST) {
                    cancelWatchListRequest(action)
                }
            }
        }
        dashboardViewModel!!.notificationInfoListMutableLiveData.observe(viewLifecycleOwner) { from: String ->
            if (from.equals("NotificationsFragment", ignoreCase = true)) {
                loadNotificationData(true)
            }
        }
        questionGamesViewModel!!.inAppReviewPopup.observe(viewLifecycleOwner) { todayAnswersCount: Boolean? ->
            val todayAnswerCount = preferenceHelper!!.getInt(
                Constants.KEY_TOTAL_QUES_ANSWERED_TODAY
            )
            val totalAnswerCount =
                preferenceHelper!!.getInt(Constants.KEY_TOTAL_QUES_ANSWERED_BEFORE_TODAY)
            val noOfTimeRequestReviewRaised =
                preferenceHelper!!.getInt(Constants.KEY_NUMBER_OF_TIME_APP_REVIEW_REQUESTS_RAISED)
            var noOfDaysAfterShownReviewLastTime = 0L
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            var lastTimeReviewShownDate =
                preferenceHelper!!.getString(Constants.KEY_LAST_TIME_WHEN_REVIEW_POPUP_WAS_SHOWN)
            if (lastTimeReviewShownDate.equals("", ignoreCase = true)) {
                lastTimeReviewShownDate = "2000-01-01 01:01:01"
            }
            try {
                val date = sdf.parse(lastTimeReviewShownDate)
                if (date != null) {
                    noOfDaysAfterShownReviewLastTime = getDaysBetweenDates(Date(), date)
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (todayAnswerCount >= 3 && totalAnswerCount > 20 && noOfDaysAfterShownReviewLastTime > 15 && noOfTimeRequestReviewRaised < 3) {
                dashboardViewModel!!.setShowInAppReview(true)
            }
        }
    }

    private fun getDaysBetweenDates(d1: Date, d2: Date): Long {
        return TimeUnit.MILLISECONDS.toDays(d1.time - d2.time)
    }

    //    private void performPullToRefresh() {
    //        if (!binding.swipeRefreshLayout.isRefreshing()) {
    //            blockedUserCount = 0;
    //            isLastPage = false;
    //            isLoading = false;
    //
    //            if (notificationIdList != null)
    //                notificationIdList.clear();
    //
    //            binding.swipeRefreshLayout.setRefreshing(true);
    //            loadNotificationData(false);
    //        }
    //    }
    private fun loadMoreNotifications() {
        if (notificationIdList == null || notificationIdList!!.isEmpty()) {
            isLastPage = true
            isLoading = false
            setFooterMessage()
            return
        }
        val start = notificationAdapter!!.itemCount + blockedUserCount
        val end =
            if (notificationIdList!!.size - start > PaginationListener.PAGE_ITEM_SIZE) start + PaginationListener.PAGE_ITEM_SIZE else notificationIdList!!.size
        if (start >= end) {
            isLastPage = true
            isLoading = false
            setFooterMessage()
            return
        }
        val ids: MutableList<Int> = ArrayList()
        for (i in start until end) {
            val id = notificationIdList!![i]
            if (id != null) {
                ids.add(id)
            }
        }

        //Log.e("####", String.format("start: %d ; end: %d ; sent: %s ; total: %d", start, end, ids, notificationIdList.size()));
        //Log.e("####", String.format("sent ids: %s", ids));
        viewModel.getNextNotificationInfo(Utilities.getTokenSeparatedString(ids, ",")).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetNextNoficationInfo> ->
            if (apiResponse.loading) {
                if (notificationAdapter != null) {
                    notificationAdapter!!.addLoading()
                }
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                if (notificationAdapter != null) {
                    notificationAdapter!!.removeLoading()
                }
                isLoading = false
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (apiResponse.body.notificationInfoList == null || apiResponse.body.notificationInfoList!!.isEmpty()) {
                        isLastPage = true
                        setFooterMessage()
                    } else {
                        //blockedUserCount += BlockedUsersUtility.filterBlockedUsersFromNotifications(blockedUserIds, list);
                        notificationAdapter!!.addItems(apiResponse.body.notificationInfoList)
                        if (apiResponse.body.notificationInfoList!!.size + blockedUserCount < PaginationListener.PAGE_ITEM_SIZE) {
                            isLastPage = true
                            setFooterMessage()
                        }
                        list!!.addAll(apiResponse.body.notificationInfoList!!)
                        setNotificationDataToActivity()
                        // Log.e("####", String.format("server: %d", apiResponse.body.getNotificationInfoList().size()));
                    }
                } else {
                    isLastPage = true
                    setFooterMessage()
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                isLoading = false
                if (notificationAdapter != null) {
                    notificationAdapter!!.removeLoading()
                }
                hideProgress()
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getNextNotificationInfo")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getNextNotificationInfo",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun setFooterMessage() {
        isLoading = false
        if (notificationAdapter != null) {
            val footerMessage: String
            footerMessage = if (notificationAdapter!!.itemCount == 0) {
                "You did not receive any notifications so far."
            } else {
                "These are all the notifications you have received so far."
            }
            binding.rvNotification.postDelayed(
                { notificationAdapter!!.addFooter(footerMessage) },
                200
            )
        }
    }

    private fun loadNotificationData(showProgress: Boolean) {
        viewModel.newNotificationInfo().observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetNewNotificationInfo> ->
            if (apiResponse.loading) {
                if (showProgress) {
                    showProgress()
                }
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (!showProgress) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                binding.tvMessage.visibility = View.GONE
                isLoading = false
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    viewModel.resetNotificationCount()
                    //                    CustomScreenPopup customScreenPopup = new CustomScreenPopup();
//                    customScreenPopup.setTitle("");
//                    customScreenPopup.setPopupType("Invite");
//                    customScreenPopup.setBtnOneText("Invite Friends");
//                    customScreenPopup.setImageName("https://dtv39ga8f8jxc.cloudfront.net/images/campaign/large/NotificationPopUpImageSmall.jpg");
//                    customScreenPopup.setFrequencyInHour(1);
//                    customScreenPopup.setDescription("Notification Screen popup");
                    (fragmentContext as DashboardActivity).showBanner(
                        apiResponse.body.isPopupRequired,
                        apiResponse.body.customScreenPopup
                    )
                    notificationIdList = apiResponse.body.notificationIds as MutableList<Int>?
                    list = apiResponse.body.notificationInfoList as MutableList<NotificationInfoList>?
                    //blockedUserCount += BlockedUsersUtility.filterBlockedUsersFromNotifications(blockedUserIds, list);
                    setNotificationDataToActivity()
                    setAdapter(list)
                } else {
                    binding.tvMessage.visibility = View.VISIBLE
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                if (!showProgress) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }
                showAlert(apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getNewNotificationInfo")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getNewNotificationInfo",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun setNotificationDataToActivity() {
        val notificationData =
            NotificationData(isReadyToMove, isLastPage, isLoading, notificationIdList, list)
        (fragmentContext as DashboardActivity).setNotificationData(notificationData)
    }

    private fun setAdapter(notificationInfoLists: List<NotificationInfoList>?) {
        if (notificationInfoLists == null || notificationInfoLists.isEmpty()) {
            binding.tvMessage.visibility = View.VISIBLE
            isLastPage = true
        } else {
            binding.tvMessage.visibility = View.GONE
        }
        //        if (notificationAdapter == null) {
        notificationAdapter = NotificationAdapter(
            navController,
            notificationInfoLists
        ) { item: Pair<Int, NotificationInfoList> ->

            if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                if (isReadyToMove) {
                    item.second.checkedStatus = "Y"
                    updateNotification(item.second)
                    setNotificationDataToActivity()
                    if (item.first == NotificationAdapter.CLICK_ACCEPT) {
                        acceptFriendRequest(item.second)
                    } else if (item.first == NotificationAdapter.CLICK_IGNORE) {
                        val title = "Do you want to reject the friend request?"
                        val actions = ArrayList<Action<*>>()
                        actions.add(
                            Action(
                                getString(R.string.action_label_reject_friend_request),
                                R.color.color_black,
                                ActionType.REJECT_FRIEND_REQUEST,
                                item.second
                            )
                        )
                        val bundle = Bundle()
                        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
                        navController.navigate(
                            MobileNavigationDirections
                                .actionGlobalNavUserActionBottomSheet(title, bundle, "")
                        )
                    }
                    if (item.first == NotificationAdapter.CLICK_ACCEPT_WATCHLIST) {
                        acceptWatchlistRequest(item.second, item.first)
                    } else if (item.first == NotificationAdapter.CLICK_IGNORE_WATCHLIST) {
                        val title = "Do you want to ignore the Watch List invitation?"
                        val actions = ArrayList<Action<*>>()
                        actions.add(
                            Action(
                                getString(R.string.action_label_reject_from_watch_list),
                                R.color.color_black,
                                ActionType.REJECT_WATCHLIST_FRIEND_REQUEST,
                                item.second
                            )
                        )
                        val bundle = Bundle()
                        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
                        navController.navigate(
                            MobileNavigationDirections
                                .actionGlobalNavUserActionBottomSheet(title, bundle, "")
                        )
                    } else if (item.first == NotificationAdapter.CLICK_READ_POST) {
                        getUserProfileFeeds(item.second)
                    } else if (item.first == NotificationAdapter.CLICK_PROFILE) {
                        if (!item.second.actionByUserID.equals("4264", ignoreCase = true)) {

                            if (item.second.anonymousOnOff == "Y" && loginUserId != item.second.actionByUserID) {
                                showAlert("Identity of the friend who sent this message will be revealed after 48 hrs from the time the message was sent")
                            } else {
                                if (!item.second.actionType.equals(
                                        NotificationActionType.IRRELEVANT_GAME_RESPONSE,
                                        ignoreCase = true
                                    )
                                ) navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavProfile(
                                        null,
                                        item.second.actionByUserID
                                    )
                                )
                            }

                        } else {
                            showAlert("You can't access profile of this admin user")
                        }
                    } else if (item.first == NotificationAdapter.CLICK_WHOLE) {
                        when (item.second.actionType) {
                            NotificationActionType.FRIEND_REQUEST,
                            NotificationActionType.STARTED_FOLLOWING,
                            NotificationActionType.FRIEND_REQUEST_ACCEPTED -> navController.navigate(
                                MobileNavigationDirections.actionGlobalNavProfile(
                                    null,
                                    item.second.actionByUserID
                                )
                            )

                            NotificationActionType.FRIEND_SUGGESTION_REQUEST -> {
                                val direction = MobileNavigationDirections.actionGlobalNavSearch(
                                    false,
                                    loginUserId!!,
                                    true,
                                    null
                                )
                                //                                direction.setInputUserId(item.second.getActionByUserID());
                                direction.inputUserId = loginUserId
                                navController.navigate(direction)
                            }

                            NotificationActionType.SEND_USER_CHAT_MESSAGE -> {
                                val conversation = Conversation()
                                conversation.id = ""
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavConversations(conversation)
                                )
                            }

                            NotificationActionType.POST_REQUEST,
                            NotificationActionType.POST_SEND,
                            NotificationActionType.POST_REQUEST_ACCEPTED,
                            NotificationActionType.LIKED_POST,
                            NotificationActionType.SHARED_POST,
                            NotificationActionType.REPLIED_POST,
                            NotificationActionType.COMMENTED_POST -> getUserProfileFeeds(
                                item.second
                            )

                            NotificationActionType.ONE_TO_1_GAME_RECEIVED,
                            NotificationActionType.PLAY_GROUP_COMMENTED_GAME,
                            NotificationActionType.COMMENTED_GAME,
                            NotificationActionType.PLAY_GROUP_1_TO_M_RESPONSE_SUBMITTED,
                            NotificationActionType.ONE_TO_M_RESPONSE_SUBMITTED,
                            NotificationActionType.D_TO_1_RESPONSE_SUBMITTED,
                            NotificationActionType.ONE_TO_1_RESPONSE_SUBMITTED,
                            NotificationActionType.ONE_TO_M_GAME_RECEIVED,
                            NotificationActionType.QUESTION_ASKED_BY_ADMIN,
                            NotificationActionType.NEW_USER_QUESTION_ASKED_BY_ADMIN,
                            NotificationActionType.D_TO_1_GAME_RECEIVED,
                            NotificationActionType.ONE_TO_M_SOLO_RESPONSE_SUBMITTED,
                            NotificationActionType.TO_ALL_FRIEND_NOT_ANSWERED_IN_PLAY_GROUP,
                            NotificationActionType.NON_FRIEND_ANSWER_IN_PLAY_GROUP,
                            NotificationActionType.FRIEND_COMMENTING_ON_FRIEND_ANSWER,
                            NotificationActionType.FRIEND_COMMENTING_ON_ANSWER_I_HAVE_COMMENTED,
                            NotificationActionType.FRIEND_COMMENTING_ON_STRANGER_ANSWER,
                            NotificationActionType.FRIEND_COMMENTING_ON_FRIEND_ANSWER_IN_PLAYGROUP,
                            NotificationActionType.FRIEND_COMMENTING_ON_ANSWER_I_HAVE_COMMENTED_IN_PLAYGROUP,
                            NotificationActionType.FRIEND_COMMENTING_ON_STRANGER_ANSWER_IN_PLAYGROUP,
                            NotificationActionType.NON_FRIEND_COMMENTING_ON_FRIEND_ANSWER_IN_PLAYGROUP,
                            NotificationActionType.IRRELEVANT_GAME_RESPONSE ->                             //End
                                // newly added constants for notification type 29th July 2020
                                getActivityInfoForNotification(item.second)

                            NotificationActionType.USER_ADDED_TO_PLAY_GROUP,
                            NotificationActionType.NOTIFY_ADMIN_FOR_NEW_LINK_USER,
                            NotificationActionType.ADMIN_ADDED_TO_PLAY_GROUP,
                            NotificationActionType.LINK_USER_ADDED_TO_PLAY_GROUP,
                            NotificationActionType.TASK_OR_MESSAGE_ASKED_BY_ADMIN_IN_PLAYGROUP,
                            NotificationActionType.TASK_ASKED_BY_ADMIN_IN_PLAYGROUP,
                            NotificationActionType.MESSAGE_ASKED_BY_ADMIN_IN_PLAYGROUP,
                            NotificationActionType.SURVEY_ASKED_BY_ADMIN_IN_PLAYGROUP,
                            NotificationActionType.EXTERNAL_ASKED_BY_ADMIN_IN_PLAYGROUP,
                            NotificationActionType.PLAY_GROUP_1_TO_M_GAME_RECEIVED,
                            NotificationActionType.PLAY_GROUP_1TOM_GAME_RECEIVED_BY_ADMIN,
                            NotificationActionType.NEW_OCLUB_EXTERNAL_ASKED_BY_ADMIN,
                            NotificationActionType.NEW_OCLUB_SURVEY_ASKED_BY_ADMIN,
                            NotificationActionType.NEW_OCLUB_CARD_BY_ADMIN,
                            NotificationActionType.NEW_OCLUB_QUESTION_ASKED_BY_ADMIN,
                            NotificationActionType.NEW_OCLUB_TASK_ASKED_BY_ADMIN,
                            NotificationActionType.NEW_OCLUB_MESSAGE_ASKED_BY_ADMIN,
                            NotificationActionType.CARD_ASKED_BY_ADMIN_IN_PLAYGROUP ->

                                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_notifications) {
                                    navController.navigate(
                                        NotificationsFragmentDirections.actionNavNotificationsToNavQuestionGames(
                                            item.second.playgroupId,
                                            "notification"
                                        )
                                    )
                                }


                            NotificationActionType.SURVEY_RECEIVED -> navController.navigate(
                                MobileNavigationDirections.actionGlobalNavAllSurveys()
                            )

                            NotificationActionType.O_CLUB_QUESTION_LIST -> getPlayGroupById(item.second.playgroupId)
                            NotificationActionType.INVITE_FRIENDS -> navController.navigate(
                                MobileNavigationDirections.actionGlobalNavInviteFriends()
                            )

                            NotificationActionType.BOND_GAME -> {

                                if ((fragmentContext as DashboardActivity).isBond003AvailableForUsers() > 0) {
                                    if ((fragmentContext as DashboardActivity).isUserPlayedGame() > 0) {
                                        navController.navigate(
                                            MobileNavigationDirections.actionGlobalNavFriendsThoughts(
                                                true
                                            )
                                        )
                                    } else {
                                        if ((fragmentContext as DashboardActivity).isUserPlayedGame() == -1) {
                                            Toast.makeText(
                                                fragmentContext,
                                                "Fetching Game data, please retry in few seconds",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            navController.navigate(
                                                MobileNavigationDirections.actionGlobalNavFriendsCircleMain()
                                            )
                                        }
                                    }
                                } else {
                                    if ((fragmentContext as DashboardActivity).isBond003AvailableForUsers() == -1) {
                                        Toast.makeText(
                                            fragmentContext,
                                            "Please try again after few seconds.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            fragmentContext,
                                            "This game is not available for the time being. Please try again later.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            }

                            NotificationActionType.REVIEW_ONOUREM -> {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(
                                    String.format(
                                        "https://play.google.com/store/apps/details?id=%s",
                                        requireActivity().packageName
                                    )
                                )

                                try {
                                    startActivity(intent)
                                } catch (ex: ActivityNotFoundException) {
                                    Toast.makeText(fragmentContext, "NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
                                }
                            }

                            NotificationActionType.WATCHLIST_REQUEST,
                            NotificationActionType.WATCH_REQUEST_ACCEPTED -> getUserWatchList(
                                item.second.actionByUserID
                            )

                            NotificationActionType.CARD_BY_ADMIN, NotificationActionType.NEW_USER_CARD_BY_ADMIN -> navController.navigate(
                                MobileNavigationDirections.actionGlobalNavFunCards(
                                    preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID)
                                )
                            )

                            NotificationActionType.ACTIVATED_COUPON -> navController.navigate(
                                MobileNavigationDirections.actionGlobalNavProfile(
                                    null,
                                    item.second.actionByUserID
                                )
                            )

                            NotificationActionType.APPROVE_AUDIO_BY_ADMIN,
                            NotificationActionType.LIKE_AUDIO,
                            NotificationActionType.USER_UPLOADED_AUDIO -> {
                                preferenceHelper!!.putValue(Constants.KEY_SELECTED_FILTER_INT, "")
                                navController.navigate(
                                    (MobileNavigationDirections.actionGlobalNavVocalsMain(
                                        "",
                                        "", "", item.second.audioIdFromNotification!!
                                    ))
                                )
//                            navController.navigate(
//                                MobileNavigationDirections.actionGlobalNavOthersVocals(
//                                    "",
//                                    "Trending Vocals",
//                                    "",
//                                    item.second.audioIdFromNotification!!
//                                )
//                            )
                            }
                            NotificationActionType.NONE -> {}

                            NotificationActionType.TASK_OR_MESSAGE_ASKED_BY_ADMIN,
                            NotificationActionType.TASK_ASKED_BY_ADMIN,
                            NotificationActionType.NEW_USER_TASK_ASKED_BY_ADMIN,
                            NotificationActionType.NEW_USER_MESSAGE_ASKED_BY_ADMIN,
                            NotificationActionType.MESSAGE_ASKED_BY_ADMIN -> {
                                getActivityInfoForNotification(item.second)
                            }

                            NotificationActionType.EXTERNAL_ASKED_BY_ADMIN, NotificationActionType.NEW_USER_EXTERNAL_ASKED_BY_ADMIN -> {
                                getExternalActivityForNotification(item.second)
                            }

                            NotificationActionType.SURVEY_ASKED_BY_ADMIN, NotificationActionType.NEW_USER_SURVEY_ASKED_BY_ADMIN -> {
                                navController.navigate(
                                    NotificationsFragmentDirections.actionNavNotificationsToNavAnonymousSurvey(
                                        item.second!!.activityId!!,
                                    )
                                )
                            }
                            else -> {}
                        }
                    }
                }
            } else {
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }

        }

        binding.rvNotification.adapter = notificationAdapter
        if ((fragmentContext as DashboardActivity).getNotificationRecyclerViewPosition() != -1) linearLayoutManager!!.scrollToPositionWithOffset(
            (fragmentContext as DashboardActivity).getNotificationRecyclerViewPosition(),
            (fragmentContext as DashboardActivity).getNotificationRecyclerViewPositionTopView()
        )

//        } else {
//
//            notificationAdapter.resetData(notificationInfoLists);
//            notificationAdapter.notifyDataSetChanged();
//        }
    }

    private fun getUserWatchList(notificationById: String?) {
        dashboardViewModel!!.getUserWatchList(notificationById!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetWatchListResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        preferenceHelper!!.putValue(
                            Constants.KEY_GAME_POINTS,
                            apiResponse.body.gamePoint
                        )
                        if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_notifications) {
                            navController.navigate(
                                NotificationsFragmentDirections.actionNavNotificationsToNavWatchList(
                                    apiResponse.body,
                                    "NotificationsFragment"
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
                            AppUtilities.showLog("Network Error", "getUserWatchList")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getUserWatchList",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun getPlayGroupById(id: String?) {
        val questionGamesViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            QuestionGamesViewModel::class.java
        )
        questionGamesViewModel.getUserPlayGroupsById(id!!)
            .observe(viewLifecycleOwner) { playGroupsResponseApiResponse: ApiResponse<PlayGroupsResponse> ->
                if (playGroupsResponseApiResponse.loading) {
                    showProgress()
                } else if (playGroupsResponseApiResponse.isSuccess && playGroupsResponseApiResponse.body != null) {
                    hideProgress()
                    if (playGroupsResponseApiResponse.body.errorCode.equals(
                            "000",
                            ignoreCase = true
                        )
                    ) {
                        val playGroupsResponse = playGroupsResponseApiResponse.body
                        if (playGroupsResponse.playGroup.isNotEmpty()) {
                            val playGroup = playGroupsResponse.playGroup[0]
                            questionGamesViewModel.reloadUI(false)
                            navController.navigate(
                                NotificationsFragmentDirections.actionNavNotificationsToNavPlayGames(
                                    playGroup,
                                    ""
                                )
                            )
                        }
                    } else {
                        showAlert(
                            getString(R.string.label_network_error),
                            playGroupsResponseApiResponse.body.errorMessage
                        )
                    }
                } else {
                    hideProgress()
                    showAlert(
                        getString(R.string.label_network_error),
                        playGroupsResponseApiResponse.errorMessage
                    )
                    if (playGroupsResponseApiResponse.errorMessage != null
                        && (playGroupsResponseApiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || playGroupsResponseApiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "getUserPlayGroupsById")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getUserPlayGroupsById",
                            playGroupsResponseApiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun getUserProfileFeeds(item: NotificationInfoList) {
        viewModel.getUserProfileFeeds(loginUserId!!, item.actionPostID!!, item.notificationId!!)
            .observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<GetUserProfileFeeds> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        getActivityInfoForNotificationMessage(item, response)
                    } else {
                        showAlert(response.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "getUserProfileFeeds")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getUserProfileFeeds",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    override fun onPause() {
        super.onPause()
        val positionIndex = linearLayoutManager!!.findFirstVisibleItemPosition()
        val startView = binding.rvNotification.getChildAt(0)
        val topView =
            if (startView == null) 0 else startView.top - binding.rvNotification.paddingTop
        (fragmentContext as DashboardActivity).setNotificationRecyclerViewPosition(
            positionIndex,
            topView
        )
    }

    private fun getActivityInfoForNotificationMessage(
        item: NotificationInfoList,
        apiResponseGetUserProfileFeeds: GetUserProfileFeeds?
    ) {
        if (TextUtils.isEmpty(item.activityId) || item.activityId.equals("0", ignoreCase = true)) {
            if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_notifications) {
                navController.navigate(
                    NotificationsFragmentDirections.actionNavNotificationsToNavNotificationsMessageDetails(
                        apiResponseGetUserProfileFeeds!!,
                        null
                    )
                )
            }
        } else {
            viewModel.getActivityInfoForNotificataion(
                apiResponseGetUserProfileFeeds!!.feedsList!![0].activityId,
                item.activityGameResponseId!!,
                item.notificationId!!
            ).observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<GetActivityInfoForNotificataion> ->
                if (apiResponse.body != null && apiResponse.body.errorCode.equals(
                        "000",
                        ignoreCase = true
                    )
                ) {
                    val response = apiResponse.body
                    if (response.loginDayActivityInfoList != null && response.loginDayActivityInfoList!!.isNotEmpty()) {
                        val loginDayActivityInfoList = response.loginDayActivityInfoList!![0]
                        if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_notifications) {
                            navController.navigate(
                                NotificationsFragmentDirections.actionNavNotificationsToNavNotificationsMessageDetails(
                                    apiResponseGetUserProfileFeeds,
                                    loginDayActivityInfoList
                                )
                            )
                        }
                    } else {
                        Snackbar.make(
                            requireView(),
                            "Activity does not found, it is either deleted or expired",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else if (!apiResponse.loading) {
                    if (apiResponse.body != null) {
                        showAlert(
                            getString(R.string.label_network_error),
                            apiResponse.body.errorMessage
                        )
                    }
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "getActivityInfoForNotificataion")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getActivityInfoForNotificataion",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
        }
    }

    private fun getExternalActivityForNotification(
        item: NotificationInfoList
    ) {
        viewModel.getExternalActivityFromNotification(
            item.activityId
        ).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetActivityInfoForNotificataion> ->
            if (apiResponse.body != null && apiResponse.body.errorCode.equals(
                    "000",
                    ignoreCase = true
                )
            ) {
                val response = apiResponse.body
                if (response.loginDayActivityInfoList != null && response.loginDayActivityInfoList!!.isNotEmpty()) {
                    val loginDayActivityInfoList = response.loginDayActivityInfoList!![0]
                    if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_notifications) {
                        navController.navigate(
                            NotificationsFragmentDirections.actionNavNotificationsToNavNotificationExternalPostDetailsFragment(
                                loginDayActivityInfoList
                            )
                        )
                    }
                } else {
                    Snackbar.make(
                        requireView(),
                        "Activity does not found, it is either deleted or expired",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else if (!apiResponse.loading) {
                if (apiResponse.body != null) {
                    showAlert(
                        getString(R.string.label_network_error),
                        apiResponse.body.errorMessage
                    )
                }
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getActivityInfoForNotificataion")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getActivityInfoForNotificataion",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun getActivityInfoForNotification(notificationItem: NotificationInfoList) {
        viewModel.getActivityInfoForNotificataion(
            notificationItem.activityId!!,
            notificationItem.activityGameResponseId!!,
            notificationItem.notificationId!!
        ).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetActivityInfoForNotificataion> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                val response = apiResponse.body
                if (response.errorCode.equals("000", ignoreCase = true)) {
                    if (response.loginDayActivityInfoList != null && response.loginDayActivityInfoList!!.isNotEmpty()) {
                        val loginDayActivityInfoList = response.loginDayActivityInfoList!![0]
                        val playGroup = PlayGroup()
                        when (notificationItem.actionType) {
                            NotificationActionType.ONE_TO_1_GAME_RECEIVED,
                            NotificationActionType.ONE_TO_1_RESPONSE_SUBMITTED -> {
                                playGroup.playGroupId = notificationItem.playgroupId
                                playGroup.playGroupName =
                                    getString(R.string.title_two_way_question)
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavOneToOneQuestionDetails(
                                        loginDayActivityInfoList,
                                        playGroup,
                                        notificationItem.gameId
                                    )
                                )
                            }
                            NotificationActionType.D_TO_1_GAME_RECEIVED,
                            NotificationActionType.D_TO_1_RESPONSE_SUBMITTED -> {
                                playGroup.playGroupId = notificationItem.playgroupId
                                playGroup.playGroupName =
                                    getString(R.string.title_one_way_question)
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalDToOneQuestionDetailsFragment(
                                        loginDayActivityInfoList,
                                        playGroup,
                                        notificationItem.gameId
                                    )
                                )
                            }
                            NotificationActionType.TASK_OR_MESSAGE_ASKED_BY_ADMIN_IN_PLAYGROUP,
                            NotificationActionType.TASK_OR_MESSAGE_ASKED_BY_ADMIN,
                            NotificationActionType.TASK_ASKED_BY_ADMIN,
                            NotificationActionType.NEW_USER_TASK_ASKED_BY_ADMIN,
                            NotificationActionType.NEW_USER_MESSAGE_ASKED_BY_ADMIN,
                            NotificationActionType.MESSAGE_ASKED_BY_ADMIN -> {
                                playGroup.playGroupId = notificationItem.playgroupId
                                playGroup.playGroupName =
                                    getString(R.string.title_one_way_question)
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                        loginDayActivityInfoList,
                                        playGroup,
                                    )
                                )
                            }
                            NotificationActionType.ONE_TO_M_GAME_RECEIVED,
                            NotificationActionType.ONE_TO_M_RESPONSE_SUBMITTED,
                            NotificationActionType.QUESTION_ASKED_BY_ADMIN, NotificationActionType.NEW_USER_QUESTION_ASKED_BY_ADMIN,
                            NotificationActionType.ONE_TO_M_SOLO_RESPONSE_SUBMITTED -> {
                                //End
                                // newly added constants for notification type 29th July 2020
                                if (notificationItem.playgroupId.equals(
                                        "",
                                        ignoreCase = true
                                    )
                                ) {
                                    playGroup.playGroupId = "AAA"
                                } else {
                                    playGroup.playGroupId = notificationItem.playgroupId
                                }
                                playGroup.playGroupName = getString(R.string.label_solo_games)
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalOneToManyQuestionDetailsFragment(
                                        loginDayActivityInfoList,
                                        playGroup,
                                        notificationItem.gameId,
                                        notificationItem.activityGameResponseId,
                                        false
                                    )
                                )
                            }
                            NotificationActionType.PLAY_GROUP_1_TO_M_RESPONSE_SUBMITTED,
//                            NotificationActionType.PLAY_GROUP_1_TO_M_GAME_RECEIVED,
//                            NotificationActionType.PLAY_GROUP_1TOM_GAME_RECEIVED_BY_ADMIN,
                            NotificationActionType.TO_ALL_FRIEND_NOT_ANSWERED_IN_PLAY_GROUP,
                            NotificationActionType.NON_FRIEND_ANSWER_IN_PLAY_GROUP -> {
                                //End
                                // newly added constants for notification type 29th July 2020
                                playGroup.playGroupId = notificationItem.playgroupId
                                playGroup.playGroupName = notificationItem.playGroupName
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalOneToManyQuestionDetailsFragment(
                                        loginDayActivityInfoList,
                                        playGroup,
                                        notificationItem.gameId,
                                        notificationItem.activityGameResponseId,
                                        false
                                    )
                                )
                            }
                            NotificationActionType.IRRELEVANT_GAME_RESPONSE -> {
                                if (notificationItem.playgroupId.equals(
                                        "",
                                        ignoreCase = true
                                    )
                                ) {
                                    playGroup.playGroupId = "AAA"
                                    playGroup.playGroupName =
                                        getString(R.string.label_solo_games)
                                } else {
                                    playGroup.playGroupId = notificationItem.playgroupId
                                    playGroup.playGroupName = notificationItem.playGroupName
                                }
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalOneToManyQuestionDetailsFragment(
                                        loginDayActivityInfoList,
                                        playGroup,
                                        notificationItem.gameId,
                                        notificationItem.activityGameResponseId,
                                        false
                                    )
                                )
                            }
                            NotificationActionType.COMMENTED_GAME,
                            NotificationActionType.FRIEND_COMMENTING_ON_FRIEND_ANSWER,
                            NotificationActionType.FRIEND_COMMENTING_ON_ANSWER_I_HAVE_COMMENTED,
                            NotificationActionType.FRIEND_COMMENTING_ON_STRANGER_ANSWER,
                            NotificationActionType.FRIEND_COMMENTING_ON_FRIEND_ANSWER_IN_PLAYGROUP,
                            NotificationActionType.FRIEND_COMMENTING_ON_ANSWER_I_HAVE_COMMENTED_IN_PLAYGROUP,
                            NotificationActionType.FRIEND_COMMENTING_ON_STRANGER_ANSWER_IN_PLAYGROUP,
                            NotificationActionType.NON_FRIEND_COMMENTING_ON_FRIEND_ANSWER_IN_PLAYGROUP ->                                     //End
                                // newly added constants for notification type 29th July 2020
                                if (ActivityType.getActivityType(loginDayActivityInfoList.activityType)
                                    == ActivityType.ONE_TO_ONE
                                ) {
                                    playGroup.playGroupId = notificationItem.playgroupId
                                    playGroup.playGroupName =
                                        getString(R.string.title_two_way_question)
                                    navController.navigate(
                                        MobileNavigationDirections.actionGlobalNavOneToOneQuestionDetails(
                                            loginDayActivityInfoList,
                                            playGroup,
                                            notificationItem.gameId
                                        )
                                    )
                                } else if (ActivityType.getActivityType(loginDayActivityInfoList.activityType)
                                    == ActivityType.D_TO_ONE
                                ) {
                                    playGroup.playGroupId = notificationItem.playgroupId
                                    playGroup.playGroupName =
                                        getString(R.string.title_one_way_question)
                                    navController.navigate(
                                        MobileNavigationDirections.actionGlobalDToOneQuestionDetailsFragment(
                                            loginDayActivityInfoList,
                                            playGroup,
                                            notificationItem.gameId
                                        )
                                    )
                                } else if (ActivityType.getActivityType(loginDayActivityInfoList.activityType) == ActivityType.ONE_TO_MANY) {
                                    if (notificationItem.playgroupId.equals(
                                            "",
                                            ignoreCase = true
                                        )
                                    ) {
                                        playGroup.playGroupId = "AAA"
                                    } else {
                                        playGroup.playGroupId = notificationItem.playgroupId
                                    }
                                    playGroup.playGroupName =
                                        getString(R.string.label_solo_games)
                                    navController.navigate(
                                        MobileNavigationDirections.actionGlobalOneToManyQuestionDetailsFragment(
                                            loginDayActivityInfoList,
                                            playGroup,
                                            notificationItem.gameId,
                                            notificationItem.activityGameResponseId,
                                            false
                                        )
                                    )
                                } else {
                                    showAlert("Unknown Action " + loginDayActivityInfoList.activityType)
                                }
                            NotificationActionType.PLAY_GROUP_COMMENTED_GAME -> if (ActivityType.getActivityType(
                                    Objects.requireNonNull(loginDayActivityInfoList.activityType)
                                ) == ActivityType.ONE_TO_MANY
                            ) {
                                playGroup.playGroupId = notificationItem.playgroupId
                                playGroup.playGroupName = notificationItem.playGroupName
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalOneToManyQuestionDetailsFragment(
                                        loginDayActivityInfoList,
                                        playGroup,
                                        notificationItem.gameId,
                                        notificationItem.activityGameResponseId,
                                        false
                                    )
                                )
                            }
                        }
                    } else {
                        Snackbar.make(
                            requireView(),
                            "Activity does not found, it is either deleted or expired",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    showAlert(getString(R.string.label_network_error), response.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                if (apiResponse.errorMessage != null
                    && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getActivityInfoForNotificataion")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getActivityInfoForNotificataion",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun ignoreFriendRequest(action: Action<Any?>?) {
        val item = action!!.data as NotificationInfoList
        viewModel.cancelPendingRequest(item.actionByUserID!!, "21")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserActionStandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        notifyItemRemoved(item)
                    } else {
                        showAlert(getString(R.string.label_network_error), response.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "cancelPendingRequest")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "cancelPendingRequest",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun cancelWatchListRequest(action: Action<*>) {
        val item = action.data as NotificationInfoList
        dashboardViewModel!!.cancelWatchListPendingRequest(item.actionByUserID!!).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    notifyItemRemoved(item)
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
                        AppUtilities.showLog("Network Error", "cancelWatchListPendingRequest")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "cancelWatchListPendingRequest",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun acceptFriendRequest(item: NotificationInfoList) {
        viewModel.acceptPendingRequest(item.actionByUserID!!, "21")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserActionStandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        item.checkedStatus = "Y"
                        item.message = item.actionByName + " and You are now friends"
                        item.actionType = NotificationActionType.FRIEND_REQUEST_ACCEPTED
                        notifyDataSetChanged(item)
                        updateNotification(item)
                    } else {
                        showAlert(getString(R.string.label_network_error), response.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "acceptPendingRequest")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "acceptPendingRequest",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun acceptWatchlistRequest(item: NotificationInfoList, first: Int) {
        dashboardViewModel!!.acceptPendingWatchRequest(item.actionByUserID!!).observe(
            viewLifecycleOwner
        ) { standardResponseApiResponse: ApiResponse<AcceptPendingWatchResponse>? ->
            item.checkedStatus = "Y"
            item.message = item.actionByName + " and you are now on each others WatchList"
            item.actionType = NotificationActionType.WATCH_REQUEST_ACCEPTED
            notifyDataSetChanged(item)
            updateNotification(item)
        }
    }

    private fun updateNotification(item: NotificationInfoList) {
        viewModel.updateNotificationStaus(
            item.notificationId,
            item.checkedStatus,
            item.activityId,
            item.actionType
        ).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<UpdateNotificationStaus> ->
            if (apiResponse.isSuccess && apiResponse.body != null) {
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    notifyDataSetChanged(item)
                }
            }
        }
    }

    private fun notifyDataSetChanged(data: NotificationInfoList) {
        if (notificationAdapter != null && isVisible) {
            notificationAdapter!!.notifyDataUpdated(data)
        }
    }

    private fun notifyItemRemoved(data: NotificationInfoList) {
        if (notificationAdapter != null) {
            notificationAdapter!!.notifyItemRemoved(data)
        }
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeHelper: SwipeHelper = object : SwipeHelper(requireActivity()) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder,
                underlayButtons: MutableList<UnderlayButton>
            ) {
                underlayButtons.add(UnderlayButton(
                    "Delete",
                    0,
                    ContextCompat.getColor(fragmentContext, R.color.color_6)
                ) { pos: Int ->
                    isReadyToMove = false
                    val item = notificationAdapter!!.data[pos]
                    if (item != null) {
                        notificationAdapter!!.removeItem(pos)
                        deleteNotification(item)
                        val snackbar = Snackbar.make(
                            binding.root,
                            "Notification was removed from the list.",
                            Snackbar.LENGTH_LONG
                        )
                        snackbar.setTextColor(Color.WHITE)
                        snackbar.show()
                    }
                })
            }
        }
        swipeHelper.attachToRecyclerView(binding.rvNotification)
    }

    private fun deleteNotification(item: NotificationInfoList) {
        viewModel.deleteNotification(item.notificationId ?: "", item.activityId ?: "", item.actionType ?: "")
            .observe(this) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
//                showProgress();
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
//                hideProgress();
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        isReadyToMove = true
                        if (notificationAdapter != null && isVisible) notificationAdapter!!.notifyDataSetChanged()
                    } else {
//                    showAlert(getString(R.string.label_network_error), apiResponse.body.getErrorMessage());
                    }
                } else {
//                hideProgress();
//                showAlert(getString(R.string.label_network_error), apiResponse.errorMessage);
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "deleteNotification")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "deleteNotification",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun showBadgeOnHome() {
        dashboardViewModel!!.getQuestionGameWatchListAndSurveyInfo("21")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetQuestionGameWatchListAndSurveyInfoResponse> ->
                if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        dashboardViewModel!!.setShowHomeBadge(
                            apiResponse.body.isQuestionNew.equals("Y", ignoreCase = true)
                                    || apiResponse.body.isWatchListNew.equals(
                                "Y",
                                ignoreCase = true
                            )
                        )
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                }
            }
    }
}