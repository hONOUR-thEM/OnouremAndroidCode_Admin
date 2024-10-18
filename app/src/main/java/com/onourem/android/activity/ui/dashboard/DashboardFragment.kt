package com.onourem.android.activity.ui.dashboard

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentDashboardMainBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.dashboard.guest.GuestFriendsPlayingFragment
import com.onourem.android.activity.ui.dashboard.landing.FriendsPlayingFragment
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.ui.games.viewmodel.GamesReceiverViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.Constants.*
import com.onourem.android.activity.ui.utils.LocalMoods.getAllMoods
import com.onourem.android.activity.ui.utils.glide.GlideApp
import com.onourem.android.activity.ui.utils.listners.RVScrollListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.*
import javax.inject.Inject


class DashboardFragment :
    AbstractBaseViewModelBindingFragment<DashboardViewModel, FragmentDashboardMainBinding>() {

    val rvScrollListener = RVScrollListener { dx, dy ->
        if (dy < 0) {
            binding.fab.extend()
            binding.fabAdmin.extend()
        } else if (dy > 0) {
            binding.fab.shrink()
            binding.fabAdmin.shrink()
        }
    }


    private var getWatchListResponse: GetWatchListResponse? = null
    var options = RequestOptions()
        .fitCenter()
        .transform(CircleCrop())
        .error(R.drawable.ic_error)
        .placeholder(R.drawable.default_user_profile_image)
        .error(R.drawable.default_user_profile_image)

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var linkUserId = ""
    private var isLinkVerified = ""
    private var adviceUpdateDialog: Dialog? = null
    private var questionGamesViewModel: QuestionGamesViewModel? = null
    private var gamesReceiverViewModel: GamesReceiverViewModel? = null
    private var userActionViewModel: UserActionViewModel? = null

    private var linkType = ""


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionGamesViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[QuestionGamesViewModel::class.java]
        gamesReceiverViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[GamesReceiverViewModel::class.java]
        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]


        questionGamesViewModel!!.inAppReviewPopup.observe(this) { show: Boolean ->
            if (show) {
                val todayAnswerCount =
                    preferenceHelper!!.getInt(Constants.KEY_TOTAL_QUES_ANSWERED_TODAY)
                val totalAnswerCount =
                    preferenceHelper!!.getInt(Constants.KEY_TOTAL_QUES_ANSWERED_BEFORE_TODAY)
                val noOfTimeRequestReviewRaised =
                    preferenceHelper!!.getInt(Constants.KEY_NUMBER_OF_TIME_APP_REVIEW_REQUESTS_RAISED)
                var noOfDaysAfterShownReviewLastTime = 0L
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                var lastTimeReviewShownDate =
                    preferenceHelper!!.getString(Constants.KEY_LAST_TIME_WHEN_REVIEW_POPUP_WAS_SHOWN)
                if (lastTimeReviewShownDate.equals("", ignoreCase = true)) {
                    lastTimeReviewShownDate = "2000-01-01 01:01:01"
                }
                try {
                    val date = sdf.parse(lastTimeReviewShownDate)
                    if (date != null) {
                        noOfDaysAfterShownReviewLastTime = getDaysBetweenDates(Date(), date)
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                if (todayAnswerCount >= 3 && totalAnswerCount > 20 && noOfDaysAfterShownReviewLastTime > 15 && noOfTimeRequestReviewRaised < 3) {
                    viewModel.setShowInAppReview(true)
                }
            }
        }

    }

    override fun layoutResource(): Int {
        return R.layout.fragment_dashboard_main
    }

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun onResume() {
        super.onResume()
        val moodsDialogFragment =
            requireActivity().supportFragmentManager.findFragmentByTag("MoodsDialogFragment") as MoodsDialogFragment?
        moodsDialogFragment?.dismiss()
        val getAppUpgradeInfoResponse = Gson().fromJson(
            preferenceHelper!!.getString(Constants.KEY_GET_APP_UPDATE_RESPONSE),
            GetAppUpgradeInfoResponse::class.java
        )
        var forceAndAdviceUpgrade: ForceAndAdviceUpgrade? = null
        if (getAppUpgradeInfoResponse != null) forceAndAdviceUpgrade =
            getAppUpgradeInfoResponse.forceAndAdviceUpgrade
        if (getAppUpgradeInfoResponse != null && forceAndAdviceUpgrade != null && AppUtilities.getAppVersion() < java.lang.Double.valueOf(
                forceAndAdviceUpgrade.androidForceUpgradeVersion ?: "0"
            ).toInt()
        ) {
            (requireActivity().supportFragmentManager.findFragmentByTag("MoodsDialogFragment") as MoodsDialogFragment?)?.dismiss()

            showAlert(
                forceAndAdviceUpgrade.screenTitle, forceAndAdviceUpgrade.androidNewVersionMessage
            ) {

                val intent = Intent(Intent.ACTION_VIEW)
                intent.data =
                    Uri.parse(
                        String.format(
                            "https://play.google.com/store/apps/details?id=%s",
                            requireActivity().packageName
                        )
                    )
                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(fragmentContext, "NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            if (!(fragmentContext as DashboardActivity).isGuestUser()) {
                loadExpressions(
                    TextUtils.isEmpty(
                        preferenceHelper!!.getString(
                            KEY_SELECTED_EXPRESSION
                        )
                    )
                )
            }
        }
    }

    private fun showMoodSelectionDialog() {
        val response = preferenceHelper!!.getString(KEY_SELECTED_EXPRESSION)
        val date = preferenceHelper!!.getLong(KEY_TIME_MILIES_LAST_MOOD_SYNC)
        if (TextUtils.isEmpty(response) || date > 0 && !AppUtilities.isSameDay(
                Date(date),
                Calendar.getInstance(Locale.getDefault()).time
            )
        ) {
            viewModel.setIsMoodsDialogShowing(true)
            MoodsDialogFragment.instance.show(requireActivity().supportFragmentManager, "MoodsDialogFragment")
        } else {
            viewModel.setIsMoodsDialogShowing(false)
        }
        linkUserId = preferenceHelper!!.getString(KEY_LINK_USER_ID)
        linkType = preferenceHelper!!.getString(KEY_LINK_TYPE)
        isLinkVerified = preferenceHelper!!.getString(KEY_IS_LINK_VERIFIED)
//        if (!linkType.equals("", ignoreCase = true) && linkType.equals("Card", ignoreCase = true)) {
//            linkType = ""
//            if (!linkUserId.equals("", ignoreCase = true)) {
//                viewModel.setShowFunCards(true)
//            }
//        }
        if (!linkType.equals("", ignoreCase = true) && linkType.equals(
                "Audio",
                ignoreCase = true
            )
        ) {
            if (!linkUserId.equals("", ignoreCase = true)) {
                viewModel.setShowAudioVocals(true)
            }
        }
    }

    private fun loadExpressions(showProgress: Boolean) {

        showMoodSelectionDialog()

        if (adviceUpdateDialog != null && adviceUpdateDialog!!.isShowing) adviceUpdateDialog!!.dismiss()
        viewModel.moodExpressions().observe(this) { apiResponse: ApiResponse<ExpressionDataResponse> ->
            if (apiResponse.loading) {
                //if (showProgress) showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                //if (showProgress) hideProgress()
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    val newSurveyCOList = apiResponse.body.surveyCOList!![0]
                    //                    surveyCOList.setUserAnserForSurvey("Y");
                    val oldSurveyData = preferenceHelper!!.getString(Constants.KEY_SELECTED_SURVEY)
                    if (!TextUtils.isEmpty(oldSurveyData)) {
                        val oldSurveyCOList =
                            Gson().fromJson(oldSurveyData, SurveyCOList::class.java)
                        if (oldSurveyCOList == null || !newSurveyCOList.id.equals(
                                oldSurveyCOList.id,
                                ignoreCase = true
                            )
                        ) preferenceHelper!!.putValue(
                            Constants.KEY_SELECTED_SURVEY, Gson().toJson(newSurveyCOList)
                        )
                    } else {
                        preferenceHelper!!.putValue(
                            Constants.KEY_SELECTED_SURVEY,
                            Gson().toJson(newSurveyCOList)
                        )
                    }

                    addLocalExpressionList(apiResponse.body)

                    if (AppUtilities.getAppVersion() < java.lang.Double.valueOf(
                            apiResponse.body.forceAndAdviceUpgrade!!.androidForceUpgradeVersion ?: "0"
                        )
                            .toInt()
                    ) {
                        (requireActivity().supportFragmentManager.findFragmentByTag("MoodsDialogFragment") as MoodsDialogFragment?)?.dismiss()
                        showAlert(
                            apiResponse.body.forceAndAdviceUpgrade!!.screenTitle,
                            apiResponse.body.forceAndAdviceUpgrade!!.androidNewVersionMessage
                        ) {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data =
                                Uri.parse(
                                    String.format(
                                        "https://play.google.com/store/apps/details?id=%s",
                                        requireActivity().packageName
                                    )
                                )
                            try {
                                startActivity(intent)
                            } catch (ex: ActivityNotFoundException) {
                                Toast.makeText(fragmentContext, "NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else if (!preferenceHelper!!.getBoolean(Constants.KEY_ANDROID_ADVICE_UPGRADE_VERSION_SKIPPED)
                        && AppUtilities.getAppVersion() < java.lang.Double.valueOf(
                            apiResponse.body.forceAndAdviceUpgrade!!.androidAdviceUpgradeVersion ?: "0"
                        )
                            .toInt()
                    ) {
                        (requireActivity().supportFragmentManager.findFragmentByTag("MoodsDialogFragment") as MoodsDialogFragment?)?.dismiss()
                        adviceUpdateDialog = TwoActionAlertDialog.showAlert(
                            requireActivity(),
                            apiResponse.body.forceAndAdviceUpgrade!!.screenTitle,
                            apiResponse.body.forceAndAdviceUpgrade!!.androidNewVersionMessage ?: "",
                            null,
                            "Not Now",
                            "Update"
                        ) { item1: Pair<Int?, Any?> ->
                            preferenceHelper!!.putValue(
                                Constants.KEY_ANDROID_ADVICE_UPGRADE_VERSION_SKIPPED,
                                true
                            )
                            if (item1.first != null && TwoActionAlertDialog.ACTION_RIGHT == item1.first) {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data =
                                    Uri.parse(
                                        String.format(
                                            "https://play.google.com/store/apps/details?id=%s",
                                            requireActivity().packageName
                                        )
                                    )
                                try {
                                    startActivity(intent)
                                } catch (ex: ActivityNotFoundException) {
                                    Toast.makeText(fragmentContext, "NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
                                }
                            } else if (item1.first != null && TwoActionAlertDialog.ACTION_LEFT == item1.first) {
                                showMoodSelectionDialog()
                                preferenceHelper!!.putValue(
                                    Constants.KEY_ANDROID_ADVICE_UPGRADE_VERSION_SKIPPED,
                                    true
                                )
                                val userSettledGameCount = apiResponse.body.userSettledGameCount
                                preferenceHelper!!.putValue(
                                    Constants.KEY_TOTAL_QUES_ANSWERED_TODAY,
                                    0
                                )
                                preferenceHelper!!.putValue(
                                    Constants.KEY_TOTAL_QUES_ANSWERED_BEFORE_TODAY,
                                    userSettledGameCount!!.toInt()
                                )

                                //viewModel.setIsMoodsDialogShowing(true)
                            }
                        }
                    } else {
//                        showMoodSelectionDialog(apiResponse.body, false)
                        preferenceHelper!!.putValue(
                            Constants.KEY_ANDROID_ADVICE_UPGRADE_VERSION_SKIPPED,
                            false
                        )
                        val userSettledGameCount = apiResponse.body.userSettledGameCount
                        preferenceHelper!!.putValue(Constants.KEY_TOTAL_QUES_ANSWERED_TODAY, 0)
                        preferenceHelper!!.putValue(
                            Constants.KEY_TOTAL_QUES_ANSWERED_BEFORE_TODAY,
                            userSettledGameCount!!.toInt()
                        )
                        //viewModel.setIsMoodsDialogShowing(false)
                    }
                } else {
                    showAlert(apiResponse.body.errorMessage) {
                        if (TextUtils.isEmpty(
                                preferenceHelper!!.getString(KEY_SELECTED_EXPRESSION)
                            )
                        ) requireActivity().finish()
                    }
                }
            } else {
                //if (showProgress) hideProgress()
                showAlert(
                    resources.getString(R.string.label_network_error),
                    apiResponse.errorMessage
                ) {
                    if (TextUtils.isEmpty(
                            preferenceHelper!!.getString(KEY_SELECTED_EXPRESSION)
                        )
                    ) requireActivity().finish()
                }
            }
        }
    }


    private fun addLocalExpressionList(response: ExpressionDataResponse) {

        response.userExpressionList = getAllMoods(preferenceHelper!!)

        //TODO EXP
        preferenceHelper!!.putValue(
            Constants.KEY_EXPRESSIONS_RESPONSE,
            Gson().toJson(response)
        )
    }

    private val badgesDetails: Unit
        get() {
            if (!(fragmentContext as DashboardActivity).isGuestUser()) {
                viewModel.getQuestionGameWatchListAndSurveyInfo("8")
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetQuestionGameWatchListAndSurveyInfoResponse> ->
                        if (apiResponse.isSuccess && apiResponse.body != null) {
//                hideProgress();
                            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                if (apiResponse.body.isWatchListNew.equals(
                                        "Y",
                                        ignoreCase = true
                                    )
                                ) {
//                        include4.setVisibility(View.VISIBLE);
//                        startSpringAnimation(include4);
                                    // binding.tvBubble.setVisibility(View.VISIBLE);
                                    animateChange(binding.cvMood)
                                } else {
                                    //include4.setVisibility(View.GONE);
                                    //binding.tvBubble.setVisibility(View.GONE);
                                }
                            } else {
                                showAlert(apiResponse.body.errorMessage)
                            }

//                viewClicksEnabled(true);
                        }
                    }
            }
        }

    private fun animateChange(viewHolder: MaterialCardView) {
        val colorFrom = ContextCompat.getColor(fragmentContext, R.color.color_highlight_game_post)
        val colorTo = ContextCompat.getColor(fragmentContext, R.color.color_white)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.startDelay = 300 // milliseconds
        colorAnimation.addUpdateListener { animator: ValueAnimator ->
            viewHolder.setCardBackgroundColor(
                animator.animatedValue as Int
            )
        }
        colorAnimation.repeatCount = 1
        colorAnimation.start()
        colorAnimation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {

                if (isAdded) {
                    view!!.post {
                        binding.clMood.background =
                            AppCompatResources.getDrawable(
                                binding.root.context,
                                R.drawable.bg_cardview
                            )
                    }
                }

            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun showDialogNotCancelable(
        title: String, message: String,
        okListener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(requireActivity())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setCancelable(false)
            .create()
            .show()
    }

    private fun setMood(userExpressionList: UserExpressionList) {

//        try {
//            val drawable = AppCompatResources.getDrawable(fragmentContext, userExpressionList.moodImage)
////        val drawable = ContextCompat.getDrawable(this, userExpressionList.moodImage)
//            if (drawable != null) {
//                binding.ivMood.setImageDrawable(drawable)
//            }
//        }catch (e : Exception) {
//            e.printStackTrace()
//        }
//        val drawable = ContextCompat.getDrawable(fragmentContext, userExpressionList.moodImage)
//        if (drawable != null) {
//            binding.ivMood.setImageDrawable(drawable)
//        }
        GlideApp.with(requireActivity())
            .load(userExpressionList.moodImage)
            .apply(options)
            .into(binding.ivMood)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //dashboardData = (fragmentContext as DashboardActivity).dashboardData
        linkUserId = preferenceHelper!!.getString(KEY_LINK_USER_ID)
        linkType = preferenceHelper!!.getString(KEY_LINK_TYPE)
        isLinkVerified = preferenceHelper!!.getString(KEY_IS_LINK_VERIFIED)

        (fragmentContext as DashboardActivity).setRVScrollListenerReference(rvScrollListener)

        val value = preferenceHelper!!.getString(KEY_SELECTED_EXPRESSION)
        val expressionList = Gson().fromJson(value, UserExpressionList::class.java)
        expressionList?.let { setMood(it) }

        if ((fragmentContext as DashboardActivity).isGuestUser()) {
            binding.tabs.visibility = View.GONE
            binding.btnLogin.visibility = View.VISIBLE
            val userExpressionList: List<UserExpressionList> = getAllMoods(preferenceHelper!!)
            setMood(
                userExpressionList[1]
            )

            binding.btnLogin.setOnClickListener(ViewClickListener {
                (fragmentContext as DashboardActivity).onLogout()
            })

        } else {
            binding.tabs.visibility = View.GONE
            binding.btnLogin.visibility = View.GONE
        }
        viewModel.selectedExpression.observe(viewLifecycleOwner) { userExpressionList: UserExpressionList ->
            setMood(
                userExpressionList
            )
        }
        viewModel.isMoodsDialogShowing.observe(viewLifecycleOwner) { show: Boolean? ->
            if (!show!!) {
                badgesDetails
            }
        }

        viewModel.showCreateFab.observe(viewLifecycleOwner) { show: Boolean? ->
            if (show!!) {
                binding.fab.visibility = View.VISIBLE
            } else {
                binding.fab.visibility = View.GONE
            }
        }

        val notificationStatus: String =
            if (NotificationManagerCompat.from(requireActivity()).areNotificationsEnabled()) {
                "Y"
            } else {
                "N"
            }

        //new change

//        questionGamesViewModel!!.actionMutableLiveData.observe(viewLifecycleOwner) { action: Action<*>? ->
//            if (action == null) return@observe
//            val actionType = action.actionType
//            if (actionType == null || actionType == ActionType.NONE || viewModel == null || navController == null || navController.currentDestination == null || navController.currentDestination!!.id != R.id.nav_home) return@observe
//            questionGamesViewModel!!.actionConsumed()
//            if (actionType == ActionType.CREATE_QUESTION) {
//                navController.navigate(
//                    DashboardFragmentDirections.actionNavHomeToNavCreateOwnQuestion(
//                        playGroup
//                    )
//                )
//            } else if (actionType == ActionType.QUESTION_PLAYED) {
////                navController.getCurrentDestination().setLabel(getString(R.string.label_all_questions_played));
//                val playedGroup = PlayGroup()
//                playedGroup.playGroupId = "ZZZ"
//                playedGroup.playGroupName = getString(R.string.label_all_questions_played)
//                playedGroup.isDummyGroup = true
//                val bundle = Bundle()
//                bundle.putParcelable("selectedGroup", playedGroup)
//                navController.navigate(R.id.nav_home, bundle)
//                //                navController.navigate(DashboardFragmentDirections.actionNavPlayGamesSelf(playedGroup));
//            } else if (actionType == ActionType.QUESTIONS_CREATED) {
////                navController.getCurrentDestination().setLabel(getString(R.string.label_questions_i_created));
//                val questionCreatedGroup = PlayGroup()
//                questionCreatedGroup.playGroupId = "YYY"
//                questionCreatedGroup.isDummyGroup = true
//                questionCreatedGroup.playGroupName = getString(R.string.label_questions_i_created)
//                val bundle = Bundle()
//                bundle.putParcelable("selectedGroup", questionCreatedGroup)
//                navController.navigate(R.id.nav_home, bundle)
//                //                navController.navigate(DashboardFragmentDirections.actionNavPlayGamesSelf(questionCreatedGroup));
//            } else if (actionType == ActionType.SOLO_GAMES) {
////                navController.getCurrentDestination().setLabel(getString(R.string.label_play_solo));
//                val playSoloGroup = PlayGroup()
//                playSoloGroup.playGroupId = "AAA"
//                playSoloGroup.playGroupName = getString(R.string.label_solo_games)
//                playSoloGroup.isDummyGroup = true
//                val bundle = Bundle()
//                bundle.putParcelable("selectedGroup", playSoloGroup)
//                navController.navigate(R.id.nav_home, bundle)
//                //                navController.navigate(DashboardFragmentDirections.actionNavPlayGamesSelf(playSoloGroup));
//            } else if (actionType == ActionType.FRIENDS_PLAYING) {
////                navController.getCurrentDestination().setLabel(getString(R.string.label_play_solo));
//                val playSoloGroup = PlayGroup()
//                playSoloGroup.playGroupId = "FFF"
//                playSoloGroup.playGroupName = getString(R.string.label_friends_playing)
//                playSoloGroup.isDummyGroup = true
//                val bundle = Bundle()
//                bundle.putParcelable("selectedGroup", playSoloGroup)
//                navController.navigate(R.id.nav_home, bundle)
//                //                navController.navigate(DashboardFragmentDirections.actionNavPlayGamesSelf(playSoloGroup));
//            }
//        }


//        System.out.println("KEY_LINK_USER_ID DASHBOARD : " + linkUserId + " KEY_IS_LINK_VERIFIED DASHBOARD : " + isLinkVerified);
        viewModel.callShowBadges.observe(viewLifecycleOwner) { showBadge: Boolean ->
            if (showBadge) {
                badgesDetails
            }
        }

        binding.ivMood.setOnClickListener(ViewClickListener {


            if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                navController.navigate(MobileNavigationDirections.actionGlobalNavShareSomething())
            } else {
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }


        })

        binding.ivSearch.setOnClickListener(ViewClickListener {

            if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                if ((fragmentContext as DashboardActivity).isGuestUser()) {
                    (fragmentContext as DashboardActivity).showGuestPopup(
                        "Moods",
                        "After you login, you can start tracking your and your friend's moods here."
                    )
                } else {
                    binding.clMood.background =
                        AppCompatResources.getDrawable(fragmentContext, R.drawable.bg_cardview_white)

                    if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_home) {
                        if (this.getWatchListResponse != null) {
                            navController.navigate(
                                DashboardFragmentDirections.actionNavHomeToNavDashboardNew(
                                    getWatchListResponse!!
                                )
                            )
                        }
                    }
                }
            } else {
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }

        })

        if ((fragmentContext as DashboardActivity).isGuestUser()) {
            renderGuestViewPager()
            renderGuestTabLayout()
        } else {
            renderViewPager()
            renderTabLayout()
        }

        if (!(fragmentContext as DashboardActivity).isGuestUser()) {
            updateAndroidNotificationStatus(notificationStatus)
            if (!TextUtils.isEmpty(linkUserId)) {
                sendPushNotificationToLinkUser()
            }
        }

        binding.fab.setOnClickListener(ViewClickListener {

            if ((fragmentContext as DashboardActivity).canUserAccessApp) {
                navController.navigate(MobileNavigationDirections.actionGlobalNavShareSomething())
            } else {
                (fragmentContext as DashboardActivity).openSubscriptionStatusDialog()
            }
        })

//        binding!!.viewpager.findViewHolderForAdapterPosition(binding!!.viewpager.currentItem)
//            .addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//
//                    if (dy < 0) {
//                        binding!!.fab.extend()
//                    } else if (dy > 0) {
//                        binding!!.fab.shrink()
//                    }
//                }
//            })


        handleAdminFlow()
    }


    private fun handleAdminFlow() {
        if (BuildConfig.IS_ADMIN) {
            //2023 nt1 jain dev admin
            //4264 info@Onourem prod admin

            var userId = ""

            when (BuildConfig.FLAVOR) {
                "AdminDev" -> userId = "2023"
                "AdminProd" -> userId = "4264"
            }

            if (preferenceHelper!!.getString(KEY_LOGGED_IN_USER_ID) == userId) {
                binding.fabAdmin.visibility = View.VISIBLE

                binding.fab.setOnClickListener(ViewClickListener {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavShareSomething())

                })

//        binding.viewpager.findViewHolderForAdapterPosition(binding.viewpager.currentItem)
//            .addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//
//                    if (dy < 0) {
//                        binding.fab.extend()
//                    } else if (dy > 0) {
//                        binding.fab.shrink()
//                    }
//                }
//            })

                binding.fabAdmin.setOnClickListener(ViewClickListener {
                    navController.navigate(MobileNavigationDirections.actionGlobalNavAdminMenu())
                })

            } else {
                binding.fabAdmin.visibility = View.GONE
//                showAlert("You can't access this app") {
//                    (fragmentContext as DashboardActivity).onLogout()
//                }
            }

        } else {
            binding.fabAdmin.visibility = View.GONE
        }
    }

//    private fun ViewPager2.findViewHolderForAdapterPosition(position: Int): RecyclerView {
//        return (getChildAt(position) as RecyclerView).rootView.findViewById(R.id.rvPlayGames)
//    }


//    private fun setDashboardData() {
//        loginDayActivityInfoList = ArrayList()
//        activityStatusList = ArrayList()
//        gameResIdList = ArrayList()
//        filterItems = ArrayList()
//        activityStatusList!!.addAll((fragmentContext as DashboardActivity).activityStatusList!!)
//        gameResIdList!!.addAll((fragmentContext as DashboardActivity).gameResIdList!!)
//        loginDayActivityInfoList!!.addAll((fragmentContext as DashboardActivity).loginDayActivityInfoList!!)
//        playGroup = (fragmentContext as DashboardActivity).playGroup!!
//        filterItems!!.addAll((fragmentContext as DashboardActivity).filterItems!!)
//        selectedFilterIndex = (fragmentContext as DashboardActivity).selectedFilterIndex!!
//        displayNumberOfActivity = (fragmentContext as DashboardActivity).displayNumberOfActivity
//        counterFriendsPlaying = (fragmentContext as DashboardActivity).counterFriendsPlaying!!
//        isFrom = "Solo"
//        when (selectedFilterIndex) {
//            0 -> {
//                playGroup = PlayGroup()
//                playGroup.playGroupId = "FFF"
//                playGroup.playGroupName = "Friends Playing"
//                playGroup.isDummyGroup = true
//            }
////            1 -> {
////                playGroup = PlayGroup()
////                playGroup.playGroupId = "AAA"
////                playGroup.playGroupName = "New"
////                playGroup.isDummyGroup = true
////            }
//            2 -> {
//                playGroup = PlayGroup()
//                playGroup.playGroupId = "ZZZ"
//                playGroup.playGroupName = "Played"
//                playGroup.isDummyGroup = true
//            }
//            3 -> {
//                playGroup = PlayGroup()
//                playGroup.playGroupId = "YYY"
//                playGroup.playGroupName = "My Qs"
//                playGroup.isDummyGroup = true
//            }
//        }
//        soloGamesAdapter =
//            PlaySoloGamesAdapter(playGroup, loginDayActivityInfoList, preferenceHelper, this)
//        binding.rvPlayGames.adapter = soloGamesAdapter
//        if (selectedFilterIndex != 0) {
//            binding.rvQuesGamesFilter.scrollToPosition(selectedFilterIndex)
//        }
//        layoutManager!!.scrollToPositionWithOffset(
//            (fragmentContext as DashboardActivity).getDashboardRecyclerViewPosition(),
//            (fragmentContext as DashboardActivity).getDashboardRecyclerViewPositionTopView()
//        )
//    }


    private fun getDaysBetweenDates(d1: Date, d2: Date): Long {
        return TimeUnit.MILLISECONDS.toDays(d1.time - d2.time)
    }


//    private fun setDashboardDataToActivity() {
//        var currentVisiblePositionRvPlayGames: Long = 0
//        currentVisiblePositionRvPlayGames =
//            (Objects.requireNonNull(binding.rvPlayGames.layoutManager) as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
//                .toLong()
//        dashboardData = DashboardData(
//            activityStatusList!!,
//            gameResIdList!!,
//            loginDayActivityInfoList!!,
//            playGroup,
//            filterItems!!,
//            selectedFilterIndex,
//            displayNumberOfActivity!!,
//            counterFriendsPlaying,
//            currentVisiblePositionRvPlayGames.toInt()
//        )
////        dashboardData!!.activityStatusList =
////        dashboardData!!.gameResIdList = gameResIdList
////        dashboardData!!.loginDayActivityInfoList = loginDayActivityInfoList
////        dashboardData!!.playGroup = playGroup
////        dashboardData!!.filterItems = filterItems
////        dashboardData!!.selectedFilterIndex = selectedFilterIndex
////        dashboardData!!.displayNumberOfActivity = displayNumberOfActivity
////        dashboardData!!.counterFriendsPlaying = counterFriendsPlaying
////        dashboardData!!.recyclerViewPosition =
////        dashboardData!!.filterRecyclerViewPosition = data.filterRecyclerViewPosition
//
////        val dashboardData = DashboardData(
////            ,
////            ,
////            ,
////            ,
////            ,
////            ,
////            ,
////            ,
////
////        )
//        (fragmentContext as DashboardActivity).setDashboardData(dashboardData!!)
//    }


    //    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (!activityPlayGroupId().equalsIgnoreCase("0")
//                && !activityPlayGroupId().equalsIgnoreCase("1")
//                && !activityPlayGroupId().equalsIgnoreCase("2")
//                && !activityPlayGroupId().equalsIgnoreCase("3")) {
//            questionGamesViewModel!!.actionOpenPlayGroupListingPerformed(true);
//        }
//
//    }
    private fun updateAndroidNotificationStatus(notificationStatus: String) {
        viewModel.updateAndroidNotificationStatus(notificationStatus)
            .observe(viewLifecycleOwner) { apiResponse1: ApiResponse<StandardResponse> ->
                if (apiResponse1.loading) {
                    //  showProgress();
                } else if (apiResponse1.isSuccess && apiResponse1.body != null) {
                    // hideProgress();
//                    if (apiResponse1.body.errorCode.equals("000", ignoreCase = true)) {
//                    } else {
//                        showAlert(apiResponse1.body.errorMessage)
//                    }
                } else {
                    // hideProgress();
                    showAlert(apiResponse1.errorMessage)
                    if (apiResponse1.errorMessage != null
                        && (apiResponse1.errorMessage.contains(getString(R.string.unable_to_connect_host_message))
                                || apiResponse1.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse1.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "updateAndroidNotificationStatus")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "updateAndroidNotificationStatus",
                            apiResponse1.code.toString()
                        )
                    }
                }
            }
    }

    //New VewPager
    private fun renderViewPager() {
        binding.viewpager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> if ((fragmentContext as DashboardActivity).isGuestUser()) GuestFriendsPlayingFragment.create(
                        linkUserId,
                        linkType,
//                        rvScrollListener
                    )
                    else FriendsPlayingFragment.create(
                        linkUserId,
                        linkType,
//                        rvScrollListener,
                    )
//                    1    -> if ((fragmentContext as DashboardActivity).isGuestUser()) GuestQuestionPlayedFragment.create(rvScrollListener)
//                    else QuestionPlayedFragment.create(
//                        rvScrollListener
//                    )
//                    2    -> if ((fragmentContext as DashboardActivity).isGuestUser()) GuestMyQuestionsFragment.create(rvScrollListener)
//                    else MyQuestionsFragment.create(
//                        rvScrollListener
//                    )
//                    else -> if ((fragmentContext as DashboardActivity).isGuestUser()) GuestFriendsPlayingFragment.create(
//                        linkUserId,
//                        linkType,
//                        rvScrollListener
//                    )
                    else -> FriendsPlayingFragment.create(
                        linkUserId,
                        linkType,
//                        rvScrollListener
                    )

                }
            }

            override fun getItemCount(): Int {
                return tabList.size
            }
        }

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        })

    }


    private fun renderGuestViewPager() {
        binding.viewpager.adapter = object : FragmentStateAdapter(this) {

            override fun createFragment(position: Int): Fragment {
                return GuestFriendsPlayingFragment.create(
                    linkUserId,
                    linkType,
//                    rvScrollListener
                )
            }

            override fun getItemCount(): Int {
                return guestTabList.size
            }
        }

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        })

    }

    fun setStyleForTab(tab: TabLayout.Tab, style: Int) {
        tab.view.children.find { it is TextView }?.let { tv ->
            (tv as TextView).post {
                tv.setTypeface(tv.typeface, style)
            }
        }
    }

    private fun renderTabLayout() {

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    setStyleForTab(it, Typeface.NORMAL)
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    setStyleForTab(it, Typeface.BOLD)
                }
            }


        })


        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = getString(tabList[position])
            //tab.icon =  R.drawable.ic_heart)
