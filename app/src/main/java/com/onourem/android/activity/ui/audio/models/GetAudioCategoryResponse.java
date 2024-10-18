package com.onourem.android.activity.ui.audio.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetAudioCategoryResponse implements Serializable {

    @SerializedName("audioCategory")
    @Expose
    private List<AudioCategory> audioCategory = null;
    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("errorCode")
    @Expose
    private String errorCode;
    @SerializedName("languageList")
    @Expose
    private List<Language> languageList = null;

    public List<AudioCategory> getAudioCategory() {
        return audioCategory;
    }

    public void setAudioCategory(List<AudioCategory> audioCategory) {
        this.audioCategory = audioCategory;
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

    public List<Language> getLanguageList() {
        return languageList;
    }

    public void setLanguageList(List<Language> languageList) {
        this.languageList = languageList;
    }

}