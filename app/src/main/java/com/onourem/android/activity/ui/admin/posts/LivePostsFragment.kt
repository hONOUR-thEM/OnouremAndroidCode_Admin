package com.onourem.android.activity.ui.admin.posts

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentPostsBinding
import com.onourem.android.activity.models.FeedsList
import com.onourem.android.activity.models.PublicPostListResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.admin.posts.adapters.PostsAdapter
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.adapters.TaskMessageCommentsAdapter
import com.onourem.android.activity.ui.games.fragments.WriteCommentDialogFragment
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.games.viewmodel.SelectPrivacyViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.VerticalSpaceItemDecoration
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject
import kotlin.math.min

class LivePostsFragment :
    AbstractBaseViewModelBindingFragment<AdminViewModel, FragmentPostsBinding>() {

    private var taskMessageCommentsAdapter: PostsAdapter? = null
    private var feedIdsList: List<String>? = null


    private lateinit var questionGamesViewModel: QuestionGamesViewModel

    private var isLastPagePagination: Boolean = false
    private var isLoadingPage: Boolean = false
    private var displayNumberOfPosts: Long = 20L

    private var layoutManager: LinearLayoutManager? = null

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var selectPrivacyViewModel: SelectPrivacyViewModel
    private lateinit var userActionViewModel: UserActionViewModel

    private var isDataLoading = false

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    companion object {
        fun create(): LivePostsFragment {
            return LivePostsFragment()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userActionViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]

        dashboardViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[DashboardViewModel::class.java]

        selectPrivacyViewModel =
            ViewModelProvider(
                requireActivity(),
                viewModelFactory
            )[SelectPrivacyViewModel::class.java]

        questionGamesViewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[QuestionGamesViewModel::class.java]

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.setOnRefreshListener {
            isDataLoading = false
            binding.swipeRefreshLayout.isRefreshing = false
            refreshUI()
        }

        loadData()

        layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        val animator = binding!!.rvPosts.itemAnimator as SimpleItemAnimator?
        if (animator != null) animator.supportsChangeAnimations = false
        binding!!.rvPosts.addItemDecoration(VerticalSpaceItemDecoration(8))
        binding!!.rvPosts.layoutManager = layoutManager
        binding!!.rvPosts.addOnScrollListener(object :
            PaginationListener(layoutManager!!) {
            override fun loadMoreItems() {
                this@LivePostsFragment.isLoadingPage = true
                loadMorePosts()
            }

            override fun isLastPage(): Boolean {
                return this@LivePostsFragment.isLastPagePagination
            }

            override fun isLoading(): Boolean {
                return this@LivePostsFragment.isLoadingPage
            }
        })

    }

    override fun layoutResource(): Int {
        return R.layout.fragment_posts
    }

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshUI() {
        isLastPagePagination = false
        isLoadingPage = false
        if (taskMessageCommentsAdapter != null)
            taskMessageCommentsAdapter!!.removeFooter()
        if (taskMessageCommentsAdapter != null) {
            taskMessageCommentsAdapter!!.clearData()
            taskMessageCommentsAdapter = null
        }
        taskMessageCommentsAdapter = null
        if (!isDataLoading) loadData()

    }

    private fun loadData() {

        isLastPagePagination = false
        isLoadingPage = false
        isDataLoading = true

        userPosts()

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter(feeds: List<FeedsList>) {
        taskMessageCommentsAdapter = PostsAdapter(
            "1",
            navController,
            feeds,
            alertDialog
        ) { item: Pair<Int, FeedsList> ->
            when (item.first) {
                PostsAdapter.CLICK_MORE -> {
                    val title = "What would you like to do?"
                    val actions = ArrayList<Action<*>>()

//                    actions.add(
//                        Action(
//                            "Delete This Message",
//                            R.color.color_red,
//                            ActionType.DELETE_GAME,
//                            item.second
//                        )
//                    )
//                    actions.add(
//                        Action(
//                            "Edit Post Visibility",
//                            R.color.color_black,
//                            ActionType.EDIT_GAME_VISIBILITY,
//                            item.second
//                        )
//                    )
                    actions.add(
                        Action(
                            "Report Abuse",
                            R.color.color_black,
                            ActionType.REPORT_ABUSE,
                            item.second
                        )
                    )

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
                PostsAdapter.CLICK_SENDER_PROFILE -> {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavProfile(
                            item.second.activityId,
                            item.second.postCreatedId
                        )
                    )
                }

                PostsAdapter.CLICK_PUSH_TO_SKY -> {
                    postPushToSky("N", item.second)
                }

                PostsAdapter.CLICK_REJECT_TO_SKY -> {
                    postPushToSky("R", item.second)
                }

                PostsAdapter.CLICK_COMMENT_COUNT -> {
                    val receiverIds = StringBuilder()
                    val receiverArray = java.util.ArrayList<String>()
                    var i = 0
                    while (i < item.second.receiverId!!.size) {
                        receiverIds.append(item.second.receiverId!![i])
                        if (i < item.second.receiverId!!.size) receiverIds.append(",")
                        i += 4
                    }

                    val userId = preferenceHelper.getString(Constants.KEY_LOGGED_IN_USER_ID)

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
                }
                PostsAdapter.CLICK_COMMENT -> {
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
                            //scrollToTop(updatedItemPosition)
                        }
                    }.show(requireActivity().supportFragmentManager, "Comment")
                }
                PostsAdapter.CLICK_ATTACHMENT -> {
                    var url: String? = ""
                    val media: Int
                    if (!TextUtils.isEmpty(item.second.videoURL)) {
                        url = item.second.videoURL
                        media = 2
                    } else {
                        url = item.second.postLargeImageURL
                        media = 1
                    }

                    if (url != null && url.isNotEmpty()) {
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavMediaView(
                                media,
                                url
                            )
                        )
                    }
                }
            }
        }
        binding!!.rvPosts.adapter = taskMessageCommentsAdapter
    }


    private fun postPushToSky(status: String, item: FeedsList) {

        viewModel!!.postPushToSky(status, item.postId!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    binding!!.swipeRefreshLayout.isRefreshing = false
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        taskMessageCommentsAdapter!!.removeItem(item)
                    } else {
                        setFooterMessage()
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    binding!!.swipeRefreshLayout.isRefreshing = false
                    showAlert(apiResponse.errorMessage)
                }
            }
    }


    private fun setFooterMessage() {
        isLoadingPage = false
        val footerMessage: String
        if (taskMessageCommentsAdapter != null) {
            footerMessage =
                if (taskMessageCommentsAdapter!!.itemCount > 0 /*&& !soloGamesAdapter.hasValidItems()*/) {
                    "These are the trending Posts. Want to play more? Check other filters"
                } else {
                    "No Posts Yet"
                }
        }
    }

    private fun userPosts() {

        viewModel!!.publicPostIdList("Y")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<PublicPostListResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    binding!!.swipeRefreshLayout.isRefreshing = false
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        binding!!.tvMessage.visibility = View.GONE
                        if (apiResponse.body.feedsList.isEmpty()) {
                            binding!!.tvMessage.visibility = View.VISIBLE
                            if (taskMessageCommentsAdapter != null) taskMessageCommentsAdapter!!.clearData()
                        } else {
                            feedIdsList = apiResponse.body.postIdList
                            val feeds = apiResponse.body.feedsList
                            //blockedUserCount += BlockedUsersUtility.filterBlockedUsersFromFeeds(blockedUserIds, feeds);
                            //Log.e("####", String.format("total post ids: %s", feedIdsList.toString()));
                            setAdapter(feeds)
                        }
                    } else {
                        setFooterMessage()
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    binding!!.swipeRefreshLayout.isRefreshing = false
                    showAlert(apiResponse.errorMessage)
                }
            }
    }

    private fun getDisplayPostIdListElements(
        myList: List<String>?,
        startIndex: Int
    ): List<String?> {
        val sub: List<String?>
        sub = myList!!.subList(
            startIndex,
            min(myList.size.toLong(), startIndex + displayNumberOfPosts).toInt()
        )
        return sub
    }

    private fun loadMorePosts() {
        if (feedIdsList == null || feedIdsList!!.isEmpty()) {
            this@LivePostsFragment.isLastPagePagination = true
            this@LivePostsFragment.isLoadingPage = false
            setFooterMessage()
            return
        }
        val start = taskMessageCommentsAdapter!!.itemCount
        val end =
            if (feedIdsList!!.size - start > displayNumberOfPosts.toInt()) start + displayNumberOfPosts.toInt() else feedIdsList!!.size
        if (start >= end) {
            this@LivePostsFragment.isLastPagePagination = true
            this@LivePostsFragment.isLoadingPage = false
            setFooterMessage()
            return
        }

        val ids = ArrayList<String>()
        val gameIdList1 = getDisplayPostIdListElements(feedIdsList, start)
        AppUtilities.showLog("**feedIdsList:", gameIdList1.size.toString())

        for (i in gameIdList1.indices) {
            val item = gameIdList1[i]!!
            ids.add(item)
        }

        val postsIds = Utilities.getTokenSeparatedString(ids, ",")
        //Log.e("####", String.format("start: %d ; end: %d ; sent: %s ; total: %d", start, end, postsIds, feedIdsList.size()));
        //Log.e("####", String.format("sent ids: %s", postsIds));
        viewModel!!.getNextPublicPostIdData(postsIds)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<PublicPostListResponse> ->
                if (apiResponse.loading) {
                    if (taskMessageCommentsAdapter != null) {
                        taskMessageCommentsAdapter!!.addLoading()
                    }
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (taskMessageCommentsAdapter != null) {
                        taskMessageCommentsAdapter!!.removeLoading()
                    }
                    this@LivePostsFragment.isLoadingPage = false
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        if (apiResponse.body.feedsList == null || apiResponse.body.feedsList.isEmpty()) {
                            this@LivePostsFragment.isLastPagePagination = true
                            setFooterMessage()
                        } else {
                            val list = apiResponse.body.feedsList
                            //blockedUserCount += BlockedUsersUtility.filterBlockedUsersFromFeeds(blockedUserIds, list);
                            taskMessageCommentsAdapter!!.addItems(list)
                            if (list.size >= feedIdsList!!.size) {
                                this@LivePostsFragment.isLastPagePagination = true
                                setFooterMessage()
                            }
                            //Log.e("####", String.format("server: %d", apiResponse.body.getFeedsList().size()));
                        }
                    } else {
                        this@LivePostsFragment.isLastPagePagination = true
                        setFooterMessage()
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    this@LivePostsFragment.isLoadingPage = false
                    if (taskMessageCommentsAdapter != null) {
                        taskMessageCommentsAdapter!!.removeLoading()
                    }
                    showAlert(apiResponse.errorMessage)
                }
            }
    }

}