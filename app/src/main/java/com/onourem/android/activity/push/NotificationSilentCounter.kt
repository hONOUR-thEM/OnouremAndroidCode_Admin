package com.onourem.android.activity.push


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class NotificationSilentCounter(@field:PrimaryKey var id: String, var countSilent: String)