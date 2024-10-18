package com.onourem.android.activity.arch.activity

import androidx.lifecycle.ViewModel
import javax.inject.Inject
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

abstract class AbstractBaseActivity<VM : ViewModel> : AbstractBaseDefaultActivity() {
    @JvmField
    @Inject
    var viewModelFactory: ViewModelProvider.Factory? = null

    protected lateinit var viewModel: VM
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory!!)[viewModelType()]
    }

    abstract fun viewModelType(): Class<VM>
}