package com.onourem.android.activity.push

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MessageNotificationCounter(
    @field:PrimaryKey var userId: String,
    var count: String,
    var conversationId: String
)