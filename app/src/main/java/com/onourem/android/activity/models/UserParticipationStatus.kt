package com.onourem.android.activity.models

object UserParticipationStatus {
    var NONE = 0
    var PENDING = 1
    var WAITING = 2
    var SETTLED = 3
    fun getParticipationStatus(statusText: String?): Int {
        val status: Int = when (statusText) {
            "Pending" -> PENDING
            "Waiting" -> WAITING
            "Settled" -> SETTLED
            else -> NONE
        }
        return status
    }
}