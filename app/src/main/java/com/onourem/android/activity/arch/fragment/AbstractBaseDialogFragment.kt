package com.onourem.android.activity.arch.fragment

import androidx.lifecycle.ViewModel
import javax.inject.Inject
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

abstract class AbstractBaseDialogFragment<VM : ViewModel> : AbstractBaseDefaultDialogFragment() {
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

    protected fun hasSharedViewModel(): Boolean {
        return true
    }

    abstract fun viewModelType(): Class<VM>
}