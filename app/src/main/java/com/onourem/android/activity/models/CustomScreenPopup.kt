package com.onourem.android.activity.models

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CustomScreenPopup : Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("screenId")
    @Expose
    var screenId: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("txtMessage")
    @Expose
    var txtMessage: String? = null

    //    public Long getCreatedDateTime() {
    //        return createdDateTime;
    //    }
    //
    //    public void setCreatedDateTime(Long createdDateTime) {
    //        this.createdDateTime = createdDateTime;
    //    }
    //    @SerializedName("createdDateTime")
    //    @Expose
    //    private Long createdDateTime;
    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("imageName")
    @Expose
    var imageName: String? = null

    @SerializedName("btnOneText")
    @Expose
    var btnOneText: String? = null

    @SerializedName("btnTwoText")
    @Expose
    var btnTwoText: String? = null

    @SerializedName("frequencyInHour")
    @Expose
    var frequencyInHour: Int? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("serviceName")
    @Expose
    var serviceName: String? = null

    @SerializedName("popupType")
    @Expose
    var popupType: String? = null
    var drawableImage: Drawable? = null
    var bitmapImage: Bitmap? = null
}