package com.onourem.android.activity.ui.games.fragments

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
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
import com.onourem.android.activity.models.CommentList
import com.onourem.android.activity.models.CommentsResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.viewmodel.CommentsViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import com.onourem.android.activity.ui.utils.*
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.utils.zoomy.Zoomy
import javax.inject.Inject

class CommentListFragment : AbstractBaseViewModelBindingFragment<CommentsViewModel, FragmentCommentsListBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var gameResId: String? = null
    private var gameId: String? = null
    private var activityId: String? = null
    private var postCreatedId: String? = null
    private var isContentOwner: Boolean = false
    private var participantId: String? = null
    private var postId: String? = null
    private var playGroupId: String? = null
    private var from: String? = null
    private var commentsAdapter: CommentsAdapter? = null
    private var snackbar: Snackbar? = null
    private var userActionViewModel: UserActionViewModel? = null
    private var mView: View? = null
    override fun viewModelType(): Class<CommentsViewModel> {
        return CommentsViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_comments_list
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = CommentListFragmentArgs.fromBundle(requireArguments())
        gameResId = args.gameResId
        gameId = args.gameId
        participantId = args.participantId
        postId = args.postId
        playGroupId = args.playGroupId
        activityId = args.activityId
        postCreatedId = args.postCreatedId
        from = args.from
        isContentOwner = args.isContentOwner
        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(UserActionViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireActivity())
        mView = view
        binding.rvComments.layoutManager = layoutManager
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.rvComments.addItemDecoration(VerticalSpaceItemDecoration(10))
        //enableSwipeToDeleteAndUndo()
        binding.tvWriteComment.setOnClickListener { v: View? ->
            WriteCommentDialogFragment.getInstance(gameId, participantId, gameResId, postId, playGroupId) { item1: Void? -> loadData(false) }
                .show(requireActivity().supportFragmentManager, "Comment")
        }
        loadData(true)
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            loadData(false)
        }

        userActionViewModel!!.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()
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
        val comment = action.data as CommentList
        commentsAdapter!!.removeItem(comment)
        viewModel.deleteComment(comment.commentId, postId, gameId).observe(
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
        val comment = action.data as CommentList
        viewModel.reportInappropriateComment(comment.commentId, from).observe(
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
                        "'" + Base64Utility.decode(comment.commentText) + "'" + " reported as Inappropriate comment",
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

    private fun loadData(showProgress: Boolean) {
        if (mView != null && isVisible) {
            viewModel.getPostComments(gameId, postId, participantId).observe(viewLifecycleOwner) { apiResponse: ApiResponse<CommentsResponse> ->
                if (apiResponse.loading) {
                    if (showProgress) showProgress()
                }
                else if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (binding.swipeRefreshLayout.isRefreshing) binding.swipeRefreshLayout.isRefreshing = false
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        if (apiResponse.body.commentList != null && apiResponse.body.commentList!!.isNotEmpty()) {
                            setAdapter(apiResponse.body.commentList!!)
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
                            AppUtilities.showLog("Network Error", "getPostComments")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo("getPostComments", apiResponse.code.toString())
                    }
                }
            }
        }
    }

    private fun setAdapter(commentList: List<CommentList?>) {
        if (commentList.isNotEmpty()) {
            viewModel.updateActivityNotificationStatus(activityId, playGroupId, gameId, gameResId)
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                    if (apiResponse.isSuccess && apiResponse.body != null && apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                            Toast.makeText(requireActivity(), "updateActivityNotificationStatus + Activity Id", Toast.LENGTH_SHORT).show();
                    }
                }
        }
        if (commentsAdapter == null) {
            commentsAdapter = CommentsAdapter(commentList) { item: Pair<Int?, CommentList?> ->
                if (item.first != null && item.first == CommentsAdapter.CLICK_ATTACHMENT && item.second != null) {
                    var url: String? = ""
                    val media: Int
                    if (!TextUtils.isEmpty(item.second!!.videoImageUrl)) {
                        url = item.second!!.videoImageUrl
                        media = 2
                    }
                    else {
                        url = item.second!!.postLargeImageUrl
                        media = 1
                    }
                    if (url != null){
                        navController.navigate(MobileNavigationDirections.actionGlobalNavMediaView(media, url))
                    }
                }
                if (item.first != null && item.first == CommentsAdapter.CLICK_PROFILE && item.second != null) {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavProfile(null, item.second!!.commentedBy))
                }
                else if (item.first != null && item.first == CommentsAdapter.CLICK_MORE && item.second != null) {
                    val userId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
                    val title = "What would you like to do?"
                    val actions = ArrayList<Action<*>>()


                    if (item.second!!.commentedBy == userId || isContentOwner || participantId == userId) {

                        actions.add(
                            Action(
                                getString(R.string.action_label_delete_comment),
                                R.color.color_black,
                                ActionType.DELETE_COMMENT,
                                item.second,
                            )
                        )

                    }

                    if (item.second!!.commentedBy != userId) {
                        actions.add(
                            Action(
                                getString(R.string.action_label_report_inappropriate),
                                R.color.color_black,
                                ActionType.REPORT_INAPPROPRIATE,
                                item.second
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
        binding.rvComments.postDelayed({ binding.rvComments.smoothScrollToPosition(commentList.size - 1) }, 500)
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeHelper: SwipeHelper = object : SwipeHelper(requireActivity()) {
            val delete = UnderlayButton(
                "Delete",
                0,
                ContextCompat.getColor(fragmentContext, R.color.color_6)
            ) { pos: Int ->
                val item = commentsAdapter!!.data[pos]
                commentsAdapter!!.removeItem(pos)
                snackbar = Snackbar.make(binding.root, "Comment deleted successfully..", Snackbar.LENGTH_LONG)
                snackbar!!.setAction("UNDO") { view: View? -> commentsAdapter!!.restoreItem(item, pos) }
                snackbar!!.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        super.onDismissed(snackbar, event)
                        if (event != DISMISS_EVENT_ACTION) {
                            viewModel.deleteComment(item.commentId, postId, gameId)
                        }
                    }
                })
                snackbar!!.setActionTextColor(ContextCompat.getColor(fragmentContext, R.color.colorAccent))
                snackbar!!.setTextColor(Color.WHITE)
                snackbar!!.show()
            }

            override fun instantiateUnderlayButton(viewHolder: RecyclerView.ViewHolder, underlayButtons: MutableList<UnderlayButton>) {
                val item = commentsAdapter!!.data[viewHolder.bindingAdapterPosition]
                if (item.commentedBy.equals(preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID), ignoreCase = true)) underlayButtons.add(delete)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                super.onSwiped(viewHolder, direction)
            }
        }
        swipeHelper.attachToRecyclerView(binding.rvComments)
    }

    override fun onDestroyView() {
        if (snackbar != null && snackbar!!.isShown) snackbar!!.dismiss()
        var count = 0
        if (commentsAdapter != null) count = commentsAdapter!!.itemCount
        viewModel.publishCommentCount(postId, gameId, count, gameResId)
        super.onDestroyView()
    }
}

