package com.onourem.android.activity.ui.counselling.onourem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentOnlineCounsellingBinding
import com.onourem.android.activity.models.OnouremOnlineInstitutionResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.counselling.ViewPagerCallback
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject


class OnouremOnlineCounsellingFragment(private val onouremOnlineInstitutionResponse: OnouremOnlineInstitutionResponse, val callback: ViewPagerCallback) :
    AbstractBaseViewModelBindingFragment<DashboardViewModel, FragmentOnlineCounsellingBinding>() {

    companion object {
        fun create(onouremOnlineInstitutionResponse: OnouremOnlineInstitutionResponse, callback: ViewPagerCallback): OnouremOnlineCounsellingFragment {
            return OnouremOnlineCounsellingFragment(onouremOnlineInstitutionResponse, callback)
        }
    }

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_online_counselling
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.setData(Uri.parse("tel:$phoneNumber"))
        startActivity(dialIntent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvProviderName.text = onouremOnlineInstitutionResponse.partnerName
        binding.tvCallText.text = onouremOnlineInstitutionResponse.partnerPhoneNumber

        binding.tvMoreInfoText.setOnClickListener(ViewClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(onouremOnlineInstitutionResponse.partnerLink))
            startActivity(intent)
        })

        binding.tvCallText.setOnClickListener(ViewClickListener {
            dialPhoneNumber(onouremOnlineInstitutionResponse.partnerPhoneNumber)
        })


    }

}