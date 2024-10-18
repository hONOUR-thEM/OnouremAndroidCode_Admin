package com.onourem.android.activity.ui.admin.create.question_schedule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.onourem.android.activity.ui.admin.create.external_posts.ExternalActivityData;

import java.util.List;

public class GetAdminActivityListResponse {

    @SerializedName("errorMessage")
    @Expose
    private String errorMessage;
    @SerializedName("errorCode")
    @Expose
    private String errorCode;

    @SerializedName("adminTaskMessageIdList")
    @Expose
    private List<String> adminTaskMessageIdList;

    @SerializedName("externalActivityDataList")
    @Expose
    private List<ExternalActivityData> externalActivityDataList;

    @SerializedName("adminActivityIdList")
    @Expose
    private List<String> adminActivityIdList;

 @SerializedName("admincardIdList")
    @Expose
    private List<String> adminCardIdList;

    public List<String> getAdminActivityIdList() {
        return adminActivityIdList;
    }

    public void setAdminActivityIdList(List<String> adminActivityIdList) {
        this.adminActivityIdList = adminActivityIdList;
    }

    @SerializedName("adminActivityResponseList")
    @Expose
    private List<AdminActivityResponse> adminActivityResponseList = null;

    @SerializedName("adminCardResponseList")
    @Expose
    private List<AdminActivityResponse> adminCardResponseList = null;

    @SerializedName("adminTaskMessageResponseList")
    @Expose
    private List<AdminActivityResponse> adminTaskMessageResponseList = null;

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

    public List<AdminActivityResponse> getAdminActivityResponseList() {
        return adminActivityResponseList;
    }

    public void setAdminActivityResponseList(List<AdminActivityResponse> adminActivityResponseList) {
        this.adminActivityResponseList = adminActivityResponseList;
    }

    public List<AdminActivityResponse> getAdminTaskMessageResponseList() {
        return adminTaskMessageResponseList;
    }

    public void setAdminTaskMessageResponseList(List<AdminActivityResponse> adminTaskMessageResponseList) {
        this.adminTaskMessageResponseList = adminTaskMessageResponseList;
    }

    public List<String> getAdminTaskMessageIdList() {
        return adminTaskMessageIdList;
    }

    public void setAdminTaskMessageIdList(List<String> adminTaskMessageIdList) {
        this.adminTaskMessageIdList = adminTaskMessageIdList;
    }

    public List<String> getAdminCardIdList() {
        return adminCardIdList;
    }

    public void setAdminCardIdList(List<String> adminCardIdList) {
        this.adminCardIdList = adminCardIdList;
    }

    public List<AdminActivityResponse> getAdminCardResponseList() {
        return adminCardResponseList;
    }

    public void setAdminCardResponseList(List<AdminActivityResponse> adminCardResponseList) {
        this.adminCardResponseList = adminCardResponseList;
    }

    public List<ExternalActivityData> getExternalActivityDataList() {
        return externalActivityDataList;
    }

    public void setExternalActivityDataList(List<ExternalActivityData> externalActivityDataList) {
        this.externalActivityDataList = externalActivityDataList;
    }
}