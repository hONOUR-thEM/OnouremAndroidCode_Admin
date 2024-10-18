package com.onourem.android.activity.ui.dashboard.subscription.ccavenue.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.util.Pair
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.activity.AbstractBaseActivity
import com.onourem.android.activity.models.PaymentStatus
import com.onourem.android.activity.ui.dashboard.subscription.ccavenue.utility.AvenuesParams
import com.onourem.android.activity.ui.games.dialogs.TwoActionAlertDialog
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.io.UnsupportedEncodingException


class WebViewActivity : AbstractBaseActivity<DashboardViewModel>() {
    var mainIntent: Intent? = null
    var encVal: String? = null
    var vResponse: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        mainIntent = intent
        dumpIntent(mainIntent)

        //get_RSA_key(mainIntent!!.getStringExtra(AvenuesParams.ACCESS_CODE), mainIntent!!.getStringExtra(AvenuesParams.ORDER_ID));
        class MyJavaScriptInterface {
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
                paymentStatus.orderId = mainIntent!!.getStringExtra(AvenuesParams.ORDER_ID)
                paymentStatus.packageId = mainIntent!!.getStringExtra(AvenuesParams.PACKAGE_ID)
                paymentStatus.discountCode = mainIntent!!.getStringExtra(AvenuesParams.DISCOUNT_CODE)

                val data = Intent()
                data.putExtra("paymentStatus", paymentStatus)
                setResult(RESULT_OK, data)
                finish()

            }
        }

        val webView = findViewById<View>(R.id.webview) as WebView
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")
        webView.webViewClient = object : WebViewClient() {

            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this@WebViewActivity)
                var message = "SSL Certificate error."
                when (error.primaryError) {
                    SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                    SslError.SSL_EXPIRED -> message = "The certificate has expired."
                    SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                    SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
                }
                message += " Do you want to continue anyway?"
                builder.setTitle("SSL Certificate Error")
                builder.setMessage(message)
                builder.setPositiveButton("continue", DialogInterface.OnClickListener { dialog, which -> handler.proceed() })
                builder.setNegativeButton("cancel", DialogInterface.OnClickListener { dialog, which -> handler.cancel() })
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(webView, url)

                hideProgress()
//                if (url!!.indexOf("/ccavResponseHandler.jsp") != -1) {
//                    webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
//                }

                if (url!! == mainIntent!!.getStringExtra(AvenuesParams.REDIRECT_URL)) {
                    webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showProgress()
            }
        }
        try {
            val url = mainIntent!!.getStringExtra(AvenuesParams.TRANS_URL) + ""
            webView.loadUrl(url)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.activity_webview
    }

    override fun onLogout() {}

//    private inner class RenderView : AsyncTask<Void, Void?, Void?>() {
//        override fun onPreExecute() {
//            super.onPreExecute()
//            // Showing progress dialog
//            LoadingDialog.showLoadingDialog(this@WebViewActivity, "Loading...")
//        }
//
//        protected override fun doInBackground(vararg arg0: Void): Void? {
//            if (ServiceUtility.chkNull(vResponse) != ""
//                && ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR") == -1
//            ) {
//                val vEncVal = StringBuffer("")
//                vEncVal.append(
//                    ServiceUtility.addToPostParams(
//                        AvenuesParams.AMOUNT,
//                        mainIntent!!.getStringExtra(AvenuesParams.AMOUNT)
//                    )
//                )
//                vEncVal.append(
//                    ServiceUtility.addToPostParams(
//                        AvenuesParams.CURRENCY,
//                        mainIntent!!.getStringExtra(AvenuesParams.CURRENCY)
//                    )
//                )
//                encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length - 1), vResponse) //encrypt amount and currency
//            }
//            return null
//        }
//
//        @SuppressLint("SetJavaScriptEnabled")
//        override fun onPostExecute(result: Void?) {
//            super.onPostExecute(result)
//            // Dismiss the progress dialog
//            LoadingDialog.cancelLoading()
//            class MyJavaScriptInterface {
//                @JavascriptInterface
//                fun processHTML(html: String) {
//                    // process the html source code to get final status of transaction
//                    var status: String? = null
//                    status = if (html.indexOf("Failure") != -1) {
//                        "Transaction Declined!"
//                    } else if (html.indexOf("Success") != -1) {
//                        "Transaction Successful!"
//                    } else if (html.indexOf("Aborted") != -1) {
//                        "Transaction Cancelled!"
//                    } else {
//                        "Status Not Known!"
//                    }
//                    Toast.makeText(applicationContext, status, Toast.LENGTH_SHORT).show()
//                    val paymentStatus = PaymentStatus()
//                    paymentStatus.status = status
//                    paymentStatus.html = html
//                    paymentStatus.orderId = mainIntent!!.getStringExtra(AvenuesParams.ORDER_ID)
//                    paymentStatus.packageId = mainIntent!!.getStringExtra(AvenuesParams.PACKAGE_ID)
//                    viewModel!!.setUpdatePaymentStatus(paymentStatus)
//                    AppUtilities.showLog("CCAVenue", html)
//                }
//            }
//
//            val webview = findViewById<View>(R.id.webview) as WebView
//            webview.settings.javaScriptEnabled = true
//            webview.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")
//            webview.webViewClient = object : WebViewClient() {
//                override fun onPageFinished(view: WebView, url: String) {
//                    super.onPageFinished(webview, url)
//                    LoadingDialog.cancelLoading()
//                    if (url.indexOf("/ccavResponseHandler.jsp") != -1) {
//                        webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
//                    }
//                }
//
//                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
//                    super.onPageStarted(view, url, favicon)
//                    LoadingDialog.showLoadingDialog(this@WebViewActivity, "Loading...")
//                }
//            }
//            try {
//                val postData = AvenuesParams.ACCESS_CODE + "=" + URLEncoder.encode(
//                    mainIntent!!.getStringExtra(AvenuesParams.ACCESS_CODE),
//                    "UTF-8"
//                ) + "&" + AvenuesParams.MERCHANT_ID + "=" + URLEncoder.encode(
//                    mainIntent!!.getStringExtra(AvenuesParams.MERCHANT_ID), "UTF-8"
//                ) + "&" + AvenuesParams.ORDER_ID + "=" + URLEncoder.encode(
//                    mainIntent!!.getStringExtra(AvenuesParams.ORDER_ID), "UTF-8"
//                ) + "&" + AvenuesParams.REDIRECT_URL + "=" + URLEncoder.encode(
//                    mainIntent!!.getStringExtra(AvenuesParams.REDIRECT_URL), "UTF-8"
//                ) + "&" + AvenuesParams.CANCEL_URL + "=" + URLEncoder.encode(
//                    mainIntent!!.getStringExtra(AvenuesParams.CANCEL_URL), "UTF-8"
//                ) + "&" + AvenuesParams.ENC_VAL + "=" + URLEncoder.encode(encVal, "UTF-8")
//                webview.postUrl(mainIntent!!.getStringExtra(AvenuesParams.TRANS_URL)!!, postData.toByteArray())
//                //                webview.postUrl(Constants.TRANS_URL, postData.getBytes());
//                AppUtilities.showLog("CCAVenue", postData)
//            } catch (e: UnsupportedEncodingException) {
//                e.printStackTrace()
//            }
//        }
//    }

