package com.onourem.android.activity.ui.dashboard.subscription

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentPurchaseStatusBinding
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject

class PurchaseStatusSheet : AbstractBaseBottomSheetBindingDialogFragment<DashboardViewModel, FragmentPurchaseStatusBinding>() {


    private lateinit var args: PurchaseStatusSheetArgs
    private var orderStatus: String = ""

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_purchase_status
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)

        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)

        args = PurchaseStatusSheetArgs.fromBundle(requireArguments())
        when (args.paymentStatus.status) {

            "Successful" -> {
                binding.constraintLayoutBS.background =
                    ContextCompat.getDrawable(fragmentContext, R.drawable.bottom_sheet_rounded_corner_green)
                binding.cvClose.setCardColor(Color.parseColor("#43AE30"))
                orderStatus = "Successful"
                binding.tvDialogTitle.text = "Payment Successful"
                binding.tvDialogDescription.text =
                    "You have a new subscription. Check Subscriptions under main menu. If you have free or discounted gift subscriptions in your package, make sure they are used before coupon's expiry date."
            }

            "Failed" -> {
                binding.constraintLayoutBS.background =
                    ContextCompat.getDrawable(fragmentContext, R.drawable.bottom_sheet_rounded_corner_red)
                binding.cvClose.setCardColor(Color.parseColor("#F9659A"))
                binding.tvDialogTitle.text = "Payment Declined"
                binding.tvDialogDescription.text =
                    "Your payment has been declined. Please try again or use a different payment method."
                orderStatus = "Failed"
            }

            "Aborted" -> {
                binding.constraintLayoutBS.background =
                    ContextCompat.getDrawable(fragmentContext, R.drawable.bottom_sheet_rounded_corner_red)
                binding.cvClose.setCardColor(Color.parseColor("#F9659A"))
                orderStatus = "Aborted"
                binding.tvDialogTitle.text = "Payment Cancelled"
                binding.tvDialogDescription.text = "You have cancelled the payment. If this was unintentional, start again."
            }

            "UserWentBack" -> {
                binding.constraintLayoutBS.background =
                    ContextCompat.getDrawable(fragmentContext, R.drawable.bottom_sheet_rounded_corner_red)
                binding.cvClose.setCardColor(Color.parseColor("#F9659A"))
                orderStatus = "Aborted"
                binding.tvDialogTitle.text = "Payment Cancelled"
                binding.tvDialogDescription.text = "You have cancelled the payment. If this was unintentional, start again."
            }

            else -> {
                binding.constraintLayoutBS.background =
                    ContextCompat.getDrawable(fragmentContext, R.drawable.bottom_sheet_rounded_corner_red)
                binding.cvClose.setCardColor(Color.parseColor("#F9659A"))
                orderStatus = "Error"
                binding.tvDialogTitle.text = "Error"
                binding.tvDialogDescription.text =
                    "An error has occurred during processing of your payment. Please contact us from the main menu if payment is deducted from your bank account."

            }
        }

        binding.cvClose.setOnClickListener(ViewClickListener {
            dismiss()
            when (args.paymentStatus.status) {
                "Successful" -> {
                    startActivity(Intent.makeRestartActivityTask(requireActivity().intent.component));
                }
            }
        })


//

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

    }

}

private fun MaterialCardView.setCardColor(color: Int) {
    this.setCardBackgroundColor(color)
//    this.backgroundTintList = ColorStateList.valueOf(color)
//    this.strokeColor = Color.parseColor("#000000")
//    this.strokeWidth = 1
//    this.cardElevation = 2f
}