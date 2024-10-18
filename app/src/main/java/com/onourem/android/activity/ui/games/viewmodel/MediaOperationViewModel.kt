package com.onourem.android.activity.ui.games.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.models.PrivacyGroup
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.network.FileUploadProgressRequestBody.ProgressCallback
import com.onourem.android.activity.repository.MediaDataBaseOperationRepository
import com.onourem.android.activity.repository.MediaDataBaseOperationRepositoryImpl
import com.onourem.android.activity.repository.MediaOperationRepository
import com.onourem.android.activity.repository.MediaOperationRepositoryImpl
import com.onourem.android.activity.ui.audio.models.*
import com.onourem.android.activity.ui.audio.playback.AudioPlaybackHistory
import com.onourem.android.activity.ui.audio.playback.Song
import javax.inject.Inject

class MediaOperationViewModel @Inject constructor(
    mediaOperationRepository: MediaOperationRepositoryImpl,
    mediaDataBaseOperationRepository: MediaDataBaseOperationRepositoryImpl
) : ViewModel(), MediaOperationRepository, MediaDataBaseOperationRepository {
    val filePath = MutableLiveData<Song?>()
    private val trimSelection = MutableLiveData<Pair<Int, Int>>()
    val privacyUpdatedItem = MutableLiveData<Song?>()
    val audioCategoryObject = MutableLiveData<AudioCategory?>()
    val audioBackgroundObject = MutableLiveData<BackgroundAudio?>()
    val audioLanguageObject = MutableLiveData<Language?>()
    val audioPrivacyObject = MutableLiveData<PrivacyGroup?>()
    val privacyGroups = MutableLiveData<List<PrivacyGroup>?>()
    val audioPlayBackHistoryData = MutableLiveData<List<AudioPlaybackHistory>>()
    val reloadUILiveData = MutableLiveData(false)
    val openRecording = MutableLiveData(false)
    val playerOperation = MutableLiveData<String>()
    val mediaDataBaseOperationRepository: MediaDataBaseOperationRepositoryImpl
    val mediaOperationRepository: MediaOperationRepository
    val fromAction = MutableLiveData<Pair<String, Song?>>()

    fun reloadUI(): LiveData<Boolean> {
        return reloadUILiveData
    }

    fun reloadUI(reload: Boolean) {
        reloadUILiveData.postValue(reload)
    }

    fun getOpenRecording(): LiveData<Boolean> {
        return openRecording
    }

    fun setOpenRecording(reload: Boolean) {
        openRecording.postValue(reload)
    }

    fun setFilePath(song: Song?) {
        filePath.value = song
    }

    fun setPrivacyUpdatedItem(song: Song?) {
        privacyUpdatedItem.value = song
    }

    override fun audioCategory(): LiveData<ApiResponse<GetAudioCategoryResponse>> {
        return mediaOperationRepository.audioCategory()
    }

    override fun getAudioInfo(
        service: String,
        linkUserId: String,
        audioIdFromNotification: String
    ): LiveData<ApiResponse<GetAudioInfoResponse>> {
        return mediaOperationRepository.getAudioInfo(service, linkUserId, audioIdFromNotification)
    }

    override fun getNextAudioInfo(audioIds: String): LiveData<ApiResponse<GetAudioInfoResponse>> {
        return mediaOperationRepository.getNextAudioInfo(audioIds)
    }

    override fun likeAudio(audioId: String): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.likeAudio(audioId)
    }

    override fun unLikeAudio(audioId: String): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.unLikeAudio(audioId)
    }

    override fun updateAudioVisibility(
        privacyIds: String,
        audioId: String
    ): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.updateAudioVisibility(privacyIds, audioId)
    }

    override fun addAudioInfoHistory(audioIds: String, audioDurations: String, creatorId: String) {
        mediaOperationRepository.addAudioInfoHistory(audioIds, audioDurations, creatorId)
    }

    override fun deleteAudio(audioId: String): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.deleteAudio(audioId)
    }

    override fun followAudioCreator(audioCreator: String): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.followAudioCreator(audioCreator)
    }

    override fun unfollowAudioCreator(audioCreator: String): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.unfollowAudioCreator(audioCreator)
    }

    override fun reportInappropriateAudio(audioId: String): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.reportInappropriateAudio(audioId)
    }

    override fun updateAudioTitle(
        audioId: String,
        title: String
    ): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.updateAudioTitle(audioId, title)
    }

    override fun getNextAudioInfoForAdmin(audioIds: String, audioForUserId: String, userFor: String): LiveData<ApiResponse<GetAudioInfoResponse>> {
        return mediaOperationRepository.getNextAudioInfoForAdmin(audioIds, audioForUserId, userFor)
    }

    override fun getAudioForOtherUser(otherUserId: String): LiveData<ApiResponse<GetAudioInfoResponse>> {
        return mediaOperationRepository.getAudioForOtherUser(otherUserId)
    }

    override fun searchAudioByTitle(title: String): LiveData<ApiResponse<GetAudioInfoResponse>> {
        return mediaOperationRepository.searchAudioByTitle(title)
    }

    override fun uploadAudioInfo(
        title: String,
        uriAudioPath: String,
        creatorId: String,
        audioCategoryId: String,
        audioDuration: String,
        languageId: String,
        privacyId: String,
        progressCallback: ProgressCallback
    ): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.uploadAudioInfo(
            title,
            uriAudioPath,
            creatorId,
            audioCategoryId,
            audioDuration,
            languageId,
            privacyId,
            progressCallback
        )
    }

    override fun getAudioInfoForAdmin(service: String, audioForUserId: String, userFor: String): LiveData<ApiResponse<GetAudioInfoResponse>> {
        return mediaOperationRepository.getAudioInfoForAdmin(service, audioForUserId, userFor)
    }

    override fun getApprovedAudioScheduleByAdmin(
        audioFor: String,
        date: String,
        audioForUserId: String,
        userFor: String
    ): LiveData<ApiResponse<GetAudioInfoResponse>> {
        return mediaOperationRepository.getApprovedAudioScheduleByAdmin(audioFor, date, audioForUserId, userFor)
    }

    override fun getNextApprovedAudioScheduleByAdmin(audioIds: String, audioForUserId: String, userFor: String): LiveData<ApiResponse<GetAudioInfoResponse>> {
        return mediaOperationRepository.getNextApprovedAudioScheduleByAdmin(audioIds, audioForUserId, userFor)

    }

    override fun blackListAudio(audioId: String): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.blackListAudio(audioId)
    }

    override fun approveAudioRequest(audioId: String, ratings: String): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.approveAudioRequest(audioId, ratings)
    }

    override fun rejectAudioRequest(audioId: String, ratings: String): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.rejectAudioRequest(audioId, ratings)
    }

    override fun updateAudioRating(audioId: String, ratings: String): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.updateAudioRating(audioId, ratings)
    }

    override fun scheduleAudioByAdmin(audioIds: String, timezone: String, triggerDateAndTime: String): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.scheduleAudioByAdmin(audioIds, timezone, triggerDateAndTime)
    }

    override fun updateScheduleAudioTimeByAdmin(audioIds: String, timezone: String, triggerDateAndTime: String): LiveData<ApiResponse<StandardResponse>> {
        return mediaOperationRepository.updateScheduleAudioTimeByAdmin(audioIds, timezone, triggerDateAndTime)
    }

    fun setAudioCategoryObject(audioCategoryObject: AudioCategory?) {
        this.audioCategoryObject.value = audioCategoryObject
    }

    fun setAudioBackgroundObject(audioCategoryObject: BackgroundAudio?) {
        audioBackgroundObject.value = audioCategoryObject
    }

    fun setAudioLanguageObject(audioCategoryObject: Language?) {
        audioLanguageObject.value = audioCategoryObject
    }

    fun setSelectedPrivacyGroup(privacyGroup: List<PrivacyGroup>?) {
        privacyGroups.postValue(privacyGroup)
    }

    val selectedPrivacyGroups: LiveData<List<PrivacyGroup>?>
        get() = privacyGroups

    fun getTrimSelection(): LiveData<Pair<Int, Int>> {
        return trimSelection
    }

    fun setTrimSelection(trimSelection: Pair<Int, Int>) {
        this.trimSelection.postValue(trimSelection)
    }

    fun getPlayerOperation(): LiveData<String> {
        return playerOperation
    }

    fun setPlayerOperation(playerOperation: String) {
        this.playerOperation.postValue(playerOperation)
    }

    fun getAudioPlayBackHistoryData(): LiveData<List<AudioPlaybackHistory>> {
        return audioPlayBackHistoryData
    }

    fun setAudioPlayBackHistoryData(audioPlayBackHistoryDataObj: List<AudioPlaybackHistory>) {
        audioPlayBackHistoryData.postValue(audioPlayBackHistoryDataObj)
    }

    override fun getAllAudioPlayBackHistory(userId: String): LiveData<List<AudioPlaybackHistory>> {
        return mediaDataBaseOperationRepository.getAllAudioPlayBackHistory(userId)
    }

    override fun reset() {
        mediaDataBaseOperationRepository.reset()
    }

    override fun insert(audioPlaybackHistory: AudioPlaybackHistory) {
        mediaDataBaseOperationRepository.insert(audioPlaybackHistory)
    }

    override fun update(audioPlaybackHistory: AudioPlaybackHistory) {
        mediaDataBaseOperationRepository.update(audioPlaybackHistory)
    }

    override fun delete(audioPlaybackHistory: AudioPlaybackHistory) {
        mediaDataBaseOperationRepository.delete(audioPlaybackHistory)
    }

    override fun deleteByList(audioPlaybackHistories: ArrayList<AudioPlaybackHistory>) {
        mediaDataBaseOperationRepository.deleteByList(audioPlaybackHistories)
    }

    @JvmName("getFromAction1")
    fun getFromAction(): MutableLiveData<Pair<String, Song?>> {
        return fromAction
    }

    fun setFromAction(fromActionStr: Pair<String, Song?>) {
        fromAction.value = fromActionStr
    }

    fun actionConsumed() {
        filePath.value = null
        audioCategoryObject.value = null
        audioBackgroundObject.value = null
        audioLanguageObject.value = null
        audioPrivacyObject.value = null
        privacyGroups.value = null
    }

    init {
        this.mediaOperationRepository = mediaOperationRepository
        this.mediaDataBaseOperationRepository = mediaDataBaseOperationRepository
    }
}