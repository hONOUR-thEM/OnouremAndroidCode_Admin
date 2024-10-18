package com.onourem.android.activity.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.gson.Gson
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentProfileBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.adapters.MoodHeaderAdapter
import com.onourem.android.activity.ui.games.adapters.TaskMessageCommentsAdapter
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.games.fragments.WriteCommentDialogFragment
import com.onourem.android.activity.ui.games.viewmodel.CommentsViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.*
import com.onourem.android.activity.ui.utils.glide.loadCircularImage
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject

class ProfileFragment :
    AbstractBaseViewModelBindingFragment<ProfileViewModel, FragmentProfileBinding>(),
    OnOffsetChangedListener {
    private lateinit var filtersAdapter: ProfileFiltersAdapter
    private lateinit var headerAdapter: MoodHeaderAdapter

    //    private List<UserList> blockedUserIds;
    private val blockedUserCount = 0
    private val options = RequestOptions()
        .centerCrop()

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var loginUserId: String? = null
    private var profileUserId: String? = null

    //    private String activityId;
    private var taskMessageCommentsAdapter: TaskMessageCommentsAdapter? = null
    private var isLastPage = false
    private var isLoading = false
    private var feedIdsList: List<Int>? = null
    private var profileData: GetUserProfileDataResponse? = null
    private var userActionViewModel: UserActionViewModel? = null
    private var commentsViewModel: CommentsViewModel? = null
    private var dashboardViewModel: DashboardViewModel? = null

    private var getWatchListResponse: GetWatchListResponse? = null

    override fun viewModelType(): Class<ProfileViewModel> {
        return ProfileViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_profile
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]
        commentsViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[CommentsViewModel::class.java]
        dashboardViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[DashboardViewModel::class.java]
        commentsViewModel!!.commentCountLiveData.observe(this) { item: Triple<String?, Int, String?>? ->
            if (taskMessageCommentsAdapter != null && item != null) {
                taskMessageCommentsAdapter!!.updateComment(item.first, item.second!!)
                commentsViewModel!!.resetCommentCountLiveData()
            }
        }

//        viewModel.getLocalBlockUserList().observe(this, listResource -> {
//            if (listResource.getStatus() == Status.SUCCESS && listResource.getData() != null && !listResource.getData().isEmpty()) {
//                blockedUserIds = listResource.getData();
//            }
//        });
    }

    private fun reportAbuse(action: Action<Any?>?) {
        val item = action?.data as FeedsList
        TwoActionAlertDialog.showAlert(
            requireActivity(),
            getString(R.string.label_confirm),
            getString(R.string.message_report_post_abuse),
            item,
            "Cancel",
            "Yes"
        ) { item1: Pair<Int?, FeedsList?>? ->
            if (item1?.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                viewModel.reportAbuse(item.postId)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<PostsActionResponse> ->
                        actionButtonResponse(
                            apiResponse,
                            item,
                            "reportAbuse"
                        )
                    }
            }
        }
    }

    private fun editGamePrivacy(action: Action<Any?>?) {
        val item = action?.data as FeedsList
        if (TextUtils.isEmpty(item.playgroupId)) {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavEditPrivacyDialog(
                    item.activityId ?: "",
                    item.postId ?: ""
                )
            )
        } else {
            showAlert("You have shared this message in ${item.playgroupName} O-Club. You can’t change the privacy setting.")
        }
    }


    private fun getUserWatchList() {
        dashboardViewModel!!.getUserWatchList("").observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetWatchListResponse> ->
            if (apiResponse.loading) {
                //showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                //hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    preferenceHelper!!.putValue(
                        Constants.KEY_GAME_POINTS,
                        apiResponse.body.gamePoint
                    )
                    getWatchListResponse = apiResponse.body
                    //                    viewClicksEnabled(false);
                    //navController.navigate(DashboardFragmentDirections.actionNavHomeToWatchListDialogFragment(apiResponse.body, "DashboardFragment"));
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                //  hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun deleteThisPost(action: Action<Any?>?) {
        val item = action?.data as FeedsList
        TwoActionAlertDialog.showAlert(
            requireActivity(),
            getString(R.string.label_confirm),
            getString(R.string.message_delete_post),
            item,
            "Cancel",
            "Yes"
        ) { item1: Pair<Int?, FeedsList?>? ->
            if (item1?.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                viewModel.deletePosts(item.postId)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<PostsActionResponse> ->
                        actionButtonResponse(
                            apiResponse,
                            item,
                            "deletePosts"
                        )
                    }
            }
        }
    }

    private fun actionButtonResponse(
        apiResponse: ApiResponse<PostsActionResponse>,
        item: FeedsList,
        serviceName: String
    ) {
        if (apiResponse.loading) {
            showProgress()
        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            hideProgress()
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                if (taskMessageCommentsAdapter != null) taskMessageCommentsAdapter!!.removeItem(item)
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
                    AppUtilities.showLog("Network Error", serviceName)
                }
                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                    serviceName,
                    apiResponse.code.toString()
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserWatchList()

        profileUserId = ProfileFragmentArgs.fromBundle(
            requireArguments()
        ).profileId
        //        activityId = ProfileFragmentArgs.fromBundle(getArguments()).getActivityId();
        loginUserId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
        binding.swipeRefreshLayout.setOnRefreshListener {
            isLoading = false
            isLastPage = false
            if (taskMessageCommentsAdapter != null) taskMessageCommentsAdapter!!.removeFooter()
            binding.swipeRefreshLayout.isRefreshing = true
            getUserProfileData(false)
        }
        val linearLayoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        val animator = binding.recyclerView.itemAnimator as SimpleItemAnimator?
        if (animator != null) animator.supportsChangeAnimations = false
        binding.recyclerView.addItemDecoration(VerticalSpaceItemDecoration(8))
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.addOnScrollListener(object :
            PaginationListener(linearLayoutManager) {
            override fun loadMoreItems() {
                this@ProfileFragment.isLoading = true
                loadMorePosts()
            }

            override fun isLastPage(): Boolean {
                return this@ProfileFragment.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@ProfileFragment.isLoading
            }
        })
        userActionViewModel!!.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()
                if (action.actionType == ActionType.EDIT_GAME_VISIBILITY) {
                    editGamePrivacy(action)
                } else if (action.actionType == ActionType.DELETE_GAME) {
                    deleteThisPost(action)
                } else if (action.actionType == ActionType.REPORT_ABUSE) {
                    reportAbuse(action)
                } else if (action.actionType == ActionType.UN_FRIEND) {
                    removeFriend(action)
                } else if (action.actionType == ActionType.CANCEL_FRIEND_REQUEST) {
                    cancelFriendRequest(action)
                } else if (action.actionType == ActionType.BLOCK) {
                    blockFriend(action)
                } else if (action.actionType == ActionType.BLOCK_REPORT) {
                    blockAndReportFriend(action)
                }
            }
        }
        if (profileData == null) {
            getUserProfileData(true)
        } else {
            loadUserProfile(false, false)
        }

        dashboardViewModel!!.selectedExpression.observe(viewLifecycleOwner) { userExpressionList: UserExpressionList ->
            setMood(
                userExpressionList
            )
        }
    }

    private fun setMood(userExpressionList: UserExpressionList) {

        if (profileUserId == preferenceHelper?.getString(Constants.KEY_LOGGED_IN_USER_ID)) {
            loadFilterData(userExpressionList)
        } else {
            binding.rvFilter.visibility = View.GONE
        }

//        headerAdapter = MoodHeaderAdapter(userExpressionList.moodImage) {
//            if (it == 1) {
//                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_profile) {
//                    if (this.getWatchListResponse != null) {
//                        navController.navigate(
//                            MobileNavigationDirections.actionGlobalNavDashboardNew(
//                                getWatchListResponse!!
//                            )
//                        )
//                    }
//                }
//            }
//        }
//
//        binding.rvFilter.adapter = ConcatAdapter(headerAdapter, filtersAdapter)


    }


    override fun onResume() {
        super.onResume()
        binding.appBar.addOnOffsetChangedListener(this)
    }

    override fun onPause() {
        super.onPause()
        binding.appBar.removeOnOffsetChangedListener(this)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
//The Refresh must be only active when the offset is zero :
        binding.swipeRefreshLayout.isEnabled = verticalOffset == 0
    }

    private fun getUserProfileData(showProgress: Boolean) {
        viewModel.getUserProfileData(profileUserId)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetUserProfileDataResponse> ->
                if (apiResponse.loading) {
                    if (showProgress) {
                        showProgress()
                    }
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (!showProgress) {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        profileData = apiResponse.body
                        loadUserProfile(showProgress, true)
                    } else {
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
                            AppUtilities.showLog("Network Error", "getUserProfileData")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getUserProfileData",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun removeFriend(action: Action<Any?>?) {
        val userId = action?.data as String
        viewModel.removeFriend(userId, "22")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserActionStandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        profileData!!.userProfileData!!.loginUserProfileRelation = "Add as Friend"
                        setUserProfileData()
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
                            AppUtilities.showLog("Network Error", "removeFriend")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "removeFriend",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun loadMorePosts() {
        if (feedIdsList == null || feedIdsList!!.isEmpty()) {
            isLastPage = true
            isLoading = false
            setFooterMessage()
            return
        }
        val start = taskMessageCommentsAdapter!!.itemCount + blockedUserCount
        val end =
            if (feedIdsList!!.size - start > PaginationListener.PAGE_ITEM_SIZE) start + PaginationListener.PAGE_ITEM_SIZE else feedIdsList!!.size
        if (start >= end) {
            isLastPage = true
            isLoading = false
            setFooterMessage()
            return
        }
        val stringBuilder = StringBuilder()
        for (i in start until end) {
            val id = feedIdsList!![i]
            stringBuilder.append(id).append(",")
        }
        val postsIds = stringBuilder.toString()
        viewModel.getNextUserProfilePosts(profileUserId, postsIds)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<TaskAndMessageGameActivityResResponse> ->
                if (apiResponse.loading) {
                    if (taskMessageCommentsAdapter != null) {
                        taskMessageCommentsAdapter!!.addLoading()
                    }
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (taskMessageCommentsAdapter != null) {
                        taskMessageCommentsAdapter!!.removeLoading()
                    }
                    isLoading = false
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        if (apiResponse.body.feedsList == null || apiResponse.body.feedsList!!.isEmpty()) {
                            isLastPage = true
                            setFooterMessage()
                        } else {
                            val list = apiResponse.body.feedsList
                            //blockedUserCount += BlockedUsersUtility.filterBlockedUsersFromFeeds(blockedUserIds, list);
                            taskMessageCommentsAdapter!!.addItems(list)
                            if (list!!.size + blockedUserCount >= feedIdsList!!.size) {
                                isLastPage = true
                                setFooterMessage()
                            }
                            //Log.e("####", String.format("server: %d", apiResponse.body.getFeedsList().size()));
                        }
                    } else {
                        isLastPage = true
                        setFooterMessage()
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    isLoading = false
                    if (taskMessageCommentsAdapter != null) {
                        taskMessageCommentsAdapter!!.removeLoading()
                    }
                    showAlert(apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "getNextUserProfilePosts")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getNextUserProfilePosts",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun setFooterMessage() {
        isLoading = false
        if (taskMessageCommentsAdapter != null) {
            val footerMessage: String = if (taskMessageCommentsAdapter!!.itemCount == 0) {
                "User has not shared any thoughts or appreciation messages so far. Share your thoughts from '+ Create' button on home screen or send messages of appreciation to your friends from the Menu tab -> Appreciate."
            } else {
                "Those were all the thoughts and appreciation messages user has exchanged so far. Share your thoughts from '+ Create' button on home screen or send messages of appreciation to your friends from the Menu tab -> Appreciate."
            }
            binding.recyclerView.postDelayed({
                taskMessageCommentsAdapter!!.addFooter(
                    footerMessage
                )
            }, 200)
        }
    }

    private fun loadUserProfile(showProgress: Boolean, loadPosts: Boolean) {
        setUserProfileData()
        if (loadPosts) {
            userProfilePosts(showProgress)
        } else {
            binding.recyclerView.adapter = taskMessageCommentsAdapter
        }
    }

    private fun userProfilePosts(showProgress: Boolean) {
        viewModel.getUserProfilePosts(profileUserId)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetUserProfilePostsResponse> ->
                if (apiResponse.loading) {
                    if (showProgress) {
                        showProgress()
                    }
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (!showProgress) {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        binding.tvMessage.visibility = View.GONE
                        if (apiResponse.body.feedsList == null || apiResponse.body.feedsList!!.isEmpty()) {
                            binding.tvMessage.visibility = View.VISIBLE
                            if (taskMessageCommentsAdapter != null) taskMessageCommentsAdapter!!.clearData()
                        } else {
                            feedIdsList = apiResponse.body.feedActionDataList
                            val feeds = apiResponse.body.feedsList
                            //blockedUserCount += BlockedUsersUtility.filterBlockedUsersFromFeeds(blockedUserIds, feeds);
                            //Log.e("####", String.format("total post ids: %s", feedIdsList.toString()));
                            taskMessageCommentsAdapter = TaskMessageCommentsAdapter(
                                navController,
                                feeds,
                                alertDialog
                            ) { item: android.util.Pair<Int, FeedsList> ->
                                if (item.first == TaskMessageCommentsAdapter.CLICK_MORE) {
                                    val title = "What would you like to do?"
                                    val actions = ArrayList<Action<*>>()
                                    if (loginUserId == item.second.postCreatedId) {
                                        actions.add(
                                            Action(
                                                "Delete This Message",
                                                R.color.color_red,
                                                ActionType.DELETE_GAME,
                                                item.second
                                            )
                                        )
                                        actions.add(
                                            Action(
                                                "Edit Post Visibility",
                                                R.color.color_black,
                                                ActionType.EDIT_GAME_VISIBILITY,
                                                item.second
                                            )
                                        )
                                    } else {
//                                    for (int i = 0; i < item.second.getReceiverId().size(); ) {
//                                        if (/*i < item.second.getReceiverId().size() &&*/ loginUserId.equals(item.second.getReceiverId().get(i))) {
//                                            actions.add(new Action<>("Delete This Message", R.color.color_red, ActionType.DELETE_GAME, item.second));
                                        actions.add(
                                            Action(
                                                "Report Abuse",
                                                R.color.color_black,
                                                ActionType.REPORT_ABUSE,
                                                item.second
                                            )
                                        )
                                        //                                            break;
//                                        }
//                                        i += 4;
//                                    }

//                                    actions.add(new Action<>("Report Abuse", R.color.color_black, ActionType.REPORT_ABUSE, item.second));
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
                                } else if (item.first == TaskMessageCommentsAdapter.CLICK_SENDER_PROFILE) {
                                    if (item.second.anonymousOnOff.equals(
                                            "Y",
                                            ignoreCase = true
                                        ) && loginUserId != item.second.postCreatedId
                                    ) {
                                        showAlert("Identity of the friend who sent this message will be revealed after 48 hrs from the time the message was sent")
                                    } else if (loginUserId == item.second.postCreatedId) {
                                        scrollToTop(0)
                                    } else {
                                        if (!item.second.postCreatedId.equals(
                                                "4264",
                                                ignoreCase = true
                                            )
                                        ) {
                                            navController.navigate(
                                                MobileNavigationDirections.actionGlobalNavProfile(
                                                    item.second.activityId,
                                                    item.second.postCreatedId
                                                )
                                            )
                                        } else {
                                            showAlert("You can't access profile of this admin user")
                                        }
                                    }
                                } else if (item.first == TaskMessageCommentsAdapter.CLICK_COMMENT_COUNT) {
                                    val receiverIds = StringBuilder()
                                    val receiverArray = java.util.ArrayList<String>()
                                    var i = 0
                                    while (i < item.second.receiverId!!.size) {
                                        receiverIds.append(item.second.receiverId!![i])
                                        receiverArray.add(item.second!!.receiverId!![i])
                                        if (i < item.second.receiverId!!.size) receiverIds.append(",")
                                        i += 4
                                    }
                                    val userId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
                                    navController.navigate(
                                        MobileNavigationDirections.actionGlobalNavPostCommentList(
                                            "",
                                            item.second.activityId ?: "",
                                            item.second.postId ?: "",
                                            receiverIds.toString(),
                                            "",
                                            item.second.activityId ?: "",
                                            item.second.postCreatedId ?: "",
                                            "Post",
                                            receiverArray.any { it == userId } || item.second.postCreatedId == userId
                                        )
                                    )
                                } else if (item.first == TaskMessageCommentsAdapter.CLICK_COMMENT) {
                                    val receiverIds = StringBuilder()
                                    var i = 0
                                    while (i < item.second.receiverId!!.size) {
                                        receiverIds.append(item.second.receiverId!![i])
                                        if (i < item.second.receiverId!!.size) receiverIds.append(",")
                                        i += 4
                                    }
                                    WriteCommentDialogFragment.getInstance(
                                        item.second.activityId,
                                        receiverIds.toString(),
                                        "",
                                        item.second.postId,
                                        ""
                                    ) { item1: Void? ->
                                        if (TextUtils.isEmpty(item.second.commentCount)) {
                                            item.second.commentCount = "1"
                                        } else {
                                            val count = item.second.commentCount!!.toInt() + 1
                                            item.second.commentCount = count.toString()
                                        }
                                        if (taskMessageCommentsAdapter != null) {
                                            val updatedItemPosition =
                                                taskMessageCommentsAdapter!!.updateItem(item.second)
                                            scrollToTop(updatedItemPosition)
                                        }
                                    }.show(requireActivity().supportFragmentManager, "Comment")
                                } else if (item.first == TaskMessageCommentsAdapter.CLICK_ATTACHMENT) {
                                    var url: String? = ""
                                    val media: Int
                                    if (!TextUtils.isEmpty(item.second.videoURL)) {
                                        url = item.second.videoURL
                                        media = 2
                                    } else {
                                        url = item.second.postLargeImageURL
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
                            binding.recyclerView.adapter = taskMessageCommentsAdapter
                        }
                    } else {
                        setFooterMessage()
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
                            AppUtilities.showLog("Network Error", "getUserProfilePosts")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getUserProfilePosts",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun scrollToTop(position: Int) {
        binding.recyclerView.smoothScrollToPosition(position)
        binding.appBar.setExpanded(true, true)
    }

    private fun setUserProfileData() {
        val profile = profileData!!.userProfileData

        binding.ivProfileImage.loadCircularImage(
            profile!!.userProfilePicture, // or any object supported by Glide
            4f, // default is 0. If you don't change it, then the image will have no border
            Color.WHITE // optional, default is white
        )

//        Glide.with(requireActivity())
//            .load(profile.userProfilePicture)
//            .apply(
//                options.error(R.drawable.default_user_profile_image)
//                    .placeholder(R.drawable.default_user_profile_image)
//            )
//            .into(binding.ivProfileImage)
        Glide.with(requireActivity())
            .load(profile.coverLargePicture)
            .apply(
                options.error(R.drawable.default_place_holder)
                    .placeholder(R.drawable.default_place_holder)
            ).into(binding.ivCoverImage)
        Utilities.verifiedUserType(
            binding.root.context,
            profile.userType,
            binding.ivIconVerified
        )
        binding.ivProfileImage.setOnClickListener(ViewClickListener { v: View? ->

            if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_profile) {
                    navController.navigate(
                        ProfileFragmentDirections.actionNavProfileToNavProfileImageEditor(
                            ProfileImageEditorFragment.TYPE_PROFILE_IMAGE,
                            profileData!!.userProfileData!!
                        )
                    )
                }
            } else {
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }

        })
        binding.ivCoverImage.setOnClickListener(ViewClickListener { v: View? ->

            if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_profile) {
                    navController.navigate(
                        ProfileFragmentDirections.actionNavProfileToNavProfileImageEditor(
                            ProfileImageEditorFragment.TYPE_COVER_IMAGE,
                            profileData!!.userProfileData!!
                        )
                    )
                }
            } else {
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }

        })
        binding.tvName.text = profile.userName
        if (!TextUtils.isEmpty(profile.location)) binding.tvLocation.text =
            profile.location
        else binding.tvLocation.visibility = View.INVISIBLE
        binding.tvIsFriend.visibility = View.GONE
        binding.tvCancel.visibility = View.GONE
        binding.ivEditBlockIcon.visibility = View.INVISIBLE
        binding.tvFriendCount.visibility = View.INVISIBLE
        when (profile.loginUserProfileRelation) {
            "Friends" -> {
                //                isAcceptRequestButtonHidden = true
//                isIgnoreRequestButtonHidden = true
//                isAddFriendButtonHidden = true
//                isCancelRequestButtonHidden = true
//                isEditProfileButtonHidden = true

//                isFriendProfileFriendButtonHidden = false
//                isBlockUserButtonHidden = false
//                isPeopleCountViewHidden = false
                binding.tvCancel.visibility = View.GONE
                //                DrawableCompat.setTint(drawable, getResources().getColor(R.color.color_blue));
                binding.tvIsFriend.backgroundTintList =
                    AppCompatResources.getColorStateList(requireActivity(), R.color.color_blue)
                binding.tvIsFriend.text = getString(R.string.label_friends_with_checkmark_icon)
                binding.tvIsFriend.visibility = View.VISIBLE
                binding.ivEditBlockIcon.visibility = View.VISIBLE
                binding.tvFriendCount.visibility = View.VISIBLE
            }
            "Accept Request" -> {

//                isAddFriendButtonHidden = true
//                isCancelRequestButtonHidden = true
//                isEditProfileButtonHidden = true
//                isFriendProfileFriendButtonHidden = true
//                isAcceptRequestButtonHidden = false
//                isIgnoreRequestButtonHidden = false
//                isBlockUserButtonHidden = false
//                isPeopleCountViewHidden = false
                binding.tvCancel.visibility = View.GONE

//                DrawableCompat.setTint(drawable, getResources().getColor(R.color.color_green));
//                binding.tvIsFriend.setBackgroundDrawable(drawable);
                binding.tvIsFriend.backgroundTintList =
                    AppCompatResources.getColorStateList(requireActivity(), R.color.color_green)

//                Drawable ignoreDrawable = AppCompatResources.getDrawable(requireActivity(), R.drawable.shape_filled_rectangle_friend_status);
//                DrawableCompat.setTint(ignoreDrawable, getResources().getColor(R.color.color_pink));
//                binding.tvCancel.setBackgroundDrawable(ignoreDrawable);
                binding.tvCancel.backgroundTintList =
                    AppCompatResources.getColorStateList(requireActivity(), R.color.color_pink)
                binding.tvIsFriend.text = getString(R.string.label_accept)
                binding.tvCancel.setText(R.string.label_ignore)
                binding.tvIsFriend.visibility = View.VISIBLE
                binding.tvCancel.visibility = View.VISIBLE
                binding.ivEditBlockIcon.visibility = View.VISIBLE
                binding.ivEditBlockIcon.setImageResource(R.drawable.ic_block_black_24dp)
                binding.tvFriendCount.visibility = View.VISIBLE
            }
            "Friend Request Sent" -> {

//                isAcceptRequestButtonHidden = true
//                isIgnoreRequestButtonHidden = true
//                isAddFriendButtonHidden = true
//                isFriendProfileFriendButtonHidden = true
//                isEditProfileButtonHidden = true
//                isBlockUserButtonHidden = false
//                isCancelRequestButtonHidden = false
//                isPeopleCountViewHidden = false
                binding.tvCancel.visibility = View.GONE

//                DrawableCompat.setTint(drawable, getResources().getColor(R.color.color_pink));
//                binding.tvIsFriend.setBackgroundDrawable(drawable);
                binding.tvIsFriend.text = getString(R.string.label_cancel)
                binding.tvIsFriend.backgroundTintList =
                    AppCompatResources.getColorStateList(requireActivity(), R.color.color_pink)
                binding.tvIsFriend.visibility = View.VISIBLE
                binding.ivEditBlockIcon.visibility = View.VISIBLE
                binding.ivEditBlockIcon.setImageResource(R.drawable.ic_block_black_24dp)
                binding.tvFriendCount.visibility = View.VISIBLE
            }
            "Add as Friend" -> {

//                isAcceptRequestButtonHidden = true
//                isIgnoreRequestButtonHidden = true
//                isCancelRequestButtonHidden = true
//                isFriendProfileFriendButtonHidden = true
//                isEditProfileButtonHidden = true
//                isBlockUserButtonHidden = false
//                isAddFriendButtonHidden = false
//                isPeopleCountViewHidden = false
                binding.tvCancel.visibility = View.GONE

//                DrawableCompat.setTint(drawable, getResources().getColor(R.color.color_green));
//                binding.tvIsFriend.setBackgroundDrawable(drawable);
                binding.tvIsFriend.backgroundTintList =
                    AppCompatResources.getColorStateList(requireActivity(), R.color.color_green)
                binding.tvIsFriend.setText(R.string.label_add_friend)
                binding.tvIsFriend.visibility = View.VISIBLE
                binding.ivEditBlockIcon.setImageResource(R.drawable.ic_block_black_24dp)
                binding.ivEditBlockIcon.visibility = View.VISIBLE
                binding.tvFriendCount.visibility = View.VISIBLE
            }
            "Me" -> {

//                isAcceptRequestButtonHidden = true
//                isIgnoreRequestButtonHidden = true
//                isAddFriendButtonHidden = true
//                isCancelRequestButtonHidden = true
//                isFriendProfileFriendButtonHidden = true
//                isBlockUserButtonHidden = true
//                isEditProfileButtonHidden = false
//                isPeopleCountViewHidden = false
                binding.ivEditBlockIcon.setImageResource(R.drawable.ic_edit_icon)
                binding.ivEditBlockIcon.tag = "edit_profile"
                binding.ivEditBlockIcon.visibility = View.VISIBLE
                binding.tvFriendCount.visibility = View.VISIBLE
            }
        }
        if (profile.userStatus == "System") {
//            isBlockUserButtonHidden = true
//            isAddFriendButtonHidden = true
//            isPeopleCountViewHidden = true
            binding.ivEditBlockIcon.visibility = View.GONE
            binding.tvFriendCount.visibility = View.GONE
            binding.tvIsFriend.visibility = View.GONE
        }
        setFriendCount("0")
        setAnswerCount("0")
        viewModel.getFriendIdList(profileUserId)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetFriendIdListResponse> ->
                if (apiResponse.isSuccess && apiResponse.body != null && apiResponse.body.friendIdList != null) {
//                Iterator<String> iterator = apiResponse.body.getFriendIdList().iterator();
//                while (iterator.hasNext()) {
//                    String userId = iterator.next();
//                    if (blockedUserIds != null && !blockedUserIds.isEmpty()) {
//                        for (UserList userList : blockedUserIds) {
//                            if (userList.getUserId().equalsIgnoreCase(userId))
//                                iterator.remove();
//                        }
//                    } else {
//                        break;
//                    }
//                }
                    setFriendCount(apiResponse.body.friendIdList!!.size.toString())
                }
            }
        binding.tvIsFriend.setOnClickListener(ViewClickListener { v: View? ->

            if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                if (binding.tvIsFriend.text.toString().trim { it <= ' ' }
                        .equals(getString(R.string.label_add_friend), ignoreCase = true)) {
                    sendFriendRequest(profileUserId)
                } else if (binding.tvIsFriend.text.toString().trim { it <= ' ' }
                        .equals(
                            getString(R.string.label_friends_with_checkmark_icon),
                            ignoreCase = true
                        )) {
                    val title = "What would you like to do?"
                    val actions = ArrayList<Action<*>>()
                    actions.add(
                        Action(
                            getString(R.string.action_label_un_friend),
                            R.color.color_black,
                            ActionType.UN_FRIEND,
                            profileUserId
                        )
                    )
                    val bundle = Bundle()
                    bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
                    navController.navigate(
                        ProfileFragmentDirections
                            .actionGlobalNavUserActionBottomSheet(title, bundle, "")
                    )
                } else if (binding.tvIsFriend.text.toString().trim { it <= ' ' }
                        .equals(getString(R.string.label_accept), ignoreCase = true)) {
                    acceptFriendRequest(profileUserId)
                } else if (binding.tvIsFriend.text.toString().trim { it <= ' ' }
                        .equals(getString(R.string.label_cancel), ignoreCase = true)) {
                    val title = "What would you like to do?"
                    val cancelActions = ArrayList<Action<*>>()
                    cancelActions.add(
                        Action(
                            getString(R.string.action_cancel_friend_request),
                            R.color.color_black,
                            ActionType.CANCEL_FRIEND_REQUEST,
                            profileUserId
                        )
                    )
                    val bundle = Bundle()
                    bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, cancelActions)
                    navController.navigate(
                        MobileNavigationDirections
                            .actionGlobalNavUserActionBottomSheet(title, bundle, "")
                    )

                    // cancelFriendRequest(profileUserId);
                }
            } else {
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }

        })
        binding.tvCancel.setOnClickListener(ViewClickListener { v: View? ->

            if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                if (binding.tvCancel.text.toString().trim { it <= ' ' }
                        .equals(getString(R.string.label_cancel), ignoreCase = true)) {
                    val title = "What would you like to do?"
                    val cancelActions = ArrayList<Action<*>>()
                    cancelActions.add(
                        Action(
                            getString(R.string.action_cancel_friend_request),
                            R.color.color_black,
                            ActionType.CANCEL_FRIEND_REQUEST,
                            profileUserId
                        )
                    )
                    val bundle = Bundle()
                    bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, cancelActions)
                    navController.navigate(
                        MobileNavigationDirections
                            .actionGlobalNavUserActionBottomSheet(title, bundle, "")
                    )

                    //cancelFriendRequest(profileUserId);
                } else if (binding.tvCancel.text.toString().trim { it <= ' ' }
                        .equals(getString(R.string.label_ignore), ignoreCase = true)) {
                    ignoreFriendRequest(profileUserId)
                }
            } else {
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }

        })
        binding.ivEditBlockIcon.setOnClickListener(ViewClickListener { v: View? ->

            if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                val tag = binding.ivEditBlockIcon.tag as String
                if ("edit_profile" == tag) {
                    navController.navigate(ProfileFragmentDirections.actionNavProfileToNavProfileSettings())
                } else {
                    val title = String.format("Block %s?", profileData!!.userProfileData!!.userName)
                    val subTitle = """
                Blocking a user means user will no longer be able to:
                - find your profile
                - see any of your content
                
                It also means you will no longer:
                - see user’s profile
                - see user’s content
                
                You will be unfriended with the user if you are already friends.
                
                Reporting a user will notify Onourem to check if the user is violating any of the terms and conditions and take action if needed.
                """.trimIndent()
                    val blockAction = ArrayList<Action<*>>()
                    blockAction.add(
                        Action(
                            getString(R.string.action_block),
                            R.color.color_red,
                            ActionType.BLOCK,
                            profileUserId
                        )
                    )
                    blockAction.add(
                        Action(
                            getString(R.string.action_block_report),
                            R.color.color_red,
                            ActionType.BLOCK_REPORT,
                            profileUserId
                        )
                    )
                    val bundle = Bundle()
                    bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, blockAction)
                    navController.navigate(
                        MobileNavigationDirections
                            .actionGlobalNavUserActionBottomSheet(title, bundle, subTitle)
                    )

//              TwoActionAlertDialog.showAlert(requireActivity(), getString(R.string.label_confirm), String.format("Do you want to block %s?", profileData.getUserProfileData().getUserName()), profileUserId, "Cancel", "Yes", item1 -> {
//                    if (item1 != null && item1.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
//
//                    }
//                });
                }

            } else {
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }
        })
        binding.tvFriendCount.setOnClickListener(ViewClickListener { v: View? ->


            if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                if (!binding.tvFriendCount.text.toString().trim { it <= ' ' }
                        .equals(
                            String.format("0\n%s", getString(R.string.label_friends)),
                            ignoreCase = true
                        )) {
                    if (profileUserId.equals(loginUserId, ignoreCase = true)) {
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavSearch(
                                true,
                                profileUserId!!,
                                loginUserId.equals(profileUserId!!, ignoreCase = true),
                                if (loginUserId.equals(
                                        profileUserId!!,
                                        ignoreCase = true
                                    )
                                ) null
                                else profile.userName
                            )
                        )
                    } else {
                        navController.navigate(
                            ProfileFragmentDirections.actionNavProfileToNavFOfFSearch(
                                profileUserId!!,
                                profile.userName!!
                            )
                        )
                    }
                } else if (profileUserId.equals(loginUserId, ignoreCase = true)) {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavSearch(
                            true,
                            profileUserId!!,
                            loginUserId.equals(profileUserId!!, ignoreCase = true),
                            if (loginUserId.equals(
                                    profileUserId!!,
                                    ignoreCase = true
                                )
                            ) null
                            else profile.userName
                        )
                    )
                }
            } else {
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }

        })

