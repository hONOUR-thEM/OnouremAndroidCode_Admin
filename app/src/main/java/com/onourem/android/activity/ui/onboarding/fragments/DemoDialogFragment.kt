package com.onourem.android.activity.ui.onboarding.fragments

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.navigation.NavDirections
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogDemoBinding
import com.onourem.android.activity.models.IntroResponse
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.models.OnboardingPage
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.onboarding.adapters.OnboardingPagerAdapter
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.OnboardingViewModel
import javax.inject.Inject

class DemoDialogFragment :
    AbstractBaseBindingDialogFragment<OnboardingViewModel, DialogDemoBinding>(),
    OnPageChangeListener {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var onItemClickListener: OnItemClickListener<LoginDayActivityInfoList>? = null
    private var currentPage = 0
    private val onboardingPages: ArrayList<OnboardingPage>? = null
    override fun layoutResource(): Int {
        return R.layout.dialog_demo
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        View.OnClickListener clickListener = new ViewClickListener(view1 -> {
//            if (onItemClickListener != null) {
        //     onItemClickListener.onItemClick(activityInfoList);
//            }
//            dismissAllowingStateLoss();
//        });
        binding.viewPagerDemoOnboarding.adapter =
            OnboardingPagerAdapter(requireActivity(), onboardingPages!!, navController)
        binding.viewPagerDemoOnboarding.addOnPageChangeListener(this)
        binding.btnContinue.setOnClickListener(ViewClickListener { v: View? -> letsContinue() })
        val radioButtons = arrayOfNulls<RadioButton>(
            binding.viewPagerDemoOnboarding.adapter!!.count
        )
        for (i in 0 until binding.viewPagerDemoOnboarding.adapter!!.count) {
            radioButtons[i] = RadioButton(requireActivity())
            radioButtons[i]!!.id = i
            radioButtons[i]!!.setButtonDrawable(R.drawable.selector_radio_button)
            binding.radioGroup.addView(radioButtons[i])
        }
        binding.radioGroup.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            binding.viewPagerDemoOnboarding.currentItem = checkedId
        }
        binding.radioGroup.check(binding.radioGroup.getChildAt(0).id)
        viewModel.intro()
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<IntroResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                    imageUrlList = apiResponse.body.getDemoImgList();
                        preferenceHelper!!.putValue(
                            Constants.KEY_IS_LINK_VERIFIED,
                            apiResponse.body.linkVerified.toString()
                        )
                        preferenceHelper!!.putValue(
                            Constants.KEY_CAN_APP_INSTALLED_DIRECTLY,
                            apiResponse.body.canAppInstalledDirectly
                        )
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                }
            }
    }

    private fun letsContinue() {
        if (navController.currentDestination!!.id != R.id.nav_onboarding) return
        navController.popBackStack(R.id.nav_splash, true)
        val action: NavDirections =
            OnboardingFragmentDirections.actionNavOnboardingToLoginFragment(null)
        navController.navigate(action)
        //        linkUserId = preferenceHelper.getString(KEY_LINK_USER_ID);
    }

    override fun isCancelable(): Boolean {
        return false
    }

    override fun viewModelType(): Class<OnboardingViewModel> {
        return OnboardingViewModel::class.java
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (binding.viewPagerDemoOnboarding.adapter != null && position == binding.viewPagerDemoOnboarding.adapter!!
                .count - 1
        ) {
            if (binding.btnContinue.visibility == View.VISIBLE && position == binding.viewPagerDemoOnboarding.adapter!!
                    .count - 1
            ) {
                currentPage = currentPage + 1
                if (currentPage > binding.viewPagerDemoOnboarding.adapter!!.count - 1) {
                    letsContinue()
                }
            }
        }
    }

    override fun onPageSelected(position: Int) {
        if (binding.viewPagerDemoOnboarding.adapter != null && position == binding.viewPagerDemoOnboarding.adapter!!
                .count - 1
        ) {
            binding.btnContinue.visibility = View.VISIBLE
            binding.radioGroup.visibility = View.GONE
        } else {
            binding.btnContinue.visibility = View.GONE
            binding.radioGroup.visibility = View.VISIBLE
        }
        binding.radioGroup.check(binding.radioGroup.getChildAt(position).id)
    }

    override fun onPageScrollStateChanged(state: Int) {
//        Log.d("####", "onPageScrollStateChanged: " + state);
    }

    companion object {
        fun getInstance(onItemClickListener: OnItemClickListener<LoginDayActivityInfoList>?): DemoDialogFragment {
            val demoDialogFragment = DemoDialogFragment()
            demoDialogFragment.onItemClickListener = onItemClickListener
            return demoDialogFragment
        }
    }
}