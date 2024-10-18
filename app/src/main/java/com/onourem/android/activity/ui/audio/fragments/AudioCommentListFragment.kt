package com.onourem.android.activity.ui.audio.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentCommentsListBinding
import com.onourem.android.activity.databinding.ItemCommentRowBinding
import com.onourem.android.activity.models.AudioComment
import com.onourem.android.activity.models.AudioCommentResponse
import com.onourem.android.activity.models.CommentList
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.audio.playback.Song
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.viewmodel.CommentsViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.onourem.android.activity.ui.utils.*
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class AudioCommentListFragment :
    AbstractBaseViewModelBindingFragment<CommentsViewModel, FragmentCommentsListBinding>() {
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var comments: MutableList<AudioComment>

    private var displayNumberOfAudios: Long = 20L

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var audioId: String? = null
    private var commentsAdapter: CommentsAdapter? = null
    private var snackbar: Snackbar? = null
    private var isLastPagePagination: Boolean = false
    private var isLoadingPage: Boolean = false

    private lateinit var userActionViewModel: UserActionViewModel

    override fun viewModelType(): Class<CommentsViewModel> {
        return CommentsViewModel::class.java
    }

    private var audioCommentIdList: ArrayList<Int>? = null
    override fun layoutResource(): Int {
        return R.layout.fragment_comments_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = AudioCommentListFragmentArgs.fromBundle(
            requireArguments()
        )
        audioId = args.audioId

        userActionViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //enableSwipeToDeleteAndUndo()
        binding.tvWriteComment.setOnClickListener(ViewClickListener {

            if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_audio_comments_list) {
                navController.navigate(
                    AudioCommentListFragmentDirections.actionNavAudioCommentsListToNavAudioComment(
                        audioId!!
                    )
                )
            }
        })

        loadData(true)

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            loadData(false)
        }

        layoutManager = LinearLayoutManager(requireActivity())

        binding.rvComments.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.rvComments.addItemDecoration(VerticalSpaceItemDecoration(10))

        binding.rvComments.addOnScrollListener(object : PaginationListener(layoutManager) {

            override fun loadMoreItems() {
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@AudioCommentListFragment.isLastPagePagination
            }

            override fun isLoading(): Boolean {
                return this@AudioCommentListFragment.isLoadingPage
            }
        })

        viewModel.reloadUi().observe(viewLifecycleOwner) {
            if (it != null && it == "reload") {
                loadData(false)
            }
        }

        userActionViewModel.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel.actionConsumed()
                when (action.actionType) {

                    ActionType.DELETE_COMMENT       -> {
                        deleteComment(action)
                    }
                    ActionType.REPORT_INAPPROPRIATE -> {
                        reportInappropriate(action)
                    }
                    else                            -> {

                    }
                }

            }
        }
    }

    private fun deleteComment(action: Action<Any?>) {
        val comment = action.data as AudioComment
        commentsAdapter!!.removeItem(comment)
        viewModel.deleteComments(comment.commentId!!).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            }
            else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    Toast.makeText(
                        fragmentContext,
                        "'" + Base64Utility.decode(comment.commentText) + "'" + " comment deleted",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    showAlert(apiResponse.body.errorMessage)
                }
            }
            else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun reportInappropriate(action: Action<Any?>) {
        val comment = action.data as AudioComment
        viewModel.reportInappropriateAudioComment(comment.commentId).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    Toast.makeText(
                        fragmentContext,
                        "'" +Base64Utility.decode(comment.commentText)+ "'" + " reported as Inappropriate comment",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun getDisplayGameIdListElements(myList: ArrayList<Int>, startIndex: Int): List<Int?> {
        var sub: List<Int?>
        sub = myList.subList(
            startIndex,
            Math.min(myList.size.toLong(), startIndex + displayNumberOfAudios).toInt()
        )
        return sub
    }

    private fun loadMoreGames() {
        isLoadingPage = true
        var start = 0
        var end = 0

        if (audioCommentIdList!!.isEmpty()) {
            isLastPagePagination = true
            isLoadingPage = false
            //setFooterMessage()
            return
        }

        start = commentsAdapter!!.itemCount
        end = audioCommentIdList!!.size
        if (start >= end) {
            isLastPagePagination = true
            isLoadingPage = false
            //setFooterMessage()
            return
        }

        val audioIds = ArrayList<Int>()
        val gameIdList1 = getDisplayGameIdListElements(audioCommentIdList!!, start) as List<*>
        AppUtilities.showLog("**audioGameIdList1:", gameIdList1.size.toString())

        for (i in gameIdList1.indices) {
            val item = gameIdList1[i]
            audioIds.add(item as Int)
        }

        viewModel.getNextAudioComments(Utilities.getTokenSeparatedString(audioIds, ","))
            .observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<AudioCommentResponse> ->
                if (apiResponse.loading && commentsAdapter != null) {
                    commentsAdapter!!.addLoading()
                }
                else if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (commentsAdapter != null) {
                        commentsAdapter!!.removeLoading()
                    }
                    isLoadingPage = false
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        //AppUtilities.showLog("PlayGamesFragment", "LoginDayActivityInfoList getNextUserLoginDayActivityInfo" + apiResponse.body.getLoginDayActivityInfoList().size() + "");
                        if (apiResponse.body.audioCommentList!!
                                .isNotEmpty()
                        ) {
                            //isLastPagePagination = true;
                            //setFooterMessage()
                            isLastPagePagination =
                                apiResponse.body.audioCommentList!!.size < displayNumberOfAudios
                            commentsAdapter!!.addItems(apiResponse.body.audioCommentList)
                        }
                        else {

                            //comments.addAll(apiResponse.body.audioCommentList)

//
//                                    Log.e("####",
//                                        String.format("server: %d",
//                                            apiResponse.body.audioCommentList.size))
                        }
                    }
                    else {
                        // isLastPage = true;
                        //setFooterMessage()
                        showAlert(apiResponse.body.errorMessage)
                    }
                }
                else {
                    isLoadingPage = false
                    if (commentsAdapter != null) {
                        commentsAdapter!!.removeLoading()
                    }
                    showAlert(apiResponse.errorMessage)
                }
            }

    }

    private fun loadData(showProgress: Boolean) {
        audioCommentIdList = ArrayList()
        comments = ArrayList()
        isLastPagePagination = false
        isLoadingPage = false
        viewModel.getAudioComments(audioId!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<AudioCommentResponse> ->
                if (apiResponse.loading) {
                    if (showProgress) showProgress()
                }
                else if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing =
                        false
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        if (apiResponse.body.audioCommentList != null && apiResponse.body.audioCommentList!!.isNotEmpty()) {
                            displayNumberOfAudios =
                                apiResponse.body.displayNumberOfComments!!.toLong()
                            audioCommentIdList!!.addAll(apiResponse.body.audioCommentIdList!!)
                            comments.addAll(apiResponse.body.audioCommentList!!)
                            setAdapter(comments)
                        }
                    }
                    else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                }
                else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "getAudioComments")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getAudioComments",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun setAdapter(commentList: List<AudioComment?>) {
        if (commentList.isNotEmpty()) {
//            viewModel.updateActivityNotificationStatus(activityId, playGroupId, gameId, gameResId).observe(getViewLifecycleOwner(), apiResponse -> {
//                if (apiResponse.isSuccess() && apiResponse.body != null && apiResponse.body.getErrorCode().equalsIgnoreCase("000")) {
////                            Toast.makeText(requireActivity(), "updateActivityNotificationStatus + Activity Id", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
        if (commentsAdapter == null) {
            commentsAdapter = CommentsAdapter(commentList) { item: Pair<Int?, AudioComment?> ->
                if (item.first != null && item.first == CommentsAdapter.CLICK_PROFILE && item.second != null) {
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavProfile(
                            null,
                            item.second!!.commentedBy
                        )
                    )
                }
                else if (item.first != null && item.first == CommentsAdapter.CLICK_MORE && item.second != null) {
                    val userId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
                    val title = "What would you like to do?"
                    val actions = ArrayList<Action<*>>()
                    val audioCreatedId = AudioCommentListFragmentArgs.fromBundle(requireArguments()).audioCreatorId


                    if (item.second!!.commentedBy == userId || audioCreatedId == userId) {

                        actions.add(
                            Action(
                                getString(R.string.action_label_delete_comment),
                                R.color.color_black,
                                ActionType.DELETE_COMMENT,
                                item.second,
                                null
                            )
                        )

                    }

                    if (item.second!!.commentedBy != userId) {
                        actions.add(
                            Action(
                                getString(R.string.action_label_report_inappropriate),
                                R.color.color_black,
                                ActionType.REPORT_INAPPROPRIATE,
                                item.second,
                                null
                            )
                        )
                    }


                    val bundle = Bundle()
                    bundle.putParcelableArrayList(
                        Constants.KEY_BOTTOM_SHEET_ACTIONS,
                        actions,
                    )
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                            title,
                            bundle, ""
                        )
                    )


                }
            }
        }
        else {
            commentsAdapter!!.resetData(commentList)
        }
        if (binding.rvComments.adapter == null) binding.rvComments.adapter = commentsAdapter
