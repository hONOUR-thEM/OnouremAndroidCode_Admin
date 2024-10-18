package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.GetInstituteCounsellingInfoResponse


interface CounsellingRepository {

    fun getInstitutionCounsellingInfo(): LiveData<ApiResponse<GetInstituteCounsellingInfoResponse>>


}