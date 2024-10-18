package com.onourem.android.activity.ui.audio.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BackgroundAudio implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("audioCategoryId")
    @Expose
    private Integer audioCategoryId;
    @SerializedName("fileName")
    @Expose
    private String fileName;

    @SerializedName("audioDuration")
    @Expose
    private String audioDuration;

    public BackgroundAudio(Integer id, Integer audioCategoryId, String fileName, String audioDuration) {
        this.id = id;
        this.audioCategoryId = audioCategoryId;
        this.fileName = fileName;
        this.audioDuration = audioDuration;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAudioCategoryId() {
        return audioCategoryId;
    }

    public void setAudioCategoryId(Integer audioCategoryId) {
        this.audioCategoryId = audioCategoryId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(String audioDuration) {
        this.audioDuration = audioDuration;
    }
}