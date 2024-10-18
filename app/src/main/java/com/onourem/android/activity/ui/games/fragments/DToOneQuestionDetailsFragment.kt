package com.onourem.android.activity.ui.games.fragments

import android.animation.ArgbEvaluator
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.ui.games.viewmodel.DToOneViewModel
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.onourem.android.activity.R
import javax.inject.Inject
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.ui.games.fragments.DToOneCommentsAdapter
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.models.PlayGroup
import com.onourem.android.activity.models.ActivityGameDirectToOneResList
import com.onourem.android.activity.ui.games.viewmodel.GamesReceiverViewModel
import com.onourem.android.activity.ui.games.viewmodel.CommentsViewModel
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.onourem.android.activity.ui.games.fragments.DToOneQuestionDetailsFragmentArgs
import com.onourem.android.activity.models.ActivityType
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.MobileNavigationDirections
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.models.GameActivityUpdateResponse
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.models.UpdateActivityTagStatusResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.games.fragments.AnswerQuestionDialogFragment
import com.onourem.android.activity.models.UploadPostResponse
import com.onourem.android.activity.ui.games.fragments.WriteCommentDialogFragment
import com.onourem.android.activity.models.GetDirectToOneGameActivityResResponse
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import android.view.ViewGroup
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.games.fragments.DToOneCommentsAdapter.D2OneViewHolder
import android.view.LayoutInflater
import android.annotation.SuppressLint
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.onourem.android.activity.ui.utils.zoomy.Zoomy
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.app.Activity
import android.view.View
import androidx.core.util.Pair
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.databinding.FragmentDToOneQuestionDetailsBinding
import com.onourem.android.activity.databinding.LayoutDToOneCommentViewBinding
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.utils.*
import com.onourem.android.activity.ui.utils.glide.GlideApp
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import java.lang.StringBuilder
import java.util.*

