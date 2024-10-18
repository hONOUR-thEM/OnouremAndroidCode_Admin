package com.onourem.android.activity.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestAddSurvey {

    @SerializedName("surveyText")
    @Expose
    private String surveyText;
    @SerializedName("surveyDate")
    @Expose
    private String surveyDate;
    @SerializedName("surveyOpt1")
    @Expose
    private String surveyOption1;
    @SerializedName("surveyOpt2")
    @Expose
    private String surveyOption2;
    @SerializedName("surveyOpt3")
    @Expose
    private String surveyOption3;
    @SerializedName("surveyOpt4")
    @Expose
    private String surveyOption4;
    @SerializedName("surveyOpt5")
    @Expose
    private String surveyOption5;
    @SerializedName("surveyOpt6")
    @Expose
    private String surveyOption6;
    @SerializedName("surveyOpt7")
    @Expose
    private String surveyOption7;
    @SerializedName("surveyOpt8")
    @Expose
    private String surveyOption8;

    @SerializedName("userSurveyType")
    @Expose
    private String userSurveyType;

    @SerializedName("showStats")
    @Expose
    private String showStats;

    @SerializedName("oclubSpecific")
    @Expose
    private String oclubSpecific;

    public String getSurveyText() {
        return surveyText;
    }

    public void setSurveyText(String surveyText) {
        this.surveyText = surveyText;
    }

    public String getSurveyDate() {
        return surveyDate;
    }

    public void setSurveyDate(String surveyDate) {
        this.surveyDate = surveyDate;
    }

    public String getSurveyOption1() {
        return surveyOption1;
    }

    public void setSurveyOption1(String surveyOption1) {
        this.surveyOption1 = surveyOption1;
    }

    public String getSurveyOption2() {
        return surveyOption2;
    }

    public void setSurveyOption2(String surveyOption2) {
        this.surveyOption2 = surveyOption2;
    }

    public String getSurveyOption3() {
        return surveyOption3;
    }

    public void setSurveyOption3(String surveyOption3) {
        this.surveyOption3 = surveyOption3;
    }

    public String getSurveyOption4() {
        return surveyOption4;
    }

    public void setSurveyOption4(String surveyOption4) {
        this.surveyOption4 = surveyOption4;
    }

    public String getSurveyOption5() {
        return surveyOption5;
    }

    public void setSurveyOption5(String surveyOption5) {
        this.surveyOption5 = surveyOption5;
    }

    public String getSurveyOption6() {
        return surveyOption6;
    }

    public void setSurveyOption6(String surveyOption6) {
        this.surveyOption6 = surveyOption6;
    }

    public String getSurveyOption7() {
        return surveyOption7;
    }

    public void setSurveyOption7(String surveyOption7) {
        this.surveyOption7 = surveyOption7;
    }

    public String getSurveyOption8() {
        return surveyOption8;
    }

    public void setSurveyOption8(String surveyOption8) {
        this.surveyOption8 = surveyOption8;
    }

    public String getUserSurveyType() {
        return userSurveyType;
    }

    public void setUserSurveyType(String userSurveyType) {
        this.userSurveyType = userSurveyType;
    }

    public String getShowStats() {
        return showStats;
    }

    public void setShowStats(String showStats) {
        this.showStats = showStats;
    }

    public String getOclubSpecific() {
        return oclubSpecific;
    }

    public void setOclubSpecific(String oclubSpecific) {
        this.oclubSpecific = oclubSpecific;
    }
}