internal class CommentsAdapter(
    activityGameOneToManyResList: List<CommentList?>?,
    private val onItemClickListener: OnItemClickListener<Pair<Int?, CommentList?>>
) : PaginationRVAdapter<CommentList?>(
    activityGameOneToManyResList!!
) {
    override fun emptyLoadingItem(): CommentList {
        return CommentList()
    }

    override fun emptyFooterItem(): CommentList {
        return CommentList()
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return OneToManyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment_row, parent, false))
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun removeItem(item: CommentList?) {
        val position = items.indexOf(item)
        items.remove(item)
        notifyItemRemoved(position)
    }


    fun restoreItem(item: CommentList?, position: Int) {
        items.add(position, item)
        notifyItemInserted(position)
    }

    val data: List<CommentList>
        get() = items as List<CommentList>

    inner class OneToManyViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val itemBinding: ItemCommentRowBinding
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
            Utilities.verifiedUserType(itemBinding.root.context, item.userType, itemBinding.ivIconVerified)
            val media: Int = if (!TextUtils.isEmpty(item.videoImageUrl)) {
                2
            }
            else {
                1
            }
            val url: String = item.postLargeImageUrl.toString()
            if (!TextUtils.isEmpty(url)) {
                if (media == 2) {
                    itemBinding.ivPlayVideo.visibility = View.VISIBLE
                }
                else {
                    itemBinding.ivPlayVideo.visibility = View.GONE
                }
                Glide.with(itemBinding.root.context)
                    .load(url)
                    .apply(optionsRoundedCorners)
                    .into(itemBinding.ivCommentAttachment)
                itemBinding.ivCommentAttachment.visibility = View.VISIBLE
                itemBinding.ivCommentAttachment.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(itemBinding.root.context as Activity)
                    .target(itemBinding.ivCommentAttachment)
                    .enableImmersiveMode(false)
                    .tapListener { v: View? -> onItemClickListener.onItemClick(Pair.create(CLICK_ATTACHMENT, items[bindingAdapterPosition])) }
                //                        .longPressListener(v -> {
//                            LoginDayActivityInfoList item = items.get(getBindingAdapterPosition());
//                            onItemClickListener.onItemClick(new Triple<>(item, CLICK_MEDIA, String.valueOf(v.getTag())));
//                        }).doubleTapListener(v -> Toast.makeText(context, "Double tap on "
//                                + v.getTag(), Toast.LENGTH_SHORT).show());
                builder.register()
            }
            else {
                itemBinding.ivPlayVideo.visibility = View.GONE
                itemBinding.ivCommentAttachment.visibility = View.GONE
            }
        }

        init {
            itemBinding = ItemCommentRowBinding.bind(itemView)

//            itemBinding.ivCommentAttachment.setOnClickListener(new ViewClickListener(
//                    v -> onItemClickListener.onItemClick(Pair.create(CLICK_ATTACHMENT, items.get(getBindingAdapterPosition())))
//            ));
            itemBinding.rivProfile.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Pair.create(
                        CLICK_PROFILE,
                        items[bindingAdapterPosition]
                    )
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
        var CLICK_MORE = 5
        var CLICK_ATTACHMENT = 4
    }
}