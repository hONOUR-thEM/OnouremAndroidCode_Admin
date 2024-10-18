package com.onourem.android.activity.ui.games.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentQuestionGamesBinding
import com.onourem.android.activity.models.DataItem
import com.onourem.android.activity.models.PlayGroup
import com.onourem.android.activity.models.PlayGroupsResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.adapters.QuestionGamesAdapter
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog.ACTION_RIGHT
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog.showAlert
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*
import javax.inject.Inject

class QuestionGamesFragment :
    AbstractBaseBottomSheetBindingDialogFragment<QuestionGamesViewModel, FragmentQuestionGamesBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var recyclerView: RecyclerView? = null
    private var questionGamesAdapter: QuestionGamesAdapter? = null
    private var strMessage: String = ""
    private var playGroupId: String? = null
    private var isFrom: String? = null
    override fun layoutResource(): Int {
        return R.layout.fragment_question_games
    }

    public override fun viewModelType(): Class<QuestionGamesViewModel> {
        return QuestionGamesViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getActionMutableLiveData().observe(this) { action ->
            if (action == null) return@observe
            val actionType: ActionType? = action.actionType
            if (actionType == null || actionType == ActionType.NONE) return@observe
            viewModel.actionConsumed()
            if (actionType == ActionType.EXIT_PLAY_GROUP) {
                val pair: Pair<PlayGroup, String> = action.data as Pair<PlayGroup, String>
                showAlert(
                    requireActivity(),
                    pair.first.playGroupName,
                    "Do you really want to exit this O-Club?",
                    action,
                    getString(R.string.label_cancel),
                    getString(R.string.label_yes)
                ) {
                    if ((it != null) && (it.first != null) && (it.first == ACTION_RIGHT)) {
                        exitGroup(action)
                    }
                }
            } else if (actionType != ActionType.DISMISS) {
                init(false)
            }
        }
        strMessage =
            "O-Clubs are groups to play questions in. Ask questions every day to initiate fun or meaningful conversations. Every member has to answer to see other answers. \n \n Different kinds of O-Clubs - \n \n \uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67 Friends and Family: Discuss memories, opinions, life updates to stay in touch. \n \n ❤️ Spouse/Partner: Add spice or depth in your relationship by asking fun or meaningful questions to each other.\n\n \uD83D\uDCBC Colleagues: A new way of team building. Ask questions to build healthier relations between colleagues. \n\n \uD83D\uDC69\uD83C\uDFFD\u200D\uD83C\uDF93 Class of Students: Know your classmates through fun questions.\n\n \uD83D\uDEB4\u200D♀️ Hobby groups: Learn about people with whom you share interests."
    }

    private fun exitGroup(action: Action<*>) {
        val pair: Pair<PlayGroup, String> = action.data as Pair<PlayGroup, String>
        viewModel.exitPlayGroupUser(pair.first.playGroupId, pair.second)
            .observe(this) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        init(false)
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                    if ((apiResponse.errorMessage != null
                                && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "exitPlayGroupUser")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "exitPlayGroupUser",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var questionGamesFragmentArgs: QuestionGamesFragmentArgs? = null
        questionGamesFragmentArgs = QuestionGamesFragmentArgs.fromBundle((requireArguments()))
        if (questionGamesFragmentArgs.playGroupId != null) {
            playGroupId = questionGamesFragmentArgs.playGroupId
        }
        if (!TextUtils.isEmpty(questionGamesFragmentArgs.getIsFrom())) isFrom =
            questionGamesFragmentArgs.isFrom
        recyclerView = requireView().findViewById(R.id.rvQuestionsGames)
        recyclerView!!.layoutManager = LinearLayoutManager(requireActivity())
        val fab: ExtendedFloatingActionButton = requireView().findViewById(R.id.fab)
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0) fab.extend() else if (dy > 0) fab.shrink()
            }
        })
        fab.setOnClickListener(ViewClickListener { view: View? ->
            navController.navigate(
                QuestionGamesFragmentDirections.actionNavQuestionGamesToNavPlayGroupMember()
            )
        })
        init(true)
    }

    private fun init(showProgress: Boolean) {
        if (questionGamesAdapter != null) {
            recyclerView!!.adapter = questionGamesAdapter
        }
        viewModel.userPlayGroups().observe(
            viewLifecycleOwner
        ) { playGroupsResponseApiResponse: ApiResponse<PlayGroupsResponse> ->
            if (playGroupsResponseApiResponse.loading) {
                if (showProgress) showProgress()
            } else if (playGroupsResponseApiResponse.isSuccess && playGroupsResponseApiResponse.body != null) {
                hideProgress()
                if (playGroupsResponseApiResponse.body.errorCode
                        .equals("000", ignoreCase = true)
                ) {
                    setAdapter(playGroupsResponseApiResponse.body)
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
                if ((playGroupsResponseApiResponse.errorMessage != null
                            && (playGroupsResponseApiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                            || playGroupsResponseApiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
                ) {
                    if (BuildConfig.DEBUG) {
                        AppUtilities.showLog("Network Error", "getUserPlayGroups")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getUserPlayGroups",
                        playGroupsResponseApiResponse.code.toString()
                    )
                }
            }
        }
        handleFab()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter(playGroupsResponse: PlayGroupsResponse?) {
        val items: MutableList<DataItem<Any>> = LinkedList()

        if (playGroupsResponse?.playGroup == null || playGroupsResponse.playGroup.isEmpty()) {

            binding.tvMessage.text = strMessage
            binding.tvMessage.visibility = View.VISIBLE
            binding.constraintLayoutHeader.constraintLayoutHeader.visibility = View.GONE
            binding.rvQuestionsGames.adapter = null
        } else {
            binding.constraintLayoutHeader.constraintLayoutHeader.visibility = View.VISIBLE
            for (playGroup: PlayGroup in playGroupsResponse.playGroup) {
                playGroup.isDummyGroup = false
                val item: DataItem<Any> = DataItem(playGroup, false, R.drawable.ic_groups)
                item.isGroup = true
                //Todo needs to remove this hard coded code
//                if (playGroup.playGroupId == "503") {
//                    playGroup.commentsEnabled = "N"
//                    playGroup.inviteLinkEnabled = "N"
//                }else{
//                    playGroup.commentsEnabled = "Y"
//                    playGroup.inviteLinkEnabled = "Y"
//                }

                items.add(item)
            }
            items.add(DataItem(getString(R.string.label_what_is_o_club), true))
            if (questionGamesAdapter == null) {
                questionGamesAdapter =
                    QuestionGamesAdapter(items, { item: DataItem<Any> ->
                        if (item.isSection) {
                            if (item.data as String == getString(R.string.label_what_is_o_club)) {
                                val s = "Hide \n\n$strMessage"
                                item.data = s
                            } else {
                                item.data = getString(R.string.label_what_is_o_club)
                            }
                            binding.tvMessage.visibility = View.GONE
                            questionGamesAdapter!!.notifyDataSetChanged()
                        } else {
                            val playGroup: PlayGroup = item.data as PlayGroup
                            if (playGroup.playGroupId.equals(
                                    "FFF",
                                    ignoreCase = true
                                ) /*|| playGroup.getPlayGroupId().equalsIgnoreCase("CCC")*/) {
                                //                        navController.navigate(QuestionGamesFragmentDirections.actionNavQuestionGamesToNavPlayGroupMember());
                            } else {

                                navController.navigate(
                                    QuestionGamesFragmentDirections.actionNavQuestionGamesToNavPlayGames(
                                        playGroup,
                                        (isFrom)!!
                                    )
                                )
                            }
                        }
                    }, { item: DataItem<Any> ->
                        val playGroup: PlayGroup = item.data as PlayGroup
                        val title: String = playGroup.playGroupName!!
                        val actions: ArrayList<Action<*>> = ArrayList()
                        if (playGroup.isUserAdmin
                                .equals("C", ignoreCase = true) || playGroup.isUserAdmin
                                .equals("Y", ignoreCase = true)
                        ) {
                            actions.add(
                                Action(
                                    getString(R.string.action_label_add_remove_members),
                                    R.color.color_black,
                                    ActionType.ADD_REMOVE_PLAY_GROUP_MEMBERS,
                                    playGroup
                                )
                            )

                            actions.add(
                                Action(
                                    getString(R.string.action_label_get_invite_link),
                                    R.color.color_black,
                                    ActionType.PLAY_GROUP_INVITE_LINK,
                                    playGroup
                                )
                            )

                        } else {
                            actions.add(
                                Action(
                                    getString(R.string.action_label_see_members),
                                    R.color.color_black,
                                    ActionType.SEE_PLAY_GROUP_MEMBERS,
                                    playGroup
                                )
                            )
                        }
                        actions.add(
                            Action(
                                getString(R.string.action_label_exit_group),
                                R.color.color_red,
                                ActionType.EXIT_PLAY_GROUP,
                                Pair.create(
                                    playGroup, preferenceHelper!!.getString(
                                        Constants.KEY_LOGGED_IN_USER_ID
                                    )
                                )
                            )
                        )
                        val bundle = Bundle()
                        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
                        navController.navigate(
                            QuestionGamesFragmentDirections
                                .actionNavQuestionGamesToNavBootomSheet(title, bundle)
                        )
                    }, playGroupId)
                recyclerView!!.adapter = questionGamesAdapter
            } else {
                questionGamesAdapter!!.resetData(items)
            }
        }

//        items.add(new DataItem<>(getString(R.string.label_my_question_history), true));
//        PlayGroup playedGroup = new PlayGroup();
//        playedGroup.setPlayGroupId("ZZZ");
//        playedGroup.setPlayGroupName(getString(R.string.label_all_questions_played));
//        playedGroup.setDummyGroup(true);
//
//        DataItem playedDataItem = new DataItem<>(playedGroup, false, R.drawable.ic_all_question_played);
//        playedDataItem.setHasMoreAction(false);
//        items.add(playedDataItem);
//
//        PlayGroup questionCreatedGroup = new PlayGroup();
//        questionCreatedGroup.setPlayGroupId("YYY");
//        questionCreatedGroup.setDummyGroup(true);
//        questionCreatedGroup.setPlayGroupName(getString(R.string.label_questions_i_created));
//        DataItem questionCreatedGroupData = new DataItem<>(questionCreatedGroup, false, R.drawable.ic_question_i_created);
//        questionCreatedGroupData.setHasMoreAction(false);
//        items.add(questionCreatedGroupData);
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.setRefreshShowBadges(true)
    }

    private fun handleFab() {
        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > oldScrollY) {
                binding.fab.shrink()
            } else if (scrollX < scrollY) {
                binding.fab.extend()
            }
        })
    }
}