package com.onourem.android.activity.ui.counselling.showcase

import android.os.Bundle
import android.view.View
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.DialogCounsellingInfoBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.counselling.CounsellingViewModel
import com.onourem.android.activity.ui.counselling.ViewPagerCallback
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class CounsellingInfoDialogFragment(val callback: ViewPagerCallback) :
    AbstractBaseViewModelBindingFragment<CounsellingViewModel, DialogCounsellingInfoBinding>() {

    @JvmField
    @Inject
    var sharedPreferenceHelper: SharedPreferenceHelper? = null
    public override fun viewModelType(): Class<CounsellingViewModel> {
        return CounsellingViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.dialog_counselling_info
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnShow.setOnClickListener(ViewClickListener {
            callback.onNextFragment()
        })
    }

    companion object {
        fun create(callback: ViewPagerCallback): CounsellingInfoDialogFragment {
            return CounsellingInfoDialogFragment(callback)
        }
    }
}