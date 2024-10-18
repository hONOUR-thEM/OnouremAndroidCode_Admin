package com.onourem.android.activity.push

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class NotificationCounter(@field:PrimaryKey var userId: String, var count: Int)