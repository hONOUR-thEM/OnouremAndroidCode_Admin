package com.onourem.android.activity.ui.dashboard.watchlist

interface OnWatchListItemClickListener<T> {
    fun onItemClick(item: T, position: Int)
}