package com.onourem.android.activity.ui.data

import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.models.FilterItem
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.models.PlayGroup

data class DashboardData(
    var activityStatusList: List<String>? = null,
    var gameResIdList: List<String>,
    var loginDayActivityInfoList: List<LoginDayActivityInfoList>? = null,
    var playGroup: PlayGroup? = null,
    var filterItems: ArrayList<FilterItem>? = null,
    var selectedFilterIndex: Int? = null,
    var displayNumberOfActivity: Long? = null,
    var counterFriendsPlaying: Int? = null,
    var recyclerViewPosition: Int? = null,
    var totalActivities: Int? = null,
    var filterActivities: String? = null,
    var layoutManager: LinearLayoutManager,
)