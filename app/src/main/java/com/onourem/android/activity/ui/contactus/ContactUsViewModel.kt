package com.onourem.android.activity.ui.contactus

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.repository.ContactsUsRepository
import com.onourem.android.activity.repository.ContactsUsRepositoryImpl
import javax.inject.Inject

class ContactUsViewModel @Inject constructor(contactsUsRepository: ContactsUsRepositoryImpl) :
    ViewModel(), ContactsUsRepository {
    private val contactsUsRepository: ContactsUsRepository
    override fun contactUs(
        user_text: String,
        actionToPerform: String
    ): LiveData<ApiResponse<StandardResponse>> {
        return contactsUsRepository.contactUs(user_text, actionToPerform)
    }

    init {
        this.contactsUsRepository = contactsUsRepository
    }
}