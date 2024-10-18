package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetVisibilityResponse {
    //    @SerializedName("outsideOnouremEmailMessage")
    //    @Expose
    //    private Object outsideOnouremEmailMessage;
    //    @SerializedName("outsideOnouremEmailSubject")
    //    @Expose
    //    private Object outsideOnouremEmailSubject;
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    var fieldName: Any? = null

    @SerializedName("token")
    @Expose
    var token: String? = null

    //    public Object getGroupList() {
    //        return groupList;
    //    }
    //
    //    public void setGroupList(Object groupList) {
    //        this.groupList = groupList;
    //    }
    //    @SerializedName("groupList")
    //    @Expose
    //    private Object groupList;
    @SerializedName("userList")
    @Expose
    var userList: Any? = null

    //    public Object getCommentList() {
    //        return commentList;
    //    }
    //
    //    public void setCommentList(Object commentList) {
    //        this.commentList = commentList;
    //    }
    //
    //    public Object getCommentId() {
    //        return commentId;
    //    }
    //
    //    public void setCommentId(Object commentId) {
    //        this.commentId = commentId;
    //    }
    //    @SerializedName("commentList")
    //    @Expose
    //    private Object commentList;
    //    @SerializedName("commentId")
    //    @Expose
    //    private Object commentId;
    @SerializedName("visibilityGroupList")
    @Expose
    internal var visibilityGroupList: List<VisibilityGroupList>? = null

    //    @SerializedName("campaignViewCO")
    //    @Expose
    //    private Object campaignViewCO;
    @SerializedName("participationStatus")
    @Expose
    internal var participationStatus: Any? = null

    @SerializedName("message")
    @Expose
    internal var message: Any? = null

    @SerializedName("activityTagStatus")
    @Expose
    internal var activityTagStatus: Any? = null
//    fun setVisibilityGroupList(visibilityGroupList: List<VisibilityGroupList>?) {
//        field = visibilityGroupList
//    }

    //    public Object getCampaignViewCO() {
    //        return campaignViewCO;
    //    }
    //
    //    public void setCampaignViewCO(Object campaignViewCO) {
    //        this.campaignViewCO = campaignViewCO;
    //    }
    fun getParticipationStatus(): Any? {
        return participationStatus
    }

    fun setParticipationStatus(participationStatus: Any?) {
        this.participationStatus = participationStatus
    }

    fun getMessage(): Any? {
        return message
    }

    fun setMessage(message: Any?) {
        this.message = message
    }

    fun getActivityTagStatus(): Any? {
        return activityTagStatus
    }

    fun setActivityTagStatus(activityTagStatus: Any?) {
        this.activityTagStatus = activityTagStatus
    } //    public Object getOutsideOnouremEmailMessage() {
    //        return outsideOnouremEmailMessage;
    //    }
    //
    //    public void setOutsideOnouremEmailMessage(Object outsideOnouremEmailMessage) {
    //        this.outsideOnouremEmailMessage = outsideOnouremEmailMessage;
    //    }
    //    public Object getOutsideOnouremEmailSubject() {
    //        return outsideOnouremEmailSubject;
    //    }
    //
    //    public void setOutsideOnouremEmailSubject(Object outsideOnouremEmailSubject) {
    //        this.outsideOnouremEmailSubject = outsideOnouremEmailSubject;
    //    }
}