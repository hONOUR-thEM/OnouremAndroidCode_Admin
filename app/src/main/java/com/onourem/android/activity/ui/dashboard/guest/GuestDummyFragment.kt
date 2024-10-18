package com.onourem.android.activity.ui.dashboard.guest

import android.os.Bundle
import android.view.View
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentGuestDummyBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.utils.Utilities
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GuestDummyFragment :
    AbstractBaseViewModelBindingFragment<DashboardViewModel, FragmentGuestDummyBinding>() {


    private var formatter: SimpleDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private var simpleDateFormat: SimpleDateFormat =
        SimpleDateFormat(Utilities.APP_DISPLAY_DATE_TIME_FORMAT, Locale.getDefault())

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null


    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_guest_dummy
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val details = GuestDummyFragmentArgs.fromBundle(requireArguments()).details

        binding.txtTitle.text = details

        binding.btnLogin.setOnClickListener(ViewClickListener {
            (fragmentContext as DashboardActivity).onLogout()
        })

    }


}