//        binding.rvComments.postDelayed({ binding.rvComments.smoothScrollToPosition(commentList.size - 1) },
//            500)
    }

//    private fun enableSwipeToDeleteAndUndo() {
//        val swipeHelper: SwipeHelper = object : SwipeHelper(requireActivity()) {
//            val delete = UnderlayButton(
//                "Delete",
//                0,
//                ContextCompat.getColor(fragmentContext, R.color.color_6)
//            ) { pos: Int ->
//                val item = commentsAdapter!!.data[pos]
//                commentsAdapter!!.removeItem(pos)
//                snackbar = Snackbar.make(
//                    binding.root,
//                    "Comment deleted successfully..",
//                    Snackbar.LENGTH_LONG
//                )
//                snackbar!!.setAction("UNDO") { view: View? ->
//                    commentsAdapter!!.restoreItem(
//                        item,
//                        pos
//                    )
//                }
//                snackbar!!.addCallback(object : Snackbar.Callback() {
//                    override fun onDismissed(snackbar: Snackbar, event: Int) {
//                        super.onDismissed(snackbar, event)
//                        if (event != DISMISS_EVENT_ACTION) {
//                            viewModel.deleteComments(item.commentId!!)
//                        }
//                    }
//                })
//                snackbar!!.setActionTextColor(
//                    ContextCompat.getColor(
//                        fragmentContext,
//                        R.color.colorAccent
//                    )
//                )
//                snackbar!!.setTextColor(Color.WHITE)
//                snackbar!!.show()
//            }
//
//            override fun instantiateUnderlayButton(
//                viewHolder: RecyclerView.ViewHolder,
//                underlayButtons: MutableList<UnderlayButton>,
//            ) {
//                val item = commentsAdapter!!.data[viewHolder.bindingAdapterPosition]
//                if (item.commentedBy.equals(
//                        preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID),
//                        ignoreCase = true
//                    )
//                ) underlayButtons.add(delete)
//            }
//
//        }
//        swipeHelper.attachToRecyclerView(binding.rvComments)
//    }

    override fun onDestroyView() {
        if (snackbar != null && snackbar!!.isShown) snackbar!!.dismiss()
        var count = 0
        if (audioCommentIdList != null) count = audioCommentIdList!!.size

        viewModel.publishCommentCount(audioId, null, count, null)
        super.onDestroyView()
    }
}

