package com.onourem.android.activity.ui.games.viewmodel

import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import android.content.Intent
import android.util.Pair
import androidx.lifecycle.LiveData

class MediaPickerViewModel @Inject constructor() : ViewModel() {
    var intentMutableLiveData: MutableLiveData<Pair<Intent, Int?>?>? = MutableLiveData()
    fun getIntentMutableLiveData(): LiveData<Pair<Intent, Int?>?>? {
        return intentMutableLiveData
    }

    fun setIntentMutableLiveData(integerPair: Pair<Intent, Int?>?) {
        intentMutableLiveData?.postValue(integerPair)
    }

    override fun onCleared() {
        super.onCleared()
        intentMutableLiveData = null
    }
}