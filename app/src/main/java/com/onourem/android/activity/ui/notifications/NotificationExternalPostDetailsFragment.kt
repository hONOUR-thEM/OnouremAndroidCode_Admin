package com.onourem.android.activity.ui.notifications

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentDashboardPlayGamesBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.YoutubeActivity
import com.onourem.android.activity.ui.bottomsheet.Action
import com.onourem.android.activity.ui.bottomsheet.ActionType
import com.onourem.android.activity.ui.dashboard.DashboardFragmentDirections
import com.onourem.android.activity.ui.dashboard.watchlist.OnWatchListItemClickListener
import com.onourem.android.activity.ui.games.adapters.FriendsPlayingGamesAdapter
import com.onourem.android.activity.ui.games.dialogs.SendNowOrSendLaterDialogFragment
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.games.fragments.AnswerQuestionDialogFragment
import com.onourem.android.activity.ui.games.fragments.SelectPlayGroupDialogFragment
import com.onourem.android.activity.ui.games.fragments.TaskAndMessageComposeDialogFragment
import com.onourem.android.activity.ui.games.fragments.WriteCommentDialogFragment
import com.onourem.android.activity.ui.games.viewmodel.*
import com.onourem.android.activity.ui.profile.ProfileViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Triple
import com.onourem.android.activity.ui.utils.listners.*
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.util.*
import javax.inject.Inject

