package com.onourem.android.activity.arch.fragment

import android.content.Context
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.HasAndroidInjector
import javax.inject.Inject
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.AndroidInjector

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment(), HasAndroidInjector {

    @JvmField
    @Inject
    var androidInjector: DispatchingAndroidInjector<Any>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector!!
    }
}