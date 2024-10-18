package com.onourem.android.activity.ui.games.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.*
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.BottomSheetAskFriendsBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.adapters.MultiChoiceShareWithFriendsAdapter
import com.onourem.android.activity.ui.games.adapters.MultiChoiceShareWithGroupAdapter
import com.onourem.android.activity.ui.games.viewmodel.GamesReceiverViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.message.ConversationsViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.util.*
import javax.inject.Inject

class AskFriendsBottomSheetFragment :
    AbstractBaseBottomSheetBindingDialogFragment<GamesReceiverViewModel, BottomSheetAskFriendsBinding>(),
    View.OnClickListener {

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var groupPointsMap: HashMap<String, String>? = null
    private var friendsAdapter: MultiChoiceShareWithFriendsAdapter? = null
    private var groupsAdapter: MultiChoiceShareWithGroupAdapter? = null
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (binding.rgTabs.checkedRadioButtonId == R.id.btnGroups) {
                if (groupsAdapter != null) groupsAdapter!!.filter.filter(s)
            } else if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends) {
                if (friendsAdapter != null) friendsAdapter!!.filter.filter(s)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }
    private var loginDayActivityInfoList: LoginDayActivityInfoList? = null
    private var isActivityForNewQuestion = false
    private var questionViewModel: QuestionGamesViewModel? = null
    private var playGroupId: String? = null
    private var friendsList: MutableList<UserList>? = null
    private var groupList: MutableList<PlayGroup>? = null
    private var dashboardViewModel: DashboardViewModel? = null
    private var conversationsViewModel: ConversationsViewModel? = null
    public override fun viewModelType(): Class<GamesReceiverViewModel> {
        return GamesReceiverViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.bottom_sheet_ask_friends
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = ViewModelProvider(this, viewModelFactory)[DashboardViewModel::class.java]
        conversationsViewModel = ViewModelProvider(this, viewModelFactory)[ConversationsViewModel::class.java]
        val args = AskFriendsBottomSheetFragmentArgs.fromBundle(
            requireArguments()
        )
        playGroupId = args.playGroupId
        loginDayActivityInfoList = args.question
        isActivityForNewQuestion = args.isActivityForNewQuestion
        questionViewModel = ViewModelProvider(requireActivity(), viewModelFactory!!)[QuestionGamesViewModel::class.java]

//        viewModel.getLocalBlockUserList().observe(this, listResource -> {
//            if (listResource.getStatus() == Status.SUCCESS && listResource.getData() != null && !listResource.getData().isEmpty()) {
//                blockedUserIds = listResource.getData();
//            }
//        });
//        setCancelable(false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val response = preferenceHelper!!.getString(Constants.KEY_EXPRESSIONS_RESPONSE)
        if (!TextUtils.isEmpty(response)) {
            val dataResponse = Gson().fromJson(response, ExpressionDataResponse::class.java)
            groupPointsMap = dataResponse.appColorCO?.groupPointsMap
            setPrivacyBubbleCount()
        }
        init()
    }

    private fun setPrivacyBubbleCount() {
        if (groupPointsMap == null || groupPointsMap!!.isEmpty()) {
            binding.bubble.tvQuestionPoints.visibility = View.GONE
        } else {
            val points = groupPointsMap!!["oo"]
            if (points != null && groupPointsMap!!.containsKey("oo") && !TextUtils.isEmpty(points) && points.toInt() > 0) {
                val builder = SpannableStringBuilder().append(points)
                    .append("\n")
                    .append("Points")
                builder.setSpan(
                    RelativeSizeSpan(1.2f),
                    0, points.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                builder.setSpan(
                    RelativeSizeSpan(0.7f),
                    points.length, builder.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.bubble.tvQuestionPoints.text = builder
                binding.bubble.tvQuestionPoints.visibility = View.VISIBLE
            } else {
                binding.bubble.tvQuestionPoints.visibility = View.GONE
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        binding.btnFriends.setOnClickListener(this)
        binding.btnGroups.setOnClickListener(this)
        binding.btnOutside.setOnClickListener(this)
        friendsList = ArrayList()
        groupList = ArrayList()
        if (ActivityType.getActivityType(loginDayActivityInfoList?.activityType) == ActivityType.ONE_TO_MANY) {
            binding.btnGroups.performClick()
        } else {
            binding.btnGroups.isActivated = false
            binding.btnFriends.performClick()
            //            binding.btnGroups.setOnTouchListener((v, event) -> {
//
//                if (!binding.btnGroups.isEnabled()){
//                    showAlert("not one to many");
//                }
//                return false;
//            });
        }
        binding.btnCancel.setOnClickListener { v: View? -> navController.popBackStack() }
        binding.btnSubmit.setOnClickListener(ViewClickListener { v: View? ->
            if (binding.rgTabs.checkedRadioButtonId != R.id.btnOutside) {
                submit()
            }
        })
        binding.rvResult.layoutManager =
            LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        Objects.requireNonNull(binding.tilSearch.editText!!).addTextChangedListener(textWatcher)
        binding.cbSelectAll.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
            onCheckedChanged(
                buttonView,
                isChecked
            )
        }
    }

    private fun submit() {
        if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends && (friendsAdapter == null || friendsAdapter!!.selected.isEmpty())) {
            showAlert("You need to select at least one friend")
            return
        }
        if (binding.rgTabs.checkedRadioButtonId == R.id.btnGroups && (groupsAdapter == null || groupsAdapter!!.selected.isEmpty())) {
            showAlert("You need to select at least one group")
            return
        }
        val createGameActivityRequest = CreateGameActivityRequest()
        createGameActivityRequest.serviceName = "createGameActivity"
        createGameActivityRequest.screenId = "48"
        createGameActivityRequest.loginDay = loginDayActivityInfoList?.loginDay
        createGameActivityRequest.activityId = loginDayActivityInfoList?.activityId
        createGameActivityRequest.activityTypeId = loginDayActivityInfoList?.activityTypeId
        createGameActivityRequest.activityGameResponseId =
            loginDayActivityInfoList?.activityGameResponseId
        createGameActivityRequest.isActivityForNewQuestion =
            if (isActivityForNewQuestion) "Y" else "N"
        createGameActivityRequest.playGroupIds = ""
        createGameActivityRequest.userIds = ""
        if (binding.rgTabs.checkedRadioButtonId == R.id.btnGroups && groupsAdapter != null && !groupsAdapter!!.selected.isEmpty()) {
//            isGroupSelected = groupsAdapter.getSelected();
            createGameActivityRequest.playGroupIds = Utilities.getTokenSeparatedString(
                groupsAdapter!!.selected, ","
            )
            createGameActivityRequest.userIds = Utilities.getTokenSeparatedString(
                groupsAdapter!!.selected, ","
            )
        }
        if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends && !friendsAdapter!!.selected.isEmpty()) {
            createGameActivityRequest.userIds = Utilities.getTokenSeparatedString(
                friendsAdapter!!.selected, ","
            )
        }
        viewModel.createGameActivity(createGameActivityRequest).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GameActivityUpdateResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    hideProgress()
                    showAlert(apiResponse.body.message) { v12: View? ->
                        var isPlayGroupSelected = false
                        if (binding.rgTabs.checkedRadioButtonId == R.id.btnGroups && groupsAdapter != null && !groupsAdapter!!.selected.isEmpty()) {
                            for (group in groupsAdapter!!.selected) {
                                if (playGroupId.equals(group.playGroupId, ignoreCase = true)) {
                                    isPlayGroupSelected = true
                                    break
                                }
                            }
                        }
                        if (!TextUtils.isEmpty(apiResponse.body.activityTagStatus)) loginDayActivityInfoList?.activityTag =
                            apiResponse.body.activityTagStatus
                        if (!TextUtils.isEmpty(apiResponse.body.participationStatus)) loginDayActivityInfoList?.userParticipationStatus =
                            apiResponse.body.participationStatus
                        if (isActivityForNewQuestion || isPlayGroupSelected) {
                            try {
                                val clonedLoginDayActivityInfoList =
                                    loginDayActivityInfoList?.clone() as LoginDayActivityInfoList
                                clonedLoginDayActivityInfoList.activityTag = "Pending"
                                clonedLoginDayActivityInfoList.userParticipationStatus =
                                    "Pending"
                                questionViewModel!!.setGameActivityUpdateStatus(
                                    Pair.create(
                                        playGroupId,
                                        clonedLoginDayActivityInfoList
                                    )
                                )
                            } catch (e: CloneNotSupportedException) {
                                e.printStackTrace()
                            }
                        } else {
                            viewModel.setGameActivityUpdateResponseLiveData(apiResponse.body)
                        }

                        if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends && friendsAdapter != null && friendsAdapter!!.selected.isNotEmpty()) {
                            (fragmentContext as DashboardActivity).sendMultipleMessages(friendsAdapter!!.selected, loginDayActivityInfoList!!)
                        }
                        hideProgress()
                        navController.popBackStack()
                    }
                } else {
                    hideProgress()
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
                        AppUtilities.showLog("Network Error", "createGameActivity")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "createGameActivity",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btnGroups) {
            binding.tilSearch.hint = getString(R.string.hint_enter_group_name_to_search)
            binding.cbSelectAll.isChecked = false
            binding.tilSearch.visibility = View.VISIBLE
            binding.tilSearch.clearFocus()
            if (ActivityType.getActivityType(loginDayActivityInfoList?.activityType) != ActivityType.ONE_TO_MANY) {
                binding.btnGroups.isActivated = false
                binding.btnFriends.isChecked = true
                showAlert("This question is only for individual friends and not for O-Clubs.")
            } else {
                binding.btnFriends.isActivated = true
                viewModel.getUserActivityPlayGroupsNames(loginDayActivityInfoList?.activityId!!)
                    .observe(
                        viewLifecycleOwner
                    ) { apiResponse: ApiResponse<PlayGroupsResponse> ->
                        handleGroupResponse(apiResponse)
                    }
            }
        } else if (view.id == R.id.btnFriends) {
            binding.cbSelectAll.isChecked = false
            binding.tilSearch.clearFocus()
            binding.tilSearch.hint = getString(R.string.hint_enter_name_to_search)
            binding.tilSearch.visibility = View.VISIBLE
            binding.btnGroups.isActivated =
                ActivityType.getActivityType(loginDayActivityInfoList?.activityType) == ActivityType.ONE_TO_MANY
            viewModel.friendList.observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<UserListResponse> ->
                handleFriendsListResponse(apiResponse)
            }
        } else if (view.id == R.id.btnOutside) {
            binding.cbSelectAll.isChecked = false
            binding.cbSelectAll.visibility = View.INVISIBLE
            binding.tilSearch.visibility = View.INVISIBLE
            binding.rvResult.visibility = View.INVISIBLE
            inviteFriends(true)
        }
    }

    @SuppressLint("DefaultLocale")
    private fun handleGroupResponse(apiResponse: ApiResponse<PlayGroupsResponse>) {
        if (apiResponse.loading) {
            showProgress()
        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            hideProgress()
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                if (apiResponse.body.playGroup != null && !apiResponse.body.playGroup.isEmpty()) {
                    groupList!!.clear()
                    groupList!!.addAll(apiResponse.body.playGroup)
                    setPlayGroupsAdapter(apiResponse.body.playGroup)
                    binding.cbSelectAll.text =
                        String.format("Select All Groups (%d)", apiResponse.body.playGroup.size)
                } else {
                    val errorMessage =
                        "You don't have any O-Clubs to post question on. Create one under Question Games by pressing Plus button and start posting questions"
                    binding.tvMessage.text = errorMessage
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.rvResult.adapter = null
                    binding.cbSelectAll.visibility = View.GONE
                }
            } else {
                hideProgress()
                showAlert(apiResponse.body.errorMessage)
            }
        } else {
            hideProgress()
            if (apiResponse.errorMessage != null
                && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                        || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
            ) {
                if (BuildConfig.DEBUG) {
                    AppUtilities.showLog("Network Error", "getUserActivityPlayGroupsNames")
                }
                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                    "getUserActivityPlayGroupsNames",
                    apiResponse.code.toString()
                )
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun handleFriendsListResponse(apiResponse: ApiResponse<UserListResponse>) {
        if (apiResponse.loading) {
            showProgress()
        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            hideProgress()
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                if (apiResponse.body.userList != null && !apiResponse.body.userList.isEmpty()) {
                    val list = apiResponse.body.userList
                    friendsList!!.clear()
                    friendsList!!.addAll(apiResponse.body.userList)
                    //BlockedUsersUtility.filterBlockedUsers(blockedUserIds, list);
                    setMyFriendsAdapter(list)
                    binding.cbSelectAll.text =
                        String.format("Select All Friends (%d)", apiResponse.body.userList.size)
                } else {
                    val errorMessage =
                        "You have not made any friends on Onourem yet. Click on the search icon to start adding friends."
                    binding.tvMessage.text = errorMessage
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.rvResult.adapter = null
                    binding.tilSearch.visibility = View.GONE
                    binding.cbSelectAll.visibility = View.GONE
                }
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
                    AppUtilities.showLog("Network Error", "getFriendList")
                }
                (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                    "getFriendList",
                    apiResponse.code.toString()
                )
            }
        }
    }

    private fun inviteFriends(fromOutSideButton : Boolean) {
        viewModel.getUserLinkInfo(
            loginDayActivityInfoList?.activityType,
            loginDayActivityInfoList?.activityId,
            "48",
            loginDayActivityInfoList?.activityText
        ).observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetUserLinkInfoResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (fromOutSideButton) {
                        if (!TextUtils.isEmpty(loginDayActivityInfoList!!.questionVideoUrl)) {
                            share(
                                apiResponse.body.title,
                                apiResponse.body.shortLink,
                                loginDayActivityInfoList!!.questionVideoUrl,
                                "2"
                            )
                        } else if (!TextUtils.isEmpty(loginDayActivityInfoList!!.activityImageLargeUrl)) {
                            share(
                                apiResponse.body.title,
                                apiResponse.body.shortLink,
                                loginDayActivityInfoList!!.activityImageLargeUrl,
                                "1"
                            )
                        } else {
                            share(apiResponse.body.title, apiResponse.body.shortLink, "", "-1")
                        }
                    } else {
//                        (fragmentContext as DashboardActivity).sendMultipleMessages(friendsAdapter!!.selected, apiResponse)
//                        conversationsViewModel?.setSendPendingMessagesForInvite(kotlin.Pair(friendsAdapter!!.selected, apiResponse.body))
//                        hideProgress()
//                        navController.popBackStack()
                    }

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
                        AppUtilities.showLog("Network Error", "getUserLinkInfo")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "getUserLinkInfo",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    //    private void shortLink(String title, String linkUserId, String linkMsg, String linkType, String activityImageUrl, String activityText) {
    //        showProgress();
    //        String titleSocial;
    //        String description;
    //        String imageUrl = "";
    //
    //        if (linkType.equalsIgnoreCase("Card")) {
    //            description = "Fun cards to share with friends";
    //        } else if (linkType.equalsIgnoreCase("1toM")) {
    //            description = "Fun questions to ask friends";
    //        } else {
    //            description = "A place for good friends";
    //        }
    //
    //        if (!TextUtils.isEmpty(activityImageUrl)) {
    //            imageUrl = activityImageUrl;
    //        }
    //
    //        titleSocial = activityText;
    //        DynamicLink.SocialMetaTagParameters builderSocialMeta = new DynamicLink.SocialMetaTagParameters.Builder()
    //                .setTitle(titleSocial)
    //                .setImageUrl(Uri.parse(imageUrl))
    //                .setDescription(description)
    //                .build();
    //
    //        DynamicLink.NavigationInfoParameters.Builder navigationInfoParameters = new DynamicLink.NavigationInfoParameters.Builder();
    //        navigationInfoParameters.setForcedRedirectEnabled(true);
    //        FirebaseDynamicLinks.getInstance().createDynamicLink()
    //                .setLink(Uri.parse("https://www.onourem.com/?linkUserId=" + linkUserId + "&linkType=" + linkType))
    //                .setDomainUriPrefix("https://e859h.app.goo.gl")
    //                .setAndroidParameters(
    //                        new DynamicLink.AndroidParameters.Builder("com.onourem.android.activity")
    ////                                .setMinimumVersion(125)
    //                                .build())
    //                .setSocialMetaTagParameters(builderSocialMeta)
    //                .setIosParameters(
    //                        new DynamicLink.IosParameters.Builder("com.onourem.onoureminternet")
    //                                .setAppStoreId("1218240628")
    ////                                .setMinimumVersion("1.0.1")
    //                                .build())
    //                // Set parameters
    //                // ...
    //                .setNavigationInfoParameters(navigationInfoParameters.build())
    //                .buildShortDynamicLink()
    //                .addOnCompleteListener(requireActivity(), task -> {
    //                    if (task.isSuccessful()) {
    //                        // Short link created
    //                        Uri shortLink = Objects.requireNonNull(task.getResult()).getShortLink();
    //                        //Log.d("shortLink", shortLink.toString());
    //
    //
    //                        dashboardViewModel!!.updateAppShortLink(linkUserId, shortLink.toString()).observe(getViewLifecycleOwner(), response -> {
    //                            if (response.loading) {
    //                                showProgress();
    //                            } else if (response.isSuccess() && response.body != null) {
    //                                hideProgress();
    //                                if (response.body.getErrorCode().equalsIgnoreCase("000")) {
    ////                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    ////                                    shareIntent.setType("text/plain");
    ////                                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem");
    ////                                    String shareMessage = linkMsg + " \n" + shortLink.toString();
    ////                                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
    ////                                    startActivityForResult(Intent.createChooser(shareIntent, title), 1212);
    ////
    //
    //                                } else {
    //                                    showAlert(response.body.getErrorMessage());
    //                                }
    //                            } else {
    //                                hideProgress();
    //                                showAlert(response.errorMessage);
    //                                if (response.errorMessage != null
    //                                        && (response.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
    //                                        || response.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))) {
    //                                    if (BuildConfig.DEBUG) {
    //                                        AppUtilities.showLog("Network Error", "updateAppShortLink");
    //                                    }
    //                                    if (getFragmentContext() != null)
    //                                        ((DashboardActivity) getFragmentContext()).addNetworkErrorUserInfo("updateAppShortLink", String.valueOf(response.code));
    //                                }
    //                            }
    //                        });
    //
    //                    }
    //                });
    //
    //    }
    private fun share(title: String?, linkMsg: String?, mediaUrl: String?, mediaType: String?) {
        if (mediaUrl !=  null && !mediaUrl.equals("", ignoreCase = true)) {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavMediaShare(
                    title?:"",
                    linkMsg?:"",
                    mediaUrl,
                    mediaType?:""
                )
            )
        } else {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
            shareIntent.putExtra(Intent.EXTRA_TEXT, linkMsg)
            try {
                fragmentContext.startActivity(Intent.createChooser(shareIntent,title));
            } catch (ex : ActivityNotFoundException) {
                Toast.makeText(fragmentContext,"NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setPlayGroupsAdapter(playGroups: List<PlayGroup>) {
        binding.tvMessage.visibility = View.INVISIBLE
        binding.rvResult.visibility = View.VISIBLE
        groupsAdapter = MultiChoiceShareWithGroupAdapter(playGroups){
            val position = it.first
            val clickedItem = it.second
            clickedItem.isSelected = !clickedItem.isSelected
            groupsAdapter?.notifyItemChanged(position, clickedItem)
        }
        binding.rvResult.adapter = groupsAdapter
    }

    private fun setMyFriendsAdapter(userList: List<UserList>) {
        binding.tvMessage.visibility = View.INVISIBLE
        binding.rvResult.visibility = View.VISIBLE
        friendsAdapter = MultiChoiceShareWithFriendsAdapter(userList)
        binding.rvResult.adapter = friendsAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1212) {
            navController.popBackStack()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        questionViewModel!!.setRefreshShowBadges(true)
    }

    private fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            if (binding.rgTabs.checkedRadioButtonId == R.id.btnGroups) {
                for (playGroup in groupList!!) {
                    playGroup.isSelected = true
                }
                setPlayGroupsAdapter(groupList!!)
            } else if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends) {
                for (userList in friendsList!!) {
                    userList.isSelected = true
                }
                setMyFriendsAdapter(friendsList!!)
            }
        } else {
            if (binding.rgTabs.checkedRadioButtonId == R.id.btnGroups) {
                for (playGroup in groupList!!) {
                    playGroup.isSelected = false
                }
                setPlayGroupsAdapter(groupList!!)
            } else if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends) {
                for (userList in friendsList!!) {
                    userList.isSelected = false
                }
                setMyFriendsAdapter(friendsList!!)
            }
        }
    }
}