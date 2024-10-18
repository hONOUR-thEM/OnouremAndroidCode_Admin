package com.onourem.android.activity.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FutureQuestionDetails implements Parcelable {

    String timezone, playGroupIds, triggerDateAndTime, questionTo, categoryId, userIds, youtubeVideoLink;

    CategoryList taskCategory;


    public FutureQuestionDetails(Parcel in) {
        timezone = in.readString();
        playGroupIds = in.readString();
        triggerDateAndTime = in.readString();
        questionTo = in.readString();
        categoryId = in.readString();
        userIds = in.readString();
        youtubeVideoLink = in.readString();
    }

    public static final Creator<FutureQuestionDetails> CREATOR = new Creator<FutureQuestionDetails>() {
        @Override
        public FutureQuestionDetails createFromParcel(Parcel in) {
            return new FutureQuestionDetails(in);
        }

        @Override
        public FutureQuestionDetails[] newArray(int size) {
            return new FutureQuestionDetails[size];
        }
    };

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getPlayGroupIds() {
        return playGroupIds;
    }

    public void setPlayGroupIds(String playGroupIds) {
        this.playGroupIds = playGroupIds;
    }

    public String getTriggerDateAndTime() {
        return triggerDateAndTime;
    }

    public void setTriggerDateAndTime(String triggerDateAndTime) {
        this.triggerDateAndTime = triggerDateAndTime;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public CategoryList getTaskCategory() {
        return taskCategory;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public void setTaskCategory(CategoryList taskCategory) {
        this.taskCategory = taskCategory;
    }

    public String getYoutubeVideoLink() {
        return youtubeVideoLink;
    }

    public void setYoutubeVideoLink(String youtubeVideoLink) {
        this.youtubeVideoLink = youtubeVideoLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(timezone);
        parcel.writeString(playGroupIds);
        parcel.writeString(triggerDateAndTime);
        parcel.writeString(questionTo);
        parcel.writeString(categoryId);
        parcel.writeString(userIds);
    }

    public FutureQuestionDetails(String timezone, String playGroupIds, String triggerDateAndTime, String questionTo, String categoryId, String userIds) {
        this.timezone = timezone;
        this.playGroupIds = playGroupIds;
        this.triggerDateAndTime = triggerDateAndTime;
        this.questionTo = questionTo;
        this.categoryId = categoryId;
        this.userIds = userIds;
    }

    public FutureQuestionDetails(String timezone, String playGroupIds, String triggerDateAndTime, String questionTo, String categoryId, CategoryList taskCategory, String userIds) {
        this.timezone = timezone;
        this.playGroupIds = playGroupIds;
        this.triggerDateAndTime = triggerDateAndTime;
        this.questionTo = questionTo;
        this.categoryId = categoryId;
        this.taskCategory = taskCategory;
        this.userIds = userIds;
    }

    public String getQuestionTo() {
        return questionTo;
    }

    public void setQuestionTo(String questionTo) {
        this.questionTo = questionTo;
    }
}
