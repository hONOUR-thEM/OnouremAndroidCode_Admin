package com.onourem.android.activity.models

data class CodeNameId(
    var id: String? = null,
    var name: String? = null
) {
    override fun toString(): String {
        return name!!
    }
}