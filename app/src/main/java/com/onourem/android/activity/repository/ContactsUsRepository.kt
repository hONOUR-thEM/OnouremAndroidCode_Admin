package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.StandardResponse

interface ContactsUsRepository {
    fun contactUs(
        user_text: String,
        actionToPerform: String
    ): LiveData<ApiResponse<StandardResponse>> //{"user_text":secondInputTextView.text,"screenId" : "35", "serviceName" : "contactUs","actionToPerform" : "ContactUs"}
}