internal class CommentsAdapter(
    activityGameOneToManyResList: List<AudioComment?>?,
    private val onItemClickListener: OnItemClickListener<Pair<Int?, AudioComment?>>,
) : PaginationRVAdapter<AudioComment?>(
    activityGameOneToManyResList!!
) {
    override fun emptyLoadingItem(): AudioComment {
        return AudioComment()
    }

    override fun emptyFooterItem(): AudioComment {
        return AudioComment()
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return OneToManyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_comment_row, parent, false)
        )
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun removeItem(item: AudioComment?) {
        val position = items.indexOf(item)
        items.remove(item)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: AudioComment?, position: Int) {
        items.add(position, item)
        notifyItemInserted(position)
    }

    val data: List<AudioComment>
        get() = items as List<AudioComment>

    inner class OneToManyViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val itemBinding: ItemCommentRowBinding = ItemCommentRowBinding.bind(itemView)
        private val options = RequestOptions()
            .fitCenter()
            .placeholder(R.drawable.default_user_profile_image)
            .error(R.drawable.default_user_profile_image)
        private val optionsRoundedCorners = RequestOptions()
            .fitCenter()
            .transform(FitCenter(), RoundedCorners(15))
            .placeholder(R.drawable.default_place_holder)
            .error(R.drawable.default_place_holder)

        override fun onBind(position: Int) {
            val item = items[position]!!
            var response: String? = ""
            itemView.tag = item
            itemBinding.tvComment.gravity = Gravity.START
            response = Base64Utility.decode(item.commentText)
            itemBinding.tvName.text = item.userName
            itemBinding.tvComment.text = response
            Glide.with(itemBinding.root.context)
                .load(item.userProfilePic)
                .apply(options)
                .into(itemBinding.rivProfile)
            Utilities.verifiedUserType(
                itemBinding.root.context,
                item.userType,
                itemBinding.ivIconVerified
            )
            itemBinding.ivPlayVideo.visibility = View.GONE
            itemBinding.ivCommentAttachment.visibility = View.GONE
        }

        init {

//            itemBinding.ivCommentAttachment.setOnClickListener(new ViewClickListener(
//                    v -> onItemClickListener.onItemClick(Pair.create(CLICK_ATTACHMENT, items.get(getBindingAdapterPosition())))
//            ));
            itemBinding.rivProfile.setOnClickListener(ViewClickListener {
                onItemClickListener.onItemClick(
                    Pair.create(CLICK_PROFILE, items[bindingAdapterPosition])
                )
            })

            itemBinding.ivUserMoreAction.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Pair.create(CLICK_MORE, items[bindingAdapterPosition])
                )
            })

            itemBinding.root.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Pair.create(CLICK_MORE, items[bindingAdapterPosition])
                )
            })

        }
    }

    companion object {
        var CLICK_PROFILE = 3
        var CLICK_ATTACHMENT = 4
        var CLICK_MORE = 5
    }
}