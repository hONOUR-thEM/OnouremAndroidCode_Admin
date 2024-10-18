package com.onourem.android.activity.models

class Mood(

    var quadrant: String,
    var x: Int,
    var y: Int,
    var moodName: String


) {
    override fun toString(): String {
        return moodName
    }
}