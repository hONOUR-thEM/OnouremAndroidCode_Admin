package com.onourem.android.activity.ui.invite

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.gson.Gson
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentInviteSheetBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.util.*
import javax.inject.Inject

class InviteSheetFragment :
    AbstractBaseBottomSheetBindingDialogFragment<QuestionGamesViewModel, FragmentInviteSheetBinding>() {
    private lateinit var infoGroupItemList: ArrayList<InviteGroupItemInfo>

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    private var dashboardViewModel: DashboardViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashboardViewModel = ViewModelProvider(this, viewModelFactory!!)[DashboardViewModel::class.java]
    }

    override fun viewModelType(): Class<QuestionGamesViewModel> {
        return QuestionGamesViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_invite_sheet
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (dialog != null) dialog!!.setCanceledOnTouchOutside(false)
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val expressionDataResponse = Gson().fromJson(
            preferenceHelper!!.getString(Constants.KEY_EXPRESSIONS_RESPONSE),
            ExpressionDataResponse::class.java
        )
        if (expressionDataResponse?.suggestAndInviteDescription != null && expressionDataResponse.suggestAndInviteDescription!!.isNotEmpty()) {
            binding.tvMessage.text = expressionDataResponse.suggestAndInviteDescription!![1]
        }
        binding.btnInvite.setOnClickListener(ViewClickListener { v: View? ->
            viewModel.getUserLinkInfo("InviteFriend", "-1", "42", "").observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<GetUserLinkInfoResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        shortLink(
                            apiResponse.body.title,
                            apiResponse.body.linkUserId,
                            apiResponse.body.userLink,
                            apiResponse.body.linkType,
                            apiResponse.body.activityImageUrl,
                            apiResponse.body.activityText
                        )
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
        })

        binding.cvClose.setOnClickListener(ViewClickListener { v: View? ->
            dismiss()
        })

        viewModel.inviteFriendImageInfo().observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetInviteFriendImageInfoResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                    loadViews(apiResponse.body.inviteFriendImageInfoList)

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
                        AppUtilities.showLog("Network Error", "inviteFriendImageInfo")
                    }
                    (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                        "inviteFriendImageInfo",
                        apiResponse.code.toString()
                    )
                }
            }
        }

    }


    private fun loadViews(inviteFriendImageInfoList: List<InviteFriendImageInfo>) {

        infoGroupItemList = ArrayList<InviteGroupItemInfo>()

        inviteFriendImageInfoList.groupBy(InviteFriendImageInfo::imageRow)
            .mapValues { entry ->
                infoGroupItemList.add(
                    InviteGroupItemInfo(
                        entry.value
                    )
                )
            }

        binding.rvGroup.layoutManager = LinearLayoutManager(fragmentContext)
        binding.rvGroup.adapter = InviteInfoGroupAdapter(infoGroupItemList, fragmentContext)

        binding.tvMessage.visibility = View.VISIBLE
        binding.tvTop.visibility = View.VISIBLE

    }

    private fun shortLink(
        title: String?,
        linkUserId: String?,
        linkMsg: String?,
        linkType: String?,
        activityImageUrl: String?,
        activityText: String?
    ) {
        showProgress()
        var imageUrl = ""
        val description = if (linkType.equals("Card", ignoreCase = true)) {
            "Fun cards to share with friends"
        } else if (linkType.equals("1toM", ignoreCase = true)) {
            "Fun questions to ask friends"
        } else {
            "A place for good friends"
        }
        if (!TextUtils.isEmpty(activityImageUrl)) {
            imageUrl = activityImageUrl?:""
        }
        val titleSocial: String = activityText?:""
        val builderSocialMeta = DynamicLink.SocialMetaTagParameters.Builder()
            .setTitle(titleSocial)
            .setImageUrl(Uri.parse(imageUrl))
            .setDescription(description)
            .build()
        val navigationInfoParameters = DynamicLink.NavigationInfoParameters.Builder()
        navigationInfoParameters.setForcedRedirectEnabled(true)
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://www.onourem.com/?linkUserId=$linkUserId&linkType=$linkType"))
            .setDomainUriPrefix("https://e859h.app.goo.gl")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder("com.onourem.android.activity") //                                .setMinimumVersion(125)
                    .build()
            )
            .setSocialMetaTagParameters(builderSocialMeta)
            .setIosParameters(
                DynamicLink.IosParameters.Builder("com.onourem.onoureminternet")
                    .setAppStoreId("1218240628") //                                .setMinimumVersion("1.0.1")
                    .build()
            ) // Set parameters
            // ...
            .setNavigationInfoParameters(navigationInfoParameters.build())
            .buildShortDynamicLink()
            .addOnCompleteListener(requireActivity()) { task: Task<ShortDynamicLink> ->
                if (task.isSuccessful) {
                    // Short link created
                    val shortLink = Objects.requireNonNull(task.result).shortLink
                    //Log.d("shortLink", shortLink.toString());
                    dashboardViewModel!!.updateAppShortLink(linkUserId!!, shortLink.toString())
                        .observe(
                            viewLifecycleOwner
                        ) { response: ApiResponse<StandardResponse> ->
                            if (response.loading) {
                                showProgress()
                            } else if (response.isSuccess && response.body != null) {
                                hideProgress()
                                if (response.body.errorCode.equals("000", ignoreCase = true)) {
                                    val shareIntent = Intent(Intent.ACTION_SEND)
                                    shareIntent.type = "text/plain"
                                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
                                    val shareMessage = "$linkMsg \n$shortLink"
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                                    try {
                                        fragmentContext.startActivity(Intent.createChooser(shareIntent, title))
                                    } catch (ex : ActivityNotFoundException) {
                                        Toast.makeText(fragmentContext,"NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    showAlert(response.body.errorMessage)
                                }
                            } else {
                                hideProgress()
                                showAlert(response.errorMessage)
                            }
                        }
                } else {
                    // Error
                    // ...
                }
            }
    }


}