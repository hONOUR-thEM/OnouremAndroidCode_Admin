package com.onourem.android.activity.ui.dashboard.mood

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentMoodReasonSheetBinding
import com.onourem.android.activity.models.StandardResponse
import com.onourem.android.activity.models.UserExpressionList
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.adapters.AudioCategoryAdapter
import com.onourem.android.activity.ui.audio.models.GetAudioCategoryResponse
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject

class MoodReasonSheetFragment :
    AbstractBaseBottomSheetBindingDialogFragment<DashboardViewModel, FragmentMoodReasonSheetBinding>() {

    private var expressionList: UserExpressionList? = null
    private lateinit var response: GetAudioCategoryResponse
    private lateinit var audioCategoryAdapter: AudioCategoryAdapter

    private val options = RequestOptions()
        .fitCenter()
        .transform(FitCenter(), RoundedCorners(1))//15
        .placeholder(R.drawable.default_place_holder)
        .error(R.drawable.default_place_holder)

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_mood_reason_sheet
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)

        val expressionMessage = MoodReasonSheetFragmentArgs.fromBundle(requireArguments()).expressionMessage

        val value = preferenceHelper!!.getString(Constants.KEY_SELECTED_EXPRESSION)
        expressionList = Gson().fromJson(value, UserExpressionList::class.java)
        expressionList?.let { setMood(it, expressionMessage) }

        binding.btnMoodData.setOnClickListener(ViewClickListener {

            updateUserMoodReason(binding.tvDialogReasonEditText.text.toString())
            navController.navigate(MobileNavigationDirections.actionGlobalNavMoodInfoSheet(expressionMessage, binding.tvDialogReasonEditText.text.toString()))

        })

        viewModel.dismissMoodsReasonDialog.observe(
            viewLifecycleOwner
        ) { isDismiss: String? ->
            if (isDismiss != null && isDismiss == "true") {
//                viewModel.setDismissMoodsDialogConsumed()
                viewModel.setDismissMoodsDialogReasonConsumed()
                dismiss()
            }
        }
    }

    private fun updateUserMoodReason(reason: String) {

        if (reason.trim() != "") {
            viewModel.updateUserMoodReason(reason).observe(
                this
            ) { apiResponse: ApiResponse<StandardResponse> ->
                if (apiResponse.loading) {
                    //showProgress();
                }
                else if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        //hideProgress()
                    }
                    else {
                        //hideProgress()
                    }
                }
                else {
                    hideProgress()
                }
            }
        }
    }

    private fun setMood(userExpressionList: UserExpressionList, expressionMessage: String) {

        binding.tvDialogTitle.text = userExpressionList.expressionText

//        if (!TextUtils.isEmpty(expressionMessage)) {
//            preferenceHelper!!.putValue(Constants.KEY_SELECTED_EXPRESSION_MESSAGE, "")
//
//        }

        Glide.with(requireActivity())
            .load(userExpressionList.moodImage)
            .apply(options)
            .into(binding.ivClose)

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (expressionList != null && expressionList?.id == "165") {
            viewModel.setSelectedExpressionNeedCounselling(expressionList)
        } else {
            viewModel.setSelectedExpressionNeedCounselling(null)
        }
    }


}