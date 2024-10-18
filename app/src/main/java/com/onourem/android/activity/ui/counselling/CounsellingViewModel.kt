package com.onourem.android.activity.ui.counselling

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.repository.CounsellingRepository
import com.onourem.android.activity.repository.CounsellingRepositoryImpl
import com.onourem.android.activity.repository.SettingsRepository
import com.onourem.android.activity.repository.SettingsRepositoryImpl
import javax.inject.Inject

class CounsellingViewModel @Inject constructor(repository: CounsellingRepositoryImpl) : ViewModel(), CounsellingRepository {
    private val repository: CounsellingRepository


    init {
        this.repository = repository
    }


    override fun getInstitutionCounsellingInfo(): LiveData<ApiResponse<GetInstituteCounsellingInfoResponse>> {
        return repository.getInstitutionCounsellingInfo()
    }

}