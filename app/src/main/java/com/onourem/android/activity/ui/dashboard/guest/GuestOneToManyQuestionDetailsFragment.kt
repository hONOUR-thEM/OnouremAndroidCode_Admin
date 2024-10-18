package com.onourem.android.activity.ui.dashboard.guest

import android.animation.ArgbEvaluator
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.ui.games.viewmodel.OneToManyViewModel
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.onourem.android.activity.R
import com.onourem.android.activity.models.ActivityGameOneToManyResList
import javax.inject.Inject
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.models.PlayGroup
import com.onourem.android.activity.ui.games.viewmodel.CommentsViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.models.UserList
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.onourem.android.activity.ui.games.fragments.OneToManyQuestionDetailsFragmentArgs
import com.onourem.android.activity.models.ActivityType
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.ui.games.fragments.OneToManyQuestionDetailsFragmentDirections
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.PlayGroupsResponse
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.models.GetOneToManyGameActivityResResponse
import com.onourem.android.activity.models.GetUserActivityGroupResponse
import android.annotation.SuppressLint
import com.onourem.android.activity.ui.survey.adapters.PaginationRVAdapter
import android.view.ViewGroup
import com.onourem.android.activity.ui.survey.BaseViewHolder
import android.view.LayoutInflater
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.onourem.android.activity.ui.utils.zoomy.Zoomy
import android.animation.ValueAnimator
import android.app.Activity
import android.util.Pair
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.databinding.FragmentGuestOneToManyQuestionDetailsBinding
import com.onourem.android.activity.databinding.ItemLayoutGuestCommentViewBinding
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.utils.*
import com.onourem.android.activity.ui.utils.glide.GlideApp
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import java.util.*

class GuestOneToManyQuestionDetailsFragment :
    AbstractBaseViewModelBindingFragment<OneToManyViewModel, FragmentGuestOneToManyQuestionDetailsBinding>() {
    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(15))
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)
    private val activityGameOneToManyResList: MutableList<ActivityGameOneToManyResList?> =
        ArrayList()
    private val gameIdList: MutableList<Int> = ArrayList()

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var question: LoginDayActivityInfoList? = null
    private var isLoading = false
    private var isLastPage = false
    private var oneToManyCommentsAdapter: OneToManyCommentsAdapter? = null
    private var userActionViewModel: UserActionViewModel? = null
    private var playGroup: PlayGroup? = null
    private var commentsViewModel: CommentsViewModel? = null
    private var gameIdToHighlight: String? = null
    private var gameActivityResIdToHighlight: String? = null
    private var isFromQuestionListing = false
    private var questionGamesViewModel: QuestionGamesViewModel? = null
    private var media = 0
    private val friendList: List<UserList>? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var scrollPosition = 0
    override fun viewModelType(): Class<OneToManyViewModel> {
        return OneToManyViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_guest_one_to_many_question_details
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            UserActionViewModel::class.java
        )
        questionGamesViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            QuestionGamesViewModel::class.java
        )
        commentsViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            CommentsViewModel::class.java
        )
        commentsViewModel!!.getCommentCountLiveData().observe(
            this
        ) { item: Triple<String?, Int, String?>? ->
            if (oneToManyCommentsAdapter != null && item != null) {
                oneToManyCommentsAdapter!!.updateComment(item.first, item.second, item.third)
                commentsViewModel!!.resetCommentCountLiveData()
            }
        }
        viewModel.reloadRequired.observe(this) { isRequired: String? ->
            if (isRequired != null && !isRequired.equals("", ignoreCase = true)) {
                if (isRequired.equals("Deleted", ignoreCase = true)) {
                    navController.popBackStack()
                } else {
                    reloadUI()
                }
            }
        }

        //loadFriendsList();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = OneToManyQuestionDetailsFragmentArgs.fromBundle(
            requireArguments()
        )
        question = args.question
        playGroup = args.playGroup
        gameIdToHighlight = args.gameIdToHighlight
        gameActivityResIdToHighlight = args.gameActivityResIdToHighlight
        isFromQuestionListing = args.isFromQuestionListing
        binding.tvQuestion.text = question!!.activityText
