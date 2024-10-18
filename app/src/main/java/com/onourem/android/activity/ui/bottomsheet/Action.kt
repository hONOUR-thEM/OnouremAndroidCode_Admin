package com.onourem.android.activity.ui.bottomsheet

import android.os.Parcelable
import android.os.Parcel
import androidx.annotation.ColorRes

class Action<T> : Parcelable {

    val label: String?
    val textColor: Int
    val actionType: ActionType?
    var data: T? = null
        private set
    var from: String? = null

    constructor(label: String?, @ColorRes textColor: Int, actionType: ActionType?, data: T) {
        this.label = label
        this.textColor = textColor
        this.actionType = actionType
        this.data = data
    }

    constructor(
        label: String?,
        @ColorRes textColor: Int,
        actionType: ActionType?,
        data: T,
        from: String?
    ) {
        this.label = label
        this.textColor = textColor
        this.actionType = actionType
        this.data = data
        this.from = from
    }

    protected constructor(`in`: Parcel) {
        label = `in`.readString()
        textColor = `in`.readInt()
        from = `in`.readString()
        val tmpActionType = `in`.readInt()
        actionType = if (tmpActionType == -1) null else ActionType.values()[tmpActionType]
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(label)
        dest.writeInt(textColor)
        dest.writeInt(actionType?.ordinal ?: -1)
    }

    companion object CREATOR : Parcelable.Creator<Action<Any>> {
        override fun createFromParcel(parcel: Parcel): Action<Any> {
            return Action(parcel)
        }

        override fun newArray(size: Int): Array<Action<Any>?> {
            return arrayOfNulls(size)
        }
    }
}