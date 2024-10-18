package com.onourem.android.activity.ui.onboarding.fragments

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.navigation.NavDirections
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentOnboardingBinding
import com.onourem.android.activity.models.IntroResponse
import com.onourem.android.activity.models.OnboardingPage
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.onboarding.adapters.OnboardingPagerAdapter
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.viewmodel.OnboardingViewModel
import javax.inject.Inject

class OnboardingFragment :
    AbstractBaseViewModelBindingFragment<OnboardingViewModel, FragmentOnboardingBinding>(),
    OnPageChangeListener {
    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var currentPage = 0
    private var onboardingPages: ArrayList<OnboardingPage>? = null
    override fun layoutResource(): Int {
        return R.layout.fragment_onboarding
    }

    //    private void showDemoScreens(Context context, ArrayList<String> stringArrayList) {
    ////        viewPagerOnboarding.setAdapter(new DemoPagerAdapter(context, stringArrayList));
    //
    //        Dialog dialog = new Dialog(context, R.style.FullScreenDialogStyle);
    ////        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    ////
    ////        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    ////
    ////        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    //
    //        // layout to display
    //        View view = LayoutInflater.from(context).inflate(R.layout.dialog_demo_screens, null, false);
    //        dialog.setContentView(view);
    //
    //        dialog.setCanceledOnTouchOutside(false);
    //        dialog.setCancelable(false);
    //        dialog.show();
    //
    //        RecyclerView rvTest = view.findViewById(R.id.rvDemoScreens);
    //        rvTest.setHasFixedSize(true);
    //        rvTest.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    //        DemoScreensAdapter rvAdapter = new DemoScreensAdapter(context, stringArrayList);
    //        rvTest.setAdapter(rvAdapter);
    //    }
    override fun viewModelType(): Class<OnboardingViewModel> {
        return OnboardingViewModel::class.java
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.intro()
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<IntroResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        preferenceHelper!!.putValue(
                            Constants.KEY_IS_LINK_VERIFIED,
                            apiResponse.body.linkVerified.toString()
                        )
                        preferenceHelper!!.putValue(
                            Constants.KEY_CAN_APP_INSTALLED_DIRECTLY,
                            apiResponse.body.canAppInstalledDirectly
                        )
                        preferenceHelper!!.putValue(
                            Constants.KEY_INTRO_VIDEO_HINDI,
                            apiResponse.body.productTourVideo1
                        )
                        preferenceHelper!!.putValue(
                            Constants.KEY_INTRO_VIDEO_ENGLISH,
                            apiResponse.body.productTourVideo2
                        )
                        setAdapter(apiResponse.body)
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                }
            }
    }

    private fun setAdapter(body: IntroResponse?) {
        onboardingPages = ArrayList()
        onboardingPages!!.add(
            OnboardingPage(
                R.string.label_page1_title,
                R.string.label_page1_desc,
                R.drawable.appintro1,
                false,
                "",
                ""
            )
        )
        onboardingPages!!.add(
            OnboardingPage(
                R.string.label_page2_title,
                R.string.label_page2_desc,
                R.drawable.appintro2,
                false,
                "",
                ""
            )
        )
        onboardingPages!!.add(
            OnboardingPage(
                R.string.label_page3_title,
                R.string.label_page3_desc,
                R.drawable.appintro3,
                false,
                "",
                ""
            )
        )
        onboardingPages!!.add(
            OnboardingPage(
                R.string.label_page3_title,
                R.string.label_page3_desc,
                R.drawable.appintro4,
                false,
                "",
                ""
            )
        )
        onboardingPages!!.add(
            OnboardingPage(
                R.string.label_page4_title,
                R.string.label_page3_desc,
                R.drawable.appintro5,
                false,
                "",
                ""
            )
        )
        onboardingPages!!.add(
            OnboardingPage(
                R.string.label_page4_title,
                R.string.label_page3_desc,
                R.drawable.appintro6,
                false,
                "",
                ""
            )
        )
        onboardingPages!!.add(
            OnboardingPage(
                R.string.label_page4_title,
                R.string.label_page3_desc,
                R.drawable.appintro7,
                false,
                "",
                ""
            )
        )
        onboardingPages!!.add(
            OnboardingPage(
                R.string.label_page5_title,
                R.string.label_page3_desc,
                R.drawable.appintro8,
                true,
                body!!.productTourVideo1!!,
                body.productTourVideo2!!
            )
        )
        binding.viewPagerOnboarding.adapter =
            OnboardingPagerAdapter(requireActivity(), onboardingPages!!, navController)
        binding.viewPagerOnboarding.addOnPageChangeListener(this)
        val radioButtons = arrayOfNulls<RadioButton>(
            binding.viewPagerOnboarding.adapter!!.count
        )
        for (i in 0 until binding.viewPagerOnboarding.adapter!!.count) {
            radioButtons[i] = RadioButton(requireActivity())
            radioButtons[i]!!.id = i
            radioButtons[i]!!.setButtonDrawable(R.drawable.selector_radio_button)
            binding.radioGroup.addView(radioButtons[i])
        }
        binding.radioGroup.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            binding.viewPagerOnboarding.currentItem = checkedId
        }
        binding.radioGroup.check(binding.radioGroup.getChildAt(0).id)
    }

    private fun letsContinue() {
        if (navController.currentDestination!!.id != R.id.nav_onboarding) return
        navController.popBackStack(R.id.nav_splash, true)
        val action: NavDirections =
            OnboardingFragmentDirections.actionNavOnboardingToLoginFragment(null)
        navController.navigate(action)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (binding.viewPagerOnboarding.adapter != null && position == binding.viewPagerOnboarding.adapter!!
                .count - 1
        ) {
            if (position == binding.viewPagerOnboarding.adapter!!.count - 1) {
//                currentPage += 1
                if (currentPage > binding.viewPagerOnboarding.adapter!!.count - 1) {
                    letsContinue()
                } else {
                    currentPage += 1
                }
            }
        }
    }

    override fun onPageSelected(position: Int) {
        currentPage = position
        if (binding.viewPagerOnboarding.adapter != null && position == binding.viewPagerOnboarding.adapter!!
                .count - 1
        ) {
            binding.radioGroup.visibility = View.GONE
        } else {
            binding.radioGroup.visibility = View.VISIBLE
        }
        binding.radioGroup.check(binding.radioGroup.getChildAt(position).id)
    }

    override fun onPageScrollStateChanged(state: Int) {
//        Log.d("####", "onPageScrollStateChanged: " + state);
    }
}