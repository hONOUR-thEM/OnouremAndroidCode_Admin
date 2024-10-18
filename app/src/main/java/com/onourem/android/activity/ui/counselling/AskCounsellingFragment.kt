package com.onourem.android.activity.ui.counselling

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentAskCounsellingBannerBinding
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.counselling.adapters.DynamicFragmentPagerAdapter
import com.onourem.android.activity.ui.counselling.offline.InstituteOfflineFragment
import com.onourem.android.activity.ui.counselling.online.InstituteOnlineFragment
import com.onourem.android.activity.ui.counselling.onourem.OnouremOnlineCounsellingFragment
import com.onourem.android.activity.ui.counselling.showcase.CounsellingInfoDialogFragment
import com.onourem.android.activity.ui.utils.HorizontalMarginItemDecoration
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject
import kotlin.math.abs


class AskCounsellingFragment :
    AbstractBaseViewModelBindingFragment<DashboardViewModel, FragmentAskCounsellingBannerBinding>(), ViewPagerCallback {
//    AbstractBaseBottomSheetBindingDialogFragment<DashboardViewModel, FragmentAskCounsellingBannerBinding>() {

    private lateinit var callback: ViewPagerCallback

    private val fragmentList: ArrayList<Fragment> = ArrayList()

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    override fun layoutResource(): Int {
        return R.layout.fragment_ask_counselling_banner
    }

    public override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callback = this

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}
            override fun onPrepareMenu(menu: Menu) {
                menu.findItem(R.id.search_nav)?.setVisible(false)
                menu.findItem(R.id.profile_nav)?.setVisible(false)
                menu.findItem(R.id.phone_nav)?.setVisible(false)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        init()
    }

    private fun init() {

        val args = AskCounsellingFragmentArgs.fromBundle(requireArguments()).apiInstituteResponse


//        binding.cardOnline.setOnClickListener(ViewClickListener {
//            actionOnline()
//        })
//
//        binding.cardOffLine.setOnClickListener(ViewClickListener {
//            actionOffline()
//        })
//
//        binding.tvNotRequired.setOnClickListener(ViewClickListener {
//            actionNotRequired()
//        })

        renderViewPager()

    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val dialIntent = Intent(Intent.ACTION_DIAL)
        dialIntent.setData(Uri.parse("tel:$phoneNumber"))
        startActivity(dialIntent)
    }

//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//
//    }


//    private fun actionNotRequired() {
//        showAlert("Thank you for your response") {
//            dismiss()
//        }
//    }

    private fun actionOffline() {
//        navController.navigate(MobileNavigationDirections.actionGlobalNavOfflineCounselling())
//        updateMood(action.data as UserExpressionList)
        dialPhoneNumber("1-800 891 4416")
        //dismiss()

    }

    private fun actionOnline() {
        dialPhoneNumber("14416")
//        navController.navigate(MobileNavigationDirections.actionGlobalNavOnlineCounselling())
//        updateMood(action.data as UserExpressionList)
        //dismiss()
    }


    private fun renderViewPager() {

        val args = AskCounsellingFragmentArgs.fromBundle(requireArguments()).apiReponseUserMood
        val argsInstitute = AskCounsellingFragmentArgs.fromBundle(requireArguments()).apiInstituteResponse

        if (args.displayCounsellingPopup == "Y") {

            fragmentList.add(
                CounsellingInfoDialogFragment.create(callback)
            )

            if (args.offlineCounselling == "Y") {
                fragmentList.add(
                    InstituteOfflineFragment.create(
                        argsInstitute.institutionInfo,
                        argsInstitute.offlineInstitutionResponseList,
                        callback
                    )
                )
            }

            if (args.onlineCounselling == "Y") {
                fragmentList.add(InstituteOnlineFragment.create(argsInstitute.onlineInstitutionResponse, callback))
            }

            if (args.onouremOnlineCounselling == "Y") {
                fragmentList.add(OnouremOnlineCounsellingFragment.create(argsInstitute.onouremOnlineInstitutionResponse, callback))
            }
        } else {
            fragmentList.add(
                CounsellingInfoDialogFragment.create(callback)
            )

            fragmentList.add(OnouremOnlineCounsellingFragment.create(argsInstitute.onouremOnlineInstitutionResponse, callback))
        }

        binding.viewpager.adapter = DynamicFragmentPagerAdapter(fragmentList, this)

//        binding.viewpager.adapter = object : FragmentStateAdapter(this) {
//
//            override fun createFragment(position: Int): Fragment {
//                return when (position) {
//                    0 -> InstituteOnlineFragment.create()
//                    1 -> InstituteOfflineFragment.create()
//                    2 -> OnouremOnlineCounsellingFragment.create()
//                    else -> {
//                        InstituteOfflineFragment.create()
//                    }
//                }
//            }
//
//            override fun getItemCount(): Int {
//
//
//                return 3
//            }
//        }

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.radioGroup.check(binding.radioGroup.getChildAt(position).id)
            }
        })

        // You need to retain one page on each side so that the next and previous items are visible
        binding.viewpager.offscreenPageLimit = 1
        // Add a PageTransformer that translates the next and previous items horizontally
        // towards the center of the screen, which makes them visible
        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleY = 1 - (0.15f * abs(position))
            // If you want a fading effect uncomment the next line:
            // page.alpha = 0.25f + (1 - abs(position))
        }
        binding.viewpager.setPageTransformer(pageTransformer)
        // The ItemDecoration gives the current (centered) item horizontal margin so that
        // it doesn't occupy the whole screen width. Without it the items overlap
        val itemDecoration = HorizontalMarginItemDecoration(
            fragmentContext,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        binding.viewpager.addItemDecoration(itemDecoration)

        val radioButtons = arrayOfNulls<RadioButton>(
            binding.viewpager.adapter!!.itemCount
        )
        for (i in 0 until binding.viewpager.adapter!!.itemCount) {
            radioButtons[i] = RadioButton(requireActivity())
            radioButtons[i]!!.id = i
            radioButtons[i]!!.setButtonDrawable(R.drawable.selector_radio_button)
            binding.radioGroup.addView(radioButtons[i])
        }
        binding.radioGroup.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
            binding.viewpager.currentItem = checkedId
        }
//        if (fragmentList.size > 1) {
//            binding.radioGroup.check(binding.radioGroup.getChildAt(1).id)
//            binding.viewpager.post { binding.viewpager.setCurrentItem(1, false) }
//        } else {
//            binding.radioGroup.check(binding.radioGroup.getChildAt(0).id)
//            binding.viewpager.post { binding.viewpager.setCurrentItem(0, false) }
//        }


    }

    override fun onNextFragment() {
        // Logic to switch to the next fragment
        val currentItem = binding.viewpager.currentItem
        binding.viewpager.setCurrentItem(currentItem + 1, true) // Move to the next fragment
    }


}