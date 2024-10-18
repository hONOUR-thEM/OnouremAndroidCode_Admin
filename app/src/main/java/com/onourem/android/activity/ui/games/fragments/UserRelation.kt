package com.onourem.android.activity.ui.games.fragments

import androidx.annotation.IntDef

interface UserRelation {
    @IntDef(FRIENDS, ADD_FRIEND, ACCEPT_REQUEST, CANCEL_REQUEST, NONE)
    annotation class Status
    companion object {
        @JvmStatic
        fun getStatus(state: String?): Int {
            @Status val status: Int
            status = if ("Friends".equals(state, ignoreCase = true)) {
                FRIENDS
            } else if ("Add as Friend".equals(state, ignoreCase = true)) {
                ADD_FRIEND
            } else if ("Accept Request".equals(state, ignoreCase = true)) {
                ACCEPT_REQUEST
            } else if ("Friend Request Sent".equals(state, ignoreCase = true)) {
                CANCEL_REQUEST
            } else {
                NONE
            }
            return status
        }

        const val FRIENDS = 0
        const val ADD_FRIEND = 1
        const val ACCEPT_REQUEST = 2
        const val CANCEL_REQUEST = 3
        const val NONE = -1
    }
}