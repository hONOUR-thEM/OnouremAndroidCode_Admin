package com.onourem.android.activity.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
class UserList {
    @PrimaryKey
    @SerializedName("userId")
    @Expose
    var userId: String = ""

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null

    @SerializedName("profilePicture")
    @Expose
    var profilePicture: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("followerStatus")
    @Expose
    var followerStatus: String? = null

    @SerializedName("userTypeId")
    @Expose
    var userType: String? = null
    var isSelected = false

    @Ignore
    var isAlreadyGroupMember = false
    override fun toString(): String {
        return userId
    }
}