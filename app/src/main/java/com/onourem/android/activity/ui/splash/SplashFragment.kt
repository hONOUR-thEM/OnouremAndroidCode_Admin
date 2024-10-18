package com.onourem.android.activity.ui.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentSplashBinding
import com.onourem.android.activity.models.IntroResponse
import com.onourem.android.activity.models.LinkData
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.onboarding.OnboardingActivity
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.viewmodel.OnboardingViewModel
import javax.inject.Inject

class SplashFragment :
    AbstractBaseViewModelBindingFragment<OnboardingViewModel, FragmentSplashBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun layoutResource(): Int {
        return R.layout.fragment_splash
    }

    override fun viewModelType(): Class<OnboardingViewModel> {
        return OnboardingViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
                if (task.isSuccessful) {
                    preferenceHelper!!.putValue(Constants.KEY_FCM_TOKEN, task.result)
                    preferenceHelper!!.putValue(Constants.KEY_DEVICE_MODEL, Build.MODEL)
                    preferenceHelper!!.putValue(Constants.KEY_DEVICE_NAME, deviceName)
                    preferenceHelper!!.putValue(
                        Constants.KEY_APP_VERSION,
                        AppUtilities.getAppVersion().toString()
                    )
                    preferenceHelper!!.putValue(Constants.KEY_OS_VERSION, Build.VERSION.RELEASE)

                }
            }
        }

        if (requireActivity().intent != null && requireActivity().intent.hasExtra(
                Constants.KEY_USER_EMAIL
            )
            && requireActivity().intent.getStringExtra(Constants.KEY_USER_EMAIL) != null
        ) {
            val emailId =
                requireActivity().intent.getStringExtra(Constants.KEY_USER_EMAIL)
            if (emailId != null) {
                if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_splash) {
                    navController.navigate(
                        SplashFragmentDirections.actionNavSplashToNavLogin(
                            emailId
                        )
                    )
                }
            }
        } else {
            readQueryParameters()
        }

    }

    private fun readQueryParameters() {
        viewModel.checkLink.observe(viewLifecycleOwner) { checkLink: Boolean? -> introService() }
    }

    private fun introService() {
        if (view != null) {
            viewModel.intro()
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<IntroResponse> ->
                    if (apiResponse.loading) {
//                    showProgress();
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                            // Log.d("**** Splash", "Success");
                            preferenceHelper!!.putValue(
                                Constants.KEY_IS_LINK_VERIFIED,
                                apiResponse.body.linkVerified.toString()
                            )
                            preferenceHelper!!.putValue(
                                Constants.KEY_CAN_APP_INSTALLED_DIRECTLY,
                                apiResponse.body.canAppInstalledDirectly
                            )
                            val linkData = LinkData()
                            linkData.linkUserId =
                                preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID)
                            linkData.linkType =
                                preferenceHelper!!.getString(Constants.KEY_LINK_TYPE)
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
                            processAsNormal(linkData)
                        } else {
                            //Log.d("**** Splash", "Failure");
                            showAlert(apiResponse.body.errorMessage)
                        }
                    } else {
                        hideProgress()
                        // Log.d("**** Splash", "Error Failure");
                        showAlert(apiResponse.errorMessage)
                        if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(
                                getString(R.string.unable_to_connect_host_message1)
                            )
                                    || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                        ) {
                            if (BuildConfig.DEBUG) {
                                AppUtilities.showLog("Network Error", "getAppDemoForUser")
                            }
//                            (fragmentContext as OnboardingActivity).addNetworkErrorUserInfo(
//                                "getAppDemoForUser",
//                                apiResponse.code.toString()
//                            )
                        }
                    }
                }
        }
    }

    private fun processAsNormal(linkData: LinkData) {
        if (preferenceHelper!!.getBoolean(Constants.KEY_IS_LOGGED_IN)) {
            val loginResponse = Gson().fromJson(
                preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
                LoginResponse::class.java
            )
            if (loginResponse != null) {
                if (loginResponse.isRegistrationStaus) {
                    if (TextUtils.isEmpty(loginResponse.dateOfBirth)) {
//                        showAlert("Date Of Birth Screen");
                        Handler(Looper.getMainLooper()).postDelayed({
                            if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_splash) {
                                navController.navigate(
                                    SplashFragmentDirections.actionNavSplashToNavLogin(
                                        loginResponse.emailAddress
                                    )
                                )
                            }
                        }, 100)
                    } else {
                        preferenceHelper!!.putValue(Constants.KEY_LINK_USER_ID, linkData.linkUserId)
                        preferenceHelper!!.putValue(Constants.KEY_LINK_TYPE, linkData.linkType)
                        preferenceHelper!!.putValue(
                            Constants.KEY_DYNAMIC_LINK,
                            linkData.pendingDynamicLinkData
                        )
                        startActivity(
                            Intent(
                                requireActivity(),
                                DashboardActivity::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .putExtra("linkData", linkData)
                        )
                        (fragmentContext as OnboardingActivity).finishAffinity()
                        requireActivity().finish()
                    }
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_splash) {
                            navController.navigate(
                                SplashFragmentDirections.actionNavSplashToNavLogin(
                                    loginResponse.emailAddress
                                )
                            )
                        }
                    }, 100)
                }
            }
        } else {
            if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_splash) {
                navController.navigate(SplashFragmentDirections.actionNavSplashToNavOnboarding())
            }
        }
    }

    companion object {
        /**
         * Returns the consumer friendly device name
         */
        val deviceName: String
            get() {
                val manufacturer = Build.MANUFACTURER
                val model = Build.MODEL
                return if (model.startsWith(manufacturer)) {
                    capitalize(model)
                } else capitalize(manufacturer) + " " + model
            }

        private fun capitalize(str: String): String {
            if (TextUtils.isEmpty(str)) {
                return str
            }
            val arr = str.toCharArray()
            var capitalizeNext = true
            val phrase = StringBuilder()
            for (c in arr) {
                if (capitalizeNext && Character.isLetter(c)) {
                    phrase.append(c.uppercaseChar())
                    capitalizeNext = false
                    continue
                } else if (Character.isWhitespace(c)) {
                    capitalizeNext = true
                }
                phrase.append(c)
            }
            return phrase.toString()
        }
    }
}