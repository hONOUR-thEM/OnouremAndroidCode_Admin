package com.onourem.android.activity.repository

import androidx.lifecycle.LiveData
import com.onourem.android.activity.ui.circle.models.QuestionForContacts

interface QuestionContactsDataBaseOperationRepository {
    fun getAllContactsByPagerPosition(pagerPosition: Int): LiveData<List<QuestionForContacts>>
    fun getContactsByPagerPosition(pagerPosition: Int): List<QuestionForContacts>
    fun getSelectedContactsForQuestions(selected: Boolean): LiveData<List<QuestionForContacts>>
    fun getSelectedFinalContactsForQuestions(selected: Boolean): List<QuestionForContacts>
    fun getSelectedContactsForQuestionsByPagerPosition(
        selected: Boolean,
        position: Int
    ): LiveData<List<QuestionForContacts>>

    fun reset()
    fun insert(questionForContacts: QuestionForContacts)
    fun update(questionForContacts: QuestionForContacts)
    fun delete(questionForContacts: QuestionForContacts)
}