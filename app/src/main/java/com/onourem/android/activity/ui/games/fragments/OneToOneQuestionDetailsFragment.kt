package com.onourem.android.activity.ui.games.fragments

import android.animation.ArgbEvaluator
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.ui.games.viewmodel.OneToOneViewModel
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.onourem.android.activity.R
import javax.inject.Inject
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.ui.games.fragments.OneToOneCommentsAdapter
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.models.PlayGroup
import com.onourem.android.activity.ui.games.viewmodel.GamesReceiverViewModel
import com.onourem.android.activity.models.ActivityGameOneToOneResList
import com.onourem.android.activity.ui.games.viewmodel.CommentsViewModel
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.onourem.android.activity.ui.games.fragments.OneToOneQuestionDetailsFragmentArgs
import com.onourem.android.activity.models.ActivityType
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.MobileNavigationDirections
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.onourem.android.activity.models.GameActivityUpdateResponse
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.UpdateActivityTagStatusResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.ui.games.fragments.AnswerQuestionDialogFragment
import com.onourem.android.activity.models.UploadPostResponse
import com.onourem.android.activity.ui.games.fragments.WriteCommentDialogFragment
import com.onourem.android.activity.models.OneToOneGameActivityResResponse
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import android.view.ViewGroup
import com.onourem.android.activity.ui.survey.BaseViewHolder
import com.onourem.android.activity.ui.games.fragments.OneToOneCommentsAdapter.One2OneViewHolder
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import android.view.Gravity
import com.onourem.android.activity.ui.utils.zoomy.Zoomy
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.app.Activity
import android.view.View
import androidx.core.util.Pair
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.onourem.android.activity.databinding.FragmentDToOneQuestionDetailsBinding
import com.onourem.android.activity.databinding.LayoutOneToOneCommentViewBinding
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.utils.*
import com.onourem.android.activity.ui.utils.glide.GlideApp
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import java.lang.StringBuilder
import java.util.*

class OneToOneQuestionDetailsFragment : AbstractBaseViewModelBindingFragment<OneToOneViewModel, FragmentDToOneQuestionDetailsBinding>() {
    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(15))
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var question: LoginDayActivityInfoList? = null
    private var oneToOneCommentsAdapter: OneToOneCommentsAdapter? = null
    private var userActionViewModel: UserActionViewModel? = null
    private var playGroup: PlayGroup? = null
    private var gamesReceiverViewModel: GamesReceiverViewModel? = null
    private var itemList: List<ActivityGameOneToOneResList>? = null

    //    private List<UserList> blockedUserIds;
    private var commentsViewModel: CommentsViewModel? = null
    private var gameIdToHighlight: String? = null
    override fun viewModelType(): Class<OneToOneViewModel> {
        return OneToOneViewModel::class.java
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
            if (oneToOneCommentsAdapter != null && item != null) {
                oneToOneCommentsAdapter!!.updateComment(item.first, item.second)
                commentsViewModel!!.resetCommentCountLiveData()
            }
        }
        //        viewModel.getLocalBlockUserList().observe(this, listResource -> {
//            if (listResource.getStatus() == Status.SUCCESS && listResource.getData() != null && !listResource.getData().isEmpty()) {
//                blockedUserIds = listResource.getData();
//            }
//        });
        val args = OneToOneQuestionDetailsFragmentArgs.fromBundle(requireArguments())
        question = args.question
        //        Log.d("###", "onViewCreated: question " + question.hashCode());
