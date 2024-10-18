package com.onourem.android.activity.models

import androidx.annotation.DrawableRes
import com.onourem.android.activity.R

object ActivityType {
    const val ONE_TO_ONE = 0
    const val D_TO_ONE = 1
    const val ONE_TO_MANY = 2
    const val TASK = 3
    const val MESSAGE = 4
    const val ASK_TO_FRIENDS = 5
    const val CARD = 6
    const val SURVEY = 7
    const val EXTERNAL = 8
    const val USER_MOOD_CARD = 11
    const val FRIEND_CIRCLE_GAME = 9
    const val POST = 10
    fun getActivityType(text: String?): Int {
        val status: Int = when (text) {
            "1to1" -> ONE_TO_ONE
            "Dto1" -> D_TO_ONE
            "1toM" -> ONE_TO_MANY
            "Task" -> TASK
            "Message" -> MESSAGE
            "ASKTOFRIENDS" -> ASK_TO_FRIENDS
            "Card" -> CARD
            "Survey" -> SURVEY
            "External" -> EXTERNAL
            "UserMoodCard" -> USER_MOOD_CARD
            "FriendCircleGame" -> FRIEND_CIRCLE_GAME
            "Post" -> POST
            else -> throw IllegalStateException("Unexpected value: $text")
        }
        return status
    }

    @JvmStatic
    @DrawableRes
    fun getActivityTypeIcon(text: String?): Int {
        var status = -1
        when (text) {
            "1to1" -> status = R.drawable.ic_action_1_to_1
            "Dto1" -> status = R.drawable.ic_action_d_to_1
            "1toM" -> status = R.drawable.one_to_m_icon
            "Card" -> status = R.drawable.card_icon
            "Task" -> status = R.drawable.task_icon
            "Message" -> status = R.drawable.message_icon
            "External" -> status = R.drawable.external_icon
            "UserMoodCard" -> status = R.drawable.external_icon
            "Survey", "FriendCircleGame", "Post" -> status = R.drawable.survey_icon
        }
        return status
    }
}