package com.onourem.android.activity.arch.fragment

import androidx.lifecycle.ViewModel
import androidx.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil

abstract class AbstractBaseViewModelBindingFragment<VM : ViewModel, B : ViewDataBinding> : AbstractBaseFragment<VM>() {

    protected lateinit var binding: B

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, layoutResource(), container, false)
        return binding.root
    }
}