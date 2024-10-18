package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class OrderInfo(
    @SerializedName("amount")
    @Expose
    var amount: Double,
    @SerializedName("bankRefNumber")
    @Expose
    var bankRefNumber: String?,
    @SerializedName("billingAddress")
    @Expose
    var billingAddress: String?,
    @SerializedName("billingCity")
    @Expose
    var billingCity: String?,
    @SerializedName("billingCountry")
    @Expose
    var billingCountry: String?,
    @SerializedName("billingEmail")
    @Expose
    var billingEmail: String?,
    @SerializedName("billingName")
    @Expose
    var billingName: String?,
    @SerializedName("billingState")
    @Expose
    var billingState: String?,
    @SerializedName("billingTelephone")
    @Expose
    var billingTelephone: String?,
    @SerializedName("billingZip")
    @Expose
    var billingZip: String?,
    @SerializedName("cardName")
    @Expose
    var cardName: String?,
    @SerializedName("comment")
    @Expose
    var comment: String?,
    @SerializedName("completedDateTime")
    @Expose
    var completedDateTime: String,
    @SerializedName("currency")
    @Expose
    var currency: String?,
    @SerializedName("discountCode")
    @Expose
    var discountCode: String,
    @SerializedName("failureMessage")
    @Expose
    var failureMessage: String?,
    @SerializedName("id")
    @Expose
    var id: Int,
    @SerializedName("initiatedDateTime")
    @Expose
    var initiatedDateTime: String,
    @SerializedName("merchantAmount")
    @Expose
    var merchantAmount: String?,
    @SerializedName("orderId")
    @Expose
    var orderId: String,
    @SerializedName("orderStatus")
    @Expose
    var orderStatus: String,
    @SerializedName("packageCode")
    @Expose
    var packageCode: String,
    @SerializedName("paymentMode")
    @Expose
    var paymentMode: String?,
    @SerializedName("responseCode")
    @Expose
    var responseCode: String?,
    @SerializedName("retry")
    @Expose
    var retry: String?,
    @SerializedName("statusCode")
    @Expose
    var statusCode: String?,
    @SerializedName("statusMessage")
    @Expose
    var statusMessage: String?,
    @SerializedName("trackingId")
    @Expose
    var trackingId: String?,
    @SerializedName("transDate")
    @Expose
    var transDate: String?,
    @SerializedName("userId")
    @Expose
    var userId: Int,
    @SerializedName("version")
    @Expose
    var version: Int,
)

