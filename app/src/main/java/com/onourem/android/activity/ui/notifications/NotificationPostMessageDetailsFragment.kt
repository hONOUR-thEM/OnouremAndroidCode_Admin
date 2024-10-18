package com.onourem.android.activity.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentNotificationPostMessageDetailsBinding
import com.onourem.android.activity.models.FeedsList
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.models.PostsActionResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.YoutubeActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.adapters.TaskMessageCommentsAdapter
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.games.fragments.WriteCommentDialogFragment
import com.onourem.android.activity.ui.games.viewmodel.CommentsViewModel
import com.onourem.android.activity.ui.games.viewmodel.TaskAndMessageViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.VerticalSpaceItemDecoration
import javax.inject.Inject

class NotificationPostMessageDetailsFragment :
    AbstractBaseViewModelBindingFragment<TaskAndMessageViewModel, FragmentNotificationPostMessageDetailsBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var question: LoginDayActivityInfoList? = null
    private var taskMessageCommentsAdapter: TaskMessageCommentsAdapter? = null
    private var userActionViewModel: UserActionViewModel? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var loginUserId: String? = null
    private var commentsViewModel: CommentsViewModel? = null

    //    private boolean scrollEnabled = true;
    override fun viewModelType(): Class<TaskAndMessageViewModel> {
        return TaskAndMessageViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_notification_post_message_details
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            UserActionViewModel::class.java
        )
        commentsViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            CommentsViewModel::class.java
        )
        commentsViewModel!!.commentCountLiveData.observe(this) { item: Triple<String?, Int, String?>? ->
            if (taskMessageCommentsAdapter != null && item != null) {
                taskMessageCommentsAdapter!!.updateComment(item.first, item.second!!)
                commentsViewModel!!.resetCommentCountLiveData()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val notificationPostMessageDetailsFragmentArgs =
            NotificationPostMessageDetailsFragmentArgs.fromBundle(
                requireArguments()
            )
        val apiResponse = notificationPostMessageDetailsFragmentArgs.apiResponse
        question = notificationPostMessageDetailsFragmentArgs.loginDayActivityInfoList
        loginUserId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
        binding.rvComments.visibility = View.VISIBLE
        linearLayoutManager = object : LinearLayoutManager(requireActivity(), VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }
        binding.rvComments.itemAnimator = DefaultItemAnimator()
        binding.rvComments.addItemDecoration(VerticalSpaceItemDecoration(10))
        binding.rvComments.layoutManager = linearLayoutManager
        //        binding.rvComments.addOnScrollListener(new PaginationListener(linearLayoutManager) {
//            @Override
//            protected void loadMoreItems() {
//
//            }
//
//            @Override
//            public boolean isLastPage() {
//                return true;
//            }
//
//            @Override
//            public boolean isLoading() {
//                return false;
//            }
//        });
        setAdapter(apiResponse.feedsList!!)
        userActionViewModel!!.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()
                if (action.actionType == ActionType.EDIT_GAME_VISIBILITY) {
                    editGamePrivacy(action)
                } else if (action.actionType == ActionType.DELETE_GAME) {
                    deleteThisPost(action)
                } else if (action.actionType == ActionType.REPORT_ABUSE) {
                    reportAbuse(action)
                }
            }
        }
    }

    private fun reportAbuse(action: Action<Any?>?) {
        val item = action!!.data as FeedsList
        TwoActionAlertDialog.showAlert(
            requireActivity(),
            getString(R.string.label_confirm),
            getString(R.string.message_report_post_abuse),
            item,
            "Cancel",
            "Yes"
        ) { item1: Pair<Int?, FeedsList?>? ->
            if (item1 != null && item1.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
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
        val item = action!!.data as FeedsList

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

    private fun deleteThisPost(action: Action<Any?>?) {
        val item = action!!.data as FeedsList
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
                if (question != null) {
                    if (!TextUtils.isEmpty(apiResponse.body.activityTagStatus)) question?.activityTag =
                        apiResponse.body.activityTagStatus
                    if (!TextUtils.isEmpty(apiResponse.body.participationStatus)) question?.userParticipationStatus =
                        apiResponse.body.participationStatus
                }
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

    private fun setAdapter(feedsList: List<FeedsList>) {

//        feedsList.forEach {
//            it.commentsEnabled = question?.commentsEnabled
//        }

        taskMessageCommentsAdapter = TaskMessageCommentsAdapter(
            navController,
            feedsList,
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
//                    var i = 0
//                    while (i < item.second.receiverId!!.size) {
//                        if (i < item.second.receiverId!!.size && loginUserId == item.second.receiverId!![i]) {
//                            actions.add(
//                                Action(
//                                    "Report Abuse",
//                                    R.color.color_black,
//                                    ActionType.REPORT_ABUSE,
//                                    item.second
//                                )
//                            )
//                            break
//                        }
//                        i += 4
//                    }

                    actions.add(
                        Action(
                            "Report Abuse",
                            R.color.color_black,
                            ActionType.REPORT_ABUSE,
                            item.second
                        )
                    )
                }
                val bundle = Bundle()
                bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
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
                } else {
                    if (!item.second.postCreatedId.equals("4264", ignoreCase = true)) {
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavProfile(
                                question?.activityId, item.second.postCreatedId
                            )
                        )
                    } else {
                        showAlert("You can't access profile of this admin user")
                    }
                }
            } else if (item.first == CLICK_COMMENT_COUNT) {
                val receiverIds = StringBuilder()
                val receiverArray = java.util.ArrayList<String>()

                var i = 0
                while (i < item.second.receiverId!!.size) {
                    receiverIds.append(item.second.receiverId!![i])
                    receiverArray.add(item.second!!.receiverId!![i])
                    if (i < item.second.receiverId!!.size) receiverIds.append(",")
                    i += 4
                }
                val userId = preferenceHelper?.getString(Constants.KEY_LOGGED_IN_USER_ID)

                if (question != null) {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavPostCommentList(
                            if (TextUtils.isEmpty(
                                    question?.activityGameResponseId?:""
                                )
                            ) ""
                            else question?.activityGameResponseId?:"",
                            item.second.activityId ?: "",
                            item.second.postId ?: "",
                            receiverIds.toString(),
                            "",
                            question?.activityId ?: "",
                            item.second.postCreatedId ?: "",
                            "Post",
                            receiverArray.any { it == userId } || item.second.postCreatedId == userId

                        )
                    )
                } else {
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
                    if (taskMessageCommentsAdapter != null) taskMessageCommentsAdapter!!.notifyDataSetChanged()
                }
                    .show(requireActivity().supportFragmentManager, "Comment")
            } else if (item.first == TaskMessageCommentsAdapter.CLICK_ATTACHMENT) {
                if (!TextUtils.isEmpty(question?.youTubeVideoId)) {
                    val intent = Intent(context, YoutubeActivity::class.java)
                    intent.putExtra(
                        "youtubeId",
                        question?.youTubeVideoId
                    )
                    (fragmentContext as DashboardActivity).exoPlayerPause(true)
                    fragmentContext.startActivity(intent)
                } else {
                    var url: String? = ""
                    val media: Int
                    if (!TextUtils.isEmpty(item.second.videoURL)) {
                        url = item.second.videoURL
                        media = 2
                    } else {
                        url = item.second.postLargeImageURL
                        media = 1
                    }

                    if (!TextUtils.isEmpty(url)) {
                        navController.navigate(
                            MobileNavigationDirections.actionGlobalNavMediaView(
                                media,
                                url!!
                            )
                        )
                    }
                }
            }
        }
        binding.rvComments.adapter = taskMessageCommentsAdapter
    }

    override fun onDestroyView() {
        commentsViewModel?.resetCommentCountLiveData()
        super.onDestroyView()
    }

    companion object {
        var CLICK_COMMENT_COUNT = 5
    }
}