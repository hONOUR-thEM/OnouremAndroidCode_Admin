package com.onourem.android.activity.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class PopUpCommonData : Serializable {
    @SerializedName("customScreenPopup")
    @Expose
    var customScreenPopup: CustomScreenPopup? = null

    @SerializedName("popupRequired")
    @Expose
    var isPopupRequired = false
}