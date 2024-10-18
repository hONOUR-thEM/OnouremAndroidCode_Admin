package com.onourem.android.activity.ui.circle.models

class ContactItem {
    var displayName: String? = null
    var photoUrl: String? = null
    var arrayListPhone = ArrayList<PhoneContact>()
    var selected = false
    var isRowCreated = false
    var selectedMobileNumber: String? = null
    var lookupKey: String? = null
}