//    fun get_RSA_key(ac: String, od: String) {
//        LoadingDialog.showLoadingDialog(this@WebViewActivity, "Loading...")
//        val errorListener = Response.ErrorListener { error: VolleyError ->
//            error.printStackTrace()
//            AppUtilities.showLog("CCAVenue", error.networkResponse.toString())
//            LoadingDialog.cancelLoading()
//            Toast.makeText(this@WebViewActivity, error.networkResponse.toString(), Toast.LENGTH_LONG).show()
//        }
//        val stringRequest: StringRequest = object : StringRequest(
//            Method.POST,
//            mainIntent!!.getStringExtra(AvenuesParams.RSA_KEY_URL),
//            Response.Listener { response: String? ->
//                //Toast.makeText(WebViewActivity.this,response,Toast.LENGTH_LONG).show();
//                LoadingDialog.cancelLoading()
//                if (response != null && response != "") {
//                    vResponse = response //save retrived rsa key
//                    if (vResponse!!.contains("!ERROR!")) {
//                        show_alert(vResponse!!)
//                    } else {
//                        RenderView().execute() // Calling async task to get display content
//                    }
//                } else {
//                    show_alert("No response")
//                }
//            },
//            errorListener
//        ) {
//            override fun getParams(): Map<String, String>? {
//                val params: MutableMap<String, String> = HashMap()
//                params[AvenuesParams.ACCESS_CODE] = ac
//                params[AvenuesParams.ORDER_ID] = od
//                return params
//            }
//        }
//        stringRequest.retryPolicy = DefaultRetryPolicy(
//            30000,
//            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        )
//        val requestQueue = Volley.newRequestQueue(this)
//        requestQueue.add(stringRequest)
//    }


    companion object {
        fun dumpIntent(i: Intent?) {
            val bundle = i!!.extras
            if (bundle != null) {
                val keys = bundle.keySet()
                val it: Iterator<String> = keys.iterator()
                Log.e("CCAVenue", "Dumping Intent start")
                while (it.hasNext()) {
                    val key = it.next()
                    Log.e(
                        "CCAVenue", """ [$key=${bundle[key]}]""".trimIndent()
                    )
                }
                Log.e("CCAVenue", "Dumping Intent end")
            }
        }
    }

    override fun onBackPressed() {
        TwoActionAlertDialog.showAlert(
            this,
            getString(R.string.label_confirm),
            "Would you like to exit?",
            null,
            "Cancel",
            "Yes"
        ) { item1: Pair<Int?, Any?> ->
            if (TwoActionAlertDialog.ACTION_RIGHT == item1.first) {

                val paymentStatus = PaymentStatus()
                paymentStatus.status = "UserWentBack"
                paymentStatus.html = "User went back manually."
                paymentStatus.orderId = mainIntent!!.getStringExtra(AvenuesParams.ORDER_ID)
                paymentStatus.packageId = mainIntent!!.getStringExtra(AvenuesParams.PACKAGE_ID)
                paymentStatus.discountCode = mainIntent!!.getStringExtra(AvenuesParams.DISCOUNT_CODE)

                val data = Intent()
                data.putExtra("paymentStatus", paymentStatus)
                setResult(RESULT_OK, data)
                super.onBackPressed()
            }
        }
    }
}