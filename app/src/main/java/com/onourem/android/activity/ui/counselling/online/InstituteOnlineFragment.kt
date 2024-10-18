package com.onourem.android.activity.ui.counselling.online

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.ItemInstituteOnlinePagerBinding
import com.onourem.android.activity.models.OnlineInstitutionResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.counselling.CounsellingViewModel
import com.onourem.android.activity.ui.counselling.ViewPagerCallback
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class InstituteOnlineFragment(val onlineInstitutionResponse: OnlineInstitutionResponse, val callback: ViewPagerCallback) :
    AbstractBaseViewModelBindingFragment<CounsellingViewModel, ItemInstituteOnlinePagerBinding>() {


    @Inject
    lateinit var preferenceHelper: SharedPreferenceHelper


    companion object {
        fun create(onlineInstitutionResponse: OnlineInstitutionResponse, callback: ViewPagerCallback): InstituteOnlineFragment {
            return InstituteOnlineFragment(onlineInstitutionResponse, callback)
        }
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.setData(Uri.parse("tel:$phoneNumber"))
        startActivity(dialIntent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvProviderName.text = onlineInstitutionResponse.partnerName
        binding.tvCallText.text = onlineInstitutionResponse.partnerPhoneNumber

        binding.tvMoreInfoText.setOnClickListener(ViewClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(onlineInstitutionResponse.partnerLink))
            startActivity(intent)
        })

        binding.btnShow.setOnClickListener(ViewClickListener {
            callback.onNextFragment()
        })

        binding.tvCallText.setOnClickListener(ViewClickListener {
            dialPhoneNumber(onlineInstitutionResponse.partnerPhoneNumber)
        })
    }

    override fun layoutResource(): Int {
        return R.layout.item_institute_online_pager
    }

    override fun viewModelType(): Class<CounsellingViewModel> {
        return CounsellingViewModel::class.java
    }


}