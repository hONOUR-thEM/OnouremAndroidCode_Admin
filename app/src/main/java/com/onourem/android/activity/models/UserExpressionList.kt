package com.onourem.android.activity.models

import androidx.annotation.DrawableRes

class UserExpressionList(
    var id: String,
    var expressionText: String,
    var expressionResponseMsg: String?,
    @field:DrawableRes var moodImage: Int,
    var positivity: String,
    var energy: String
) {
    override fun toString(): String {
        return expressionText
    }
}