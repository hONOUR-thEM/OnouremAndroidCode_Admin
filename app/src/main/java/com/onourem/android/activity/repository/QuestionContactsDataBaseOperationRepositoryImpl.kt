package com.onourem.android.activity.repository

import javax.inject.Inject
import com.onourem.android.activity.arch.helper.AppExecutors
import com.onourem.android.activity.dao.QuestionContactsDao
import com.onourem.android.activity.repository.QuestionContactsDataBaseOperationRepository
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import androidx.lifecycle.LiveData
import com.onourem.android.activity.ui.circle.models.QuestionForContacts

class QuestionContactsDataBaseOperationRepositoryImpl @Inject internal constructor(
    private val appExecutors: AppExecutors,
    private val questionContactsDao: QuestionContactsDao
) : QuestionContactsDataBaseOperationRepository {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    override fun getAllContactsByPagerPosition(pagerPosition: Int): LiveData<List<QuestionForContacts>> {
        return questionContactsDao.getAllContactsByPagerPosition(pagerPosition)
    }

    override fun getContactsByPagerPosition(pagerPosition: Int): List<QuestionForContacts> {
        return questionContactsDao.getContactsByPagerPosition(pagerPosition)
    }

    override fun getSelectedContactsForQuestions(selected: Boolean): LiveData<List<QuestionForContacts>> {
        return questionContactsDao.getSelectedContactsForQuestions(selected)
    }

    override fun getSelectedFinalContactsForQuestions(selected: Boolean): List<QuestionForContacts> {
        return questionContactsDao.getSelectedFinalContactsForQuestions(selected)
    }

    override fun getSelectedContactsForQuestionsByPagerPosition(
        selected: Boolean,
        position: Int
    ): LiveData<List<QuestionForContacts>> {
        return questionContactsDao.getSelectedContactsForQuestionsByPagerPosition(
            selected,
            position
        )
    }

    override fun reset() {
        appExecutors.diskIO().execute { questionContactsDao.reset() }
    }

    override fun insert(questionForContacts: QuestionForContacts) {
        appExecutors.diskIO().execute { questionContactsDao.insert(questionForContacts) }
    }

    override fun update(questionForContacts: QuestionForContacts) {
        appExecutors.diskIO().execute { questionContactsDao.update(questionForContacts) }
    }

    override fun delete(questionForContacts: QuestionForContacts) {
        appExecutors.diskIO().execute { questionContactsDao.delete(questionForContacts) }
    }
}