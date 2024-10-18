package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RegisterSocialResponse {
    @SerializedName("deviceId")
    @Expose
    internal var deviceId: String? = null

    @SerializedName("firstName")
    @Expose
    internal var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    internal var lastName: String? = null

    @SerializedName("emailAddress")
    @Expose
    internal var emailAddress: String? = null

    @SerializedName("gender")
    @Expose
    internal var gender: String? = null

    @SerializedName("socialId")
    @Expose
    internal var socialId: String? = null

    @SerializedName("socialSource")
    @Expose
    internal var socialSource: String? = null

    @SerializedName("profilePicture")
    @Expose
    internal var profilePicture: String? = null

    @SerializedName("largeProfilePicture")
    @Expose
    internal var largeProfilePicture: String? = null

    @SerializedName("linkUserId")
    @Expose
    internal var linkUserId: String? = null

    @SerializedName("languageId")
    @Expose
    internal var languageId: String? = null

    @SerializedName("countryCode")
    @Expose
    internal var countryCode: String? = null

    @SerializedName("screenId")
    @Expose
    internal var screenId: String? = null

    @SerializedName("serviceName")
    @Expose
    internal var serviceName: String? = null
    fun getDeviceId(): String? {
        return deviceId
    }

    fun setDeviceId(deviceId: String?) {
        this.deviceId = deviceId
    }

    fun getFirstName(): String? {
        return firstName
    }

    fun setFirstName(firstName: String?) {
        this.firstName = firstName
    }

    fun getLastName(): String? {
        return lastName
    }

    fun setLastName(lastName: String?) {
        this.lastName = lastName
    }

    fun getEmailAddress(): String? {
        return emailAddress
    }

    fun setEmailAddress(emailAddress: String?) {
        this.emailAddress = emailAddress
    }

    fun getGender(): String? {
        return gender
    }

    fun setGender(gender: String?) {
        this.gender = gender
    }

    fun getSocialId(): String? {
        return socialId
    }

    fun setSocialId(socialId: String?) {
        this.socialId = socialId
    }

    fun getSocialSource(): String? {
        return socialSource
    }

    fun setSocialSource(socialSource: String?) {
        this.socialSource = socialSource
    }

    fun getProfilePicture(): String? {
        return profilePicture
    }

    fun setProfilePicture(profilePicture: String?) {
        this.profilePicture = profilePicture
    }

    fun getLargeProfilePicture(): String? {
        return largeProfilePicture
    }

    fun setLargeProfilePicture(largeProfilePicture: String?) {
        this.largeProfilePicture = largeProfilePicture
    }

    fun getLinkUserId(): String? {
        return linkUserId
    }

    fun setLinkUserId(linkUserId: String?) {
        this.linkUserId = linkUserId
    }

    fun getLanguageId(): String? {
        return languageId
    }

    fun setLanguageId(languageId: String?) {
        this.languageId = languageId
    }

    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?) {
        this.countryCode = countryCode
    }

    fun getScreenId(): String? {
        return screenId
    }

    fun setScreenId(screenId: String?) {
        this.screenId = screenId
    }

    fun getServiceName(): String? {
        return serviceName
    }

    fun setServiceName(serviceName: String?) {
        this.serviceName = serviceName
    }
}