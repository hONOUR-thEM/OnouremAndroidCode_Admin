package com.onourem.android.activity.arch.fragment

import androidx.lifecycle.ViewModel
import javax.inject.Inject
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

abstract class AbstractBaseFragment<VM : ViewModel> : AbstractBaseDefaultFragment() {
    @Inject
    protected lateinit var viewModelFactory: ViewModelProvider.Factory

    protected lateinit var viewModel: VM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = if (hasSharedViewModel()) {
            ViewModelProvider(requireActivity(), viewModelFactory)[viewModelType()]
        } else {
            ViewModelProvider(this, viewModelFactory)[viewModelType()]
        }
    }

    abstract fun viewModelType(): Class<VM>

    protected fun hasSharedViewModel(): Boolean {
        return true
    }
}