class DToOneQuestionDetailsFragment : AbstractBaseViewModelBindingFragment<DToOneViewModel, FragmentDToOneQuestionDetailsBinding>() {
    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(15))
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var question: LoginDayActivityInfoList? = null
    private var dToOneCommentsAdapter: DToOneCommentsAdapter? = null
    private var userActionViewModel: UserActionViewModel? = null
    private var playGroup: PlayGroup? = null
    private var itemList: List<ActivityGameDirectToOneResList>? = null
    private var gamesReceiverViewModel: GamesReceiverViewModel? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var commentsViewModel: CommentsViewModel? = null

    //    private List<UserList> blockedUserIds;
    private var gameIdToHighlight: String? = null
    override fun viewModelType(): Class<DToOneViewModel> {
        return DToOneViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_d_to_one_question_details
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(UserActionViewModel::class.java)
        gamesReceiverViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(GamesReceiverViewModel::class.java)
        commentsViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(CommentsViewModel::class.java)
        commentsViewModel!!.commentCountLiveData.observe(this) { item: Triple<String?, Int, String?>? ->
            if (dToOneCommentsAdapter != null && item != null) {
                dToOneCommentsAdapter!!.updateComment(item.first, item.second)
                commentsViewModel!!.resetCommentCountLiveData()
            }
        }
        //        viewModel.getLocalBlockUserList().observe(this, listResource -> {
//            if (listResource.getStatus() == Status.SUCCESS && listResource.getData() != null && !listResource.getData().isEmpty()) {
//                blockedUserIds = listResource.getData();
//            }
//        });
        val args = DToOneQuestionDetailsFragmentArgs.fromBundle(requireArguments())
        question = args.question
        playGroup = args.playGroup
        gameIdToHighlight = args.gameIdToHighlight
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvQuestion.text = question!!.activityText
        val imageOptions =
            RequestOptions().fitCenter().transform(RoundedCorners(20)).placeholder(R.drawable.default_place_holder)
                .error(R.drawable.default_place_holder)

        GlideApp.with(binding.root.context)
            .load(ActivityType.getActivityTypeIcon(Objects.requireNonNull(question!!.activityType)))
            .apply(imageOptions)
            .into(binding.ivRelation)

        //binding.ivRelation.setImageResource(ActivityType.getActivityTypeIcon(question!!.activityType))
        binding.btnAskToFriends.setOnClickListener(ViewClickListener { v: View? ->
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavAskQuestionsSheet(
                    question!!, false, playGroup!!.playGroupId!!
                )
            )
        })
        binding.ivRelation.setOnClickListener(ViewClickListener { v: View? ->
            val title = "Know Someone"
            showAlert(title, question!!.activityTypeHint)
        })
        if (!TextUtils.isEmpty(question!!.activityImageLargeUrl)) {
            binding.ivImage.visibility = View.VISIBLE
            Glide.with(fragmentContext)
                .load(question!!.activityImageLargeUrl)
                .apply(options)
                .into(binding.ivImage)
            binding.ivImage.setOnClickListener(ViewClickListener { v: View? ->
                val url = question!!.activityImageLargeUrl
                val media = 1
                navController.navigate(MobileNavigationDirections.actionGlobalNavMediaView(media, url!!))
            })
        }
        else {
            binding.ivImage.visibility = View.GONE
        }
        binding.rvComments.visibility = View.VISIBLE
        linearLayoutManager = object : LinearLayoutManager(requireActivity(), VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }
        binding.rvComments.itemAnimator = DefaultItemAnimator()
        binding.rvComments.addItemDecoration(VerticalSpaceItemDecoration(10))
        binding.rvComments.layoutManager = linearLayoutManager
        binding.rvComments.addOnScrollListener(object : PaginationListener(linearLayoutManager!!) {
            override fun loadMoreItems() {}
            override fun isLastPage(): Boolean {
                return true
            }

            override fun isLoading(): Boolean {
                return false
            }
        })
        if (dToOneCommentsAdapter == null) {
            loadData(true)
        }
        else {
            binding.rvComments.adapter = dToOneCommentsAdapter
        }
        userActionViewModel!!.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()
                if (action.actionType == ActionType.DELETE_GAME) {
                    deleteThisGame(action)
                }
                else if (action.actionType == ActionType.IGNORE) {
                    ignoreGame(action)
                }
                else if (action.actionType == ActionType.CANCEL_GAME) {
                    cancelThisGame(action)
                }
            }
        }
        gamesReceiverViewModel!!.gameActivityUpdateResponseLiveData.observe(viewLifecycleOwner) { activityResponse: GameActivityUpdateResponse? ->
            if (activityResponse != null) {
                if (!TextUtils.isEmpty(activityResponse.activityTagStatus)) question!!.activityTag = activityResponse.activityTagStatus
                if (!TextUtils.isEmpty(activityResponse.participationStatus)) question!!.userParticipationStatus = activityResponse.participationStatus
                gamesReceiverViewModel!!.setGameActivityUpdateResponseLiveData(null)
                loadData(true)
            }
        }
    }

    private fun cancelThisGame(action: Action<Any?>) {
        val item = action.data as ActivityGameDirectToOneResList
        TwoActionAlertDialog.showAlert(
            requireActivity(),
            getString(R.string.label_confirm),
            getString(R.string.message_cancel_game),
            item,
            "Cancel",
            "Yes"
        ) { item1: Pair<Int?, ActivityGameDirectToOneResList?>? ->
            if (item1 != null && item1.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                viewModel.cancelDirectToOneGameActivity(item.gameId!!)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GameActivityUpdateResponse> -> actionButtonResponse(apiResponse, item) }
            }
        }
    }

    private fun ignoreGame(action: Action<Any?>) {
        val item = action.data as ActivityGameDirectToOneResList
        TwoActionAlertDialog.showAlert(
            requireActivity(),
            getString(R.string.label_confirm),
            getString(R.string.message_ignore_game),
            item,
            "Cancel",
            "Yes"
        ) { item1: Pair<Int?, ActivityGameDirectToOneResList?>? ->
            if (item1 != null && item1.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                viewModel.ignoreDirectToOneGameActivity(item.gameId!!)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GameActivityUpdateResponse> -> actionButtonResponse(apiResponse, item) }
            }
        }
    }

    private fun deleteThisGame(action: Action<Any?>) {
        val item = action.data as ActivityGameDirectToOneResList
        TwoActionAlertDialog.showAlert(
            requireActivity(),
            getString(R.string.label_confirm),
            getString(R.string.message_delete_game),
            item,
            "Cancel",
            "Yes"
        ) { item1: Pair<Int?, ActivityGameDirectToOneResList?>? ->
            if (item1 != null && item1.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                viewModel.deleteDirectToOneGameActivity(item.gameId!!)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GameActivityUpdateResponse> -> actionButtonResponse(apiResponse, item) }
            }
        }
    }

    private fun actionButtonResponse(apiResponse: ApiResponse<GameActivityUpdateResponse>, item: ActivityGameDirectToOneResList) {
        if (apiResponse.loading) {
            showProgress()
        }
        else if (apiResponse.isSuccess && apiResponse.body != null) {
            hideProgress()
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                if (!TextUtils.isEmpty(apiResponse.body.activityTagStatus)) question!!.activityTag = apiResponse.body.activityTagStatus
                if (!TextUtils.isEmpty(apiResponse.body.participationStatus)) question!!.userParticipationStatus = apiResponse.body.participationStatus
                if (dToOneCommentsAdapter != null) {
                    dToOneCommentsAdapter!!.removeItem(item)
                    val questionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
                        QuestionGamesViewModel::class.java
                    )
                    questionViewModel!!.reloadUI(true)
                    navController.popBackStack()
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
                    AppUtilities.showLog("Network Error", "deleteDirectToOneGameActivity")
                }
                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                    "deleteDirectToOneGameActivity",
                    apiResponse.code.toString()
                )
            }
        }
    }

    private fun setAdapter(itemList: List<ActivityGameDirectToOneResList>?) {
        this.itemList = itemList
        val ids = StringBuilder()
        var scrollPosition = -1
        if (itemList != null && !itemList.isEmpty()) {
            for (i in itemList.indices) {
                ids.append(itemList[i].gameId)
                if (i != itemList.size - 1) {
                    ids.append(",")
                }
            }
            if (!TextUtils.isEmpty(gameIdToHighlight)) {
                for (i in itemList.indices) {
                    val item = itemList[i]
                    if (!TextUtils.isEmpty(gameIdToHighlight) && item.gameId.equals(gameIdToHighlight, ignoreCase = true)) {
                        scrollPosition = i
                        break
                    }
                }
            }
        }
        viewModel.updateActivityTagStatus(ids.toString(), question!!.activityId!!, question!!.activityType!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UpdateActivityTagStatusResponse> ->
                if (apiResponse.isSuccess && apiResponse.body != null && apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (!TextUtils.isEmpty(apiResponse.body.activityTagStatus)) question!!.activityTag = apiResponse.body.activityTagStatus
                    if (!TextUtils.isEmpty(apiResponse.body.participantResponseStatus)) question!!.userParticipationStatus =
                        apiResponse.body.participantResponseStatus
                    setFooterLabel()
                }
            }
        viewModel.updateActivityNotificationStatus(question!!.activityId!!)
            .observe(viewLifecycleOwner) { obj: ApiResponse<StandardResponse> -> obj.isSuccess }
        binding.btnAskToFriends.text = getString(R.string.label_ask_this_question)
        dToOneCommentsAdapter = DToOneCommentsAdapter(
            gameIdToHighlight,
            preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID),
            itemList
        ) { item: android.util.Pair<Int, ActivityGameDirectToOneResList> ->
            if (item.first == DToOneCommentsAdapter.CLICK_MORE) {
                val userId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
                //                var moreActionTranslations = ["deleteGame": "Delete My
//                Answer", "ignoreGame": "Ignore", "cancelGame": "Cancel This
//                Game", "editGame": "Edit Answer", "reportInappropriate":
//                "Report Inappropriate", "editGameVisibility": "Edit Privacy"]
                val title = "What would you like to do?"
                val actions = ArrayList<Action<*>>()
                if (item.second.participantResponseStatus.equals("Y", ignoreCase = true) && item.second.isResponseDeleted.equals("N", ignoreCase = true)) {

//                    if (item.second.getParticipantId().equalsIgnoreCase(userId)
//                            && item.second.getIsGameExpired().equalsIgnoreCase("N")
//                            && item.second.getIsResponseDeleted().equalsIgnoreCase("N")) {
                    //actions.add(new Action<>("Delete This Game", R.color.color_black, ActionType.DELETE_GAME, item.second));
//                    }
                    if (item.second.participantId.equals(userId, ignoreCase = true) && item.second.showEditOrDelete.equals("Y", ignoreCase = true)) {
                        actions.add(Action("Delete This Game", R.color.color_black, ActionType.DELETE_GAME, item.second))
                    }
                    if (!item.second.participantId.equals(userId, ignoreCase = true)) {
                        actions.add(Action("Delete This Game", R.color.color_black, ActionType.DELETE_GAME, item.second))
                    }
                }
                // below line was commented on June 7th. isGameDeletedByAsker was coming out to be "" and also we have now made game status = deleted when either of the players delete etc
                //if games[i].participantResponseStatus == "N" && games[i].isGameDeletedByAsker == "N" {
                if (item.second.participantResponseStatus.equals("N", ignoreCase = true)) {
                    if (item.second.participantId.equals(userId, ignoreCase = true)) {
                        actions.add(Action("Ignore This Game", R.color.color_black, ActionType.IGNORE, item.second))
                    }
                    else {
                        actions.add(Action("Cancel This Game", R.color.color_black, ActionType.CANCEL_GAME, item.second))
                    }
                }
                if (actions.isEmpty()) {
                    showAlert("You can delete your response after 48 hrs from answering")
                }
                else {
                    val bundle = Bundle()
                    bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
                    navController.navigate(MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(title, bundle, ""))
                }
            }
            else if (item.first == DToOneCommentsAdapter.CLICK_ANSWER) {
                question!!.gameId = item.second.gameId
                AnswerQuestionDialogFragment.getInstance(question, playGroup) { apiResponse: UploadPostResponse? ->
                    if (!TextUtils.isEmpty(apiResponse!!.participationStatus)) question!!.userParticipationStatus = apiResponse.participationStatus
                    if (!TextUtils.isEmpty(apiResponse.activityTagStatus)) question!!.activityTag = apiResponse.activityTagStatus
                    loadData(false)
                }.show(requireActivity().supportFragmentManager, "Answer")
            }
            else if (item.first == DToOneCommentsAdapter.CLICK_PROFILE) {
                navController.navigate(MobileNavigationDirections.actionGlobalNavProfile(question!!.activityId, item.second.participantId))
            }
            else if (item.first == OneToManyCommentsAdapter.CLICK_COMMENT_COUNT) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavPostCommentList(
                        (if (TextUtils.isEmpty(question!!.activityGameResponseId)) "" else question!!.activityGameResponseId)!!,
                        item.second.gameId!!,
                        "",
                        item.second.participantId!!,
                        "",
                        question!!.activityId!!,
                        "",
                        "", false
                    )
                )
            }
            else if (item.first == DToOneCommentsAdapter.CLICK_COMMENT) {
                WriteCommentDialogFragment.getInstance(item.second.gameId, item.second.participantId, "", "", "") { item1: Void? ->
                    if (TextUtils.isEmpty(item.second.commentCount)) {
                        item.second.commentCount = "1"
                    }
                    else {
                        val count = item.second.commentCount!!.toInt() + 1
                        item.second.commentCount = count.toString()
                    }
                    if (dToOneCommentsAdapter != null) dToOneCommentsAdapter!!.updateComment(item.second.gameId, 1)
                }
                    .show(requireActivity().supportFragmentManager, "Comment")
            }
            else if (item.first == DToOneCommentsAdapter.CLICK_ATTACHMENT) {
                val url: String
                val media: Int
                if (!TextUtils.isEmpty(item.second.participantResponseVideoLink)) {
                    url = item.second.participantResponseVideoLink.toString()
                    media = 2
                }
                else {
                    url = item.second.participantResponseImageLargeLink.toString()
                    media = 1
                }
                navController.navigate(MobileNavigationDirections.actionGlobalNavMediaView(media, url))
            }
        }
        binding.rvComments.adapter = dToOneCommentsAdapter
        //        binding.rvComments.setNestedScrollingEnabled(false);
        if (scrollPosition >= 0) {
//            int finalScrollPosition = scrollPosition;
            binding.nestedScrollView.postDelayed({
//                linearLayoutManager.scrollToPosition( finalScrollPosition);
//                linearLayoutManager.scrollToPositionWithOffset(finalScrollPosition, 0);
                binding.nestedScrollView.smoothScrollTo(0, binding.rvComments.y.toInt())
            }, 300)
        }
        setFooterLabel()
    }

    private fun loadData(showProgress: Boolean) {
        viewModel.getDirectToOneGameActivityRes(question!!.activityId!!, gameIdToHighlight!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetDirectToOneGameActivityResResponse> ->
                if (apiResponse.loading) {
                    if (showProgress) showProgress()
                }
                else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        val dto1Activities = apiResponse.body.activityGameDirectToOneResList

//                    if (blockedUserIds != null && !blockedUserIds.isEmpty()) {
//                        for (UserList blockedUser : blockedUserIds) {
//                            Iterator<ActivityGameDirectToOneResList> iterator = dto1Activities.iterator();
//                            while (iterator.hasNext()) {
//                                ActivityGameDirectToOneResList directToOneResList = iterator.next();
//                                if (directToOneResList.getParticipantId().equals(blockedUser.getUserId()) || directToOneResList.getGameStartedByUserID().equals(blockedUser.getUserId())) {
//                                    iterator.remove();
//                                }
//                            }
//                        }
//                    }
                        setAdapter(dto1Activities)
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
                            AppUtilities.showLog("Network Error", "getDirectToOneGameActivityRes")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getDirectToOneGameActivityRes",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun setFooterLabel() {
        var footerMessage = ""
        footerMessage = if (itemList!!.isEmpty()) {
            "Start by answering this question. You can then see responses of your friends and can ask this question further."
        }
        else if (itemList!!.size == 1 && "Pending" == question!!.userParticipationStatus) {
            "Start by answering this question. You can then see responses of your friends and can ask this question further. Your response will be visible to all of your friends on Onourem."
        }
        else if (itemList!!.size == 1 && "Settled" == question!!.userParticipationStatus) {
            "Looks like none of your friends have answered this question yet. Forward this question to friends. You will be able to see their responses as soon as they answer."
        }
        else {
            "Forward this question to more friends"
        }

//        binding.includeErrorFooter.tvFooter.setText(footerMessage);
//        if (dToOneCommentsAdapter != null)
//            dToOneCommentsAdapter.addFooter(footerMessage);
    }

    override fun onDestroyView() {
        commentsViewModel!!.resetCommentCountLiveData()
        super.onDestroyView()
    }
}