//        binding.ivRelation.setImageResource(
//            ActivityType.getActivityTypeIcon(
//                Objects.requireNonNull(
//                    question!!.activityType
//                )
//            )
//        )

        val imageOptions =
            RequestOptions().fitCenter().transform(RoundedCorners(20)).placeholder(R.drawable.default_place_holder)
                .error(R.drawable.default_place_holder)

        GlideApp.with(binding.root.context)
            .load(ActivityType.getActivityTypeIcon(Objects.requireNonNull(question!!.activityType)))
            .apply(imageOptions)
            .into(binding.ivRelation)

        binding.btnAskToFriends.setOnClickListener(ViewClickListener { v: View? -> handleButtonAction() })
        binding.ivRelation.setOnClickListener(ViewClickListener { v: View? ->
            var title = ""
            title = "Group Bonding Question"
            showAlert(title, question!!.activityTypeHint)
        })

//        question.setActivityImageLargeUrl("https://images.unsplash.com/photo-1544005313-94ddf0286df2?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1976&q=80");
        if (!TextUtils.isEmpty(question!!.activityImageLargeUrl)) {
            binding.ivImage.visibility = View.VISIBLE
            Glide.with(requireActivity())
                .load(question!!.activityImageLargeUrl)
                .apply(options)
                .into(binding.ivImage)
        } else {
            binding.ivImage.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(question!!.questionVideoUrl)) {
            binding.ivPlayVideo.visibility = View.VISIBLE
            media = 2
        } else {
            media = 1
            binding.ivPlayVideo.visibility = View.GONE
        }
        binding.ivImage.setOnClickListener(ViewClickListener { v: View? ->
            val url: String?
            url = if (media == 2) {
                question!!.questionVideoUrl
            } else {
                question!!.activityImageLargeUrl
            }
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavMediaView(
                    media,
                    url!!
                )
            )
        })
        userActionViewModel!!.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
            if (action != null && action.actionType != ActionType.DISMISS) {
                userActionViewModel!!.actionConsumed()
            }
        }
        binding.rvComments.itemAnimator = DefaultItemAnimator()
        binding.rvComments.addItemDecoration(VerticalSpaceItemDecoration(10))
        linearLayoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvComments.layoutManager = linearLayoutManager
        if (oneToManyCommentsAdapter == null) loadData(true) else binding.rvComments.adapter =
            oneToManyCommentsAdapter
        binding.rvComments.visibility = View.VISIBLE
        handleFab()
        viewModel.setReloadRequired("")
        isLastPage = false
        isLoading = false
    }

    private fun handleFab() {
        binding.rvComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0) binding.fab.extend() else if (dy > 0) binding.fab.shrink()
            }
        })