//        Log.d("###", "onViewCreated: question " + question);
        playGroup = args.playGroup
        gameIdToHighlight = args.gameIdToHighlight
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvQuestion.text = question?.activityText
        val imageOptions =
            RequestOptions().fitCenter().transform(RoundedCorners(20)).placeholder(R.drawable.default_place_holder)
                .error(R.drawable.default_place_holder)

        GlideApp.with(binding.root.context)
            .load(ActivityType.getActivityTypeIcon(Objects.requireNonNull(question?.activityType)))
            .apply(imageOptions)
            .into(binding.ivRelation)
        //binding.ivRelation.setImageResource(ActivityType.getActivityTypeIcon(question?.activityType))
        binding.btnAskToFriends.setOnClickListener(ViewClickListener { v: View? ->
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavAskQuestionsSheet(
                    question!!, false, playGroup!!.playGroupId!!
                )
            )
        })
        binding.ivRelation.setOnClickListener(ViewClickListener { v: View? -> showAlert("One-On-One Question", question?.activityTypeHint) })
        if (!TextUtils.isEmpty(question?.activityImageLargeUrl)) {
            binding.ivImage.visibility = View.VISIBLE
            Glide.with(requireActivity())
                .load(question?.activityImageLargeUrl)
                .apply(options)
                .into(binding.ivImage)
            binding.ivImage.setOnClickListener(ViewClickListener { v: View? ->
                val url = question?.activityImageLargeUrl
                val media = 1
                navController.navigate(MobileNavigationDirections.actionGlobalNavMediaView(media, url!!))
            })
        }
        else {
            binding.ivImage.visibility = View.GONE
        }
        if (oneToOneCommentsAdapter == null) {
            loadData(true)
        }
        else {
            binding.rvComments.adapter = oneToOneCommentsAdapter
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
        val linearLayoutManager: LinearLayoutManager = object : LinearLayoutManager(requireActivity(), VERTICAL, false) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }
        binding.rvComments.itemAnimator = DefaultItemAnimator()
        binding.rvComments.addItemDecoration(VerticalSpaceItemDecoration(15))
        binding.rvComments.layoutManager = linearLayoutManager
        gamesReceiverViewModel!!.gameActivityUpdateResponseLiveData.observe(viewLifecycleOwner) { activityResponse: GameActivityUpdateResponse? ->
            if (activityResponse != null) {
                if (!TextUtils.isEmpty(activityResponse.activityTagStatus)) question?.activityTag = activityResponse.activityTagStatus
                if (!TextUtils.isEmpty(activityResponse.participationStatus)) question?.userParticipationStatus = activityResponse.participationStatus
                gamesReceiverViewModel!!.setGameActivityUpdateResponseLiveData(null)
                loadData(true)
            }
        }
    }

    private fun cancelThisGame(action: Action<Any?>) {
        val item = action.data as ActivityGameOneToOneResList
        TwoActionAlertDialog.showAlert(
            requireActivity(),
            getString(R.string.label_confirm),
            getString(R.string.message_cancel_game),
            item,
            "Cancel",
            "Yes"
        ) { item1: Pair<Int?, ActivityGameOneToOneResList?>? ->
            if (item1 != null && item1.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                viewModel.cancelOneToOneGameActivity(item.gameId!!)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GameActivityUpdateResponse> -> actionButtonResponse(apiResponse, item) }
            }
        }
    }

    private fun ignoreGame(action: Action<Any?>) {
        val item = action.data as ActivityGameOneToOneResList
        TwoActionAlertDialog.showAlert(
            requireActivity(),
            getString(R.string.label_confirm),
            getString(R.string.message_ignore_game),
            item,
            "Cancel",
            "Yes"
        ) { item1: Pair<Int?, ActivityGameOneToOneResList?>? ->
            if (item1 != null && item1.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                viewModel.ignoreOneToOneGameActivity(item.gameId!!)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GameActivityUpdateResponse> -> actionButtonResponse(apiResponse, item) }
            }
        }
    }

    private fun deleteThisGame(action: Action<Any?>) {
        val item = action.data as ActivityGameOneToOneResList
        TwoActionAlertDialog.showAlert(
            requireActivity(),
            getString(R.string.label_confirm),
            getString(R.string.message_delete_game),
            item,
            "Cancel",
            "Yes"
        ) { item1: Pair<Int?, ActivityGameOneToOneResList?>? ->
            if (item1 != null && item1.first != null && item1.first == TwoActionAlertDialog.ACTION_RIGHT) {
                viewModel.deleteOneToOneGameActivity(item.gameId!!)
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GameActivityUpdateResponse> -> actionButtonResponse(apiResponse, item) }
            }
        }
    }

    private fun actionButtonResponse(apiResponse: ApiResponse<GameActivityUpdateResponse>, item: ActivityGameOneToOneResList) {
        if (apiResponse.loading) {
            showProgress()
        }
        else if (apiResponse.isSuccess && apiResponse.body != null) {
            hideProgress()
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                if (!TextUtils.isEmpty(apiResponse.body.activityTagStatus)) question?.activityTag = apiResponse.body.activityTagStatus
                if (!TextUtils.isEmpty(apiResponse.body.participationStatus)) question?.userParticipationStatus = apiResponse.body.participationStatus
                if (oneToOneCommentsAdapter != null) oneToOneCommentsAdapter!!.removeItem(item)
                if (itemList!!.isEmpty()) {
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
        }
    }

    private fun setAdapter(itemList: List<ActivityGameOneToOneResList>?) {
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
            for (i in itemList.indices) {
                val item = itemList[i]
                if (!TextUtils.isEmpty(gameIdToHighlight) && item.gameId.equals(gameIdToHighlight, ignoreCase = true)) {
                    scrollPosition = i
                    break
                }
            }
        }
        viewModel.updateActivityTagStatus(ids.toString(), question?.activityId!!, question?.activityType!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UpdateActivityTagStatusResponse> ->
                if (apiResponse.isSuccess && apiResponse.body != null && apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (!TextUtils.isEmpty(apiResponse.body.activityTagStatus)) question?.activityTag = apiResponse.body.activityTagStatus
                    if (!TextUtils.isEmpty(apiResponse.body.participantResponseStatus)) question?.userParticipationStatus =
                        apiResponse.body.participantResponseStatus
                    setFooterLabel()
                }
            }
        viewModel.updateActivityNotificationStatus(question?.activityId!!).observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.isSuccess && apiResponse.body != null && apiResponse.body.errorCode.equals(
                    "000",
                    ignoreCase = true
                )
            ) {
            }
        }
        binding.btnAskToFriends.text = getString(R.string.label_play_with_specific_person)
        oneToOneCommentsAdapter = OneToOneCommentsAdapter(
            gameIdToHighlight,
            preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID),
            itemList
        ) { item: Triple<Int, ActivityGameOneToOneResList, Int> ->
            if (item.first == OneToOneCommentsAdapter.CLICK_MORE) {
                val userId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
                val title = "What would you like to do?"
                val actions = ArrayList<Action<*>>()
                if (item.second.participantResponseStatus[0].equals("Y", ignoreCase = true) && item.second.isResponseDeleted[0].equals(
                        "N",
                        ignoreCase = true
                    )
                ) {
                    if (item.second.participantId[0].equals(userId, ignoreCase = true) && item.second.showEditOrDelete[0].equals("Y", ignoreCase = true)) {
                        actions.add(Action("Delete This Game", R.color.color_black, ActionType.DELETE_GAME, item.second))
                    }
                }
                if (item.second.participantResponseStatus[0].equals("N", ignoreCase = true)) {
                    if (item.second.participantId[0].equals(item.second.gameStartedByUserID, ignoreCase = true)) {
                        if (item.second.participantResponseStatus[1].equals("N", ignoreCase = true)) {
                            actions.add(Action("Cancel This Game", R.color.color_black, ActionType.CANCEL_GAME, item.second))
                        }
                        else if (item.second.participantResponseStatus[1].equals("Y", ignoreCase = true)) {
                            actions.add(Action("Delete This Game", R.color.color_black, ActionType.DELETE_GAME, item.second))
                        }
                    }
                    else {
                        if (item.second.participantResponseStatus[1].equals("N", ignoreCase = true) || item.second.participantResponseStatus[1].equals(
                                "Y",
                                ignoreCase = true
                            )
                        ) {
                            actions.add(Action("Ignore This Game", R.color.color_black, ActionType.IGNORE, item.second))
                        }
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
            else if (item.first == OneToOneCommentsAdapter.CLICK_ANSWER) {
                question?.gameId = item.second.gameId
                //                navController.navigate(MobileNavigationOneions.actionGlobalNavAnswerQuestionDialog(question, playGroup));
                AnswerQuestionDialogFragment.getInstance(question, playGroup) { apiResponse: UploadPostResponse? ->
                    if (!TextUtils.isEmpty(apiResponse!!.participationStatus)) question?.userParticipationStatus = apiResponse.participationStatus
                    if (!TextUtils.isEmpty(apiResponse.activityTagStatus)) question?.activityTag = apiResponse.activityTagStatus
                    loadData(false)
                }.show(requireActivity().supportFragmentManager, "Answer")
            }
            else if (item.first == OneToOneCommentsAdapter.CLICK_ACTION_FOR_USER || item.first == OneToOneCommentsAdapter.CLICK_ACTION_FOR_FRIEND) {
                val userId = if (item.first == OneToOneCommentsAdapter.CLICK_ACTION_FOR_USER) item.second.participantId[0] else item.second.participantId[1]
                navController.navigate(MobileNavigationDirections.actionGlobalNavProfile(question?.activityId, userId))
            }
            else if (item.first == OneToOneCommentsAdapter.CLICK_COMMENT_COUNT) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavPostCommentList(
                        (if (TextUtils.isEmpty(question?.activityGameResponseId)) "" else question?.activityGameResponseId)!!,
                        item.second.gameId!!,
                        "",
                        Utilities.getTokenSeparatedString(item.second.participantId, ","),
                        "",
                        question?.activityId!!,
                        "",
                        "1To1", false
                    )
                )
            }
            else if (item.first == OneToOneCommentsAdapter.CLICK_COMMENT) {
                WriteCommentDialogFragment.getInstance(item.second.gameId, item.second.participantId[0], "", "", "") { item1: Void? ->
                    if (TextUtils.isEmpty(item.second.commentCount)) {
                        item.second.commentCount = "1"
                    }
                    else {
                        val count = item.second.commentCount!!.toInt() + 1
                        item.second.commentCount = count.toString()
                    }
                    if (oneToOneCommentsAdapter != null) oneToOneCommentsAdapter!!.notifyDataSetChanged()
                }
                    .show(requireActivity().supportFragmentManager, "Comment")
            }
            else if (item.first == OneToOneCommentsAdapter.CLICK_ATTACHMENT) {
                var url: String? = ""
                var media = 0
                if (item.third == OneToOneCommentsAdapter.CLICK_ACTION_FOR_USER) {
                    if (!TextUtils.isEmpty(item.second.participantResponseVideoLink[0])) {
                        url = item.second.participantResponseVideoLink[0]
                        media = 2
                    }
                    else {
                        url = item.second.participantResponseImageLargeLink[0]
                        media = 1
                    }
                }
                else if (item.third == OneToOneCommentsAdapter.CLICK_ACTION_FOR_FRIEND) {
                    if (!TextUtils.isEmpty(item.second.participantResponseVideoLink[1])) {
                        url = item.second.participantResponseVideoLink[1]
                        media = 2
                    }
                    else {
                        url = item.second.participantResponseImageLargeLink[1]
                        media = 1
                    }
                }
                navController.navigate(MobileNavigationDirections.actionGlobalNavMediaView(media, url!!))
            }
        }
        binding.rvComments.adapter = oneToOneCommentsAdapter
        binding.rvComments.visibility = View.VISIBLE
        if (scrollPosition >= 0) {
//            int finalScrollPosition = scrollPosition;
            binding.nestedScrollView.postDelayed({ binding.nestedScrollView.smoothScrollTo(0, binding.rvComments.y.toInt()) }, 300)
        }
        setFooterLabel()
    }

    private fun loadData(showProgress: Boolean) {
        viewModel.getOneToOneGameActivityRes(question?.activityId!!, gameIdToHighlight!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<OneToOneGameActivityResResponse> ->
                if (apiResponse.loading) {
                    if (showProgress) showProgress()
                }
                else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        val dto1Activities = apiResponse.body.activityGameOneToOneResList

//                    if (blockedUserIds != null && !blockedUserIds.isEmpty()) {
//                        for (UserList blockedUser : blockedUserIds) {
//                            Iterator<ActivityGameOneToOneResList> iterator = dto1Activities.iterator();
//                            while (iterator.hasNext()) {
//                                ActivityGameOneToOneResList oneToOneResList = iterator.next();
//                                if (blockedUser.getUserId().equals(oneToOneResList.getParticipantId().get(0)) || blockedUser.getUserId().equals(oneToOneResList.getParticipantId().get(1))) {
//                                    iterator.remove();
//                                }
//                            }
//                        }
//                    }
                        setAdapter(apiResponse.body.activityGameOneToOneResList)
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

    private fun setFooterLabel() {
        val footerMessage: String
        footerMessage = if (itemList == null || itemList!!.isEmpty()) {
            "You have not played this question with anyone yet. Start a game by asking this question to a friend (click on the above button)."
        }
        else {
            "Both you and the friend you are playing this question with need to answer this question before you can see each others responses"
        }
        //        binding.includeErrorFooter.tvFooter.setText(footerMessage);
//        if (oneToOneCommentsAdapter != null)
//            oneToOneCommentsAdapter.addFooter(footerMessage);
    }

    override fun onDestroyView() {
        commentsViewModel!!.resetCommentCountLiveData()
        super.onDestroyView()
    }
}

internal class OneToOneCommentsAdapter(
    private var gameIdToHighlight: String?,
    private val userId: String,
    activityGameOneToOneResList: List<ActivityGameOneToOneResList>?,
    private val onItemClickListener: OnItemClickListener<Triple<Int, ActivityGameOneToOneResList, Int>>
) : PaginationRVAdapter<ActivityGameOneToOneResList?>(
    activityGameOneToOneResList!!
) {
    private val options = RequestOptions()
        .fitCenter()
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)
    private val optionsRoundedCorners = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(15))
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)

    override fun emptyLoadingItem(): ActivityGameOneToOneResList {
        return ActivityGameOneToOneResList()
    }

    override fun emptyFooterItem(): ActivityGameOneToOneResList {
        return ActivityGameOneToOneResList()
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return One2OneViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_one_to_one_comment_view, parent, false))
    }

    fun updateComment(gameId: String?, count: Int) {
        for (i in items.indices) {
            val item = items[i]!!
            if (item.gameId.equals(gameId, ignoreCase = true)) {
                var currentCount = count
                if (currentCount <= 0) {
                    currentCount = 0
                }
                item.commentCount = currentCount.toString()
                notifyItemChanged(i)
                break
            }
        }
    }

    private fun setUserComment(item: ActivityGameOneToOneResList, itemBinding: LayoutOneToOneCommentViewBinding, position: Int) {
        var response: String? = ""
        itemBinding.userComment.textAskedBy.visibility = View.VISIBLE
        if (!TextUtils.isEmpty(item.gameStartedByUserID) && item.gameStartedByUserID.equals(userId, ignoreCase = true)) {
            itemBinding.userComment.textAskedBy.text = "You asked this question"
        }
        else {
            itemBinding.userComment.textAskedBy.text = String.format("%s asked this question", item.participantName[1])
        }
        if (item.participantId[0].equals(userId, ignoreCase = true) && item.participantResponseStatus[0].equals("N", ignoreCase = true)
            && item.isGameExpired.equals("N", ignoreCase = true)
        ) {
            itemBinding.userComment.tvCommentsCount.visibility = View.GONE
            itemBinding.userComment.ivComment.visibility = View.GONE
            itemBinding.userComment.btnAnswer.visibility = View.VISIBLE
            itemBinding.userComment.btnAnswer.text = "ANSWER"
            response = "Response Awaited"
            itemBinding.userComment.tvComment.setTextColor(ContextCompat.getColor(itemBinding.card.context, R.color.gray_color))
        }
        else if (!item.participantId[0].equals(userId, ignoreCase = true) && item.participantResponseStatus[0].equals("N", ignoreCase = true)
            && item.isGameExpired.equals("N", ignoreCase = true)
        ) {
            itemBinding.userComment.tvCommentsCount.visibility = View.GONE
            itemBinding.userComment.ivComment.visibility = View.GONE
            itemBinding.userComment.btnAnswer.visibility = View.GONE //TODO Need to check remind functionality
            itemBinding.userComment.btnAnswer.text = "REMIND"
            response = "Response Awaited"
        }
        else if (item.participantResponseStatus[0].equals("N", ignoreCase = true)
            && item.isGameExpired.equals("Y", ignoreCase = true)
        ) {
            itemBinding.userComment.tvCommentsCount.visibility = View.GONE
            itemBinding.userComment.ivComment.visibility = View.GONE
            itemBinding.userComment.btnAnswer.visibility = View.GONE
            response = "Game Expired \nUser Did Not Respond In Time"
            itemBinding.userComment.tvComment.setTextColor(ContextCompat.getColor(itemBinding.card.context, R.color.gray_color))
        }
        else {
            itemBinding.userComment.tvCommentsCount.visibility = View.VISIBLE
            itemBinding.userComment.ivComment.visibility = View.VISIBLE
            itemBinding.userComment.btnAnswer.visibility = View.GONE
            val count = item.commentCount
            if (!TextUtils.isEmpty(count) && count != "0") {
                itemBinding.userComment.tvCommentsCount.text = count
                itemBinding.userComment.tvCommentsCount.visibility = View.VISIBLE
            }
            else {
                itemBinding.userComment.tvCommentsCount.visibility = View.GONE
            }
            itemBinding.userComment.tvComment.gravity = Gravity.START
            response = Base64Utility.decode(item.participantResponse[0])
            itemBinding.userComment.tvComment.setTextColor(ContextCompat.getColor(itemBinding.card.context, R.color.color_black))
        }
        if (item.participantResponseStatus[0].equals("N", ignoreCase = true)
            || item.participantResponseStatus[1].equals("N", ignoreCase = true)
        ) {
            itemBinding.userComment.ivComment.visibility = View.GONE
        }
        if (item.isResponseDeleted[0].equals("Y", ignoreCase = true)) {
            response = "User Deleted Response"
            itemBinding.userComment.tvComment.setTextColor(ContextCompat.getColor(itemBinding.card.context, R.color.gray_color))
        }
        if (item.participantResponseStatus[1] == "N" && item.isGameExpired == "Y") {
            response = "Game Expired \\n User Did Not Respond In Time"
            itemBinding.userComment.tvComment.setTextColor(ContextCompat.getColor(itemBinding.card.context, R.color.gray_color))
        }
        itemBinding.userComment.tvName.text = item.participantName[0]
        itemBinding.userComment.tvComment.text = response
        Glide.with(itemBinding.root.context)
            .load(item.participantImageUrl[0])
            .apply(options)
            .into(itemBinding.userComment.rivProfile)
        if (item.participantId[0].equals(userId, ignoreCase = true)) {
            Utilities.verifiedUserType(itemBinding.root.context, item.userType[0], itemBinding.userComment.ivIconVerified)
        }
        else if (!item.participantId[0].equals(userId, ignoreCase = true)) {
            Utilities.verifiedUserType(itemBinding.root.context, item.userType[0], itemBinding.friendComment.ivIconVerified)
        }
        val url: String
        val media: Int
        if (!TextUtils.isEmpty(item.participantResponseVideoLink[0])) {
            url = item.participantResponseVideoLink[0]
            media = 2
        }
        else {
            url = item.participantResponseImageLargeLink[0]
            media = 1
        }
        if (!TextUtils.isEmpty(url)) {
            if (media == 2) {
                itemBinding.userComment.ivPlayVideo.visibility = View.VISIBLE
            }
            else {
                itemBinding.userComment.ivPlayVideo.visibility = View.GONE
            }
            Glide.with(itemBinding.root.context)
                .load(url)
                .apply(optionsRoundedCorners)
                .into(itemBinding.userComment.ivCommentAttachment)
            itemBinding.userComment.ivCommentAttachment.visibility = View.VISIBLE
            itemBinding.userComment.ivCommentAttachment.tag = position
            val builder = Zoomy.Builder(itemBinding.root.context as Activity)
                .target(itemBinding.userComment.ivCommentAttachment)
                .enableImmersiveMode(false)
            //                        .longPressListener(v -> {
//                            LoginDayActivityInfoList item = items.get(getBindingAdapterPosition());
//                            onItemClickListener.onItemClick(new Triple<>(item, CLICK_MEDIA, String.valueOf(v.getTag())));
//                        }).doubleTapListener(v -> Toast.makeText(context, "Double tap on "
//                                + v.getTag(), Toast.LENGTH_SHORT).show());
            builder.register()
        }
        else {
            itemBinding.userComment.ivPlayVideo.visibility = View.GONE
            itemBinding.userComment.ivCommentAttachment.visibility = View.GONE
        }
    }

    private fun setFriendComment(item: ActivityGameOneToOneResList, itemBinding: LayoutOneToOneCommentViewBinding, position: Int) {
        var response: String? = ""
        itemBinding.friendComment.ivUserMoreAction.visibility = View.GONE
        itemBinding.friendComment.tvCommentsCount.visibility = View.GONE
        itemBinding.friendComment.ivComment.visibility = View.GONE
        itemBinding.friendComment.btnAnswer.visibility = View.GONE
        itemBinding.friendComment.textAskedBy.visibility = View.GONE
        itemBinding.friendComment.tvComment.setTextColor(ContextCompat.getColor(itemBinding.card.context, R.color.gray_color))
        if (item.participantResponseStatus[1].equals("N", ignoreCase = true)) {
//            response = "";
            response = "Response Awaited"
            itemBinding.friendComment.tvComment.setTextColor(ContextCompat.getColor(itemBinding.card.context, R.color.gray_color))
        }
        else if (item.participantResponseStatus[0].equals("N", ignoreCase = true)
            && item.participantResponseStatus[1].equals("Y", ignoreCase = true)
        ) {
            response = "Response Received. Answer Now To Unlock It"
            itemBinding.friendComment.tvComment.setTextColor(ContextCompat.getColor(itemBinding.card.context, R.color.color_pink))
        }
        else {
            val url: String
            val media: Int
            if (!TextUtils.isEmpty(item.participantResponseVideoLink[1])) {
                url = item.participantResponseVideoLink[1]
                media = 2
            }
            else {
                url = item.participantResponseImageLargeLink[1]
                media = 1
            }
            if (!TextUtils.isEmpty(url)) {
                if (media == 2) {
                    itemBinding.friendComment.ivPlayVideo.visibility = View.VISIBLE
                }
                else {
                    itemBinding.friendComment.ivPlayVideo.visibility = View.GONE
                }
                Glide.with(itemBinding.root.context)
                    .load(url)
                    .apply(optionsRoundedCorners)
                    .into(itemBinding.friendComment.ivCommentAttachment)
                itemBinding.friendComment.ivCommentAttachment.visibility = View.VISIBLE
                itemBinding.friendComment.ivCommentAttachment.tag = position
                val builder = Zoomy.Builder(itemBinding.root.context as Activity)
                    .target(itemBinding.friendComment.ivCommentAttachment)
                    .enableImmersiveMode(false)
                //                        .longPressListener(v -> {
//                            LoginDayActivityInfoList item = items.get(getBindingAdapterPosition());
//                            onItemClickListener.onItemClick(new Triple<>(item, CLICK_MEDIA, String.valueOf(v.getTag())));
//                        }).doubleTapListener(v -> Toast.makeText(context, "Double tap on "
//                                + v.getTag(), Toast.LENGTH_SHORT).show());
                builder.register()
            }
            else {
                itemBinding.friendComment.ivPlayVideo.visibility = View.GONE
                itemBinding.friendComment.ivCommentAttachment.visibility = View.GONE
            }
            response = Base64Utility.decode(item.participantResponse[1])
            itemBinding.friendComment.tvComment.setTextColor(ContextCompat.getColor(itemBinding.card.context, R.color.color_black))
        }
        if (item.isResponseDeleted[1].equals("Y", ignoreCase = true)) {
            response = "User Deleted Response"
            itemBinding.friendComment.tvComment.setTextColor(ContextCompat.getColor(itemBinding.card.context, R.color.gray_color))
        }
        if (item.participantResponseStatus[1] == "N" && item.isGameExpired == "Y") {
            response = "Game Expired \nUser Did Not Respond In Time"
            itemBinding.friendComment.tvComment.setTextColor(ContextCompat.getColor(itemBinding.card.context, R.color.gray_color))
        }
        itemBinding.friendComment.tvName.text = item.participantName[1]
        itemBinding.friendComment.tvComment.text = response
        Glide.with(itemBinding.root.context)
            .load(item.participantImageUrl[1])
            .apply(options)
            .into(itemBinding.friendComment.rivProfile)
    }

    inner class One2OneViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val itemBinding: LayoutOneToOneCommentViewBinding
        override fun onBind(position: Int) {
            val item = items[position]!!
            setUserComment(item, itemBinding, position)
            setFriendComment(item, itemBinding, position)
            if (item.gameId.equals(gameIdToHighlight, ignoreCase = true)) {
                itemBinding.root.postDelayed({
                    animateChange(itemBinding)
                    gameIdToHighlight = null
                }, 1000)
            }
        }

        fun animateChange(viewHolder: LayoutOneToOneCommentViewBinding) {
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
            itemBinding = LayoutOneToOneCommentViewBinding.bind(itemView)
            itemBinding.userComment.btnAnswer.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        CLICK_ANSWER,
                        items[bindingAdapterPosition],
                        CLICK_ACTION_FOR_USER
                    )
                )
            })
            itemBinding.userComment.ivUserMoreAction.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        CLICK_MORE,
                        items[bindingAdapterPosition],
                        CLICK_ACTION_FOR_USER
                    )
                )
            })
            itemBinding.userComment.ivComment.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        CLICK_COMMENT,
                        items[bindingAdapterPosition],
                        CLICK_ACTION_FOR_USER
                    )
                )
            })
            itemBinding.userComment.ivCommentAttachment.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        CLICK_ATTACHMENT, items[bindingAdapterPosition], CLICK_ACTION_FOR_USER
                    )
                )
            })
            itemBinding.userComment.tvCommentsCount.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        CLICK_COMMENT_COUNT, items[bindingAdapterPosition], CLICK_ACTION_FOR_USER
                    )
                )
            })
            itemBinding.userComment.rivProfile.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        CLICK_ACTION_FOR_USER,
                        items[bindingAdapterPosition],
                        CLICK_ACTION_FOR_USER
                    )
                )
            })

            //***********************************************************************************************************
            itemBinding.friendComment.rivProfile.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        CLICK_ACTION_FOR_FRIEND, items[bindingAdapterPosition], CLICK_ACTION_FOR_FRIEND
                    )
                )
            })
            itemBinding.friendComment.btnAnswer.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Triple(
                        CLICK_ANSWER,
                        items[bindingAdapterPosition],
                        CLICK_ACTION_FOR_FRIEND
                    )
                )
            })

