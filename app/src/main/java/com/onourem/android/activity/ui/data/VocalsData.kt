package com.onourem.android.activity.ui.data

import com.onourem.android.activity.models.PrivacyGroup
import com.onourem.android.activity.ui.audio.playback.Song

data class VocalsData(
    var isLastPagePagination: Boolean = false,
    var isLoadingPage: Boolean = false,
    var displayNumberOfAudios: Long = 20L,
    var selectedFilter: String = "",
    var selectedFilterInt: String = "",
    var privacyIds: String = "",
    var songsFromServer: MutableList<Song> = mutableListOf<Song>(),
    var privacyGroupsDefault: ArrayList<PrivacyGroup> = ArrayList(),
    var privacyGroups: ArrayList<PrivacyGroup> = ArrayList(),
    var isDataLoading: Boolean = false,
    var audioGameIdList: ArrayList<Int> = ArrayList(),
    var screenTitle: String = "",
    var vocalUserId: String = "",
)