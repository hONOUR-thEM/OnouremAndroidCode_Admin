package com.onourem.android.activity.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.tasks.Task
import com.google.android.material.card.MaterialCardView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.activity.AbstractBaseActivity
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.IntroResponse
import com.onourem.android.activity.models.LinkData
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.utils.AppValidate
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.viewmodel.OnboardingViewModel
import javax.inject.Inject

class OnboardingActivity : AbstractBaseActivity<OnboardingViewModel>(),
    NavController.OnDestinationChangedListener {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var navController: NavController? = null
    override fun layoutResource(): Int {
        return R.layout.activity_onboarding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val cvMood = findViewById<MaterialCardView>(R.id.cvMood)

        cvMood.visibility = View.GONE
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val host = NavHostFragment.create(R.navigation.onboarding_navigation)
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainerView, host, "onboarding-nav-host")
            .commitNow()
        navController = host.navController
        navController!!.addOnDestinationChangedListener(this)
        dynamicLinkFirebase()
        if (preferenceHelper!!.getBoolean(Constants.KEY_IS_LOGGED_IN)) {
            val loginResponse = Gson().fromJson(
                preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
                LoginResponse::class.java
            )
            if (loginResponse != null) {
                val crashlytics = FirebaseCrashlytics.getInstance()
                crashlytics.setUserId(loginResponse.userId)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
            if (task.isSuccessful) {
                preferenceHelper!!.putValue(Constants.KEY_FCM_TOKEN, task.result)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        dynamicLinkFirebase()
        preferenceHelper!!.putValue(Constants.KEY_SELECTED_FILTER_INT, "")
    }

    override fun viewModelType(): Class<OnboardingViewModel> {
        return OnboardingViewModel::class.java
    }

    private fun dynamicLinkFirebase() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData: PendingDynamicLinkData? ->
                if (pendingDynamicLinkData != null) {
                    //String postId = pendingDynamicLinkData.getLink().getQueryParameter("postId");
                    val linkUserId = pendingDynamicLinkData.link.getQueryParameter("linkUserId")
                    val linkType = pendingDynamicLinkData.link.getQueryParameter("linkType")
                    //String campaignId = pendingDynamicLinkData.getLink().getQueryParameter(ISysCodes.CAMPAIGN_ID_PARAMETER_NAME);
                    //String linkType = pendingDynamicLinkData.getLink().getQueryParameter(ISysCodes.DYNAMIC_LINK_TYPE_PARAMETER_NAME);
//                        boolean isUserLoggedIn = preferenceHelper.getBoolean(KEY_IS_LOGGED_IN);
                    if (AppValidate.isNotEmpty(linkUserId)) {
                        preferenceHelper!!.putValue(Constants.KEY_LINK_USER_ID, linkUserId)
                        preferenceHelper!!.putValue(Constants.KEY_LINK_TYPE, linkType)
                        preferenceHelper!!.putValue(
                            Constants.KEY_DYNAMIC_LINK,
                            pendingDynamicLinkData.link.toString()
                        )
                        introService()
                    }
                } else {
                    viewModel.setCheckLink(false)
                }
            }
            .addOnFailureListener(this) { e: Exception? ->
                // showAlert("Deep link fetch failed." + e.getLocalizedMessage(), v -> introService());
                introService()
            }
            .addOnCompleteListener { task: Task<PendingDynamicLinkData?>? -> }
    }

    private fun introService() {
        viewModel.intro().observe(this) { apiResponse: ApiResponse<IntroResponse> ->
            if (apiResponse.loading) {
                //showProgress();
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                // hideProgress();
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    //Log.d("**** Activity", "Success");
                    preferenceHelper!!.putValue(
                        Constants.KEY_IS_LINK_VERIFIED,
                        apiResponse.body.linkVerified.toString()
                    )
                    preferenceHelper!!.putValue(
                        Constants.KEY_CAN_APP_INSTALLED_DIRECTLY,
                        apiResponse.body.canAppInstalledDirectly
                    )
                    val linkData = LinkData()
                    linkData.linkUserId = preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID)
                    linkData.linkType = preferenceHelper!!.getString(Constants.KEY_LINK_TYPE)
                    linkData.isVerified =
                        preferenceHelper!!.getString(Constants.KEY_IS_LINK_VERIFIED)
                    linkData.canInstallDirectly =
                        preferenceHelper!!.getString(Constants.KEY_CAN_APP_INSTALLED_DIRECTLY)
                    preferenceHelper!!.putValue(
                        Constants.KEY_INTRO_VIDEO_HINDI,
                        apiResponse.body.productTourVideo1
                    )
                    preferenceHelper!!.putValue(
                        Constants.KEY_INTRO_VIDEO_ENGLISH,
                        apiResponse.body.productTourVideo2
                    )
                    viewModel.setLinkLiveData(linkData)
                    viewModel.setCheckLink(true)
                } else {
                    //Log.d("**** Activity", "Failure");
                    showAlert(apiResponse.body.errorMessage)
                }
            } else {
                //  hideProgress();
                //Log.d("**** Activity", "Error Failure");
                showAlert(apiResponse.errorMessage)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return (navController!!.navigateUp()
                || super.onSupportNavigateUp())
    }

    override fun onBackPressed() {
        viewModel.setCheckUserVerification("")
        if (navController!!.currentDestination != null && (R.id.nav_onboarding == navController!!.currentDestination!!.id || R.id.nav_login == navController!!.currentDestination!!.id || R.id.nav_splash == navController!!.currentDestination!!.id)) super.onBackPressed() else navController!!.navigateUp()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val tvAppTitle = findViewById<AppCompatTextView>(R.id.tvAppTitle)
        val parentLayout = findViewById<View>(R.id.parentLayout)
        findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
        if (destination.id == R.id.nav_onboarding || destination.id == R.id.nav_splash) {
            tvAppTitle.visibility = View.GONE
        } else {
            tvAppTitle.visibility = View.VISIBLE
            tvAppTitle.text = destination.label
            tvAppTitle.isAllCaps = true
        }
        //        if (destination.getId() == R.id.nav_splash) {
//            findViewById(R.id.toolbar).setVisibility(View.GONE);
//            parentLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.color_splash_header_shape));
//        } else {
//            findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
//            parentLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
//        }
        if (destination.id == R.id.nav_splash || destination.id == R.id.nav_onboarding || destination.id == R.id.nav_login) {
            supportActionBar!!.setDisplayShowHomeEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        } else {
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

//        if (destination.getId() == R.id.nav_onboarding) {
//            tvAppTitle.setVisibility(View.INVISIBLE);
//        } else {
//            tvAppTitle.setVisibility(View.VISIBLE);
//            tvAppTitle.setText(destination.getLabel());
//        }
    }

    override fun onLogout() {

        NotificationManagerCompat.from(this).cancelAll()
        //viewModel.resetSilentCount(preferenceHelper.getString(Constants.KEY_LOGGED_IN_USER_ID));
        val canAppInstalledDirectly = preferenceHelper!!.getString(Constants.KEY_CAN_APP_INSTALLED_DIRECTLY)
        val todayAnswerCount = preferenceHelper!!.getInt(Constants.KEY_TOTAL_QUES_ANSWERED_TODAY)
        val totalAnswerCount = preferenceHelper!!.getInt(Constants.KEY_TOTAL_QUES_ANSWERED_BEFORE_TODAY)
        val noOfTimeRequestReviewRaised = preferenceHelper!!.getInt(Constants.KEY_NUMBER_OF_TIME_APP_REVIEW_REQUESTS_RAISED)
        val lastTimeReviewShownDate = preferenceHelper!!.getString(Constants.KEY_LAST_TIME_WHEN_REVIEW_POPUP_WAS_SHOWN)
        preferenceHelper!!.clear()
        preferenceHelper!!.putValue(
            Constants.KEY_CAN_APP_INSTALLED_DIRECTLY, canAppInstalledDirectly
        )
        preferenceHelper!!.putValue(Constants.KEY_TOTAL_QUES_ANSWERED_TODAY, todayAnswerCount)
        preferenceHelper!!.putValue(
            Constants.KEY_TOTAL_QUES_ANSWERED_BEFORE_TODAY, totalAnswerCount
        )
        preferenceHelper!!.putValue(
            Constants.KEY_NUMBER_OF_TIME_APP_REVIEW_REQUESTS_RAISED, noOfTimeRequestReviewRaised
        )
        preferenceHelper!!.putValue(
            Constants.KEY_LAST_TIME_WHEN_REVIEW_POPUP_WAS_SHOWN, lastTimeReviewShownDate
        )
    }

    fun addNetworkErrorUserInfo(serviceName: String?, networkErrorCode: String?) {

        viewModel.addNetworkErrorUserInfo(serviceName!!, networkErrorCode!!)
            .observe(this) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    //showProgress();
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    //hideProgress();
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    } else {
                        // showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    //  hideProgress();
                    // showAlert(apiResponse.errorMessage)
                }
            }

    }
}