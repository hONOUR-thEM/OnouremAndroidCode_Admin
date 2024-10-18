package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CategoryList() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("catCode")
    @Expose
    var catCode: String? = null

    @SerializedName("receiverRequired")
    @Expose
    var receiverRequired: String? = null

    @SerializedName("colorCode")
    @Expose
    var colorCode: String? = null

    @SerializedName("imageCode")
    @Expose
    var imageCode: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("publicCommentAllowed")
    @Expose
    var publicCommentAllowed: String? = null

    @SerializedName("showInAll")
    @Expose
    var showInAll: String? = null

    @SerializedName("visibleInAllCountries")
    @Expose
    var visibleInAllCountries: String? = null

    @SerializedName("postSubCatCOList")
    @Expose
    var postSubCatCOList: List<Any>? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        catCode = parcel.readString()
        receiverRequired = parcel.readString()
        colorCode = parcel.readString()
        imageCode = parcel.readString()
        status = parcel.readString()
        description = parcel.readString()
        publicCommentAllowed = parcel.readString()
        showInAll = parcel.readString()
        visibleInAllCountries = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(catCode)
        parcel.writeString(receiverRequired)
        parcel.writeString(colorCode)
        parcel.writeString(imageCode)
        parcel.writeString(status)
        parcel.writeString(description)
        parcel.writeString(publicCommentAllowed)
        parcel.writeString(showInAll)
        parcel.writeString(visibleInAllCountries)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoryList> {
        override fun createFromParcel(parcel: Parcel): CategoryList {
            return CategoryList(parcel)
        }

        override fun newArray(size: Int): Array<CategoryList?> {
            return arrayOfNulls(size)
        }
    }
}