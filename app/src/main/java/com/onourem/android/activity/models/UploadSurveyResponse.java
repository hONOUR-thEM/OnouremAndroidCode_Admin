package com.onourem.android.activity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadSurveyResponse {

    @SerializedName("surveyText")
    @Expose
    private String surveyText;
    @SerializedName("surveyDate")
    @Expose
    private String surveyDate;
    @SerializedName("surveyId")
    @Expose
    private String surveyId;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    public String getSurveyText() {
        return surveyText;
    }

    public void setSurveyText(String surveyText) {
        this.surveyText = surveyText;
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSurveyDate() {
        return surveyDate;
    }

    public void setSurveyDate(String surveyDate) {
        this.surveyDate = surveyDate;
    }
}
