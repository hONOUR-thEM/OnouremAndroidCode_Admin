package com.onourem.android.activity.ui.dashboard.subscription

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentShareCouponsBinding
import com.onourem.android.activity.models.FreeSubscriptionRes
import com.onourem.android.activity.models.GetFreeSubscriptionsResponse
import com.onourem.android.activity.models.ShareCouponInfo
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject

class ShareCodeFragment : AbstractBaseBottomSheetBindingDialogFragment<DashboardViewModel, FragmentShareCouponsBinding>() {

    private lateinit var adapter: ShareCouponsAdapter

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_share_coupons
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)

        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)

        binding.cvClose.setOnClickListener(ViewClickListener {
            dismiss()
        })

        val args = ShareCodeFragmentArgs.fromBundle(requireArguments())

        viewModel.getFreeSubscriptions(args.info.packageCode!!, args.info.startDate!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetFreeSubscriptionsResponse> ->
                if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (apiResponse.loading) {
                        showProgress()
                    }
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                        if (apiResponse.body.freeSubscriptionResList.isNotEmpty()){
                            setAdapter(apiResponse.body.freeSubscriptionResList)
                        }

                        hideProgress()

                    } else {
                        hideProgress()
                        showAlert(apiResponse.body.errorMessage)
                    }
                }
            }
    }

    private fun setAdapter(shareCodeList: List<FreeSubscriptionRes>) {
        val layoutManager = LinearLayoutManager(activity)
        binding.rvShareCoupons.layoutManager = layoutManager

        adapter = ShareCouponsAdapter(shareCodeList as ArrayList<FreeSubscriptionRes>) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Onourem")
            val shareMessage = it.shareText
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)

            try {
                fragmentContext.startActivity(Intent.createChooser(shareIntent, "Share this subscription to"))
            } catch (ex : ActivityNotFoundException) {
                Toast.makeText(fragmentContext,"NO APP TO HANDLE THIS", Toast.LENGTH_LONG).show()
            }
        }

        binding.rvShareCoupons.adapter = adapter
    }

}