package com.onourem.android.activity.ui.audio.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetAudioInfoResponse {

    @SerializedName("audioResponseList")
    @Expose
    private List<AudioResponse> audioResponseList = null;
    @SerializedName("audioIdList")
    @Expose
    private List<Integer> audioIdList = null;
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("errorCode")
    @Expose
    private String errorCode;

    @SerializedName("audioDuration")
    @Expose
    private int audioDuration;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("audioViewDuration")
    @Expose
    private int audioViewDuration;

    @SerializedName("displayNumberOfAudios")
    @Expose
    private int displayNumberOfAudios;

    public List<AudioResponse> getAudioResponseList() {
        return audioResponseList;
    }

    public void setAudioResponseList(List<AudioResponse> audioResponseList) {
        this.audioResponseList = audioResponseList;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(int audioDuration) {
        this.audioDuration = audioDuration;
    }

    public int getAudioViewDuration() {
        return audioViewDuration;
    }

    public void setAudioViewDuration(int audioViewDuration) {
        this.audioViewDuration = audioViewDuration;
    }

    public List<Integer> getAudioIdList() {
        return audioIdList;
    }

    public void setAudioIdList(List<Integer> audioIdList) {
        this.audioIdList = audioIdList;
    }

    public int getDisplayNumberOfAudios() {
        return displayNumberOfAudios;
    }

    public void setDisplayNumberOfAudios(int displayNumberOfAudios) {
        this.displayNumberOfAudios = displayNumberOfAudios;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}