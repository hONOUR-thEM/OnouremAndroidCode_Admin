package com.onourem.android.activity.ui.games.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Pair
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.dynamiclinks.DynamicLink.*
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogGroupCategoryBinding
import com.onourem.android.activity.databinding.DialogGroupNameBinding
import com.onourem.android.activity.databinding.DialogUserLinkBinding
import com.onourem.android.activity.databinding.FragmentPlayGroupMemberBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.games.adapters.MembersListAdapter
import com.onourem.android.activity.ui.games.adapters.MembersPaginationAdapter
import com.onourem.android.activity.ui.games.adapters.SingleChoiceCategoryAdapter
import com.onourem.android.activity.ui.games.adapters.SingleChoiceLanguageAdapter
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog.ACTION_RIGHT
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog.showAlert
import com.onourem.android.activity.ui.games.fragments.UserRelation.Companion.getStatus
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.PaginationListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.util.*
import javax.inject.Inject
import kotlin.math.min

class PlayGroupMemberFragment :
    AbstractBaseViewModelBindingFragment<QuestionGamesViewModel, FragmentPlayGroupMemberBinding>() {
    private lateinit var layoutManager: LinearLayoutManager
    private var displayNumberOfUsers: Long? = null

    //    private List<UserList> blockedUserIds;
    private val playGroupUserInfoList: MutableList<PlayGroupUserInfoList> = ArrayList()
    private var playgroupUserIdList: ArrayList<Int>? = null


    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var membersListAdapter: MembersPaginationAdapter? = null
    private var dashboardViewModel: DashboardViewModel? = null
    private var categories: ArrayList<PlayGroupCategoryList>? = null
    private var appLanguageList: ArrayList<AppLanguageList>? = null
    private var singleChoiceAdapter: SingleChoiceCategoryAdapter? = null
    private var singleChoiceLanguageAdapter: SingleChoiceLanguageAdapter? = null
    private var checkedRadioButton: String = ""
    private var body: GetPlayGroupUsersResponse? = null
    private var playGroupCategories: GetPlayGroupCategories? = null
    private var alertDialogCategory: AlertDialog? = null
    private var dialogViewBindingCategory: DialogGroupCategoryBinding? = null
    private var alertDialogLanguage: AlertDialog? = null
    private var dialogViewBindingLanguage: DialogGroupCategoryBinding? = null


    private var isLastPagePagination: Boolean = false
    private var isLoadingPage: Boolean = false

    override fun viewModelType(): Class<QuestionGamesViewModel> {
        return QuestionGamesViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_play_group_member
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userActionViewModel: UserActionViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]
        dashboardViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(
            DashboardViewModel::class.java
        )

//        viewModel.getLocalBlockUserList().observe(this, listResource -> {
//            if (listResource.getStatus() == Status.SUCCESS && listResource.getData() != null && !listResource.getData().isEmpty()) {
//                blockedUserIds = listResource.getData();
//            }
//        });
        viewModel.getSelectedFriendForGroup().observe(this) { friend ->
            if (friend != null) {
//                if (viewModel.getPlayGroup().isNew()) {
//                    createNewGroup(friend);
//                } else {
                addNewFriendInGroup(friend)
                //                }
                viewModel.resetAddNewFriendInGroup()
            }
        }
        viewModel.selectedFriendListForGroup.observe(
            this,
            Observer { friends: ArrayList<UserList?>? ->
                if (friends != null) {
//                if (viewModel.getPlayGroup().isNew()) {
//                    createNewGroup(friend);
//                } else {
                    addNewSelectedFriendsInGroup(friends)
                    //                }
                    viewModel.resetSelectedNewFriendsInGroup()
                }
            }
        )
        userActionViewModel.getActionMutableLiveData().observe(this) { action ->
            if (action == null) return@observe
            val actionType: ActionType? = action.actionType
            if (actionType == null || actionType == ActionType.NONE) return@observe
            userActionViewModel.actionConsumed()
            if (actionType == ActionType.CANCEL_FRIEND_REQUEST) {
                cancelFriendRequest(action)
            }
        }
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
            } else if (actionType == ActionType.MAKE_PLAY_GROUP_ADMIN || actionType == ActionType.REMOVE_AS_PLAY_GROUP_ADMIN) {
                membersListAdapter!!.notifyDataSetChanged()
            } else if (actionType == ActionType.REMOVE_FROM_PLAY_GROUP) {
                val item: Pair<PlayGroup, PlayGroupUserInfoList> =
                    action.data as Pair<PlayGroup, PlayGroupUserInfoList>
                val iterator: MutableIterator<PlayGroupUserInfoList> =
                    playGroupUserInfoList.iterator()
                while (iterator.hasNext()) {
                    val user: PlayGroupUserInfoList = iterator.next()
                    if ((user.userId == item.second.userId)) {
                        iterator.remove()
                        break
                    }
                }
                membersListAdapter!!.notifyDataSetChanged()
            } else if (actionType == ActionType.UN_FRIEND) {
                val item: PlayGroupUserInfoList = action.data as PlayGroupUserInfoList
                item.userRelation = "Add as Friend"
                notifyDataSetChanged(item)
            }
        }
    }

    private fun addNewSelectedFriendsInGroup(userLists: ArrayList<UserList?>) {
        val ids: MutableList<Int> = ArrayList()
        for (user in userLists) {
            ids.add(Integer.valueOf(user!!.userId))
            val newMember = PlayGroupUserInfoList()
            newMember.userId = user.userId
            newMember.userName = user.firstName + " " + user.lastName
            newMember.isUserAdmin = "N"
            newMember.userRelation = "Friends"
            newMember.userProfilePicUrl = user.profilePicture
            playGroupUserInfoList.add(0, newMember)
        }
        if (!viewModel.getPlayGroup().playGroupId
                .equals("", ignoreCase = true) && !((viewModel.getPlayGroup().playGroupId
                .equals("AAA", ignoreCase = true)
                    || viewModel.getPlayGroup().playGroupId.equals("FFF", ignoreCase = true)
                    || viewModel.getPlayGroup().playGroupId.equals("YYY", ignoreCase = true)
                    || viewModel.getPlayGroup().playGroupId
                .equals("ZZZ", ignoreCase = true)))
        ) {
            viewModel.addPlayGroupUser(
                viewModel.getPlayGroup().playGroupId,
                Utilities.getTokenSeparatedString(ids, ",")
            ).observe(
                viewLifecycleOwner
            ) { standardResponseApiResponse: ApiResponse<StandardResponse> ->
                if (standardResponseApiResponse.loading) {
                    showProgress()
                } else if (standardResponseApiResponse.isSuccess && standardResponseApiResponse.body != null) {
                    hideProgress()
                    val response: StandardResponse = standardResponseApiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
//                    PlayGroupUserInfoList newMember = new PlayGroupUserInfoList();
//                    newMember.setUserId(item.getUserId());
//                    newMember.setUserName(item.getFirstName() + " " + item.getLastName());
//                    newMember.setIsUserAdmin("N");
//                    newMember.setUserRelation("Friends");
//                    newMember.setUserProfilePicUrl(item.getProfilePicture());
//                    playGroupUserInfoList.add(0, newMember);
                        if (membersListAdapter != null) membersListAdapter!!.notifyDataSetChanged()
                    } else {
                        showAlert(response.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(standardResponseApiResponse.errorMessage)
                    if ((standardResponseApiResponse.errorMessage != null
                                && (standardResponseApiResponse.errorMessage.contains(
                            getString(
                                R.string.unable_to_connect_host_message1
                            )
                        )
                                || standardResponseApiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3))))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "addPlayGroupUser")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "addPlayGroupUser",
                            standardResponseApiResponse.code.toString()
                        )
                    }
                }
            }
        }
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
                        navController.popBackStack(R.id.nav_play_games, true)
                    } else {
                        showAlert(
                            getString(R.string.label_network_error),
                            apiResponse.body.errorMessage
                        )
                    }
                } else {
                    hideProgress()
                    showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                }
            }
    }

    @SuppressLint("NonConstantResourceId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(requireActivity())
        binding.rvMembers.layoutManager = layoutManager
//        layoutManager.orientation = RecyclerView.VERTICAL
        binding.rvMembers.isNestedScrollingEnabled = false

        binding.rvMembers.addOnScrollListener(object : PaginationListener(layoutManager) {


//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (dy < 0) binding.fab.extend() else if (dy > 0) binding.fab.shrink()
//            }

            override fun loadMoreItems() {
                loadMoreGames()
            }

            override fun isLastPage(): Boolean {
                return this@PlayGroupMemberFragment.isLastPagePagination
            }

            override fun isLoading(): Boolean {
                return this@PlayGroupMemberFragment.isLoadingPage
            }
        })


        categories = ArrayList()
        appLanguageList = ArrayList()
        val addGroupMemberListener: ViewClickListener =
            ViewClickListener(View.OnClickListener { v: View? ->
//            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
//                    .addSharedElement(binding.clContainer, "clContainer")
//                    .addSharedElement(binding.rvMembers, "rvMembers")
//                    .build();
                val groupMemberIds: ArrayList<String> = ArrayList()
                for (item: PlayGroupUserInfoList in playGroupUserInfoList) {
                    groupMemberIds.add(item.userId!!)
                }
                navController.navigate(
                    PlayGroupMemberFragmentDirections.actionNavPlayGroupMemberToNavAddGroupMember(
                        "O-Club Members",
                        groupMemberIds
                    )
                )
            })
        binding.tvAddFriends.setOnClickListener(addGroupMemberListener)
        binding.imageButton1.setOnClickListener(addGroupMemberListener)
        binding.clContainer.setOnClickListener(addGroupMemberListener)
        binding.etAddFriends.setOnClickListener(addGroupMemberListener)

        //binding.switchCompat.setChecked(true);
        //binding.switchCompat.setText(getString(R.string.group_members_can_post_questions));

//        binding.rbGroup.setOnClickListener((View v) -> {
//            if (!viewModel.getPlayGroup().getIsUserAdmin() && !viewModel.getPlayGroup().isNew()) {
//                //binding.rbAdmins.setChecked(!binding.rbAdmins.isChecked());
//                showAlert("Only admin can change this");
//                return;
//            }
//            if (binding.switchCompat.isChecked()) {
//                binding.switchCompat.setText(getString(R.string.group_members_can_post_questions));
//            } else {
//                binding.switchCompat.setText(getString(R.string.message_admin_can_post_questions));
//            }
//
//            if (!viewModel.getPlayGroup().isNew()) {
//                viewModel.updateAllCanSeeFlag(viewModel.getPlayGroup().getPlayGroupId(), binding.switchCompat.isChecked() ? "Y" : "N").observe(getViewLifecycleOwner(), apiResponse -> {
//                    if (apiResponse.loading) {
//                        showProgress();
//                    } else if (apiResponse.isSuccess() && apiResponse.body != null) {
//                        hideProgress();
//                    } else {
//                        hideProgress();
//                        showAlert(apiResponse.errorMessage);
//                    }
//                });
//            }
//        });

//        if (viewModel.getPlayGroup().isNew() || (!TextUtils.isEmpty(viewModel.getPlayGroup().getAllCanAsk()) && viewModel.getPlayGroup().getAllCanAsk().equalsIgnoreCase("Y"))) {
//            binding.switchCompat.setChecked(true);
//            binding.switchCompat.setText(getString(R.string.group_members_can_post_questions));
//
//        } else {
//            binding.switchCompat.setChecked(false);
//            binding.switchCompat.setText(getString(R.string.message_admin_can_post_questions));
//        }
//        binding.rvMembers.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
//                if (dy < 0)
//                    binding.fab.extend();
//                else if (dy > 0)
//                    binding.fab.shrink();
//            }
//        });


        binding.fab.setOnClickListener(ViewClickListener { v: View? ->
            getUserLinkInfo(
                viewModel.getPlayGroup().playGroupId!!
            )
//            if (viewModel.getPlayGroup().inviteLinkEnabled == "Y") {
//
//            } else {
//                showAlert("Invite Link is disabled for this O-club")
//            }
        })
        init()
        handleFab()
        binding.rbGroup.setOnCheckedChangeListener { arg0: RadioGroup?, id: Int ->
            when (id) {
                -1 -> {}
                R.id.rbEveryOne -> {
                    checkedRadioButton = "Y"
                    updateAllCanAsk(checkedRadioButton)
                }
                R.id.rbAdmins -> {
                    checkedRadioButton = "N"
                    updateAllCanAsk(checkedRadioButton)
                }
                R.id.rbCreator -> {
                    checkedRadioButton = "C"
                    updateAllCanAsk(checkedRadioButton)
                }
            }
        }
    }

    private fun getDisplayGameIdListElements(myList: ArrayList<Int>, startIndex: Int): List<Int?> {
        val sub: List<Int?>
        sub = myList.subList(
            startIndex,
            min(myList.size.toLong(), startIndex + displayNumberOfUsers!!).toInt()
        )
        return sub
    }

    private fun loadMoreGames() {
        isLoadingPage = true
        var start = 0
        var end = 0

        if (playgroupUserIdList!!.isEmpty()) {
            isLastPagePagination = true
            isLoadingPage = false
            //setFooterMessage()
            return
        }

        start = membersListAdapter!!.itemCount
        end = playgroupUserIdList!!.size
        if (start >= end) {
            isLastPagePagination = true
            isLoadingPage = false
            //setFooterMessage()
            return
        }

        val audioIds = ArrayList<Int>()
        val gameIdList1 = getDisplayGameIdListElements(playgroupUserIdList!!, start) as List<*>
        AppUtilities.showLog("**audioGameIdList1:", gameIdList1.size.toString())

        for (i in gameIdList1.indices) {
            val item = gameIdList1[i]
            audioIds.add(item as Int)
        }

        viewModel.getNextPlayGroupUsers(viewModel.getPlayGroup().playGroupId, Utilities.getTokenSeparatedString(audioIds, ","))
            .observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<GetPlayGroupUsersResponse> ->
                if (apiResponse.loading && membersListAdapter != null) {
                    membersListAdapter!!.addLoading()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (membersListAdapter != null) {
                        membersListAdapter!!.removeLoading()
                    }
                    isLoadingPage = false
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        //AppUtilities.showLog("PlayGamesFragment", "LoginDayActivityInfoList getNextUserLoginDayActivityInfo" + apiResponse.body.getLoginDayActivityInfoList().size() + "");
                        if (apiResponse.body.playGroupUserInfoList!!
                                .isNotEmpty()
                        ) {
                            //isLastPagePagination = true;
                            //setFooterMessage()
                            isLastPagePagination =
                                apiResponse.body.playGroupUserInfoList!!.size < displayNumberOfUsers!!
                            membersListAdapter!!.addItems(apiResponse.body.playGroupUserInfoList)
                        } else {

                            //comments.addAll(apiResponse.body.audioCommentList)

//
//                                    Log.e("####",
//                                        String.format("server: %d",
//                                            apiResponse.body.audioCommentList.size))
                        }
                    } else {
                        // isLastPage = true;
                        //setFooterMessage()
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    isLoadingPage = false
                    if (membersListAdapter != null) {
                        membersListAdapter!!.removeLoading()
                    }
                    showAlert(apiResponse.errorMessage)
                }
            }

    }

    private fun updateAllCanAsk(checkedRadioButton: String) {
        if (!viewModel.getPlayGroup().isNew) {
            viewModel.updateAllCanSeeFlag(
                viewModel.getPlayGroup().playGroupId,
                checkedRadioButton
            ).observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                }
            }
        }
    }

    private fun getCategories(alertDialog: AlertDialog, dialogViewBinding: DialogGroupNameBinding) {
        viewModel.playGroupCategory().observe(
            viewLifecycleOwner,
            Observer { standardResponseApiResponse: ApiResponse<GetPlayGroupCategories> ->
                if (standardResponseApiResponse.loading) {
                    showProgress()
                } else if (standardResponseApiResponse.isSuccess && standardResponseApiResponse.body != null) {
                    hideProgress()
                    playGroupCategories = standardResponseApiResponse.body
                    if (playGroupCategories!!.errorCode.equals("000", ignoreCase = true)) {
                        categories = ArrayList()
                        categories!!.addAll(playGroupCategories!!.playGroupCategoryList!!)
                        appLanguageList = ArrayList()
                        appLanguageList!!.addAll(playGroupCategories!!.appLanguageList!!)
                        val builderCategory: AlertDialog.Builder =
                            AlertDialog.Builder(requireActivity())
                        dialogViewBindingCategory =
                            DialogGroupCategoryBinding.inflate(LayoutInflater.from(requireActivity()))
                        builderCategory.setView(dialogViewBindingCategory!!.root)
                        builderCategory.setCancelable(true)
                        alertDialogCategory = builderCategory.create()
                        alertDialogCategory!!.getWindow()!!
                            .setLayout(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            )
                        alertDialogCategory!!.getWindow()!!
                            .setBackgroundDrawableResource(android.R.color.transparent)
                        val builderLanguage: AlertDialog.Builder =
                            AlertDialog.Builder(requireActivity())
                        dialogViewBindingLanguage =
                            DialogGroupCategoryBinding.inflate(LayoutInflater.from(requireActivity()))
                        builderLanguage.setView(dialogViewBindingLanguage!!.root)
                        builderLanguage.setCancelable(true)
                        alertDialogLanguage = builderLanguage.create()
                        alertDialogLanguage!!.getWindow()!!
                            .setLayout(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                            )
                        alertDialogLanguage!!.getWindow()!!
                            .setBackgroundDrawableResource(android.R.color.transparent)
                        singleChoiceAdapter = SingleChoiceCategoryAdapter(
                            requireActivity(),
                            alertDialogCategory!!,
                            categories
                        ) { item: PlayGroupCategoryList? ->
                            dialogViewBinding.btnDialogSelectCategory.text = item!!.categoryName
                            singleChoiceAdapter!!.updateItem(item.id.toString())
                            viewModel.getPlayGroup().playGroupCategoryName = item.categoryName
                            viewModel.getPlayGroup().playGroupCategoryId = item.id.toString()
                        }
                        singleChoiceLanguageAdapter = SingleChoiceLanguageAdapter(
                            requireActivity(),
                            alertDialogLanguage!!,
                            appLanguageList
                        ) { item: AppLanguageList? ->
                            dialogViewBinding.btnDialogSelectLanguage.text = item!!.languageName
                            singleChoiceLanguageAdapter!!.updateItem(item.id.toString())
                            viewModel.getPlayGroup().playGroupLanguageId = item.id.toString()
                            viewModel.getPlayGroup().playGroupLanguageName = item.languageName
                        }
                        if (!TextUtils.isEmpty(
                                viewModel.getPlayGroup().playGroupCategoryName
                            )
                        ) {
                            dialogViewBinding.btnDialogSelectCategory.text =
                                viewModel.getPlayGroup().playGroupCategoryName
                            singleChoiceAdapter!!.updateItem(
                                viewModel.getPlayGroup().playGroupCategoryId!!
                            )
                        } else {
                            dialogViewBinding.btnDialogSelectCategory.text = "--"
                        }
                        if (!TextUtils.isEmpty(
                                viewModel.getPlayGroup().playGroupLanguageName
                            )
                        ) {
                            dialogViewBinding.btnDialogSelectLanguage.text =
                                viewModel.getPlayGroup().playGroupLanguageName
                            singleChoiceLanguageAdapter!!.updateItem(
                                viewModel.getPlayGroup().playGroupLanguageId!!
                            )
                        } else {
                            dialogViewBinding.btnDialogSelectLanguage.text = "--"
                        }
                        alertDialog.show()
                    } else {
                        showAlert(playGroupCategories!!.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(standardResponseApiResponse.errorMessage)
                }
            }
        )
    }

    private fun showCategories(
        alertDialog: AlertDialog?,
        dialogViewBinding: DialogGroupCategoryBinding?,
        dialogViewBindingMain: DialogGroupNameBinding
    ) {
        dialogViewBinding!!.rvCategory.layoutManager = LinearLayoutManager(requireActivity())
        dialogViewBinding.rvCategory.adapter = singleChoiceAdapter
        if ((viewModel.getPlayGroup().playGroupCategoryName != null
                    && !viewModel.getPlayGroup().playGroupCategoryName
                .equals("", ignoreCase = true))
        ) {
            singleChoiceAdapter!!.updateItem(viewModel.getPlayGroup().playGroupCategoryId!!)
        }
        alertDialog!!.show()
    }

    private fun showLanguages(
        alertDialog: AlertDialog?,
        dialogViewBinding: DialogGroupCategoryBinding?,
        dialogViewBindingMain: DialogGroupNameBinding
    ) {
        dialogViewBinding!!.rvCategory.layoutManager = LinearLayoutManager(requireActivity())
        dialogViewBinding.rvCategory.adapter = singleChoiceLanguageAdapter
        if ((viewModel.getPlayGroup().playGroupLanguageName != null
                    && !viewModel.getPlayGroup().playGroupLanguageName
                .equals("", ignoreCase = true))
        ) {
            singleChoiceLanguageAdapter!!.updateItem(
                viewModel.getPlayGroup().playGroupLanguageId!!
            )
        }
        alertDialog!!.show()
    }

    private fun handleFab() {
//        binding.nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
//            if (scrollY > oldScrollY) {
//                binding.fab.shrink()
//            } else if (scrollX < scrollY) {
//                binding.fab.extend()
//            }
//        } as NestedScrollView.OnScrollChangeListener?
//        )
    }

    private fun setAllCanAskSwitch(response: GetPlayGroupUsersResponse?) {
        if (viewModel.getPlayGroup().isUserAdmin.equals("Y", ignoreCase = true)) {
            binding.rbAdmins.isEnabled = false
            binding.rbCreator.isEnabled = false
            binding.rbEveryOne.isEnabled = false
            binding.fab.visibility = View.GONE
        } else if (viewModel.getPlayGroup().isUserAdmin.equals("C", ignoreCase = true)) {
            binding.rbAdmins.isEnabled = true
            binding.rbCreator.isEnabled = true
            binding.rbEveryOne.isEnabled = true
            binding.fab.visibility = View.VISIBLE
        } else {
            binding.rbAdmins.isEnabled = false
            binding.rbCreator.isEnabled = false
            binding.rbEveryOne.isEnabled = false
            binding.fab.visibility = View.GONE
        }
        if (!TextUtils.isEmpty(response!!.allCanAsk)) {
            if (response.allCanAsk.equals("N", ignoreCase = true)) {
                binding.rbAdmins.isChecked = true
            } else if (response.allCanAsk.equals("Y", ignoreCase = true)) {
                binding.rbEveryOne.isChecked = true
            } else if (response.allCanAsk.equals("C", ignoreCase = true)) {
                binding.rbCreator.isChecked = true
            }
        }
        if (viewModel.getPlayGroup().isNew) {
            binding.rbEveryOne.isChecked = true
            binding.tvGroupName.text = viewModel.getPlayGroup().playGroupName
            if (viewModel.getPlayGroup().playGroupCategoryName != null) {
                binding.tvGroupNameHint.text = String.format(
                    "%s O-Club",
                    viewModel.getPlayGroup().playGroupCategoryName
                )
            }
        }
    }

    private fun init() {
        playgroupUserIdList = ArrayList()
        isLastPagePagination = false
        isLoadingPage = false
        binding.fab.visibility = View.VISIBLE
        if (!viewModel.getPlayGroup().isNew) {
            if (membersListAdapter == null) {
                viewModel.getPlayGroupUsers(viewModel.getPlayGroup().playGroupId).observe(
                    viewLifecycleOwner
                ) { apiResponse: ApiResponse<GetPlayGroupUsersResponse> ->
                    if (apiResponse.loading) {
                        showProgress()
                    } else if (apiResponse.isSuccess) {
                        hideProgress()
                        if (apiResponse.body != null) {
                            if (apiResponse.body.errorCode
                                    .equals("000", ignoreCase = true)
                            ) {

                                displayNumberOfUsers = apiResponse.body.displayNumberOfUsers

                                viewModel.getPlayGroup().allCanAsk =
                                    apiResponse.body.allCanAsk
                                body = apiResponse.body
                                setAllCanAskSwitch(body)
                                val list: List<PlayGroupUserInfoList>? =
                                    apiResponse.body.playGroupUserInfoList
                                if (list != null && list.isNotEmpty()) {
                                    playGroupUserInfoList.clear()
                                    playGroupUserInfoList.addAll(list)
                                }

                                val idList: List<Int>? =
                                    apiResponse.body.playgroupUserIdList
                                if (idList != null && idList.isNotEmpty()) {
                                    playgroupUserIdList!!.clear()
                                    playgroupUserIdList!!.addAll(idList)
                                }

                                viewModel.getPlayGroup().newMemeberNumber = 0
                                //                                if (blockedUserIds != null && !blockedUserIds.isEmpty()) {
//                                    for (UserList blockedUser : blockedUserIds) {
//                                        Iterator<PlayGroupUserInfoList> iterator = playGroupUserInfoList.iterator();
//                                        while (iterator.hasNext()) {
//                                            PlayGroupUserInfoList user = iterator.next();
//                                            if (user.getUserId().equals(blockedUser.getUserId())) {
//                                                iterator.remove();
//                                            }
//                                        }
//                                    }
//                                }
                                setUserListAdapter(playGroupUserInfoList)
                            } else {
                                showAlert(apiResponse.body.errorMessage)
                            }
                        }
                    } else {
                        hideProgress()
                        showAlert(apiResponse.errorMessage)
                    }
                }
            } else {
                setAllCanAskSwitch(body)
                binding.rvMembers.adapter = membersListAdapter
            }
        } else if (TextUtils.isEmpty(viewModel.getPlayGroup().playGroupName)) {
            showGroupNameDialog("")
        }
        binding.tvGroupName.text = viewModel.getPlayGroup().playGroupName
        if (viewModel.getPlayGroup().playGroupCategoryName != null) {
            binding.tvGroupNameHint.text = String.format(
                "%s O-Club",
                viewModel.getPlayGroup().playGroupCategoryName
            )
        }
        val ivEditGroupName: View = requireView().findViewById(R.id.ivEditGroupName)
        if (viewModel.getPlayGroup().isNew || (viewModel.getPlayGroup().isUserAdmin
                .equals("C", ignoreCase = true) || viewModel.getPlayGroup().isUserAdmin
                .equals("Y", ignoreCase = true))
        ) {
            ivEditGroupName.setOnClickListener(ViewClickListener { v: View? ->
                showGroupNameDialog(
                    viewModel.getPlayGroup().playGroupName!!
                )
            })
        } else {
            binding.clContainer.visibility = View.GONE
            ivEditGroupName.visibility = View.GONE
        }
//        binding.rvMembers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                if (dy < 0) binding.fab.extend() else if (dy > 0) binding.fab.shrink()
//            }
//        })

    }

    private fun setUserListAdapter(playGroupUserInfoList: List<PlayGroupUserInfoList>) {
        membersListAdapter = MembersPaginationAdapter(
            playGroupUserInfoList,
            preferenceHelper!!,
            body!!.createdBy!!,
            { item: Pair<String, PlayGroupUserInfoList> ->
                when (item.first) {
                    MembersListAdapter.ACTION_MORE -> showMoreActions(item.second)
                    MembersListAdapter.ACTION_ROW -> showUserProfile(item.second)
                    MembersListAdapter.ACTION_IGNORE_FRIEND -> ignoreFriendRequest(item)
                    MembersListAdapter.ACTION_IS_FRIEND -> {
                        val title: String = "What would you like to do?"
                        when (getStatus(item.second.userRelation)) {
                            UserRelation.FRIENDS -> {
                                val actions: ArrayList<Action<*>> = ArrayList()
                                actions.add(
                                    Action(
                                        getString(R.string.action_label_un_friend),
                                        R.color.color_black,
                                        ActionType.UN_FRIEND,
                                        item.second
                                    )
                                )
                                val bundle: Bundle = Bundle()
                                bundle.putParcelableArrayList(
                                    Constants.KEY_BOTTOM_SHEET_ACTIONS,
                                    actions
                                )
                                navController.navigate(
                                    PlayGroupMemberFragmentDirections
                                        .actionNavPlayGroupMemberToNavBootomSheet(title, bundle)
                                )
                            }
                            UserRelation.ADD_FRIEND -> sendFriendRequest(item)
                            UserRelation.ACCEPT_REQUEST -> acceptFriendRequest(item)
                            UserRelation.CANCEL_REQUEST -> {
                                val cancelActions: ArrayList<Action<*>> = ArrayList()
                                cancelActions.add(
                                    Action(
                                        getString(R.string.action_cancel_friend_request),
                                        R.color.color_black,
                                        ActionType.CANCEL_FRIEND_REQUEST,
                                        item.second
                                    )
                                )
                                val bundle = Bundle()
                                bundle.putParcelableArrayList(
                                    Constants.KEY_BOTTOM_SHEET_ACTIONS,
                                    cancelActions
                                )
                                navController.navigate(
                                    MobileNavigationDirections
                                        .actionGlobalNavUserActionBottomSheet(title, bundle, "")
                                )
                            }
                            UserRelation.NONE -> {}
                        }
                    }
                }
            },
            preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID),
            viewModel.getPlayGroup().isUserAdmin!!
        )

        binding.rvMembers.adapter = membersListAdapter


    }

    private fun sendFriendRequest(item: Pair<String, PlayGroupUserInfoList>) {
        viewModel.sendFriendRequest(item.second.userId, "24").observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<UserActionStandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                val response: UserActionStandardResponse = apiResponse.body
                if (response.errorCode.equals("000", ignoreCase = true)) {
                    item.second.userRelation = "Friend Request Sent"
                    notifyDataSetChanged(item.second)
                } else {
                    showAlert(
                        getString(R.string.label_network_error),
                        response.errorMessage
                    )
                }
            } else {
                hideProgress()
                showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
            }
        }
    }

    private fun cancelFriendRequest(action: Action<*>) {
        val userList: PlayGroupUserInfoList = action.data as PlayGroupUserInfoList
        viewModel.cancelSentRequest(userList.userId, "24").observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<UserActionStandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                val response: UserActionStandardResponse = apiResponse.body
                if (response.errorCode.equals("000", ignoreCase = true)) {
                    userList.userRelation = "Add as Friend"
                    notifyDataSetChanged(userList)
                } else {
                    showAlert(
                        getString(R.string.label_network_error),
                        response.errorMessage
                    )
                }
            } else {
                hideProgress()
                showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
            }
        }
    }

    private fun ignoreFriendRequest(item: Pair<String, PlayGroupUserInfoList>) {
        viewModel.cancelPendingRequest(item.second.userId, "24").observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<UserActionStandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                val response: UserActionStandardResponse = apiResponse.body
                if (response.errorCode.equals("000", ignoreCase = true)) {
                    item.second.userRelation = "Add as Friend"
                    notifyDataSetChanged(item.second)
                } else {
                    showAlert(
                        getString(R.string.label_network_error),
                        response.errorMessage
                    )
                }
            } else {
                hideProgress()
                showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
            }
        }
    }

    private fun acceptFriendRequest(item: Pair<String, PlayGroupUserInfoList>) {
        viewModel.acceptPendingRequest(item.second.userId, "24").observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<UserActionStandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                val response: UserActionStandardResponse = apiResponse.body
                if (response.errorCode.equals("000", ignoreCase = true)) {
                    item.second.userRelation = "Friends"
                    notifyDataSetChanged(item.second)
                } else {
                    showAlert(
                        getString(R.string.label_network_error),
                        response.errorMessage
                    )
                }
            } else {
                hideProgress()
                showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
            }
        }
    }

    private fun notifyDataSetChanged(data: PlayGroupUserInfoList) {
        if (membersListAdapter != null) {
            membersListAdapter!!.notifyDataUpdated(data)
        }
    }

    private fun showMoreActions(playGroupUserInfo: PlayGroupUserInfoList) {
        val title: String = playGroupUserInfo.userName!!
        val actions: ArrayList<Action<*>> = ArrayList()
        val playGroup: PlayGroup = viewModel.getPlayGroup()
        if (playGroupUserInfo.userId.equals(
                preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID),
                ignoreCase = true
            )
        ) {
            actions.add(
                Action(
                    getString(R.string.action_label_exit_group),
                    R.color.color_red,
                    ActionType.EXIT_PLAY_GROUP,
                    Pair(
                        playGroup, preferenceHelper!!.getString(
                            Constants.KEY_LOGGED_IN_USER_ID
                        )
                    )
                )
            )
        } else {
            if (playGroupUserInfo.isUserAdmin
                    .equals("Y", ignoreCase = true) && (playGroup.isUserAdmin
                    .equals("Y", ignoreCase = true) || playGroup.isUserAdmin
                    .equals("C", ignoreCase = true))
            ) {
                actions.add(
                    Action(
                        getString(R.string.action_label_remove_as_play_group_admin),
                        R.color.color_red,
                        ActionType.REMOVE_AS_PLAY_GROUP_ADMIN,
                        Pair(playGroup, playGroupUserInfo)
                    )
                )
            } else if (playGroupUserInfo.isUserAdmin
                    .equals("N", ignoreCase = true) && (playGroup.isUserAdmin
                    .equals("Y", ignoreCase = true) || playGroup.isUserAdmin
                    .equals("C", ignoreCase = true))
            ) {
                actions.add(
                    Action(
                        getString(R.string.action_label_make_group_admin),
                        R.color.color_black,
                        ActionType.MAKE_PLAY_GROUP_ADMIN,
                        Pair(playGroup, playGroupUserInfo)
                    )
                )
            }
            if (playGroup.isUserAdmin
                    .equals("Y", ignoreCase = true) || playGroup.isUserAdmin
                    .equals("C", ignoreCase = true)
            ) actions.add(
                Action(
                    getString(R.string.action_label_remove_from_play_group),
                    R.color.color_red,
                    ActionType.REMOVE_FROM_PLAY_GROUP,
                    Pair(playGroup, playGroupUserInfo)
                )
            )
        }
        val bundle: Bundle = Bundle()
        bundle.putParcelableArrayList(Constants.KEY_BOTTOM_SHEET_ACTIONS, actions)
        navController.navigate(
            PlayGroupMemberFragmentDirections
                .actionNavPlayGroupMemberToNavBootomSheet(title, bundle)
        )
    }

    private fun showUserProfile(userInfoList: PlayGroupUserInfoList) {
        navController.navigate(
            MobileNavigationDirections.actionGlobalNavProfile(
                null,
                userInfoList.userId
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addNewFriendInGroup(item: UserList) {
        viewModel.addPlayGroupUser(viewModel.getPlayGroup().playGroupId, item.userId)
            .observe(
                viewLifecycleOwner
            ) { standardResponseApiResponse: ApiResponse<StandardResponse> ->
                if (standardResponseApiResponse.loading) {
                    showProgress()
                } else if (standardResponseApiResponse.isSuccess && standardResponseApiResponse.body != null) {
                    hideProgress()
                    val response: StandardResponse = standardResponseApiResponse.body
                    if (response.errorCode.equals("000", ignoreCase = true)) {
                        val newMember: PlayGroupUserInfoList = PlayGroupUserInfoList()
                        newMember.userId = item.userId
                        newMember.userName = item.firstName + " " + item.lastName
                        newMember.isUserAdmin = "N"
                        newMember.userRelation = "Friends"
                        newMember.userProfilePicUrl = item.profilePicture
                        playGroupUserInfoList.add(0, newMember)
                        if (membersListAdapter != null) membersListAdapter!!.notifyDataSetChanged()
                    } else {
                        showAlert(response.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(standardResponseApiResponse.errorMessage)
                }
            }
    }

    private fun createNewGroup(
        playGroupCategoryId: String,
        categoryName: String,
        playGroupLanguageId: String,
        languageName: String
    ) {
        if (!isVisible) return
        viewModel.createPlayGroup(
            binding.tvGroupName.text.toString(),
            preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID),
            true /*binding.switchCompat.isChecked()*/,
            playGroupCategoryId,
            playGroupLanguageId
        ).observe(
            viewLifecycleOwner
        ) { createPlayGroupResponseApiResponse: ApiResponse<CreatePlayGroupResponse> ->
            if (createPlayGroupResponseApiResponse.loading) {
                showProgress()
            } else if (createPlayGroupResponseApiResponse.isSuccess && createPlayGroupResponseApiResponse.body != null) {
                hideProgress()
                val response: CreatePlayGroupResponse? = createPlayGroupResponseApiResponse.body
                if (response!!.errorCode.equals("000", ignoreCase = true)) {
                    val playGroup: PlayGroup = viewModel.getPlayGroup()
                    playGroup.allCanAsk = checkedRadioButton
                    playGroup.playGroupName = response.playGroupName
                    playGroup.playGroupId = response.playGroupId.toString()
                    playGroup.isUserAdmin = "C"
                    playGroup.playGroupCategoryId = playGroupCategoryId
                    playGroup.playGroupCategoryName = categoryName
                    playGroup.playGroupLanguageId = playGroupLanguageId
                    playGroup.playGroupLanguageName = languageName
                    playGroup.isNew = false
                    init()
                } else {
                    showAlert(
                        getString(R.string.label_network_error),
                        response.errorMessage
                    )
                }
            } else {
                hideProgress()
                showAlert(
                    getString(R.string.label_network_error),
                    createPlayGroupResponseApiResponse.errorMessage
                )
            }
        }
    }

    private fun showGroupNameDialog(groupName: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(binding.root.context)
        val dialogViewBinding: DialogGroupNameBinding =
            DialogGroupNameBinding.inflate(LayoutInflater.from(requireActivity()))
        builder.setView(dialogViewBinding.root)
        builder.setCancelable(false)
        val alertDialog: AlertDialog = builder.create()
        dialogViewBinding.tvDialogTitle.setText(R.string.label_set_play_group_name)
        if (!TextUtils.isEmpty(groupName)) {
            dialogViewBinding.edtGroupName.setText(groupName)
        }
        dialogViewBinding.btnDialogCancel.setOnClickListener(
            ViewClickListener { view1: View? ->
                alertDialog.dismiss()
                if (TextUtils.isEmpty(
                        viewModel.getPlayGroup().playGroupName
                    )
                ) navController.popBackStack()
            }
        )
        dialogViewBinding.btnDialogSelectCategory.setOnClickListener(
            ViewClickListener { view1: View? ->
                showCategories(
                    alertDialogCategory,
                    dialogViewBindingCategory,
                    dialogViewBinding
                )
            }
        )
        dialogViewBinding.btnDialogSelectLanguage.setOnClickListener(
            ViewClickListener { view1: View? ->
                showAlert(
                    getString(R.string.message_language_selection)
                ) { v: View? ->
                    showLanguages(
                        alertDialogLanguage,
                        dialogViewBindingLanguage,
                        dialogViewBinding
                    )
                }
            }
        )
        dialogViewBinding.btnDialogOk.setOnClickListener(ViewClickListener(View.OnClickListener { view12: View? ->
            if (singleChoiceAdapter!!.selected != null && singleChoiceLanguageAdapter!!.selected != null) {
                val name: String =
                    dialogViewBinding.edtGroupName.text.toString().trim({ it <= ' ' })
                if (!TextUtils.isEmpty(name) && !AppUtilities.hasSpecialChars(name)) {
                    binding.tvGroupName.text = name
                    binding.tvGroupNameHint.text = String.format(
                        "%s O-Club",
                        singleChoiceAdapter!!.selected!!.categoryName
                    )
                    if (!viewModel.getPlayGroup().isNew) {
                        viewModel.updatePlayGroupName(
                            viewModel.getPlayGroup().playGroupId,
                            name,
                            singleChoiceAdapter!!.selected!!.id.toString(),
                            singleChoiceLanguageAdapter!!.selected!!.id.toString()
                        )
                            .observe(
                                viewLifecycleOwner
                            ) { response: ApiResponse<StandardResponse> ->
                                if (response.loading) {
                                    showProgress()
                                } else if (response.isSuccess && (response.body != null) && response.body.errorCode
                                        .equals("000", ignoreCase = true)
                                ) {
                                    hideProgress()
                                    // init();
                                    viewModel.getPlayGroup().playGroupName = name
                                    viewModel.getPlayGroup().playGroupCategoryName =
                                        singleChoiceAdapter!!.selected!!.categoryName
                                    viewModel.getPlayGroup().playGroupLanguageId =
                                        singleChoiceLanguageAdapter!!.selected!!.id
                                            .toString()
                                    viewModel.getPlayGroup().playGroupLanguageName =
                                        singleChoiceLanguageAdapter!!.selected!!
                                            .languageName
                                } else {
                                    hideProgress()
                                    showAlert(response.errorMessage)
                                }
                            }
                    } else {
                        createNewGroup(
                            singleChoiceAdapter!!.selected!!.id.toString(),
                            singleChoiceAdapter!!.selected!!.categoryName,
                            singleChoiceLanguageAdapter!!.selected!!.id.toString(),
                            singleChoiceLanguageAdapter!!.selected!!.languageName!!
                        )
                    }
                    alertDialog.dismiss()
                } else {
                    showAlert("Please enter a valid O-Club name")
                }
            } else {
                if (singleChoiceAdapter!!.selected == null) {
                    showAlert("Please select O-Club category")
                } else if (singleChoiceLanguageAdapter!!.selected == null) {
                    showAlert("Please select O-Club questions language")
                }
            }
        }))
        alertDialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        getCategories(alertDialog, dialogViewBinding)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setPlayGroup(null)
    }

    private fun getUserLinkInfo(playGroupId: String) {
        viewModel.getUserLinkInfo("PlayGroup", playGroupId, "23", "").observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetUserLinkInfoResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                    shortLink(
//                        apiResponse.body.title,
//                        apiResponse.body.linkUserId,
//                        apiResponse.body.userLink,
//                        apiResponse.body.linkType,
//                        apiResponse.body.activityImageUrl,
//                        apiResponse.body.activityText
//                    )
                    if (apiResponse.body.linkSharingDisabledMessage != "") {
                        showAlert(apiResponse.body.linkSharingDisabledMessage);
                    } else {
                        copyLink(apiResponse.body.shortLink!!);
                    }
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

//    private fun shortLink(
//        title: String?,
//        linkUserId: String?,
//        linkMsg: String?,
//        linkType: String?,
//        activityImageUrl: String?,
//        actvityText: String?
//    ) {
//        showProgress()
//        var imageUrl: String? = ""
//        val description = if (linkType.equals("Card", ignoreCase = true)) {
//            "Fun cards to share with friends"
//        } else if (linkType.equals("1toM", ignoreCase = true)) {
//            "Fun questions to ask friends"
//        } else {
//            "A place for good friends"
//        }
//        if (!TextUtils.isEmpty(activityImageUrl)) {
//            imageUrl = activityImageUrl
//        }
//        val titleSocial: String = actvityText ?: ""
//        val builderSocialMeta: SocialMetaTagParameters = SocialMetaTagParameters.Builder()
//            .setTitle(titleSocial)
//            .setImageUrl(Uri.parse(imageUrl))
//            .setDescription(description)
//            .build()
//        val navigationInfoParameters: NavigationInfoParameters.Builder =
//            NavigationInfoParameters.Builder()
//        navigationInfoParameters.setForcedRedirectEnabled(true)
//        FirebaseDynamicLinks.getInstance().createDynamicLink()
//            .setLink(Uri.parse("https://www.onourem.com/?linkUserId=" + linkUserId + "&linkType=" + linkType))
//            .setDomainUriPrefix("https://e859h.app.goo.gl")
//            .setAndroidParameters(
//                AndroidParameters.Builder("com.onourem.android.activity") //                                .setMinimumVersion(125)
//                    .build()
//            )
//            .setSocialMetaTagParameters(builderSocialMeta)
//            .setIosParameters(
//                IosParameters.Builder("com.onourem.onoureminternet")
//                    .setAppStoreId("1218240628") //                                .setMinimumVersion("1.0.1")
//                    .build()
//            ) // Set parameters
//            // ...
//            .setNavigationInfoParameters(navigationInfoParameters.build())
//            .buildShortDynamicLink()
//            .addOnCompleteListener(
//                requireActivity()
//            ) { task: Task<ShortDynamicLink> ->
//                if (task.isSuccessful) {
//                    // Short link created
//                    val shortLink: Uri = Objects.requireNonNull(task.result).shortLink
//                    //Log.d("shortLink", shortLink.toString());
//                    val shareMessage: String = linkMsg + " \n" + shortLink.toString()
//                    copyLink(shareMessage, linkUserId ?: "", shortLink.toString())
//                } else {
//                    // Error
//                    // ...
//                }
//            }
//    }

    private fun copyLink(link: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(binding.root.context)
        val dialogBinding: DialogUserLinkBinding =
            DialogUserLinkBinding.inflate(LayoutInflater.from(requireActivity()))
        builder.setView(dialogBinding.root)
        val alertDialog: AlertDialog = builder.create()
        dialogBinding.tvDialogTitle.text = "O-Club Invite Link"
        enableScroll(dialogBinding.tvUserLink)
        dialogBinding.tvUserLink.setTextIsSelectable(true)
        if (!TextUtils.isEmpty(link)) {
            dialogBinding.tvUserLink.text = link
        }
        dialogBinding.btnDialogCancel.setOnClickListener(ViewClickListener { view1: View? ->
            alertDialog.dismiss()
            navController.popBackStack()
            hideProgress()
        })
        dialogBinding.btnDialogOk.setOnClickListener(ViewClickListener { view12: View? ->
            if (!dialogBinding.tvUserLink.text.toString().equals("", ignoreCase = true)) {
                val myClip: ClipData = ClipData.newPlainText("text", link)
                (requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?)?.setPrimaryClip(
                    myClip
                )

                Toast.makeText(
                    requireActivity(),
                    "Link copied..",
                    Toast.LENGTH_SHORT
                ).show()

                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
                shareIntent.putExtra(Intent.EXTRA_TEXT, link)
                try {
                    fragmentContext.startActivity(Intent.createChooser(shareIntent, "Choose One"))
                } catch (ex : ActivityNotFoundException) {
                    Toast.makeText(fragmentContext,"NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
                }
                alertDialog.dismiss()
                navController.popBackStack()

//                dashboardViewModel!!.updateAppShortLink(linkUserId, shortLink).observe(
//                    viewLifecycleOwner
//                ) { response ->
//                    if (response.loading) {
//                        showProgress()
//                    } else if (response.isSuccess && response.body != null) {
//                        hideProgress()
//                        if (response.body.errorCode.equals("000", ignoreCase = true)) {
//                            Toast.makeText(
//                                requireActivity(),
//                                "Link copied..",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            val shareIntent = Intent(Intent.ACTION_SEND)
//                            shareIntent.type = "text/plain"
//                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
//                            shareIntent.putExtra(Intent.EXTRA_TEXT, link)
//                            startActivity(Intent.createChooser(shareIntent, "Choose One"))
//                            alertDialog.dismiss()
//                            navController.popBackStack()
//                        } else {
//                            showAlert(response.body.errorMessage)
//                        }
//                    } else {
//                        hideProgress()
//                        showAlert(response.errorMessage)
//                    }
//                }
            }
        })
        alertDialog.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            alertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
        alertDialog.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun enableScroll(view: View) {
        if (view is TextView) {
            view.movementMethod = ScrollingMovementMethod()
        }
        view.setOnTouchListener { v: View, event: MotionEvent ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }
}


