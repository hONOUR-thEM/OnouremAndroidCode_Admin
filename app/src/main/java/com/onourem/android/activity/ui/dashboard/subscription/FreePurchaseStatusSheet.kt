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

class FreePurchaseStatusSheet : AbstractBaseBottomSheetBindingDialogFragment<DashboardViewModel, FragmentPurchaseStatusBinding>() {


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

        binding.constraintLayoutBS.background =
            ContextCompat.getDrawable(fragmentContext, R.drawable.bottom_sheet_rounded_corner_green)
        binding.cvClose.setCardColor(Color.parseColor("#43AE30"))

        binding.cvClose.setOnClickListener(ViewClickListener {
            startActivity(Intent.makeRestartActivityTask(requireActivity().intent.component));
        })

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