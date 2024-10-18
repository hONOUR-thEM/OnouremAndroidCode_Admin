package com.onourem.android.activity.models


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.Expose

@Parcelize
class GetInstituteCounsellingInfoResponse(
    @SerializedName("errorCode")
    @Expose
    var errorCode: String,
    @SerializedName("errorMessage")
    @Expose
    var errorMessage: String,
    @SerializedName("institutionInfo")
    @Expose
    var institutionInfo: InstitutionInfo,
    @SerializedName("offlineInstitutionResponseList")
    @Expose
    var offlineInstitutionResponseList: List<OfflineInstitutionResponse>,
    @SerializedName("onlineInstitutionResponse")
    @Expose
    var onlineInstitutionResponse: OnlineInstitutionResponse,
    @SerializedName("onouremOnlineInstitutionResponse")
    @Expose
    var onouremOnlineInstitutionResponse: OnouremOnlineInstitutionResponse
) : Parcelable