package com.onourem.android.activity.ui.counselling.offline

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.ItemInstituteOfflinePagerBinding
import com.onourem.android.activity.models.InstitutionInfo
import com.onourem.android.activity.models.OfflineInstitutionResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.counselling.CounsellingViewModel
import com.onourem.android.activity.ui.counselling.ViewPagerCallback
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class InstituteOfflineFragment(
    val institutionInfo: InstitutionInfo,
    val offlineInstitutionResponseList: List<OfflineInstitutionResponse>,
    val callback: ViewPagerCallback
) :
    AbstractBaseViewModelBindingFragment<CounsellingViewModel, ItemInstituteOfflinePagerBinding>() {


    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper

    companion object {
        fun create(
            institutionInfo: InstitutionInfo,
            offlineInstitutionResponseList: List<OfflineInstitutionResponse>,
            callback: ViewPagerCallback
        ): InstituteOfflineFragment {
            return InstituteOfflineFragment(institutionInfo, offlineInstitutionResponseList, callback)
        }
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.setData(Uri.parse("tel:$phoneNumber"))
        startActivity(dialIntent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvProviderName.text = institutionInfo.name
        binding.tvLocationText.text = institutionInfo.address
        binding.tvCallText.text = institutionInfo.counsellingContactNumber

        binding.tvEmergencyContactText.text =
            "${offlineInstitutionResponseList[0].name} \n ${offlineInstitutionResponseList[0].contactNumber1}"

        binding.tvOpenHoursText.text = "${institutionInfo.timings}"

        binding.tvCallText.setOnClickListener(ViewClickListener {
            dialPhoneNumber(institutionInfo.counsellingContactNumber)
        })

        binding.tvEmergencyContactText.setOnClickListener(ViewClickListener {
            dialPhoneNumber(offlineInstitutionResponseList[0].contactNumber1)
        })

        binding.tvMoreInfoText.setOnClickListener(ViewClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(institutionInfo.officialHelpLink))
            startActivity(intent)
        })

        binding.btnShow.setOnClickListener(ViewClickListener {
            callback.onNextFragment()
        })
    }

    override fun layoutResource(): Int {
        return R.layout.item_institute_offline_pager
    }

    override fun viewModelType(): Class<CounsellingViewModel> {
        return CounsellingViewModel::class.java
    }

}