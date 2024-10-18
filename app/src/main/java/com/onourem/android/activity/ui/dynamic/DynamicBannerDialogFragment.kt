package com.onourem.android.activity.ui.dynamic

import com.onourem.android.activity.arch.fragment.AbstractBaseBindingDialogFragment
import com.onourem.android.activity.ui.appreciation.viewmodel.AppreciationViewModel
import javax.inject.Inject
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.models.CustomScreenPopup
import android.os.CountDownTimer
import com.onourem.android.activity.R
import android.os.Bundle
import android.view.View
import com.onourem.android.activity.databinding.DialogDynamicBannerBinding
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.ui.dynamic.DynamicBannerDialogFragment

class DynamicBannerDialogFragment :
    AbstractBaseBindingDialogFragment<AppreciationViewModel, DialogDynamicBannerBinding>() {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    var customScreenPopup: CustomScreenPopup? = null
    private var countDownTimer: CountDownTimer? = null
    override fun viewModelType(): Class<AppreciationViewModel> {
        return AppreciationViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.dialog_dynamic_banner
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        if (customScreenPopup != null) {
            binding.tvTitle.text = customScreenPopup!!.title
            if (customScreenPopup!!.txtMessage != null) {
                binding.tvMessage.text = customScreenPopup!!.txtMessage
            } else {
                binding.tvMessage.visibility = View.GONE
            }

//            Glide.with(requireActivity())
//                    .load(customScreenPopup.getImageName())
//                    .into(binding.ivDynamicImage);
            if (customScreenPopup!!.btnOneText != null) {
                binding.btn1.text = customScreenPopup!!.btnOneText
                binding.btn1.setOnClickListener(ViewClickListener { v: View? -> navController.popBackStack() })
            } else {
                binding.btn1.visibility = View.GONE
            }
            if (customScreenPopup!!.btnTwoText != null) {
                binding.btn2.text = customScreenPopup!!.btnTwoText
                binding.btn2.setOnClickListener(ViewClickListener { v: View? -> navController.popBackStack() })
            } else {
                binding.btn2.visibility = View.GONE
            }
            binding.cvClose.setOnClickListener(ViewClickListener { v: View? -> popBackStack() })
            countDownTimer = object : CountDownTimer(150000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    binding.tvCounter.text = String.format(
                        "This banner will be auto closed in %s seconds",
                        millisUntilFinished / 1000
                    )
                }

                override fun onFinish() {
                    dismiss()
                }
            }.start()
        }
    }

    private fun popBackStack() {
        countDownTimer!!.cancel()
        dismiss()
    }

    companion object {
        fun getInstance(expressionDataResponse: CustomScreenPopup?): DynamicBannerDialogFragment {
            val dynamicBannerDialogFragment = DynamicBannerDialogFragment()
            dynamicBannerDialogFragment.customScreenPopup = expressionDataResponse
            return dynamicBannerDialogFragment
        }
    }
}