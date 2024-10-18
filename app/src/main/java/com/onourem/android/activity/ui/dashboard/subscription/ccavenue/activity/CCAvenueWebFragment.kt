package com.onourem.android.activity.ui.dashboard.subscription.ccavenue.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentCcavenueWebBinding
import com.onourem.android.activity.models.PaymentStatus
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.io.UnsupportedEncodingException

class CCAvenueWebFragment :
    AbstractBaseViewModelBindingFragment<DashboardViewModel, FragmentCcavenueWebBinding>() {
    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_ccavenue_web
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderInfoRes = CCAvenueWebFragmentArgs.fromBundle(requireArguments()).orderInfo

        class MyJavaScriptInterface {
            private lateinit var orderStatus: String

            @JavascriptInterface
            fun processHTML(html: String) {
                // process the html source code to get final status of transaction
                var status: String? = null
                status = if (html.indexOf("Failure") != -1) {
                    "Failed"
                } else if (html.indexOf("Success") != -1) {
                    "Successful"
                } else if (html.indexOf("Aborted") != -1) {
                    "Aborted"
                } else {
                    "Error"
                }
                //Toast.makeText(this@WebViewActivity, status, Toast.LENGTH_SHORT).show();
                val paymentStatus = PaymentStatus()
                paymentStatus.status = status
                paymentStatus.html = html
                paymentStatus.orderId = orderInfoRes.createdOrderId
                paymentStatus.packageId = orderInfoRes.packageCode
                paymentStatus.discountCode = orderInfoRes.discountCode


                viewModel.updateOrderStatus(
                    orderInfoRes.createdOrderId?:"",
                    html,
                    orderInfoRes.packageCode?:"",
                    orderInfoRes.discountCode?:"",
                    orderStatus
                )
                    .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StandardResponse> ->
                        if (apiResponse.isSuccess && apiResponse.body != null) {
                            if (apiResponse.loading) {
                                showProgress()
                            }
                            if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                                if (!TextUtils.isEmpty(apiResponse.body.message)) {
                                    showAlert(apiResponse.body.message)
                                }else{
                                    navController.popBackStack()
                                    viewModel.setUpdatePaymentStatus(paymentStatus)
                                }
                                hideProgress()
                            } else {
                                hideProgress()
                                showAlert(apiResponse.body.errorMessage)
                            }
                        }
                    }
            }
        }

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(binding.webView, url)
                hideProgress()
//                if (url!!.indexOf("/ccavResponseHandler.jsp") != -1) {
//                    webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
//                }

                if (url!! == orderInfoRes.redirectUrl) {
                    binding.webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showProgress()
            }
        }
        try {
            val url = orderInfoRes.transactionUrl
            binding.webView.loadUrl(url?:"");
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }
}