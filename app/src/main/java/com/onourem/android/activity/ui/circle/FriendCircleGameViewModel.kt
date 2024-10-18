package com.onourem.android.activity.ui.circle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.*
import com.onourem.android.activity.repository.FriendsCircleGameRepository
import com.onourem.android.activity.repository.FriendsCircleGameRepositoryImpl
import com.onourem.android.activity.repository.QuestionContactsDataBaseOperationRepository
import com.onourem.android.activity.repository.QuestionContactsDataBaseOperationRepositoryImpl
import com.onourem.android.activity.ui.circle.models.ContactListData
import com.onourem.android.activity.ui.circle.models.QuestionForContacts
import javax.inject.Inject

class FriendCircleGameViewModel @Inject constructor(
    repository: FriendsCircleGameRepositoryImpl,
    questionContactsDataBaseOperationRepository: QuestionContactsDataBaseOperationRepositoryImpl
) :
    ViewModel(), FriendsCircleGameRepository, QuestionContactsDataBaseOperationRepository {

    private val questionContactsDataBaseOperationRepository: QuestionContactsDataBaseOperationRepositoryImpl

    private val repository: FriendsCircleGameRepository


    init {
        this.repository = repository
        this.questionContactsDataBaseOperationRepository =
            questionContactsDataBaseOperationRepository
    }

    private val updateNumber = MutableLiveData<String?>()

    private var updateTotalSelectedContacts = MutableLiveData<String?>()

    private val updateContactList = MutableLiveData<ContactListData?>()

    fun getUpdatedNumber(): MutableLiveData<String?> {
        return updateNumber
    }

    fun getSelectedContacts(): MutableLiveData<String?> {
        return updateTotalSelectedContacts
    }

    fun setUpdatedNumber(number: String?) {
        updateNumber.value = number
    }

    fun setUpdateMobileConsumed() {
        updateNumber.value = null
    }

    fun setSelectedContacts(colors: String?) {
        updateTotalSelectedContacts.value = colors
    }

    fun setSelectedContactsConsumed() {
        updateTotalSelectedContacts.value = null
    }

    fun getUpdateContactList(): MutableLiveData<ContactListData?> {
        return updateContactList
    }

    fun setUpdateContactList(contactListData: ContactListData) {
        updateContactList.value = contactListData
    }

    fun setUpdateContactListConsumed() {
        updateContactList.value = null
    }

    override fun getAllContactsByPagerPosition(pagerPosition: Int): LiveData<List<QuestionForContacts>> {
        return questionContactsDataBaseOperationRepository.getAllContactsByPagerPosition(
            pagerPosition
        )
    }

    override fun getContactsByPagerPosition(pagerPosition: Int): List<QuestionForContacts> {
        return questionContactsDataBaseOperationRepository.getContactsByPagerPosition(pagerPosition)
    }

    override fun getSelectedContactsForQuestions(selected: Boolean): LiveData<List<QuestionForContacts>> {
        return questionContactsDataBaseOperationRepository.getSelectedContactsForQuestions(
            selected
        )
    }

    override fun getSelectedFinalContactsForQuestions(selected: Boolean): List<QuestionForContacts> {
        return questionContactsDataBaseOperationRepository.getSelectedFinalContactsForQuestions(
            selected
        )
    }

    override fun getSelectedContactsForQuestionsByPagerPosition(
        selected: Boolean,
        position: Int
    ): LiveData<List<QuestionForContacts>> {
        return questionContactsDataBaseOperationRepository.getSelectedContactsForQuestionsByPagerPosition(
            selected,
            position
        )
    }

    override fun reset() {
        questionContactsDataBaseOperationRepository.reset()
    }

    override fun getTaggedByUserList(questionId: String): LiveData<ApiResponse<GetTaggedByUserListResponse>> {
        return repository.getTaggedByUserList(questionId)
    }

    override fun addQualityQuestionVisibility(): LiveData<ApiResponse<StandardResponse>> {
        return repository.addQualityQuestionVisibility()
    }

    override fun insert(questionForContacts: QuestionForContacts) {
        questionContactsDataBaseOperationRepository.insert(questionForContacts)
    }

    override fun update(questionForContacts: QuestionForContacts) {
        questionContactsDataBaseOperationRepository.update(questionForContacts)
    }

    override fun delete(questionForContacts: QuestionForContacts) {
        questionContactsDataBaseOperationRepository.delete(questionForContacts)
    }

    override fun verifyPhoneNumber(
        phoneNumber: String,
        countryCode: String
    ): LiveData<ApiResponse<SendOtpResponse>> {
        return repository.verifyPhoneNumber(phoneNumber, countryCode)
    }

    override fun getVerifiedPhoneNumbers(): LiveData<ApiResponse<GetVerifiedPhoneNumbersResponse>> {
        return repository.getVerifiedPhoneNumbers()
    }

    override fun deleteVerifiedPhoneNumber(phoneNumber: String): LiveData<ApiResponse<StandardResponse>> {
        return repository.deleteVerifiedPhoneNumber(phoneNumber)
    }

    override fun verifyOTP(
        phoneNumber: String,
        countryCode: String,
        otp: String
    ): LiveData<ApiResponse<StandardResponse>> {
        return repository.verifyOTP(phoneNumber, countryCode, otp)
    }

    override fun getQualityQuestions(): LiveData<ApiResponse<GetQualityQuestionsResponse>> {
        return repository.getQualityQuestions()
    }

    override fun createQualityQuestionGame(questionMappingData: String): LiveData<ApiResponse<StandardResponse>> {
        return repository.createQualityQuestionGame(questionMappingData)
    }

    override fun getTaggedUserQualityInfo(): LiveData<ApiResponse<UserQualityResponse>> {
        return repository.getTaggedUserQualityInfo()
    }

    override fun updateQualitySeenStatus(): LiveData<ApiResponse<StandardResponse>> {
        return repository.updateQualitySeenStatus()
    }


}