internal class DToOneCommentsAdapter(
    private var gameIdToHighlight: String?,
    private val userId: String,
    activityGameDirectToOneResList: List<ActivityGameDirectToOneResList>?,
    private val onItemClickListener: OnItemClickListener<android.util.Pair<Int, ActivityGameDirectToOneResList>>
) : PaginationRVAdapter<ActivityGameDirectToOneResList?>(
    activityGameDirectToOneResList!!
) {
    override fun emptyLoadingItem(): ActivityGameDirectToOneResList {
        return ActivityGameDirectToOneResList()
    }

    override fun emptyFooterItem(): ActivityGameDirectToOneResList {
        return ActivityGameDirectToOneResList()
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return D2OneViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_d_to_one_comment_view, parent, false))
    }

    fun updateComment(gameId: String?, count: Int) {
        for (i in items.indices) {
            val feedsList = items[i]!!
            if (feedsList.gameId.equals(gameId, ignoreCase = true)) {
                var currentCount = count
                if (currentCount <= 0) {
                    currentCount = 0
                }
                feedsList.commentCount = currentCount.toString()
                notifyItemChanged(i)
                break
            }
        }
    }

    inner class D2OneViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val itemBinding: LayoutDToOneCommentViewBinding
        private val options = RequestOptions()
            .fitCenter()
            .placeholder(R.drawable.default_user_profile_image)
            .error(R.drawable.default_user_profile_image)
        private val optionsRoundedCorners = RequestOptions()
            .fitCenter()
            .transform(FitCenter(), RoundedCorners(15))
            .placeholder(R.drawable.default_place_holder)
            .error(R.drawable.default_place_holder)

        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            val item = items[position]!!
            val response: String

            //New CR update row
            if (item.isNewResponse.equals("Y", ignoreCase = true)) {
                itemBinding.comment.root.background = AppCompatResources.getDrawable(itemView.context, R.drawable.bg_card_round_corner)
            }
            else {
                itemBinding.comment.root.background = AppCompatResources.getDrawable(itemView.context, R.drawable.bg_card_round_corner_gray)
            }
            if (item.isNewComment.equals("Y", ignoreCase = true)) {
                itemBinding.comment.tvCommentsCount.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                        itemView.context,
                        R.drawable.shape_outlined_rectangle_accent
                    )
                )
                itemBinding.comment.tvCommentsCount.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            }
            else {
                itemBinding.comment.tvCommentsCount.setTextColor(ContextCompat.getColor(itemView.context, R.color.color_black))
                itemBinding.comment.tvCommentsCount.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                        itemView.context,
                        R.drawable.shape_outlined_rectangle_black
                    )
                )
            }
            if (item.participantId.equals(userId, ignoreCase = true) && item.participantResponseStatus.equals("N", ignoreCase = true)
                && item.isGameExpired.equals("N", ignoreCase = true)
            ) {
                itemBinding.comment.tvCommentsCount.visibility = View.GONE
                itemBinding.comment.ivComment.visibility = View.GONE
                itemBinding.comment.btnAnswer.visibility = View.VISIBLE
                itemBinding.comment.btnAnswer.text = "ANSWER"
                itemBinding.comment.tvComment.setTextColor(ContextCompat.getColor(itemView.context, R.color.gray_color))
                response = "Response Awaited"
            }
            else if (!item.participantId.equals(userId, ignoreCase = true) && item.participantResponseStatus.equals("N", ignoreCase = true)
                && item.isGameExpired.equals("N", ignoreCase = true)
            ) {
                itemBinding.comment.tvCommentsCount.visibility = View.GONE
                itemBinding.comment.ivComment.visibility = View.GONE
                itemBinding.comment.btnAnswer.visibility = View.GONE //TODO Need to check remind functionality
                itemBinding.comment.btnAnswer.text = "REMIND"
                response = "Response Awaited"
                itemBinding.comment.tvComment.setTextColor(ContextCompat.getColor(itemView.context, R.color.gray_color))
            }
            else if (item.participantResponseStatus.equals("N", ignoreCase = true)
                && item.isGameExpired.equals("Y", ignoreCase = true)
            ) {
                itemBinding.comment.tvCommentsCount.visibility = View.GONE
                itemBinding.comment.ivComment.visibility = View.GONE
                itemBinding.comment.btnAnswer.visibility = View.GONE
                response = "Game Expired \nUser Did Not Respond In Time"
                itemBinding.comment.tvComment.setTextColor(ContextCompat.getColor(itemView.context, R.color.gray_color))
            }
            else {
                itemBinding.comment.tvCommentsCount.visibility = View.VISIBLE
                itemBinding.comment.ivComment.visibility = View.VISIBLE
                itemBinding.comment.btnAnswer.visibility = View.GONE
                itemBinding.comment.tvComment.setTextColor(ContextCompat.getColor(itemView.context, R.color.color_black))
                val count = item.commentCount
                if (!TextUtils.isEmpty(count) && count != "0") {
                    itemBinding.comment.tvCommentsCount.text = count
                    itemBinding.comment.tvCommentsCount.visibility = View.VISIBLE
                }
                else {
                    itemBinding.comment.tvCommentsCount.visibility = View.GONE
                }
                response = Base64Utility.decode(item.participantResponse)
            }
            itemBinding.comment.tvName.text = item.participantName
            itemBinding.comment.tvComment.text = response
            if (!TextUtils.isEmpty(item.askedByFirstName)) {
                if (item.gameStartedByUserID.equals(userId, ignoreCase = true)) {
                    itemBinding.comment.textAskedBy.text = "You asked this question"
                }
                else {
                    itemBinding.comment.textAskedBy.text = item.askedByFirstName + " asked this question"
                }
                itemBinding.comment.textAskedBy.visibility = View.VISIBLE
            }
            else {
                itemBinding.comment.textAskedBy.visibility = View.GONE
            }
            Glide.with(itemBinding.root.context)
                .load(item.participantImageUrl)
                .apply(options)
                .into(itemBinding.comment.rivProfile)
            Utilities.verifiedUserType(itemBinding.root.context, item.userType, itemBinding.comment.ivIconVerified)
            val url: String
            val media: Int
            if (!TextUtils.isEmpty(item.participantResponseVideoLink)) {
                url = item.participantResponseVideoLink.toString()
                media = 2
            }
            else {
                url = item.participantResponseImageLargeLink.toString()
                media = 1
            }
            if (!TextUtils.isEmpty(url)) {
                if (media == 2) {
                    itemBinding.comment.ivPlayVideo.visibility = View.VISIBLE
                }
                else {
                    itemBinding.comment.ivPlayVideo.visibility = View.GONE
                }
                Glide.with(itemBinding.root.context)
                    .load(url)
                    .apply(optionsRoundedCorners)
                    .placeholder(R.drawable.ic_place_holder_image)
                    .into(itemBinding.comment.ivCommentAttachment)
                itemBinding.comment.ivCommentAttachment.visibility = View.VISIBLE
                itemBinding.comment.ivCommentAttachment.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(itemBinding.root.context as Activity)
                    .target(itemBinding.comment.ivCommentAttachment)
                    .enableImmersiveMode(false)
                //                        .longPressListener(v -> {
//                            LoginDayActivityInfoList item = items.get(getBindingAdapterPosition());
//                            onItemClickListener.onItemClick(new Triple<>(item, CLICK_MEDIA, String.valueOf(v.getTag())));
//                        }).doubleTapListener(v -> Toast.makeText(context, "Double tap on "
//                                + v.getTag(), Toast.LENGTH_SHORT).show());
                builder.register()
            }
            else {
                itemBinding.comment.ivPlayVideo.visibility = View.GONE
                itemBinding.comment.ivCommentAttachment.visibility = View.GONE
            }
            if (item.gameId.equals(gameIdToHighlight, ignoreCase = true)) {
                itemBinding.root.postDelayed({
                    animateChange(itemBinding)
                    gameIdToHighlight = null
                }, 1000)
            }
        }

        fun animateChange(viewHolder: LayoutDToOneCommentViewBinding) {
            val colorFrom = ContextCompat.getColor(viewHolder.root.context, R.color.color_highlight_game_post)
            val colorTo = ContextCompat.getColor(viewHolder.root.context, R.color.color_white)
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.duration = 500 // milliseconds
            colorAnimation.startDelay = 300 // milliseconds
            colorAnimation.addUpdateListener { animator: ValueAnimator -> viewHolder.card.setCardBackgroundColor(animator.animatedValue as Int) }
            colorAnimation.repeatCount = 1
            colorAnimation.start()
        }

        init {
            itemBinding = LayoutDToOneCommentViewBinding.bind(itemView)
            itemBinding.comment.btnAnswer.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    android.util.Pair.create(
                        CLICK_ANSWER, items[bindingAdapterPosition]
                    )
                )
            })
            itemBinding.comment.ivUserMoreAction.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    android.util.Pair.create(
                        CLICK_MORE, items[bindingAdapterPosition]
                    )
                )
            })
            itemBinding.comment.ivComment.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    android.util.Pair.create(
                        CLICK_COMMENT, items[bindingAdapterPosition]
                    )
                )
            })
            itemBinding.comment.ivCommentAttachment.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    android.util.Pair.create(
                        CLICK_ATTACHMENT, items[bindingAdapterPosition]
                    )
                )
            })
            itemBinding.comment.tvCommentsCount.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    android.util.Pair.create(
                        CLICK_COMMENT_COUNT, items[bindingAdapterPosition]
                    )
                )
            })
            itemBinding.comment.rivProfile.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    android.util.Pair.create(
                        CLICK_PROFILE, items[bindingAdapterPosition]
                    )
                )
            })
        }
    }

    companion object {
        var CLICK_ANSWER = 0
        var CLICK_MORE = 1
        var CLICK_COMMENT = 2
        var CLICK_PROFILE = 3
        var CLICK_ATTACHMENT = 4
        var CLICK_COMMENT_COUNT = 5
    }
}