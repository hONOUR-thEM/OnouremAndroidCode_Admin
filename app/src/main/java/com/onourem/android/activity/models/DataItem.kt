package com.onourem.android.activity.models

import androidx.annotation.DrawableRes

class DataItem<T> {
    val isSection: Boolean
    var icon = -1
        private set
    var isGroup = false
    var isHasMoreAction = true
    var data: T

    constructor(data: T, isSection: Boolean) {
        this.data = data
        this.isSection = isSection
    }

    constructor(data: T, isSection: Boolean, @DrawableRes icon: Int) {
        this.data = data
        this.isSection = isSection
        this.icon = icon
    }
}