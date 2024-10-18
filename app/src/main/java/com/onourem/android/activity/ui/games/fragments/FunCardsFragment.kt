package com.onourem.android.activity.ui.games.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentFunCardsBinding
import com.onourem.android.activity.models.CardInfo
import com.onourem.android.activity.models.GetFunCardsResponse
import com.onourem.android.activity.models.GetUserLinkInfoResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.games.adapters.FunCardsAdapter
import com.onourem.android.activity.ui.games.adapters.PlaySoloGamesAdapter.Companion.CLICK_SHARE
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject


class FunCardsFragment :
    AbstractBaseBottomSheetBindingDialogFragment<DashboardViewModel, FragmentFunCardsBinding>() {

    private lateinit var funCardsAdapter: FunCardsAdapter
    private var linkUserId: String = ""

    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    override fun layoutResource(): Int {
        return R.layout.fragment_fun_cards
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val funCardsFragmentArgs = FunCardsFragmentArgs.fromBundle(requireArguments())
        linkUserId = funCardsFragmentArgs.linkUserId
        getFunCards()


//        binding.cvClose.setOnClickListener(ViewClickListener {
//            navController.popBackStack()
//        })
        binding.tvDialogTitle.text = "Make friends smile by sending them fun cards."
    }

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    private fun getFunCards() {
        viewModel.getFunCards(linkUserId).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetFunCardsResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                linkUserId = ""
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    setAdapter(apiResponse.body.cardInfoList as ArrayList<CardInfo>)
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun setAdapter(cards: ArrayList<CardInfo>) {
        val layoutManager = LinearLayoutManager(activity)
        binding.rvFunCard.layoutManager = layoutManager

        funCardsAdapter = FunCardsAdapter(
            cards, requireActivity()
        ) {
            if (it.first == CLICK_SHARE) {
                inviteFriends(it.second)
            }
        }
        binding.rvFunCard.adapter = funCardsAdapter
//        funCardsAdapter.notifyDataSetChanged()

    }

    private fun inviteFriends(card: CardInfo) {
        viewModel.getUserLinkInfo(
            "Card",
            card.activityId!!,
            "50",
            ""
        ).observe(
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
//                        card
//                    )

                    share(
                        apiResponse.body.title,
                        apiResponse.body.shortLink,
                        apiResponse.body.activityImageUrl,
                        "1"
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
    }


//    private fun shortLink(title: String, linkUserId: String, linkMsg: String, card: CardInfo) {
//
//        val navigationInfoParameters = NavigationInfoParameters.Builder()
//        navigationInfoParameters.setForcedRedirectEnabled(true)
//        FirebaseDynamicLinks.getInstance().createDynamicLink()
//            .setLink(Uri.parse("https://www.onourem.com/?linkUserId=$linkUserId&linkType=Card"))
//            .setDomainUriPrefix("https://e859h.app.goo.gl")
//            .setAndroidParameters(
//                AndroidParameters.Builder("com.onourem.android.activity") //                                .setMinimumVersion(125)
//                    .build()
//            )
//            .setIosParameters(
//                IosParameters.Builder("com.onourem.onoureminternet")
//                    .setAppStoreId("1218240628") //                                .setMinimumVersion("1.0.1")
//                    .build()
//            ) // Set parameters
//            // ...
//            .setSocialMetaTagParameters(
//                SocialMetaTagParameters.Builder()
//                    .setDescription("Fun cards to share with friends")
//                    .setTitle(card.activityText!!)
//                    .setImageUrl(Uri.parse(card.smallImageUrl))
//                    .build()
//            )
//            .setNavigationInfoParameters(navigationInfoParameters.build())
//            .buildShortDynamicLink()
//            .addOnCompleteListener(
//                requireActivity()
//            ) { task: Task<ShortDynamicLink?> ->
//                if (task.isSuccessful) {
//                    // Short link created
//                    val shortLink =
//                        Objects.requireNonNull(task.result!!).shortLink
//                    //Log.d("shortLink", shortLink.toString())
//                    viewModel.updateAppShortLink(linkUserId, shortLink.toString())
//                        .observe(
//                            viewLifecycleOwner,
//                            androidx.lifecycle.Observer { response: ApiResponse<StandardResponse?> ->
//                                if (response.loading) {
//                                    showProgress()
//                                } else if (response.isSuccess && response.body != null) {
//                                    hideProgress()
//                                    if (response.body.errorCode.equals("000", ignoreCase = true)) {
////                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
////                                    shareIntent.setType("text/plain");
////                                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem");
////                                    String shareMessage = linkMsg + " \n" + shortLink.toString();
////                                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
////                                    startActivityForResult(Intent.createChooser(shareIntent, title), 1212);
////
////                                        if (!TextUtils.isEmpty(loginDayActivityInfoList.getQuestionVideoUrl())) {
////                                            share(
////                                                title,
////                                                linkMsg,
////                                                shortLink,
////                                                loginDayActivityInfoList.getQuestionVideoUrl(),
////                                                2
////                                            )
////                                        } else
//                                        if (!TextUtils.isEmpty(card.largeImageUrl)) {
//                                            share(
//                                                title,
//                                                linkMsg,
//                                                card.largeImageUrl!!,
//                                                1
//                                            )
//                                        } else {
//                                            share(title, linkMsg, shortLink!!, "", -1)
//                                        }
//                                    } else {
//                                        showAlert(response.body.errorMessage)
//                                    }
//                                } else {
//                                    hideProgress()
//                                    showAlert(response.errorMessage)
//                                    if (response.errorMessage != null
//                                        && (response.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
//                                                || response.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
//                                    ) {
//                                        if (BuildConfig.DEBUG) {
//                                            AppUtilities.showLog(
//                                                "Network Error",
//                                                "updateAppShortLink"
//                                            )
//                                        }
//                                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
//                                            "updateAppShortLink",
//                                            response.code.toString()
//                                        )
//                                    }
//                                }
//                            })
//                }
//            }
//    }
//

    private fun share(
        title: String?,
        linkMsg: String?,
        mediaUrl: String?,
        mediaType: String?,
    ) {
//        if (!mediaUrl.equals("", ignoreCase = true)) {
//            navController.navigate(
//                MobileNavigationDirections.actionGlobalNavMediaShare(
//                    title?:"",
//                    linkMsg?:"",
//                    mediaUrl?:"",
//                    mediaType?:""
//                )
//            )
//        } else {
//            val shareIntent = Intent(Intent.ACTION_SEND)
//            shareIntent.type = "text/plain"
//            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
//            shareIntent.putExtra(Intent.EXTRA_TEXT, linkMsg)
//            try {
//                fragmentContext.startActivity(Intent.createChooser(shareIntent,title));
//            } catch (ex : ActivityNotFoundException) {
//                Toast.makeText(fragmentContext,"NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
//            }
//        }

        navController.navigate(
            MobileNavigationDirections.actionGlobalNavShareContent(
                title ?: "",
                linkMsg ?: "",
                mediaUrl ?: "",
                mediaType ?: ""
            )
        )
    }


}