package com.onourem.android.activity.ui.message.share

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.*
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.BottomSheetShareContentBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.adapters.MultiChoiceShareWithFriendsAdapter
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.message.ConversationsViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.util.*
import javax.inject.Inject


class ShareContentSheetFragment :
    AbstractBaseBottomSheetBindingDialogFragment<ConversationsViewModel, BottomSheetShareContentBinding>(),
    View.OnClickListener {
    private lateinit var args: ShareContentSheetFragmentArgs
    private var counter = 0
    private var shareLauncher: ActivityResultLauncher<Intent>? = null


    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var groupPointsMap: HashMap<String, String>? = null
    private var friendsAdapter: MultiChoiceShareWithFriendsAdapter? = null
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends) {
                if (friendsAdapter != null) friendsAdapter!!.filter.filter(s)
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }
    private var questionViewModel: QuestionGamesViewModel? = null
    private var friendsList: MutableList<UserList>? = null
    private var dashboardViewModel: DashboardViewModel? = null
    public override fun viewModelType(): Class<ConversationsViewModel> {
        return ConversationsViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.bottom_sheet_share_content
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = ViewModelProvider(this, viewModelFactory)[DashboardViewModel::class.java]
        questionViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[QuestionGamesViewModel::class.java]

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        args = ShareContentSheetFragmentArgs.fromBundle(requireArguments())

        binding.btnFriends.setOnClickListener(this)
        binding.btnOutside.setOnClickListener(this)
        friendsList = ArrayList()
        binding.btnFriends.performClick()

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

        counter = 0
        showProgress()

        if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends && friendsAdapter!!.selected.isNotEmpty()) {

            (fragmentContext as DashboardActivity).sendMultipleMessages(friendsAdapter!!.selected, args)
//            viewModel.setSendPendingMessagesForArgs(Pair(friendsAdapter!!.selected, args))
            dismiss()
        }

    }

    override fun onClick(view: View) {
        if (view.id == R.id.btnFriends) {
            binding.cbSelectAll.isChecked = false
            binding.tilSearch.clearFocus()
            binding.tilSearch.hint = getString(R.string.hint_enter_name_to_search)
            binding.tilSearch.visibility = View.VISIBLE
            questionViewModel!!.friendList.observe(viewLifecycleOwner) { apiResponse: ApiResponse<UserListResponse> ->
                handleFriendsListResponse(apiResponse)
            }
        } else if (view.id == R.id.btnOutside) {
            binding.cbSelectAll.isChecked = false
            binding.cbSelectAll.visibility = View.INVISIBLE
            binding.tilSearch.visibility = View.INVISIBLE
            binding.rvResult.visibility = View.INVISIBLE
            inviteFriends()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun handleFriendsListResponse(apiResponse: ApiResponse<UserListResponse>) {
        if (apiResponse.loading) {
            showProgress()
        } else if (apiResponse.isSuccess && apiResponse.body != null) {
            hideProgress()
            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                if (apiResponse.body.userList.isNotEmpty()) {
                    val list = apiResponse.body.userList
                    friendsList!!.clear()
                    friendsList!!.addAll(apiResponse.body.userList)
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

    private fun inviteFriends() {

        if (!args.mediaUrl.equals("", ignoreCase = true)) {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavMediaShare(
                    args.title,
                    args.linkMsg,
                    args.mediaUrl,
                    args.mediaType
                )
            )
        } else {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
            shareIntent.putExtra(Intent.EXTRA_TEXT, args.linkMsg)
            try {
                // Create a chooser intent to show a dialog with available sharing options
                val chooserIntent = Intent.createChooser(shareIntent, "Share via")

                // Launch the chooser intent using the ActivityResultLauncher
                shareLauncher?.launch(chooserIntent)
//                fragmentContext.startActivity(Intent.createChooser(shareIntent, title));
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(fragmentContext, "NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
            }
        }


//        viewModel.getUserLinkInfo(
//            loginDayActivityInfoList!!.activityType,
//            loginDayActivityInfoList!!.activityId,
//            "48",
//            loginDayActivityInfoList!!.activityText
//        ).observe(
//            viewLifecycleOwner,
//            Observer<ApiResponse<GetUserLinkInfoResponse>> { apiResponse: ApiResponse<GetUserLinkInfoResponse> ->
//                if (apiResponse.loading) {
//                    showProgress()
//                } else if (apiResponse.isSuccess && apiResponse.body != null) {
//                    hideProgress()
//                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                        if (!TextUtils.isEmpty(loginDayActivityInfoList!!.questionVideoUrl)) {
//                            share(
//                                apiResponse.body.title,
//                                apiResponse.body.shortLink,
//                                loginDayActivityInfoList!!.questionVideoUrl,
//                                "2"
//                            )
//                        } else if (!TextUtils.isEmpty(loginDayActivityInfoList!!.activityImageLargeUrl)) {
//                            share(
//                                apiResponse.body.title,
//                                apiResponse.body.shortLink,
//                                loginDayActivityInfoList!!.activityImageLargeUrl,
//                                "1"
//                            )
//                        } else {
//                            share(apiResponse.body.title, apiResponse.body.shortLink, "", "-1")
//                        }
//                    } else {
//                        showAlert(apiResponse.body.errorMessage)
//                    }
//                } else {
//                    hideProgress()
//                    showAlert(apiResponse.errorMessage)
//                    if (apiResponse.errorMessage != null
//                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
//                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
//                    ) {
//                        if (BuildConfig.DEBUG) {
//                            AppUtilities.showLog("Network Error", "getUserLinkInfo")
//                        }
//                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
//                            "getUserLinkInfo",
//                            apiResponse.code.toString()
//                        )
//                    }
//                }
//            })
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
        if (mediaUrl != null && !mediaUrl.equals("", ignoreCase = true)) {
            navController.navigate(
                MobileNavigationDirections.actionGlobalNavMediaShare(
                    title ?: "",
                    linkMsg ?: "",
                    mediaUrl,
                    mediaType ?: ""
                )
            )
        } else {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
            shareIntent.putExtra(Intent.EXTRA_TEXT, linkMsg)
            try {
                // Create a chooser intent to show a dialog with available sharing options
                val chooserIntent = Intent.createChooser(shareIntent, "Share via")

                // Launch the chooser intent using the ActivityResultLauncher
                shareLauncher?.launch(chooserIntent)
//                fragmentContext.startActivity(Intent.createChooser(shareIntent, title));
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(fragmentContext, "NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setMyFriendsAdapter(userList: List<UserList>) {
        binding.tvMessage.visibility = View.INVISIBLE
        binding.rvResult.visibility = View.VISIBLE
        friendsAdapter = MultiChoiceShareWithFriendsAdapter(userList)
        binding.rvResult.adapter = friendsAdapter
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 1212) {
//            navController.popBackStack()
//        }
//    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        questionViewModel!!.setRefreshShowBadges(true)
    }

    private fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends) {
                for (userList in friendsList!!) {
                    userList.isSelected = true
                }
                setMyFriendsAdapter(friendsList!!)
            }
        } else {
            if (binding.rgTabs.checkedRadioButtonId == R.id.btnFriends) {
                for (userList in friendsList!!) {
                    userList.isSelected = false
                }
                setMyFriendsAdapter(friendsList!!)
            }
        }
    }
}