//        binding.rvComments.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
//            if (scrollY > oldScrollY) {
//                binding.fab.shrink();
//            } else if (scrollX < scrollY) {
//                binding.fab.extend();
//            }
//
//        });
        if (playGroup!!.playGroupId.equals("AAA", ignoreCase = true)
            || playGroup!!.playGroupId.equals("FFF", ignoreCase = true)
            || playGroup!!.playGroupId.equals("YYY", ignoreCase = true)
            || playGroup!!.playGroupId.equals("ZZZ", ignoreCase = true)
        ) {
            binding.fab.text = "All Questions"
        } else {
            binding.fab.text = "All O-Club Questions"
        }
        binding.fab.setOnClickListener(ViewClickListener { v: View? ->
            if (isFromQuestionListing) {
                navController.popBackStack()
            } else {
                if (playGroup!!.playGroupId.equals("AAA", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("FFF", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("YYY", ignoreCase = true)
                    || playGroup!!.playGroupId.equals("ZZZ", ignoreCase = true)
                ) {
                    val playSoloGroup = PlayGroup()
                    playSoloGroup.playGroupId = "FFF"
                    playSoloGroup.playGroupName = getString(R.string.label_friends_playing)
                    playSoloGroup.isDummyGroup = true
                    questionGamesViewModel!!.reloadUI(false)
                    if (Objects.requireNonNull(navController.currentDestination!!).id == R.id.nav_one_to_many_question_details) {
                        navController.navigate(OneToManyQuestionDetailsFragmentDirections.actionNavOneToManyQuestionDetailsToNavHome())
                    }
                } else {
                    questionGamesViewModel!!.getUserPlayGroupsById(playGroup!!.playGroupId).observe(
                        viewLifecycleOwner
                    ) { playGroupsResponseApiResponse: ApiResponse<PlayGroupsResponse> ->
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
                                    questionGamesViewModel!!.reloadUI(false)
                                    if (Objects.requireNonNull(navController.currentDestination!!).id == R.id.nav_one_to_many_question_details) {
                                        navController.navigate(
                                            OneToManyQuestionDetailsFragmentDirections.actionNavOneToManyQuestionDetailsToNavPlayGames(
                                                playGroup,
                                                ""
                                            )
                                        )
                                    }
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
                                        || playGroupsResponseApiResponse.errorMessage.contains(
                                    getString(R.string.unable_to_connect_host_message3)
                                ))
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
            }
        })
    }

    override fun onDestroyView() {
        commentsViewModel!!.resetCommentCountLiveData()
        if (question!!.activityType != null && question!!.activityType.equals(
                "1toM",
                ignoreCase = true
            )
        ) {
            questionGamesViewModel!!.setInAppReviewPopup(true)
        }
        super.onDestroyView()
    }

    private fun loadData(showProgress: Boolean) {
        val playGroupId = playGroup!!.playGroupId
        //        if (TextUtils.isEmpty(playGroupId) || playGroupId.equalsIgnoreCase("0"))
//            playGroupId = "AAA";
        if (playGroupId.equals("FFF", ignoreCase = true) || playGroupId.equals(
                "YYY",
                ignoreCase = true
            ) || playGroupId.equals("ZZZ", ignoreCase = true)
        ) {
            viewModel.getQuestionResponseForGuestUser(question!!.activityId!!).observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<GetOneToManyGameActivityResResponse> ->
                if (apiResponse.loading) {
                    if (showProgress) showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

//                        if (getFragmentContext() != null)
//                            ((DashboardActivity) getFragmentContext()).showBanner(apiResponse.body.isPopupRequired(), apiResponse.body.getCustomScreenPopup());

//                        if (/*apiResponse.body.isPopupRequired()*/ true) {
//                            navController.navigate(MobileNavigationDirections.actionGlobalNavInviteSheet());
//                        }
                        if (apiResponse.body.gameIdList != null && !apiResponse.body.gameIdList!!.isEmpty()) {
                            gameIdList.clear()
                            gameIdList.addAll(apiResponse.body.gameIdList!!)
                        }
                        if (apiResponse.body.activityGameOneToManyResList != null && !apiResponse.body.activityGameOneToManyResList!!.isEmpty()) {
                            activityGameOneToManyResList.clear()
                            activityGameOneToManyResList.addAll(apiResponse.body.activityGameOneToManyResList!!)
                        }
                        setAdapter()
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
                            AppUtilities.showLog("Network Error", "getOneToManyGameActivityRes")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getOneToManyGameActivityRes",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
        } else {
            viewModel.getUserActivityGroupResponse(
                question!!.activityId!!,
                activityPlayGroupId()!!,
                question!!.activityGameResponseId!!,
                gameActivityResIdToHighlight!!,
                question!!.oClubActivityId
            ).observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<GetUserActivityGroupResponse> ->
                if (apiResponse.loading) {
                    if (showProgress) showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        (fragmentContext as DashboardActivity).showBanner(
                            apiResponse.body.isPopupRequired,
                            apiResponse.body.customScreenPopup
                        )
                        gameIdList.clear()
                        activityGameOneToManyResList.clear()
                        if (apiResponse.body.activityGameResponseIdList.isNotEmpty()) gameIdList.addAll(
                            apiResponse.body.activityGameResponseIdList
                        )
                        if (apiResponse.body.activityGameOneToManyResList.isNotEmpty()) activityGameOneToManyResList.addAll(
                            apiResponse.body.activityGameOneToManyResList
                        )
                        setAdapter()
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "getUserActivityGroupResponse")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getUserActivityGroupResponse",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
        }
    }

    private fun handleButtonAction() {
//        if (binding.btnAskToFriends.getText().toString().equalsIgnoreCase(getString(R.string.label_ask_this_to_friends))) {
//            navController.navigate(MobileNavigationDirections.actionGlobalNavAskQuestionsSheet(question, false, playGroup.getPlayGroupId()));
//        } else if (binding.btnAskToFriends.getText().toString().equalsIgnoreCase(getString(R.string.label_answer_question))) {
//            AnswerQuestionDialogFragment.getInstance(question, playGroup, apiResponse -> {
//                if (!TextUtils.isEmpty(apiResponse.getParticipationStatus()))
//                    question.setUserParticipationStatus(apiResponse.getParticipationStatus());
//                if (!TextUtils.isEmpty(apiResponse.getActivityTagStatus()))
//                    question.setActivityTag(apiResponse.getActivityTagStatus());
//                if (gameIdList != null && !gameIdList.isEmpty()) gameIdList.clear();
//                isLastPage = false;
//                isLoading = false;
//                loadData(false);
//            }).show(requireActivity().getSupportFragmentManager(), "Answer");
//        }
        (fragmentContext as DashboardActivity).showGuestPopup(
            "Ask Question",
            "After you login, you can ask questions to friends from this icon."
        )
    }

    private fun reloadUI() {
        val count =
            if (activityGameOneToManyResList == null || activityGameOneToManyResList.isEmpty()) 0 else activityGameOneToManyResList.size
        if (count > 1) {
            binding.btnAskToFriends.text = getString(R.string.label_ask_this_to_friends)
        } else if (count == 1 && "Y" == activityGameOneToManyResList[0]!!.participantResponseStatus) {
            binding.btnAskToFriends.text = getString(R.string.label_ask_this_to_friends)
        } else {
            binding.btnAskToFriends.text = getString(R.string.label_answer_question)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter() {
        scrollPosition = -1
        if (!activityGameOneToManyResList.isEmpty() && (!TextUtils.isEmpty(
                gameActivityResIdToHighlight
            ) || !TextUtils.isEmpty(gameIdToHighlight))
        ) {
            for (i in activityGameOneToManyResList.indices) {
                val item = activityGameOneToManyResList[i]
                if (!TextUtils.isEmpty(gameActivityResIdToHighlight) && item!!.activityGameResponseId.equals(
                        gameActivityResIdToHighlight,
                        ignoreCase = true
                    )
                ) {
                    scrollPosition = i
                    break
                } else if (!TextUtils.isEmpty(item!!.gameId) && item.gameId.equals(
                        gameIdToHighlight,
                        ignoreCase = true
                    ) && TextUtils.isEmpty(gameActivityResIdToHighlight)
                ) {
                    scrollPosition = i
                    break
                }
            }
        }
        val count =
            if (activityGameOneToManyResList.isEmpty()) 0 else activityGameOneToManyResList.size
        oneToManyCommentsAdapter = OneToManyCommentsAdapter(
            gameIdToHighlight,
            gameActivityResIdToHighlight,
            activityGameOneToManyResList
        ) { item: Pair<Int, ActivityGameOneToManyResList> ->
            if (item.first == OneToManyCommentsAdapter.CLICK_MORE) {
                val userId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)
                val title = "What would you like to do?"
                val actions = ArrayList<Action<*>>()
                if (item.second.participantResponseStatus.equals("Y", ignoreCase = true)
                    && item.second.participantId.equals(userId, ignoreCase = true)
                ) {
                    actions.add(
                        Action(
                            "Delete My Answer",
                            R.color.color_black,
                            ActionType.DELETE_MY_ANSWER,
                            item.second
                        )
                    )
                } else if (item.second.participantResponseStatus.equals(
                        "Y",
                        ignoreCase = true
                    ) && item.second.showEditOrDelete.equals("N", ignoreCase = true)
                ) {
                } else if (item.second.participantResponseStatus.equals(
                        "N",
                        ignoreCase = true
                    ) && item.second.isGameExpired.equals("N", ignoreCase = true)
                ) {
                    actions.add(
                        Action(
                            "Ignore",
                            R.color.color_black,
                            ActionType.IGNORE,
                            item.second
                        )
                    )
                }
                if (item.second.participantResponseStatus.equals(
                        "Y",
                        ignoreCase = true
                    ) && item.second.participantId.equals(userId, ignoreCase = true)
                    && (playGroup!!.playGroupId == "AAA" || TextUtils.isEmpty(item.second.playGroupId) || item.second.playGroupId == "0")
                ) {
                    actions.add(
                        Action(
                            "Edit Privacy",
                            R.color.color_black,
                            ActionType.EDIT_GAME_VISIBILITY,
                            item.second
                        )
                    )
                }
                if (friendList != null && friendList.size > 0) {
                    for (user in friendList) {
                        if (item.second.participantId.equals(user.userId, ignoreCase = true)) {
                            actions.add(
                                Action(
                                    getString(R.string.action_label_send_private_message),
                                    R.color.color_black,
                                    ActionType.SEND_PRIVATE_MESSAGE,
                                    item.second
                                )
                            )
                            break
                        }
                    }
                }
                if (!item.second.participantId.equals(userId, ignoreCase = true)) {
                    actions.add(
                        Action(
                            "Report Inappropriate",
                            R.color.color_black,
                            ActionType.REPORT_INAPPROPRIATE,
                            item.second
                        )
                    )
                }
                if (item.second.participantResponseStatus.equals(
                        "Y",
                        ignoreCase = true
                    ) && !item.second.participantId.equals(userId, ignoreCase = true)
                ) {
                    actions.add(
                        Action(
                            "Irrelevant Response",
                            R.color.color_black,
                            ActionType.IRRELEVANT_RESPONSE,
                            item.second
                        )
                    )
                }
                if (actions.isEmpty()) {
                    showAlert("You can delete your response after 48 hrs from answering")
                } else {
                    val bundle = Bundle()
                    bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavUserActionBottomSheet(
                            title,
                            bundle,
                            ""
                        )
                    )
                }
            } else if (item.first == OneToManyCommentsAdapter.CLICK_ANSWER) {
                handleButtonAction()
            } else if (item.first == OneToManyCommentsAdapter.CLICK_PROFILE) {
                navController.navigate(
                    MobileNavigationDirections.actionGlobalNavProfile(
                        question!!.activityId,
                        item.second.participantId
                    )
                )
            } else if (item.first == OneToManyCommentsAdapter.CLICK_COMMENT_COUNT) {
                (fragmentContext as DashboardActivity).showGuestPopup(
                    "Comments",
                    "You can read comments only after login."
                )
            } else if (item.first == OneToManyCommentsAdapter.CLICK_COMMENT) {
                (fragmentContext as DashboardActivity).showGuestPopup(
                    "Comments",
                    "You can write comments only after login."
                )
            } else if (item.first == OneToManyCommentsAdapter.CLICK_ATTACHMENT) {
                var url: String? = ""
                val media: Int
                if (!TextUtils.isEmpty(item.second.participantResponseVideoLink)) {
                    url = item.second.participantResponseVideoLink
                    media = 2
                } else {
                    url = item.second.participantResponseImageLargeLink
                    media = 1
                }

                if (url != null){
                    navController.navigate(
                        MobileNavigationDirections.actionGlobalNavMediaView(
                            media,
                            url
                        )
                    )
                }
            }
        }
        binding.rvComments.adapter = oneToManyCommentsAdapter
        if (count > 1) {
            binding.btnAskToFriends.text = getString(R.string.label_ask_this_to_friends)
        } else if (count == 1 && activityGameOneToManyResList[0] != null && activityGameOneToManyResList[0]!!.participantResponseStatus != null && activityGameOneToManyResList[0]!!.participantResponseStatus == "Y") {
            binding.btnAskToFriends.text = getString(R.string.label_ask_this_to_friends)
        } else {
            binding.btnAskToFriends.text = getString(R.string.label_answer_question)
        }
        if (scrollPosition >= 0) {
            binding.rvComments.postDelayed({
                binding.rvComments.smoothScrollToPosition(
                    scrollPosition
                )
            }, 300)
        }
    }

    private fun setFooterMessage() {
        isLoading = false
        if (oneToManyCommentsAdapter != null) {
            val footerMessage: String
            footerMessage = if (oneToManyCommentsAdapter!!.itemCount == 0) {
                "Start by answering this question. You can then see responses of your friends and can ask this question further."
            } else if (oneToManyCommentsAdapter!!.itemCount == 1 && "N".equals(
                    activityGameOneToManyResList[0]!!.participantResponseStatus, ignoreCase = true
                )
            ) {
                "Start by answering this question. You can then see responses of your friends and can ask this question further."
            } else if (oneToManyCommentsAdapter!!.itemCount == 1 && "Y".equals(
                    activityGameOneToManyResList[0]!!.participantResponseStatus, ignoreCase = true
                )
            ) {
                "Looks like none of your friends have answered this question yet. Forward this question to friends by clicking on the above button. You will be able to see their responses as soon as they answer."
            } else {
                "Forward this question to more friends by clicking on the button below the question."
            }
            binding.rvComments.postDelayed(
                { oneToManyCommentsAdapter!!.addFooter(footerMessage) },
                200
            )
        }
    }

    private fun activityPlayGroupId(): String? {
        var activityPlayGroupId: String? = null
        val playGroupId = playGroup!!.playGroupId
        activityPlayGroupId = if (playGroupId.equals("AAA", ignoreCase = true)) {
            "0"
        } else if (playGroupId.equals("BBB", ignoreCase = true)) {
            playGroupId
        } else if (playGroupId.equals("YYY", ignoreCase = true)) {
            "1"
        } else if (playGroupId.equals("ZZZ", ignoreCase = true)) {
            "2"
        } else {
            playGroupId
        }
        return activityPlayGroupId
    }
}

internal class OneToManyCommentsAdapter(
    private var gameIdToHighlight: String?,
    private var gameActivityResIdToHighlight: String?,
    activityGameOneToManyResList: List<ActivityGameOneToManyResList?>,
    private val onItemClickListener: OnItemClickListener<Pair<Int, ActivityGameOneToManyResList>>
) : PaginationRVAdapter<ActivityGameOneToManyResList?>(activityGameOneToManyResList) {
    override fun emptyLoadingItem(): ActivityGameOneToManyResList {
        return ActivityGameOneToManyResList()
    }

    override fun emptyFooterItem(): ActivityGameOneToManyResList {
        return ActivityGameOneToManyResList()
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return OneToManyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_layout_guest_comment_view, parent, false)
        )
    }

    fun updateComment(gameId: String?, count: Int, gameResId: String?) {
        for (i in items.indices) {
            val item = items[i]!!
            if (item.gameId != null && item.gameId.equals(gameId, ignoreCase = true)
                && item.activityGameResponseId != null && item.activityGameResponseId.equals(
                    gameResId,
                    ignoreCase = true
                )
            ) {
                var currentCount = count
                if (currentCount <= 0) {
                    currentCount = 0
                }
                item.commentCount = currentCount.toString()
                gameIdToHighlight = gameId
                gameActivityResIdToHighlight = gameResId
                notifyItemChanged(i)
                break
            }
        }
    }

    inner class OneToManyViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val itemBinding: ItemLayoutGuestCommentViewBinding
        private val options = RequestOptions()
            .fitCenter()
            .transform(CircleCrop())
            .placeholder(R.drawable.default_user_profile_image)
            .error(R.drawable.default_user_profile_image)
        private val optionsRoundedCorners = RequestOptions()
            .fitCenter()
            .transform(FitCenter(), RoundedCorners(15))
            .placeholder(R.drawable.default_place_holder)
            .error(R.drawable.default_place_holder)

        override fun onBind(position: Int) {
            val item = items[position]!!
            itemBinding.root.background =
                AppCompatResources.getDrawable(itemView.context, R.drawable.bg_card_round_corner)
            itemBinding.tvCommentsCount.background = AppCompatResources.getDrawable(
                itemView.context,
                R.drawable.shape_outlined_rectangle_black
            )
            itemBinding.tvCommentsCount.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.color_black
                )
            )
            var response: String? = ""
            if (position == 0 && item.participantResponseStatus == "N" && item.isGameExpired == "N") {
                itemBinding.tvCommentsCount.visibility = View.GONE
                itemBinding.ivComment.visibility = View.GONE
                itemBinding.btnAnswer.visibility = View.VISIBLE
                itemBinding.tvComment.setTextColor(
                    ContextCompat.getColor(
                        itemBinding.getRoot().context,
                        R.color.color_black
                    )
                )
                response = "Response Awaited"
            } else {
                itemBinding.tvCommentsCount.visibility = View.VISIBLE
                itemBinding.ivComment.visibility = View.VISIBLE
                itemBinding.tvComment.setTextColor(
                    ContextCompat.getColor(
                        itemBinding.getRoot().context,
                        R.color.color_black
                    )
                )
                val count = item.commentCount
                if (!TextUtils.isEmpty(count) && count != "0") {
                    itemBinding.tvCommentsCount.text = count
                    itemBinding.tvCommentsCount.visibility = View.VISIBLE
                } else {
                    itemBinding.tvCommentsCount.visibility = View.GONE
                }
                response = Base64Utility.decode(item.participantResponse)
            }
            itemBinding.tvName.text = item.participantName
            // itemBinding.tvComment.setVisibility(View.GONE);
            if (response != null && !response.equals("", ignoreCase = true)) {
                itemBinding.tvComment.visibility = View.VISIBLE
                itemBinding.tvComment.text = response
            } else {
                itemBinding.tvComment.visibility = View.GONE
            }

