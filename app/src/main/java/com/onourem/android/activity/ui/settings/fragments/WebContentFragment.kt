package com.onourem.android.activity.ui.settings.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentWebBinding
import com.onourem.android.activity.models.GetTermsConditionsResponse
import com.onourem.android.activity.models.WebContent
import com.onourem.android.activity.ui.settings.SettingsViewModel
import com.onourem.android.activity.ui.utils.AppUtilities

class WebContentFragment :
    AbstractBaseViewModelBindingFragment<SettingsViewModel, FragmentWebBinding>() {
    override fun viewModelType(): Class<SettingsViewModel> {
        return SettingsViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_web
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webContentFragmentArgs = WebContentFragmentArgs.fromBundle(
            requireArguments()
        )
        val webContent = webContentFragmentArgs.webContent
        if (webContent.tnc.equals("TCPP", ignoreCase = true)) {
            binding.view4.visibility = View.VISIBLE
            loadSignupData(webContent)
        } else {
            binding.view4.visibility = View.GONE
            loadData(webContent)
        }
    }

    private fun loadSignupData(webContent: WebContent) {
        viewModel.getTermsAndConditionAndPolicy("", webContent.screenId!!, webContent.property!!)
            .observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<GetTermsConditionsResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        val response = apiResponse.body
                        setData(response)
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
                            AppUtilities.showLog("Network Error", "getTermsConditions")
                        }
//                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
//                            "getTermsConditions",
//                            apiResponse.code.toString()
//                        )
                    }
                }
            }
    }

    private fun loadData(webContent: WebContent) {
        viewModel.getTermsConditions(webContent.tnc!!, webContent.screenId!!, webContent.property!!)
            .observe(
                viewLifecycleOwner
            ) { apiResponse: ApiResponse<GetTermsConditionsResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        val response = apiResponse.body
                        setData(response)
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
                            AppUtilities.showLog("Network Error", "getTermsConditions")
                        }
//                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
//                            "getTermsConditions",
//                            apiResponse.code.toString()
//                        )
                    }
                }
            }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setData(response: GetTermsConditionsResponse?) {
        binding.webView.webViewClient = WebViewClient()
        binding.webView.webChromeClient = WebChromeClient()
        val settings = binding.webView.settings
        settings.defaultTextEncodingName = "utf-8"
        settings.javaScriptEnabled = true
        settings.textZoom = (100 * 0.53).toInt()
//        settings.cursiveFontFamily = "montserrat"
//        settings.fixedFontFamily = "montserrat"
        binding.webView.loadDataWithBaseURL(null, response!!.htmlText!!, "text/html", "UTF-8", null)
    }
}