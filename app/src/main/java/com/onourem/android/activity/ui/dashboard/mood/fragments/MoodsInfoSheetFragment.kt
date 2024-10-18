package com.onourem.android.activity.ui.dashboard

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.onourem.android.activity.MobileNavigationDirections
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentMoodInfoSheetBinding
import com.onourem.android.activity.models.GetUserMoodResponseMsgResponse
import com.onourem.android.activity.models.UserExpressionList
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.audio.adapters.AudioCategoryAdapter
import com.onourem.android.activity.ui.audio.models.GetAudioCategoryResponse
import com.onourem.android.activity.ui.dashboard.mood.adapters.MoodInfoImagesAdapter
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject


class MoodsInfoSheetFragment :
    AbstractBaseBottomSheetBindingDialogFragment<DashboardViewModel, FragmentMoodInfoSheetBinding>() {


    private var reasonText: String = ""
    private lateinit var reason: String
    private lateinit var expressionMessage: String
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var response: GetAudioCategoryResponse
    private lateinit var audioCategoryAdapter: AudioCategoryAdapter
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private var mTask: Runnable = Runnable {
        (fragmentContext as DashboardActivity).updateUserMoodReason(reasonText)
    }

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
        return R.layout.fragment_mood_info_sheet
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (dialog != null) dialog!!.setCanceledOnTouchOutside(false)
        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)

        getUserMoodReasonImages()

//        viewModel.updateMoodResponseMsg.observe(viewLifecycleOwner) {
//            if (it) {
//                getUserMoodReasonImages()
//            }
//        }

        expressionMessage =
            MoodsInfoSheetFragmentArgs.fromBundle(requireArguments()).expressionMessage
        reason =
            MoodsInfoSheetFragmentArgs.fromBundle(requireArguments()).reason

        val value = preferenceHelper!!.getString(Constants.KEY_SELECTED_EXPRESSION)
        val expression = Gson().fromJson(value, UserExpressionList::class.java)
        expression?.let { setMood(it, expressionMessage, reason) }

        val moodResponseMsgResponse = Gson().fromJson(
            preferenceHelper!!.getString(Constants.CHANGED_MOOD),
            GetUserMoodResponseMsgResponse::class.java
        )


        if (moodResponseMsgResponse != null) {
            binding.tvDialogStatisticsText.text = if (expression.positivity.toInt() > 0) {
                moodResponseMsgResponse.positiveResponseMsg
            } else {
                moodResponseMsgResponse.negativeResponseMsg
            }
        }

        if (expression.expressionResponseMsg != null) {
            binding.tvDialogPerspectiveText.text = expression.expressionResponseMsg
        }

        binding.btnMoodData.setOnClickListener(ViewClickListener {

            if (navController.currentDestination?.id == R.id.nav_mood_info_sheet) {
                navController.navigate(MobileNavigationDirections.actionGlobalNavMoodHistory())
            }

        })

        binding.btnNext.setOnClickListener(ViewClickListener {
            (fragmentContext as DashboardActivity).updateUserMoodReason(binding.tvDialogReasonEditText.text.toString())
            binding.btnNext.visibility = View.GONE
            binding.clRemaining.visibility = View.VISIBLE
        })

        binding.cvSubmit.setOnClickListener(ViewClickListener {
            dismiss()
        })

        binding.tvDialogReasonEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString()
                        .trim { it <= ' ' }.length >= 3
                ) {
                    reasonText = s.toString()
                    mHandler.removeCallbacks(mTask)
                    mHandler.postDelayed(mTask, 1500)
                }
            }
        })
    }

    private fun getUserMoodReasonImages() {

        val response = Gson().fromJson(
            preferenceHelper!!.getString(Constants.CHANGED_MOOD),
            GetUserMoodResponseMsgResponse::class.java
        )

        if (response != null) {
            val layoutManager =
                LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
            binding.rvItem.layoutManager = layoutManager
            val adapter = MoodInfoImagesAdapter(response.userMoodReasonImageList)
            binding.rvItem.adapter = adapter
        } else {
            viewModel.userMoodResponseMsg()
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<GetUserMoodResponseMsgResponse> ->
                    if (apiResponse.isSuccess && apiResponse.body != null) {
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                            preferenceHelper!!.putValue(
                                Constants.CHANGED_MOOD,
                                Gson().toJson(apiResponse.body)
                            )
                            val layoutManager =
                                LinearLayoutManager(
                                    requireActivity(),
                                    RecyclerView.HORIZONTAL,
                                    false
                                )
                            binding.rvItem.layoutManager = layoutManager
                            val adapter =
                                MoodInfoImagesAdapter(apiResponse.body.userMoodReasonImageList)
                            binding.rvItem.adapter = adapter
                        } else {
                            showAlert(apiResponse.body.errorMessage)
                        }
                    }
                }
        }

    }

    private fun setMood(
        userExpressionList: UserExpressionList,
        expressionMessage: String,
        reason: String
    ) {

        binding.tvDialogTitle.text = userExpressionList.expressionText
        binding.tvDialogReasonEditText.setText(reason)
        binding.tvDialogPerspectiveText.text = expressionMessage
        binding.tvDialogStatisticsText.text = expressionMessage

        Glide.with(requireActivity())
            .load(userExpressionList.moodImage)
            .apply(options)
            .into(binding.ivClose)

    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mHandler.removeCallbacks(mTask)
        (fragmentContext as DashboardActivity).loadAfterSetMood()
    }


}