//            setStyleForTab(tab, Typeface.NORMAL )
            // val tabs = binding.tabs.getChildAt(0) as ViewGroup

            for (i in 0 until (binding.tabs.getChildAt(0) as ViewGroup).childCount) {
                val tabView = (binding.tabs.getChildAt(0) as ViewGroup).getChildAt(i)
                val p = tabView.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(0, 0, 20, 0)
                tabView.requestLayout()
            }
        }.attach()

        binding.viewpager.postDelayed({
            binding.viewpager.currentItem =
                (fragmentContext as DashboardActivity).getDashboardSelectedFilter()
        }, 10)

    }

    private fun renderGuestTabLayout() {

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    setStyleForTab(it, Typeface.NORMAL)
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    setStyleForTab(it, Typeface.BOLD)
                }
            }


        })


        TabLayoutMediator(binding.tabs, binding.viewpager) { tab, position ->
            tab.text = getString(guestTabList[position])
            //tab.icon =  R.drawable.ic_heart)
//            setStyleForTab(tab, Typeface.NORMAL )
            // val tabs = binding.tabs.getChildAt(0) as ViewGroup

            for (i in 0 until (binding.tabs.getChildAt(0) as ViewGroup).childCount) {
                val tabView = (binding.tabs.getChildAt(0) as ViewGroup).getChildAt(i)
                val p = tabView.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(0, 0, 20, 0)
                tabView.requestLayout()
            }
        }.attach()

    }

    companion object {

        val tabList = listOf(
            R.string.tabFriendsPlaying,
//            R.string.tabPlayed,
//            R.string.tabMyQuestions
        )

        val guestTabList = listOf(
            R.string.tabFriendsPlaying,
        )
    }

    private fun sendPushNotificationToLinkUser() {

        viewModel.sendPushNotificationToLinkUser(linkUserId, "8")
            .observe(viewLifecycleOwner) { apiResponse1: ApiResponse<SendPushNotificationToLinkUserResponse> ->
                if (apiResponse1.loading) {
                    //  showProgress();
                } else if (apiResponse1.isSuccess && apiResponse1.body != null) {
                    // hideProgress();
                    if (apiResponse1.body.errorCode.equals("000", ignoreCase = true)) {

                        AppUtilities.showLog(
                            "Link", "LinkType :${
                                if (linkType != "") {
                                    linkType
                                } else "Api getting called without LinkUserId"
                            } API: sendPushNotificationToLinkUser | sent LinkUserId = $linkUserId"
                        )


                        if (apiResponse1.body.message != null && !apiResponse1.body.message.equals(
                                "",
                                ignoreCase = true
                            )
                        ) {
                            showAlert(apiResponse1.body.message)
                        }
                    } else {
                        showAlert(apiResponse1.body.errorMessage)
                    }
                } else {
                    // hideProgress();
                    showAlert(apiResponse1.errorMessage)
                    if (apiResponse1.errorMessage != null
                        && (apiResponse1.errorMessage.contains(getString(R.string.unable_to_connect_host_message))
                                || apiResponse1.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse1.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "sendPushNotificationToLinkUser")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "sendPushNotificationToLinkUser",
                            apiResponse1.code.toString()
                        )
                    }
                }
            }
    }

}