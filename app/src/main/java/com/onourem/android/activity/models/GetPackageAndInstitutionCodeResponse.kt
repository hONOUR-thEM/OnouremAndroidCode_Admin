package com.onourem.android.activity.models


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetPackageAndInstitutionCodeResponse(
    @SerializedName("errorCode")
    @Expose
    val errorCode: String,
    @SerializedName("errorMessage")
    @Expose
    val errorMessage: String,

    @SerializedName("institutionCodeList")
    @Expose
    val institutionCodeList: List<InstituteNameId>,

    @SerializedName("packageCategoryList")
    @Expose
    val packageCategoryList: List<PackageCategory>,

    @SerializedName("packageIdAndNameList")
    @Expose
    val packageIdAndNameList: List<PackageNameId>
)