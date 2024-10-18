package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UpdateProfileResponse {
    @SerializedName("errorCode")
    @Expose
    internal var errorCode: String? = null

    @SerializedName("errorMessage")
    @Expose
    internal var errorMessage: String? = null

    @SerializedName("fieldName")
    @Expose
    internal var fieldName: Any? = null

    @SerializedName("profilePicture")
    @Expose
    internal var profilePicture: String? = null

    @SerializedName("coverPicture")
    @Expose
    internal var coverPicture: String? = null

    @SerializedName("emailAddress")
    @Expose
    internal var emailAddress: String? = null

    @SerializedName("firstName")
    @Expose
    internal var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    internal var lastName: String? = null

    @SerializedName("password")
    @Expose
    internal var password: Any? = null

    @SerializedName("mobileNumber")
    @Expose
    internal var mobileNumber: String? = null

    @SerializedName("gender")
    @Expose
    internal var gender: String? = null

    @SerializedName("city")
    @Expose
    internal var city: String? = null

    @SerializedName("state")
    @Expose
    internal var state: String? = null

    @SerializedName("country")
    @Expose
    internal var country: String? = null

    @SerializedName("cityId")
    @Expose
    internal var cityId: Any? = null

    @SerializedName("geoName")
    @Expose
    internal var geoName: Any? = null

    @SerializedName("token")
    @Expose
    internal var token: String? = null

    @SerializedName("userId")
    @Expose
    internal var userId: String? = null

    @SerializedName("status")
    @Expose
    internal var status: String? = null

    @SerializedName("feeds")
    @Expose
    internal var feeds: Any? = null

    @SerializedName("largeProfilePicture")
    @Expose
    internal var largeProfilePicture: String? = null

    @SerializedName("largeCoverPicture")
    @Expose
    internal var largeCoverPicture: String? = null

    @SerializedName("registrationStaus")
    @Expose
    internal var registrationStaus: Boolean? = null

    @SerializedName("notificationAlertSettings")
    @Expose
    internal var notificationAlertSettings: Any? = null

    @SerializedName("refName")
    @Expose
    internal var refName: String? = null

    @SerializedName("languageId")
    @Expose
    internal var languageId: String? = null

    @SerializedName("dateOfBirth")
    @Expose
    internal var dateOfBirth: String? = null
    fun getErrorCode(): String? {
        return errorCode
    }

    fun setErrorCode(errorCode: String?) {
        this.errorCode = errorCode
    }

    fun getErrorMessage(): String? {
        return errorMessage
    }

    fun setErrorMessage(errorMessage: String?) {
        this.errorMessage = errorMessage
    }

    fun getFieldName(): Any? {
        return fieldName
    }

    fun setFieldName(fieldName: Any?) {
        this.fieldName = fieldName
    }

    fun getProfilePicture(): String? {
        return profilePicture
    }

    fun setProfilePicture(profilePicture: String?) {
        this.profilePicture = profilePicture
    }

    fun getCoverPicture(): String? {
        return coverPicture
    }

    fun setCoverPicture(coverPicture: String?) {
        this.coverPicture = coverPicture
    }

    fun getEmailAddress(): String? {
        return emailAddress
    }

    fun setEmailAddress(emailAddress: String?) {
        this.emailAddress = emailAddress
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

    fun getPassword(): Any? {
        return password
    }

    fun setPassword(password: Any?) {
        this.password = password
    }

    fun getMobileNumber(): String? {
        return mobileNumber
    }

    fun setMobileNumber(mobileNumber: String?) {
        this.mobileNumber = mobileNumber
    }

    fun getGender(): String? {
        return gender
    }

    fun setGender(gender: String?) {
        this.gender = gender
    }

    fun getCity(): String? {
        return city
    }

    fun setCity(city: String?) {
        this.city = city
    }

    fun getState(): String? {
        return state
    }

    fun setState(state: String?) {
        this.state = state
    }

    fun getCountry(): String? {
        return country
    }

    fun setCountry(country: String?) {
        this.country = country
    }

    fun getCityId(): Any? {
        return cityId
    }

    fun setCityId(cityId: Any?) {
        this.cityId = cityId
    }

    fun getGeoName(): Any? {
        return geoName
    }

    fun setGeoName(geoName: Any?) {
        this.geoName = geoName
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String?) {
        this.token = token
    }

    fun getUserId(): String? {
        return userId
    }

    fun setUserId(userId: String?) {
        this.userId = userId
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getFeeds(): Any? {
        return feeds
    }

    fun setFeeds(feeds: Any?) {
        this.feeds = feeds
    }

    fun getLargeProfilePicture(): String? {
        return largeProfilePicture
    }

    fun setLargeProfilePicture(largeProfilePicture: String?) {
        this.largeProfilePicture = largeProfilePicture
    }

    fun getLargeCoverPicture(): String? {
        return largeCoverPicture
    }

    fun setLargeCoverPicture(largeCoverPicture: String?) {
        this.largeCoverPicture = largeCoverPicture
    }

    fun getRegistrationStaus(): Boolean? {
        return registrationStaus
    }

    fun setRegistrationStaus(registrationStaus: Boolean?) {
        this.registrationStaus = registrationStaus
    }

    fun getNotificationAlertSettings(): Any? {
        return notificationAlertSettings
    }

    fun setNotificationAlertSettings(notificationAlertSettings: Any?) {
        this.notificationAlertSettings = notificationAlertSettings
    }

    fun getRefName(): String? {
        return refName
    }

    fun setRefName(refName: String?) {
        this.refName = refName
    }

    fun getLanguageId(): String? {
        return languageId
    }

    fun setLanguageId(languageId: String?) {
        this.languageId = languageId
    }

    fun getDateOfBirth(): String? {
        return dateOfBirth
    }

    fun setDateOfBirth(dateOfBirth: String?) {
        this.dateOfBirth = dateOfBirth
    }
}