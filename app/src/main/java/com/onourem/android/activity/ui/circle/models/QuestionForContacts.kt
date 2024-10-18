package com.onourem.android.activity.ui.circle.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class QuestionForContacts(
    @PrimaryKey
    @NonNull
    var id: String,
    var userName: String,
    var mobileNumber: String? = null,
    var questionId: String? = null,
    var selected: Boolean = false,
    var pagerPosition: Int? = null,
) : Serializable