//        if (!profileUserId.equalsIgnoreCase(loginUserId) && profileData !=null && profileData.getUserProfileData().getLoginUserProfileRelation().equalsIgnoreCase("Friends")){
//            binding.ivMessage.setVisibility(View.VISIBLE);
//
//            binding.ivMessage.setOnClickListener(new ViewClickListener(v -> {
//                Conversation conversation = new Conversation();
//                conversation.setId("EMPTY");
//                conversation.setUserName(profile.getUserName());
//                conversation.setUserOne(loginUserId);
//                conversation.setUserTwo(profileUserId);
//                conversation.setProfilePicture(profile.getUserProfilePicture());
//                conversation.setUserTypeId(profile.getUserType());
//
//                navController.navigate(MobileNavigationDirections.actionGlobalNavConversations(conversation));
//            }));
//
//        }
    }

    private fun setFriendCount(friendCount: String) {
        val builder = SpannableStringBuilder()
            .append(friendCount)
            .append("\n ")
            .append(getString(R.string.label_friends))
            .append(" ")
        builder.setSpan(
            RelativeSizeSpan(1.4f),
            0, friendCount.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(
            RelativeSizeSpan(1f),
            friendCount.length, builder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvFriendCount.text = builder

        if (profileUserId == preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)) {
            loadFilterData(null)
        } else {
            binding.rvFilter.visibility = View.GONE
        }

    }

    private fun setAnswerCount(answerCount: String) {
        val builder = SpannableStringBuilder().append(answerCount)
            .append("\n")
            .append(getString(R.string.label_answers))
        builder.setSpan(
            RelativeSizeSpan(1.4f),
            0, answerCount.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setSpan(
            RelativeSizeSpan(1f),
            answerCount.length, builder.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //binding.tvAnswerCount.setText(builder);
    }

    private fun sendFriendRequest(userId: String?) {
        viewModel.sendFriendRequest(userId!!, "22")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserActionStandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        profileData!!.userProfileData!!.loginUserProfileRelation =
                            "Friend Request Sent"
                        setUserProfileData()
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
                            AppUtilities.showLog("Network Error", "sendFriendRequest")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "sendFriendRequest",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun blockFriend(action: Action<Any?>?) {
        val userId = action?.data as String
        val userList = UserList()
        userList.userId = userId
        userList.status = "Y"
        userList.firstName = profileData!!.userProfileData!!.userName
        userList.lastName = ""
        userList.profilePicture = profileData!!.userProfileData!!.userProfilePicture
        userList.followerStatus = profileData!!.userProfileData!!.followStatus
        viewModel.blockUser(userList)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        navController.popBackStack()
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
                            AppUtilities.showLog("Network Error", "blockUser")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "blockUser",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun blockAndReportFriend(action: Action<Any?>?) {
        val userId = action?.data as String
        val userList = UserList()
        userList.userId = userId
        userList.status = "Y"
        userList.firstName = profileData!!.userProfileData!!.userName
        userList.lastName = ""
        userList.profilePicture = profileData!!.userProfileData!!.userProfilePicture
        userList.followerStatus = profileData!!.userProfileData!!.followStatus
        viewModel.blockAndReportUser(userList)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        navController.popBackStack()
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
                            AppUtilities.showLog("Network Error", "blockUser")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "blockUser",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun cancelFriendRequest(action: Action<Any?>?) {
        val userId = action?.data as String
        viewModel.cancelSentRequest(userId, "22")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserActionStandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        profileData!!.userProfileData!!.loginUserProfileRelation = "Add as Friend"
                        setUserProfileData()
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
                            AppUtilities.showLog("Network Error", "cancelSentRequest")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "cancelSentRequest",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun ignoreFriendRequest(userId: String?) {
        viewModel.cancelPendingRequest(userId!!, "22")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserActionStandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        profileData!!.userProfileData!!.loginUserProfileRelation = "Add as Friend"
                        setUserProfileData()
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

    private fun acceptFriendRequest(userId: String?) {
        viewModel.acceptPendingRequest(userId!!, "22")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserActionStandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    val response = apiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        profileData!!.userProfileData!!.loginUserProfileRelation = "Friends"
                        setUserProfileData()
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

    override fun onDestroyView() {
        commentsViewModel!!.resetCommentCountLiveData()
//        dashboardViewModel!!.updateTrendingData(true)
        super.onDestroyView()
    }


    private fun loadFilterData(userExpressionList: UserExpressionList?) {
        binding.rvFilter.visibility = View.VISIBLE
        val linearLayoutManager = object : LinearLayoutManager(requireActivity(), HORIZONTAL, false) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        binding.rvFilter.layoutManager = linearLayoutManager

//Thoughts - Post - Vocals - Questions - Surveys - My Question
        val itemArrayList = ArrayList<ProfileFilterItem>()

        itemArrayList.add(
            ProfileFilterItem(
                "1",
                "My Vocals",
            )
        )
        itemArrayList.add(
            ProfileFilterItem(
                "2",
                "Answered",
            )
        )
        itemArrayList.add(
            ProfileFilterItem(
                "3",
                "Surveys",
            )
        )
        itemArrayList.add(
            ProfileFilterItem(
                "4",
                "My\nQuestions",
            )
        )

        var preX = 0f
        var preY = 0f
        val Y_BUFFER = ViewConfiguration.get(fragmentContext).scaledPagingTouchSlop.toFloat()

        binding.rvFilter.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN) {
                    binding.rvFilter.parent.requestDisallowInterceptTouchEvent(true)
                }
                if (e.action == MotionEvent.ACTION_MOVE) {
                    if (Math.abs(e.x - preX) > Math.abs(e.y - preY)) {
                        binding.rvFilter.parent.requestDisallowInterceptTouchEvent(true)
                    } else if (Math.abs(e.y - preY) > Y_BUFFER) {
                        binding.rvFilter.parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
                preX = e.x
                preY = e.y
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

        setAdapter(itemArrayList, userExpressionList)

    }

    private fun setAdapter(itemArrayList: ArrayList<ProfileFilterItem>, userExpressionList: UserExpressionList?) {

        var expressionList: UserExpressionList? = null
        expressionList = if (userExpressionList != null) {
            userExpressionList
        } else {
            val value = preferenceHelper!!.getString(Constants.KEY_SELECTED_EXPRESSION)
            Gson().fromJson(value, UserExpressionList::class.java)
        }

        if (expressionList != null) {
            headerAdapter = MoodHeaderAdapter(expressionList.moodImage) {

                if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                    if (it == 1) {
                        if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_profile) {
                            if (this.getWatchListResponse != null) {
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavDashboardNew(
                                        getWatchListResponse!!
                                    )
                                )
                            }
                        }
                    }
                } else {
                    (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
                }

            }

            filtersAdapter = ProfileFiltersAdapter(itemArrayList) { item: ProfileFilterItem ->

                if ((fragmentContext as DashboardActivity).canUserAccessApp) {

                    when (item.id) {
                        "1" -> {
                            navController.navigate(MobileNavigationDirections.actionGlobalNavMyVocalsProfile())
                        }
                        "2" -> {
                            navController.navigate(MobileNavigationDirections.actionGlobalNavMyQuestionPlayed())
                        }
                        "3" -> {
                            navController.navigate(MobileNavigationDirections.actionGlobalNavAllSurveys())
                        }
                        "4" -> {
                            navController.navigate(MobileNavigationDirections.actionGlobalNavMyQuestions())
                        }
                    }

                } else {
                    (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
                }


            }

            binding.rvFilter.adapter = ConcatAdapter(headerAdapter, filtersAdapter)
        }


    }
}