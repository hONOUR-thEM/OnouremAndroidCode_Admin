package com.onourem.android.activity.arch.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

abstract class AbstractBaseBottomSheetBindingDialogFragment<VM : ViewModel, B : ViewDataBinding> : AbstractBaseBottomSheetDialogFragment<VM>() {
    protected lateinit var binding: B
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, layoutResource(), container, false)
        return binding.root
    }
}