//            itemBinding.friendComment.ivUserMoreAction.setOnClickListener(new ViewClickListener(
//                    v -> onItemClickListener.onItemClick(new Triple<>(CLICK_MORE, items.get(getBindingAdapterPosition()), CLICK_ACTION_FOR_FRIEND))
//            ));

//            itemBinding.friendComment.ivComment.setOnClickListener(new ViewClickListener(
//                    v -> onItemClickListener.onItemClick(new Triple<>(CLICK_COMMENT, items.get(getBindingAdapterPosition()), CLICK_ACTION_FOR_FRIEND))
//            ));

//            itemBinding.friendComment.ivCommentAttachment.setOnClickListener(new ViewClickListener(
//                    v -> onItemClickListener.onItemClick(new Triple<>(CLICK_ATTACHMENT, items.get(getBindingAdapterPosition()), CLICK_ACTION_FOR_FRIEND))
//            ));

//            itemBinding.friendComment.tvCommentsCount.setOnClickListener(new ViewClickListener(
//                    v -> onItemClickListener.onItemClick(new Triple<>(CLICK_COMMENT_COUNT, items.get(getBindingAdapterPosition()), CLICK_ACTION_FOR_FRIEND))
//            ));
        }
    }

    companion object {
        var CLICK_ACTION_FOR_USER = 0
        var CLICK_ACTION_FOR_FRIEND = 1
        var CLICK_ANSWER = 2
        var CLICK_MORE = 3
        var CLICK_COMMENT = 4
        var CLICK_ATTACHMENT = 5
        var CLICK_COMMENT_COUNT = 6
    }
}