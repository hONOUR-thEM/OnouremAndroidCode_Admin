package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OrderInfoResponse {
    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("accessCode")
    @Expose
    var vAccessCode: String? = null

    @SerializedName("merchantId")
    @Expose
    var vMerchantId: String? = null

    @SerializedName("currency")
    @Expose
    var vCurrency: String? = null

    @SerializedName("createdOrderId")
    @Expose
    var vOrderId: String? = null

    @SerializedName("redirectUrl")
    @Expose
    var vRedirectUrl: String? = null

    @SerializedName("cancelUrl")
    @Expose
    var vCancelUrl: String? = null

    @SerializedName("rsaKeyUrl")
    @Expose
    var vGetRSAUrl: String? = null

    @SerializedName("transactionUrl")
    @Expose
    var transactionUrl: String? = null

}