class NotificationExternalPostDetailsFragment :
    AbstractBaseViewModelBindingFragment<DashboardViewModel, FragmentDashboardPlayGamesBinding>(),
    OnItemClickListener<Triple<LoginDayActivityInfoList?, Int?, String?>?>,
    OnWatchListItemClickListener<Pair<Int, UserWatchList>> {

    private lateinit var loginUserId: String
    private lateinit var mediaViewModel: MediaOperationViewModel
    private lateinit var playGroup: PlayGroup

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var loginDayActivityInfoList: MutableList<LoginDayActivityInfoList>? = ArrayList()
    private var questionGamesViewModel: QuestionGamesViewModel? = null
    private var soloGamesAdapter: FriendsPlayingGamesAdapter? = null
    private var gamesReceiverViewModel: GamesReceiverViewModel? = null
    private var userActionViewModel: UserActionViewModel? = null
    private var profileViewModel: ProfileViewModel? = null
    private var layoutManager: LinearLayoutManager? = null

    companion object {

        fun create(
            linkUserId: String,
            linkType: String,
        ): NotificationExternalPostDetailsFragment {
            val fragment = NotificationExternalPostDetailsFragment()
            val args = Bundle()
            args.putString("linkUserId", linkUserId)
            args.putString("linkType", linkType)
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        questionGamesViewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[QuestionGamesViewModel::class.java]

        gamesReceiverViewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[GamesReceiverViewModel::class.java]

        userActionViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]

        mediaViewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[MediaOperationViewModel::class.java]

        profileViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)[ProfileViewModel::class.java]

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        loginUserId = preferenceHelper!!.getString(Constants.KEY_LOGGED_IN_USER_ID)

        layoutManager = LinearLayoutManager(requireActivity())

        playGroup = PlayGroup()
        playGroup.playGroupId = "FFF"
        playGroup.playGroupName = "Friends Playing"
        playGroup.isDummyGroup = true

        setAdapter()

    }

    private fun setAdapter() {

        loginDayActivityInfoList!!.add(NotificationExternalPostDetailsFragmentArgs.fromBundle(requireArguments()).activity)
        binding.swipeRefreshLayout.isRefreshing = false
        binding.rvPlayGames.layoutManager = layoutManager
        if (loginDayActivityInfoList != null && loginDayActivityInfoList!!.isNotEmpty()) {
            if (soloGamesAdapter == null) {
                soloGamesAdapter = FriendsPlayingGamesAdapter(
                    PlayGroup(),
                    loginDayActivityInfoList,
                    preferenceHelper!!,
                    this,
                    null,
                    viewModel,
                    viewLifecycleOwner,
                    navController,
                    alertDialog
                )
                binding.rvPlayGames.adapter = soloGamesAdapter
            }

            binding.tvMessage.visibility = View.GONE
            binding.rvPlayGames.visibility = View.VISIBLE
            //            soloGamesAdapter.updateItems(loginDayActivityInfoList);


        } else {
            binding.tvMessage.text = ""
            binding.tvMessage.visibility = View.VISIBLE
            binding.rvPlayGames.visibility = View.GONE
        }
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_dashboard_play_games
    }

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun onItemClick(item: Triple<LoginDayActivityInfoList?, Int?, String?>?) {
        //Hiding Keyboard
//        if (binding.cardSearch.getVisibility() == View.VISIBLE) {
//            AppUtilities.hideKeyboard(requireActivity());
//            binding.edtSearchQuery.clearFocus();
//        }
        if (item != null) {
            if (item.first != null) {

                if ((fragmentContext as DashboardActivity).canUserAccessApp){
                    when (item.second) {

                        FriendsPlayingGamesAdapter.CLICK_BUTTON ->
                            if (getString(R.string.click_action_send_to_friends) == item.third) {
                            if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.CARD) {
                                inviteFriends(item.first!!)
                            } else if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.EXTERNAL) {
                                inviteFriends(item.first!!)
                                (fragmentContext as DashboardActivity).updateExternalActivityInfo(
                                    item.first!!
                                )
                            } else {
                                showAlert("Unknown Action " + item.first!!.activityType!!)
                            }
                        }
                        else if (getString(R.string.click_action_show_more) == item.third) {
                            if (ActivityType.getActivityType(item.first!!.activityType!!) == ActivityType.EXTERNAL) {
                                (fragmentContext as DashboardActivity).updateExternalActivityInfo(
                                    item.first!!
                                )
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavWebExternalContent(
                                        item.first!!.questionVideoUrl!!
                                    )
                                )
                            } else {
                                showAlert("Unknown Action " + item.first!!.activityType!!)
                            }
                        }
                        FriendsPlayingGamesAdapter.CLICK_MEDIA -> {
                            if (item.first!!.activityType!! == "External") {
                                (fragmentContext as DashboardActivity).updateExternalActivityInfo(
                                    item.first!!
                                )
                                if (!TextUtils.isEmpty(item.first!!.youTubeVideoId)) {
                                    val intent = Intent(context, YoutubeActivity::class.java)
                                    intent.putExtra(
                                        "youtubeId",
                                        item.first!!.youTubeVideoId!!
                                    )
                                    (fragmentContext as DashboardActivity).exoPlayerPause(true)
                                    fragmentContext.startActivity(intent)
                                } else {
                                    if (!TextUtils.isEmpty(item.first!!.questionVideoUrl)) {
                                        navController.navigate(
                                            MobileNavigationDirections.actionGlobalNavWebExternalContent(
                                                item.first!!.questionVideoUrl!!
                                            )
                                        )
                                    } else if (!TextUtils.isEmpty(item.first!!.activityImageLargeUrl)) {
                                        navController.navigate(
                                            MobileNavigationDirections.actionGlobalNavMediaView(
                                                1,
                                                item.first!!.activityImageLargeUrl!!
                                            )
                                        )
                                    }

                                }
                            }

                        }

                    }
                }else{
                    (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showSendNowSendLater(activityInfoList: LoginDayActivityInfoList?) {
        val createGameActivityRequest = CreateGameActivityRequest()
        createGameActivityRequest.serviceName = "createGameActivity"
        createGameActivityRequest.screenId = "12"
        createGameActivityRequest.loginDay = activityInfoList!!.loginDay
        createGameActivityRequest.activityId = activityInfoList.activityId
        createGameActivityRequest.activityTypeId = activityInfoList.activityTypeId
        questionGamesViewModel!!.createGameActivity(createGameActivityRequest).observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GameActivityUpdateResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    if (!TextUtils.isEmpty(apiResponse.body.activityTagStatus)) activityInfoList.activityTag =
                        apiResponse.body.activityTagStatus
                    if (!TextUtils.isEmpty(apiResponse.body.participationStatus)) activityInfoList.userParticipationStatus =
                        apiResponse.body.participationStatus
                    soloGamesAdapter!!.notifyDataSetChanged()
                    val fragment = SendNowOrSendLaterDialogFragment.getInstance(
                        apiResponse.body.message,
                        activityInfoList
                    ) { pair: android.util.Pair<LoginDayActivityInfoList?, Int> ->
                        if (pair.second == 1) {
                            TaskAndMessageComposeDialogFragment.getInstance(pair.first) { item1: Pair<LoginDayActivityInfoList?, UploadPostResponse?>? ->
                                navController.navigate(
                                    MobileNavigationDirections.actionGlobalNavTaskMessageQuestionDetails(
                                        pair.first!!,
                                        playGroup
                                    )
                                )
                            }
                                .show(
                                    requireActivity().supportFragmentManager,
                                    "TaskMessageCompose"
                                )
                            //                            navController.navigate(DashboardFragmentDirections.actionNavPlayGamesToNavTaskMessageCompose(pair));
                        }
                    }
                    fragment.show(parentFragmentManager, "SendNowOrSendLaterDialogFragment")
                } else {
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    private fun share(
        title: String?,
        linkMsg: String?,
        mediaUrl: String?,
        mediaType: String?,
    ) {
//        if (!mediaUrl.equals("", ignoreCase = true)) {
//            navController.navigate(
//                MobileNavigationDirections.actionGlobalNavMediaShare(
//                    title ?: "",
//                    linkMsg ?: "",
//                    mediaUrl ?: "",
//                    mediaType ?: ""
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

    private fun inviteFriends(first: LoginDayActivityInfoList?) {
        viewModel.getUserLinkInfo(
            first!!.activityType,
            first.activityId!!,
            "48",
            first.activityText
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
//                        first,
//                        apiResponse.body.linkType,
//                        apiResponse.body.activityImageUrl,
//                        apiResponse.body.activityText
//                    )

                    if (!TextUtils.isEmpty(first.questionVideoUrl)) {
                        if (first.activityType == "External") {
                            share(
                                apiResponse.body.title,
                                apiResponse.body.shortLink,
                                apiResponse.body.activityImageUrl,
                                "1"
                            )
                        } else {
                            share(
                                apiResponse.body.title,
                                apiResponse.body.shortLink,
                                first.questionVideoUrl!!,
                                "2"
                            )
                        }

                    } else if (!TextUtils.isEmpty(first.activityImageLargeUrl)) {
                        share(
                            apiResponse.body.title,
                            apiResponse.body.shortLink,
                            apiResponse.body.activityImageUrl,
                            "1"
                        )
                    } else {
                        share(
                            apiResponse.body.title,
                            apiResponse.body.shortLink,
                            "", "-1"
                        )
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
                    if (com.onourem.android.activity.BuildConfig.DEBUG) {
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

    override fun onDestroy() {
        super.onDestroy()
        //        state = null;
        loginDayActivityInfoList!!.clear()
//        if (activityStatusList != null) {
//            activityStatusList!!.clear()
//        }

        questionGamesViewModel!!.actionConsumed()

    }

    override fun onItemClick(item: Pair<Int, UserWatchList>, position: Int) {

    }

}