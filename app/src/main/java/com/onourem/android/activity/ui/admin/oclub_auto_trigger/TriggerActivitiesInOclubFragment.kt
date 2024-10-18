package com.onourem.android.activity.ui.admin.oclub_auto_trigger

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseBottomSheetBindingDialogFragment
import com.onourem.android.activity.databinding.FragmentTriggerActivitesInOclubBinding
import com.onourem.android.activity.models.GetPlayGroupCategories
import com.onourem.android.activity.models.PlayGroupCategoryList
import com.onourem.android.activity.models.UpdateOclubCategoryInfo
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.admin.oclub_auto_trigger.adapters.TriggerActivitiesAdapter
import com.onourem.android.activity.ui.admin.viewmodel.AdminViewModel
import com.onourem.android.activity.ui.games.viewmodel.QuestionGamesViewModel
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import javax.inject.Inject

class TriggerActivitiesInOclubFragment :
    AbstractBaseBottomSheetBindingDialogFragment<AdminViewModel, FragmentTriggerActivitesInOclubBinding>() {

    private var hasError: Boolean = false
    private lateinit var items: java.util.ArrayList<UpdateOclubCategoryInfo>
    private val playGroupCategoryList = ArrayList<PlayGroupCategoryList>()
    private var selectedCategory = ""
    private var selectedCategoryId = 0

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null

    private lateinit var questionGamesViewModel: QuestionGamesViewModel
    private var categoriesResponse: GetPlayGroupCategories? = null

    override fun viewModelType(): Class<AdminViewModel> {
        return AdminViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_trigger_activites_in_oclub
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        questionGamesViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[QuestionGamesViewModel::class.java]
    }


    private fun getCategories() {

        questionGamesViewModel.getPlayGroupCategory().observe(viewLifecycleOwner) { standardResponseApiResponse ->
            if (standardResponseApiResponse.loading) {
                showProgress()
            } else if (standardResponseApiResponse.isSuccess && standardResponseApiResponse.body != null) {
                hideProgress()
                val playGroupCategories: GetPlayGroupCategories = standardResponseApiResponse.body
                if (playGroupCategories.errorCode.equals("000")) {
                    categoriesResponse = standardResponseApiResponse.body
                    playGroupCategoryList.addAll(categoriesResponse?.playGroupCategoryList!!)
                    items = ArrayList()

                    playGroupCategoryList.forEach {
                        val info = UpdateOclubCategoryInfo(it.categoryName, "", "", "", "")
                        items.add(info)
                    }

                    val adapter = TriggerActivitiesAdapter(items)
                    binding.rvCategories.adapter = adapter

                } else {
                    showAlert(playGroupCategories.errorMessage)
                }
            } else {
                hideProgress()
                showAlert(standardResponseApiResponse.errorMessage)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = TriggerActivitiesInOclubFragmentArgs.fromBundle(requireArguments())

        dialog.setCanceledOnTouchOutside(true)

        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)

        binding.tvDialogTitle.text = data.activityText

        binding.tvDialogSubTitle.text = "Activity Id :- ${data.activityId} | Activity Type :- ${data.activityType}"

        binding.rvCategories.layoutManager = LinearLayoutManager(fragmentContext)

        getCategories()

        binding.cvClose.setOnClickListener(ViewClickListener {
            navController.popBackStack()
        })



        binding.btnDialogOk.setOnClickListener(ViewClickListener { v: View? ->
            hasError = false
            val listOfCategories = ArrayList<UpdateOclubCategoryInfo>()

            val has = items.filter { it.hasDayNumberError == "dayNumber" || it.hasDayPriorityError == "dayPriority"}

            if (has.isEmpty()) {
                items.forEach { item ->
                    if (item.dayNumber.isNotEmpty() && item.dayPriority.isNotEmpty()) {
                        listOfCategories.add(item)
                        Log.d("Trigger", "Category: ${item.name}, DayNumber: ${item.dayNumber}, DayPriority: ${item.dayPriority}")
                    }
                }

                val oclubCategoryId = listOfCategories.joinToString(separator = ",") { it.name }
                val dayNumber = listOfCategories.joinToString(separator = ",") { it.dayNumber }
                val dayPriority = listOfCategories.joinToString(separator = ",") { it.dayPriority }
                Log.d("Trigger", "Category: $oclubCategoryId, DayNumber: $dayNumber, DayPriority: $dayPriority")
                submitOclubAutoTrigger(oclubCategoryId, dayNumber, dayPriority, data.activityId, data.activityType)

            } else {
                val errorInCategory = has.joinToString(separator = ", ") { it.name }
                showAlert("$errorInCategory has an error")
            }

        })
    }

    private fun submitOclubAutoTrigger(
        oclubCategoryId: String,
        dayNumber: String,
        dayPriority: String,
        activityId: String,
        activityType: String
    ) {

        viewModel.addOclubAutoTriggerDailyActivityByAdmin(oclubCategoryId, dayNumber, dayPriority, activityId, activityType)
            .observe(viewLifecycleOwner) { apiResponse ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()

                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                        if (apiResponse.body.message!!.isNotEmpty()) {
                            showAlert(apiResponse.body.message)
                        }
                        dialog.dismiss()
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