package com.onourem.android.activity.ui.data

import com.onourem.android.activity.models.PrivacyGroup
import com.onourem.android.activity.ui.audio.playback.Song

data class VocalsDataHold(
    var isLastPagePagination: Boolean = false,
    var isLoadingPage: Boolean = false,
    var displayNumberOfAudios: Long = 20L,
    var privacyIds: String = "",
    var songsFromServer: MutableList<Song> = mutableListOf<Song>(),
    var privacyGroupsDefault: ArrayList<PrivacyGroup> = ArrayList(),
    var privacyGroups: ArrayList<PrivacyGroup> = ArrayList(),
    var audioGameIdList: ArrayList<Int> = ArrayList(),
)