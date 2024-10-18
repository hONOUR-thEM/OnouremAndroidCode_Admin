package com.onourem.android.activity.ui.onboarding.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentVerificationBinding
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.models.SendPushNotificationToLinkUserResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.OnboardingViewModel
import javax.inject.Inject

class VerificationFragment :
    AbstractBaseViewModelBindingFragment<OnboardingViewModel, FragmentVerificationBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var strEmail: String? = null
    private var isLinkVerified = "false"
    private var linkUserId: String? = null
    private var linkType = ""
    override fun layoutResource(): Int {
        return R.layout.fragment_verification
    }

    override fun viewModelType(): Class<OnboardingViewModel> {
        return OnboardingViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val verificationFragmentArgs = VerificationFragmentArgs.fromBundle(
            requireArguments()
        )
        strEmail = verificationFragmentArgs.emailId
        linkUserId = preferenceHelper!!.getString(Constants.KEY_LINK_USER_ID)
        linkType = preferenceHelper!!.getString(Constants.KEY_LINK_TYPE)
        isLinkVerified = preferenceHelper!!.getString(Constants.KEY_IS_LINK_VERIFIED)
        binding.btnContinue.setOnClickListener(ViewClickListener { v: View? ->
            if (isConnected) {
                viewModel.registrationStatus().observe(viewLifecycleOwner) { apiResponse: ApiResponse<LoginResponse> ->
                    if (apiResponse.loading) {
                        showProgress()
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                        val gson = Gson()
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                            val loginResponse = gson.fromJson(
                                preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
                                LoginResponse::class.java
                            )
                            if (apiResponse.body.isRegistrationStaus) {
                                viewModel.setCheckUserVerification("goToVerification")
                                loginResponse.isRegistrationStaus = apiResponse.body.isRegistrationStaus
                                preferenceHelper!!.putValue(
                                    Constants.KEY_USER_OBJECT,
                                    gson.toJson(loginResponse)
                                )
                                sendPushNotificationToLinkUser()
                            } else {
                                showAlert("Please verify your account, It is not verified yet.")
                            }
                        } else {
                            showAlert(apiResponse.body.errorMessage)
                        }
                    } else {
                        hideProgress()
                        showAlert(apiResponse.errorMessage)
                    }
                }
            } else {
                showAlert(
                    getString(R.string.network_error_title),
                    getString(R.string.no_internet_message)
                )
            }
        })
        binding.btnResetPassword.setOnClickListener(ViewClickListener { v: View? ->
            viewModel.resendEmailVerification().observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        viewModel.setCheckUserVerification("")
                        showAlert(
                            String.format(
                                "To verify your account, just click on the link in the email sent by us to %s",
                                strEmail
                            )
                        )
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                }
            }
        })
        binding.btnGoBack.setOnClickListener(ViewClickListener { v: View? ->
            viewModel.setCheckUserVerification("")
            navController.popBackStack()
        })
    }

    private val isConnected: Boolean
        get() {
            val connectivityManager =
                requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }

    private fun sendPushNotificationToLinkUser() {
        viewModel.sendPushNotificationToLinkUser(linkUserId!!, "2")
            .observe(viewLifecycleOwner) { apiResponse1: ApiResponse<SendPushNotificationToLinkUserResponse> ->
                if (apiResponse1.loading) {
                    showProgress()
                } else if (apiResponse1.isSuccess && apiResponse1.body != null) {
                    hideProgress()
                    if (apiResponse1.body.errorCode.equals("000", ignoreCase = true)) {
//                    linkUserId = "";
//                    isLinkVerified = "false";
                        preferenceHelper!!.putValue(Constants.KEY_LINK_USER_ID, linkUserId)
                        preferenceHelper!!.putValue(Constants.KEY_LINK_TYPE, linkType)
                        preferenceHelper!!.putValue(Constants.KEY_IS_LINK_VERIFIED, isLinkVerified)
                        if (navController.currentDestination != null && navController.currentDestination!!.id == R.id.nav_verification) {
                            navController.navigate(VerificationFragmentDirections.actionNavVerificationToNavDatePicker())
                        }
                    } else {
                        showAlert(apiResponse1.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse1.errorMessage)
                    if (preferenceHelper!!.getBoolean(Constants.KEY_IS_LOGGED_IN)) if (apiResponse1.errorMessage != null
                        && (apiResponse1.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
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