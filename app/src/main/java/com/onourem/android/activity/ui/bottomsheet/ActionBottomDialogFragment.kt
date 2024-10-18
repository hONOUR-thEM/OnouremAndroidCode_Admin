package com.onourem.android.activity.ui.bottomsheet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Pair
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink.*
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogBottomSheetBinding
import com.onourem.android.activity.databinding.DialogUserLinkBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.util.*

class ActionBottomDialogFragment :
    AbstractBaseBottomSheetBindingDialogFragment<QuestionGamesViewModel, DialogBottomSheetBinding>(),
    OnItemClickListener<Action<Any?>> {
    private var dashboardViewModel: DashboardViewModel? = null
    override fun layoutResource(): Int {
        return R.layout.dialog_bottom_sheet
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = ViewModelProvider(this, viewModelFactory!!)[DashboardViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        var actionBottomDialogFragmentArgs: ActionBottomDialogFragmentArgs? = null
        if (arguments != null) {
            actionBottomDialogFragmentArgs =
                ActionBottomDialogFragmentArgs.fromBundle(requireArguments())
        }
        binding.rvActions.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvActions.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                DividerItemDecoration.VERTICAL
            )
        )
        if (actionBottomDialogFragmentArgs != null) {
            val actions: MutableList<Action<Any?>> =
                actionBottomDialogFragmentArgs.actions.getParcelableArrayList(
                    Constants.KEY_BOTTOM_SHEET_ACTIONS
                )!!
            actions.add(
                actions.size,
                Action(
                    getString(R.string.label_dismiss),
                    R.color.color_black,
                    ActionType.DISMISS,
                    null
                )
            )
            binding.rvActions.adapter = ActionBottomSheetDialogAdapter(actions, this)
        }
        if (actionBottomDialogFragmentArgs != null) {
            binding.tvTitle.text = actionBottomDialogFragmentArgs.title
        }
    }

    override fun viewModelType(): Class<QuestionGamesViewModel> {
        return QuestionGamesViewModel::class.java
    }

    private fun getUserLinkInfo(playGroupId: String) {
        viewModel.getUserLinkInfo("PlayGroup", playGroupId, "23", "")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetUserLinkInfoResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                        if (apiResponse.body.linkSharingDisabledMessage != "") {
                            showAlert(apiResponse.body.linkSharingDisabledMessage);
                        } else {
                            copyLink(apiResponse.body.shortLink!!);
                        }
//                        shortLink(
//                            apiResponse.body.title,
//                            apiResponse.body.linkUserId,
//                            apiResponse.body.userLink,
//                            apiResponse.body.linkType,
//                            apiResponse.body.activityImageUrl,
//                            apiResponse.body.activityText
//                        )
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

//    private fun shortLink(
//        title: String?,
//        linkUserId: String?,
//        linkMsg: String?,
//        linkType: String?,
//        activityImageUrl: String?,
//        activityText: String?
//    ) {
//        showProgress()
//        var imageUrl = ""
//        val description: String = if (linkType.equals("Card", ignoreCase = true)) {
//            "Fun cards to share with friends"
//        } else if (linkType.equals("1toM", ignoreCase = true)) {
//            "Fun questions to ask friends"
//        } else {
//            "A place for good friends"
//        }
//        if (!TextUtils.isEmpty(activityImageUrl)) {
//            imageUrl = activityImageUrl?:""
//        }
//        val titleSocial: String = activityText?:""
//        val builderSocialMeta = SocialMetaTagParameters.Builder()
//            .setTitle(titleSocial)
//            .setImageUrl(Uri.parse(imageUrl))
//            .setDescription(description)
//            .build()
//        val navigationInfoParameters = NavigationInfoParameters.Builder()
//        navigationInfoParameters.setForcedRedirectEnabled(true)
//        FirebaseDynamicLinks.getInstance().createDynamicLink()
//            .setLink(Uri.parse("https://www.onourem.com/?linkUserId=$linkUserId&linkType=$linkType"))
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
//            .addOnCompleteListener(requireActivity()) { task: Task<ShortDynamicLink> ->
//                if (task.isSuccessful) {
//                    // Short link created
//                    val shortLink = Objects.requireNonNull(task.result).shortLink
//                    val shareMessage = "$linkMsg \n$shortLink"
//                    copyLink(shareMessage, linkUserId?:"", shortLink.toString())
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
            if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }

    override fun onItemClick(action: Action<Any?>) {
        when (action.actionType) {
            ActionType.SEE_PLAY_GROUP_MEMBERS, ActionType.ADD_REMOVE_PLAY_GROUP_MEMBERS -> {
                viewModel.setPlayGroup(action.data as PlayGroup)
                navController.navigate(ActionBottomDialogFragmentDirections.actionNavBootomSheetToNavPlayGroupMember())
            }
            ActionType.EXIT_PLAY_GROUP ->                 //exitGroup(action);
                dismissAndRefresh(action)
            ActionType.PLAY_GROUP_INVITE_LINK -> {
                val playGroup = action.data as PlayGroup
                getUserLinkInfo(playGroup.playGroupId?:"")
            }
            ActionType.DISMISS -> navController.popBackStack()
            ActionType.UN_FRIEND -> removeFriend(action)
            ActionType.MAKE_PLAY_GROUP_ADMIN -> changeAdminState(action, "Y")
            ActionType.REMOVE_AS_PLAY_GROUP_ADMIN -> changeAdminState(action, "N")
            ActionType.REMOVE_FROM_PLAY_GROUP -> removeFromPlayGroup(action)
            ActionType.DELETE_PRIVACY_GROUP -> deletePrivacyGroup(action)
            ActionType.ADD_REMOVE_PRIVACY_GROUP_MEMBERS -> //                viewModel.setPrivacyGroup((PrivacyGroup) action.getData());
//                navController.navigate(ActionBottomDialogFragmentDirections.actionNavBootomSheetToNavPrivacyGroupMember());
//                navController.popBackStack();
                dismissAndRefresh(action)
            ActionType.REMOVE_FROM_PRIVACY_GROUP -> removeFromPrivacyGroup(action)
            ActionType.SOLO_GAMES, ActionType.CREATE_QUESTION, ActionType.QUESTION_PLAYED, ActionType.QUESTIONS_CREATED, ActionType.FRIENDS_PLAYING -> dismissAndRefresh(
                action
            )
            ActionType.REMOVE_FROM_WATCHLIST -> cancelWatchListRequest(action)
            ActionType.REJECT_FRIEND_REQUEST, ActionType.REJECT_WATCHLIST_FRIEND_REQUEST -> {}
            ActionType.EDIT_QUESTION, ActionType.DELETE_QUESTION, ActionType.DELETE_QUESTION_CONFIRMATION, ActionType.IGNORE_QUESTION, ActionType.IGNORE_QUESTION_CONFIRMATION -> {}
            else -> {}
        }
    }

    private fun cancelWatchListRequest(action: Action<*>) {
        val userWatchList = action.data as UserWatchList
        viewModel.cancelWatchListRequest(userWatchList.userId)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        dismissAndRefresh(action)
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
                            AppUtilities.showLog("Network Error", "cancelWatchListRequest")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "cancelWatchListRequest",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun deletePrivacyGroup(action: Action<*>) {
        val privacyGroup = action.data as PrivacyGroup
        viewModel.deleteGroup(privacyGroup.groupId.toString())
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetAllGroupsResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        dismissAndRefresh(action)
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
                            AppUtilities.showLog("Network Error", "deleteGroup")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "deleteGroup",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun removeFromPlayGroup(action: Action<*>) {
        val pair2 = action.data as Pair<PlayGroup, PlayGroupUserInfoList>
        viewModel.removePlayGroupUser(pair2.first.playGroupId, pair2.second.userId?:"").observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<StandardResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    dismissAndRefresh(action)
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
                        AppUtilities.showLog("Network Error", "removePlayGroupUser")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "removePlayGroupUser",
                        apiResponse.code.toString()
                    )
                }
            }
        }
    }

    private fun removeFromPrivacyGroup(action: Action<*>) {
        val pair2 = action.data as Pair<PrivacyGroup, GroupMember>
        viewModel.removePrivacyGroupUser(pair2.first.groupId.toString(), pair2.second.userId)
            .observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        dismissAndRefresh(action)
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
                            AppUtilities.showLog("Network Error", "removePrivacyGroupUser")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "removePrivacyGroupUser",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun changeAdminState(action: Action<*>, adminState: String) {
        val pair = action.data as Pair<PlayGroup, PlayGroupUserInfoList>
        viewModel.addPlayGroupAdmin(pair.first.playGroupId, adminState, pair.second.userId)
            .observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        pair.second.isUserAdmin = adminState
                        dismissAndRefresh(action)
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
                            AppUtilities.showLog("Network Error", "addPlayGroupAdmin")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "addPlayGroupAdmin",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun removeFriend(action: Action<*>) {
        val playGroupUserInfoList = action.data as PlayGroupUserInfoList
        viewModel.removeFriend(playGroupUserInfoList.userId, "24")
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserActionStandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        dismissAndRefresh(action)
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
                            AppUtilities.showLog("Network Error", "removeFriend")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "removeFriend",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    //    private void exitGroup(Action action) {
    //        Pair<PlayGroup, String> pair = (Pair<PlayGroup, String>) action.getData();
    //        viewModel.exitPlayGroupUser(pair.first.getPlayGroupId(), pair.second).observe(this, apiResponse -> {
    //            if (apiResponse.loading) {
    //                showProgress();
    //            } else if (apiResponse.isSuccess() && apiResponse.body != null) {
    //                hideProgress();
    //                if (apiResponse.body.getErrorCode().equalsIgnoreCase("000")) {
    //                    dismissAndRefresh(action);
    //                } else {
    //                    showAlert(getString(R.string.label_network_error), apiResponse.body.getErrorMessage());
    //                }
    //            } else {
    //                hideProgress();
    //                showAlert(getString(R.string.label_network_error), apiResponse.errorMessage);
    //            }
    //        });
    //    }
    private fun dismissAndRefresh(action: Action<*>) {
        if (action.actionType != ActionType.DISMISS) viewModel.actionPerformed(action)
        navController.popBackStack()
    }
}