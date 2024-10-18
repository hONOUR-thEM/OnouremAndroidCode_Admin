package com.onourem.android.activity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateExternalContent {

    @SerializedName("externalId")
    @Expose
    private String externalId;

    @SerializedName("summary")
    @Expose
    private String summary;

    @SerializedName("videoLink")
    @Expose
    private String videoLink;

    @SerializedName("externalLink")
    @Expose
    private String externalLink;

    @SerializedName("sourceName")
    @Expose
    private String sourceName;

    @SerializedName("isYouTubeLink")
    @Expose
    private String isYouTubeLink;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("smallPostImage")
    @Expose
    private String smallPostImage;

    @SerializedName("status")
    @Expose
    private String status;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getExternalLink() {
        return externalLink;
    }

    public void setExternalLink(String externalLink) {
        this.externalLink = externalLink;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getIsYouTubeLink() {
        return isYouTubeLink;
    }

    public void setIsYouTubeLink(String isYouTubeLink) {
        this.isYouTubeLink = isYouTubeLink;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSmallPostImage() {
        return smallPostImage;
    }

    public void setSmallPostImage(String smallPostImage) {
        this.smallPostImage = smallPostImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
