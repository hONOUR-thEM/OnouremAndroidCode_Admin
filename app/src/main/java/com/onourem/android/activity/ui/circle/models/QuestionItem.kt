package com.onourem.android.activity.ui.circle.models

import android.graphics.drawable.Drawable

class QuestionItem {
    var questionId: String? = null
    var questionText: String? = null
    var questionImage: String? = null
    var bgColor: String? = null
    var waveColor: String? = null
    var drawable: Drawable? = null
    var contactList = ArrayList<QuestionForContacts>()
}