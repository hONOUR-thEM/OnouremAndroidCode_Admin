package com.onourem.android.activity.arch.fragment

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

abstract class AbstractBaseBindingDialogFragment<VM : ViewModel, B : ViewDataBinding> : AbstractBaseDefaultBindingDialogFragment<B>() {
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