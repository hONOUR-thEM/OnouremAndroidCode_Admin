package com.onourem.android.activity.ui.admin.details

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.databinding.FragmentInactiveActivityBinding
import com.onourem.android.activity.models.GetPlayGroupCategories
import com.onourem.android.activity.models.PlayGroupCategoryList
import com.onourem.android.activity.models.UpdateOclubCategoryInfo
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class InactiveActivityFragment :
    AbstractBaseBottomSheetBindingDialogFragment<AdminViewModel, FragmentInactiveActivityBinding>() {

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    private lateinit var questionGamesViewModel: QuestionGamesViewModel
    private var categoriesResponse: GetPlayGroupCategories? = null

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_inactive_activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionGamesViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[QuestionGamesViewModel::class.java]
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = InactiveActivityFragmentArgs.fromBundle(requireArguments())

        dialog.setCanceledOnTouchOutside(true)

        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)

        binding.tvDialogTitle.text = data.activityText

        binding.tvDialogSubTitle.text = "Activity Id :- ${data.activityId} | Activity Type :- ${data.activityType}"

        binding.cvClose.setOnClickListener(ViewClickListener {
            navController.popBackStack()
        })

        binding.btnDialogOk.setOnClickListener(ViewClickListener {
            submit(data.activityId, data.activityType)
        })
    }

    private fun submit(
        activityId: String,
        activityType: String
    ) {

        viewModel.updateActivityStatusByAdmin(activityId, activityType)
            .observe(viewLifecycleOwner) { apiResponse ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()

                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                        showAlert("Activity $activityId of type $activityType has made inactive"){
                            dialog.dismiss()
                        }

                    } else {
                        showAlert(apiResponse.body.errorMessage.toString())
                    }

                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                }
            }
    }


}