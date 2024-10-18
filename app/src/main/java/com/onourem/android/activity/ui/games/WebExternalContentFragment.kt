package com.onourem.android.activity.ui.games

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.*
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentWebBinding
import com.onourem.android.activity.ui.settings.SettingsViewModel

class WebExternalContentFragment :
    AbstractBaseViewModelBindingFragment<SettingsViewModel, FragmentWebBinding>() {
    override fun viewModelType(): Class<SettingsViewModel> {
        return SettingsViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_web
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webContentFragmentArgs = WebExternalContentFragmentArgs.fromBundle(
            requireArguments()
        )
        setData(webContentFragmentArgs)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setData(response: WebExternalContentFragmentArgs) {
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                showProgress()
                super.onPageStarted(view, url, favicon)
            }

            //            override fun shouldOverrideUrlLoading(
//                view: WebView?,
//                request: WebResourceRequest?
//            ): Boolean {
//                view!!.loadUrl(request!!.url.toString())
//
//                return super.shouldOverrideUrlLoading(view, request)
//            }
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view!!.loadUrl(url!!)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                hideProgress()
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                hideProgress()
                super.onReceivedError(view, request, error)
            }

            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                hideProgress()
                super.onReceivedSslError(view, handler, error)
                handler!!.proceed()
            }
        }
        //        binding.webView.setWebChromeClient(new WebChromeClient(){
//
//        });
        val settings = binding.webView.settings
        settings.defaultTextEncodingName = "utf-8"
        settings.javaScriptEnabled = true
        settings.textZoom = (100 * 0.75).toInt()
        settings.cursiveFontFamily = "montserrat"
        binding.webView.loadUrl(response.webUrl)
    }
}