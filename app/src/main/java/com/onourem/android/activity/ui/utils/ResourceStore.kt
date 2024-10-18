package com.onourem.android.activity.ui.utils

import com.onourem.android.activity.R

interface ResourceStore {
    companion object {
        val vocalsTabList = listOf(
            R.string.tabVocalsTrending,
            R.string.tabVocalsFriends,
            R.string.tabMyVocals,
            R.string.tabVocalsFavorite
        )
//        val vocalsPagerFragments = listOf(
//            TrendingVocalsFragment.create(), FriendsVocalsFragment.create(
//                navController,
//                viewModelFactory,
//                preferenceHelper,
//                vocalUserId
//            ),
//            MyVocalsFragment.create(navController, viewModelFactory, preferenceHelper, vocalUserId), FavoriteVocalsFragment.create(
//                navController,
//                viewModelFactory,
//                preferenceHelper,
//                vocalUserId
//            )
//        )
    }
}