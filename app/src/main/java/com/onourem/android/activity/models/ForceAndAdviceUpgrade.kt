package com.onourem.android.activity.models

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class ForceAndAdviceUpgrade : Parcelable {
    @SerializedName("iosForceUpgradeVersion")
    @Expose
    var iosForceUpgradeVersion: String? = null

    @SerializedName("iosAdviceUpgradeVersion")
    @Expose
    var iosAdviceUpgradeVersion: String? = null

    @SerializedName("androidForceUpgradeVersion")
    @Expose
    var androidForceUpgradeVersion: String? = null

    @SerializedName("androidAdviceUpgradeVersion")
    @Expose
    var androidAdviceUpgradeVersion: String? = null

    @SerializedName("iosNewVersionMessage")
    @Expose
    var iosNewVersionMessage: String? = null

    @SerializedName("androidNewVersionMessage")
    @Expose
    var androidNewVersionMessage: String? = null

    @SerializedName("appStoreUrl")
    @Expose
    var appStoreUrl: String? = null

    @SerializedName("playStoreUrl")
    @Expose
    var playStoreUrl: String? = null

    @SerializedName("screenTitle")
    @Expose
    var screenTitle: String? = null

    @SerializedName("oKButtonTitle")
    @Expose
    var oKButtonTitle: String? = null

    @SerializedName("cancelButtonTitle")
    @Expose
    var cancelButtonTitle: String? = null

    @SerializedName("frequency")
    @Expose
    var frequency: Long? = null

    constructor()
    protected constructor(`in`: Parcel) {
        iosForceUpgradeVersion = `in`.readString()
        iosAdviceUpgradeVersion = `in`.readString()
        androidForceUpgradeVersion = `in`.readString()
        androidAdviceUpgradeVersion = `in`.readString()
        iosNewVersionMessage = `in`.readString()
        androidNewVersionMessage = `in`.readString()
        appStoreUrl = `in`.readString()
        playStoreUrl = `in`.readString()
        screenTitle = `in`.readString()
        oKButtonTitle = `in`.readString()
        cancelButtonTitle = `in`.readString()
        frequency = if (`in`.readByte().toInt() == 0) {
            null
        } else {
            `in`.readLong()
        }
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(iosForceUpgradeVersion)
        dest.writeString(iosAdviceUpgradeVersion)
        dest.writeString(androidForceUpgradeVersion)
        dest.writeString(androidAdviceUpgradeVersion)
        dest.writeString(iosNewVersionMessage)
        dest.writeString(androidNewVersionMessage)
        dest.writeString(appStoreUrl)
        dest.writeString(playStoreUrl)
        dest.writeString(screenTitle)
        dest.writeString(oKButtonTitle)
        dest.writeString(cancelButtonTitle)
        if (frequency == null) {
            dest.writeByte(0.toByte())
        } else {
            dest.writeByte(1.toByte())
            dest.writeLong(frequency!!)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    @JvmName("getAndroidForceUpgradeVersion1")
    fun getAndroidForceUpgradeVersion(): String {
        return if (TextUtils.isEmpty(androidForceUpgradeVersion)) "0" else androidForceUpgradeVersion!!
    }

    @JvmName("setAndroidForceUpgradeVersion1")
    fun setAndroidForceUpgradeVersion(androidForceUpgradeVersion: String?) {
        this.androidForceUpgradeVersion = androidForceUpgradeVersion
    }

    @JvmName("getAndroidAdviceUpgradeVersion1")
    fun getAndroidAdviceUpgradeVersion(): String {
        return if (TextUtils.isEmpty(androidAdviceUpgradeVersion)) "0" else androidAdviceUpgradeVersion!!
    }

    @JvmName("setAndroidAdviceUpgradeVersion1")
    fun setAndroidAdviceUpgradeVersion(androidAdviceUpgradeVersion: String?) {
        this.androidAdviceUpgradeVersion = androidAdviceUpgradeVersion
    }


    companion object CREATOR : Parcelable.Creator<ForceAndAdviceUpgrade> {
        override fun createFromParcel(parcel: Parcel): ForceAndAdviceUpgrade {
            return ForceAndAdviceUpgrade(parcel)
        }

        override fun newArray(size: Int): Array<ForceAndAdviceUpgrade?> {
            return arrayOfNulls(size)
        }
    }
}