//            if (item.getParticipantImageUrl() != null && item.getParticipantImageUrl().contains("userProfileDefaultPic.jpeg")) {
//                Glide.with(itemBinding.getRoot().getContext())
//                        .load(R.drawable.ic_profile_placeholder)
//                        .apply(options)
//                        .thumbnail(0.1f)
//                        .into(itemBinding.rivProfile);
//            } else {
//                Glide.with(itemBinding.getRoot().getContext())
//                        .load(item.getParticipantImageUrl())
//                        .apply(options)
//                        .thumbnail(0.1f)
//                        .into(itemBinding.rivProfile);
//            }
            AppUtilities.setUserProfile(
                itemBinding.root.context,
                itemBinding.rivProfile,
                item.participantImageUrl
            )
            Utilities.verifiedUserType(
                itemBinding.getRoot().context,
                item.userType,
                itemBinding.ivIconVerified
            )
            val url: String
            val media: Int
            if (!TextUtils.isEmpty(item.participantResponseVideoLink)) {
                url = item.participantResponseVideoLink.orEmpty()
                media = 2
            } else {
                url = item.participantResponseImageLargeLink.orEmpty()
                media = 1
            }
            if (!TextUtils.isEmpty(url)) {
                if (media == 2) {
                    itemBinding.ivPlayVideo.visibility = View.VISIBLE
                } else {
                    itemBinding.ivPlayVideo.visibility = View.GONE
                }
                Glide.with(itemBinding.getRoot().context)
                    .load(url)
                    .apply(optionsRoundedCorners)
                    .into(itemBinding.ivCommentAttachment)
                itemBinding.ivCommentAttachment.visibility = View.VISIBLE
                itemBinding.ivCommentAttachment.tag = bindingAdapterPosition
                val builder = Zoomy.Builder(itemBinding.getRoot().context as Activity)
                    .target(itemBinding.ivCommentAttachment)
                    .enableImmersiveMode(false)
                    .tapListener { v: View? ->
                        onItemClickListener.onItemClick(
                            Pair.create(
                                CLICK_ATTACHMENT, items[bindingAdapterPosition]
                            )
                        )
                    }
                //                        .longPressListener(v -> {
//                            LoginDayActivityInfoList item = items.get(getBindingAdapterPosition());
//                            onItemClickListener.onItemClick(new Triple<>(item, CLICK_MEDIA, String.valueOf(v.getTag())));
//                        }).doubleTapListener(v -> Toast.makeText(context, "Double tap on "
//                                + v.getTag(), Toast.LENGTH_SHORT).show());
                builder.register()
            } else {
                itemBinding.ivPlayVideo.visibility = View.GONE
                itemBinding.ivCommentAttachment.visibility = View.GONE
            }
            if (!TextUtils.isEmpty(item.activityGameResponseId) && item.activityGameResponseId.equals(
                    gameActivityResIdToHighlight, ignoreCase = true
                )
            ) {
                itemBinding.getRoot().postDelayed({
                    animateChange(itemBinding)
                    gameIdToHighlight = null
                    gameActivityResIdToHighlight = null
                }, 1000)
            } else if (!TextUtils.isEmpty(item.gameId) && item.gameId.equals(
                    gameIdToHighlight, ignoreCase = true
                ) && TextUtils.isEmpty(
                    gameActivityResIdToHighlight
                )
            ) {
                itemBinding.getRoot().postDelayed({
                    animateChange(itemBinding)
                    gameIdToHighlight = null
                    gameActivityResIdToHighlight = null
                }, 1000)
            }
        }

        fun animateChange(viewHolder: ItemLayoutGuestCommentViewBinding) {
            val colorFrom = ContextCompat.getColor(
                viewHolder.getRoot().context,
                R.color.color_highlight_game_post
            )
            val colorTo = ContextCompat.getColor(viewHolder.getRoot().context, R.color.color_white)
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.duration = 500 // milliseconds
            colorAnimation.startDelay = 300 // milliseconds
            colorAnimation.addUpdateListener { animator: ValueAnimator ->
                viewHolder.parent.setBackgroundColor(
                    animator.animatedValue as Int
                )
            }
            colorAnimation.repeatCount = 1
            colorAnimation.start()
        }

        init {
            itemBinding = ItemLayoutGuestCommentViewBinding.bind(itemView)

//            itemBinding.btnAnswer.setOnClickListener(new ViewClickListener(
//                    v -> onItemClickListener.onItemClick(Pair.create(CLICK_ANSWER, items.get(getBindingAdapterPosition())))
//            ));
//
//            itemBinding.ivUserMoreAction.setOnClickListener(new ViewClickListener(
//                    v -> onItemClickListener.onItemClick(Pair.create(CLICK_MORE, items.get(getBindingAdapterPosition())))
//            ));
//
            itemBinding.ivComment.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Pair.create(CLICK_COMMENT, items[bindingAdapterPosition])
                )
            })
            itemBinding.tvCommentsCount.setOnClickListener(ViewClickListener { v: View? ->
                onItemClickListener.onItemClick(
                    Pair.create(CLICK_COMMENT_COUNT, items[bindingAdapterPosition])
                )
            })

//            itemBinding.ivCommentAttachment.setOnClickListener(new ViewClickListener(
//                    v -> onItemClickListener.onItemClick(Pair.create(CLICK_ATTACHMENT, items.get(getBindingAdapterPosition())))
//            ));
//
//            itemBinding.rivProfile.setOnClickListener(new ViewClickListener(
//                    v -> onItemClickListener.onItemClick(Pair.create(CLICK_PROFILE, items.get(getBindingAdapterPosition())))
//            ));
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