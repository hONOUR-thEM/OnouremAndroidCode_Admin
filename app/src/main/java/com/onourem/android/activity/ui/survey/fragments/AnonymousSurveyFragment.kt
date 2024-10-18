package com.onourem.android.activity.ui.survey.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentAnonymousSurveyBinding
import com.onourem.android.activity.models.*
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.survey.adapters.MultiChoiceAdapter
import com.onourem.android.activity.ui.survey.adapters.SingleChoiceAdapter
import com.onourem.android.activity.ui.survey.viewmodel.SurveyViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject

class AnonymousSurveyFragment : AbstractBaseViewModelBindingFragment<SurveyViewModel, FragmentAnonymousSurveyBinding>() {
    private var dashboardViewModel: DashboardViewModel? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var singleChoiceAdapter: SingleChoiceAdapter? = null
    private var multiChoiceAdapter: MultiChoiceAdapter? = null
    private var surveyOptionList: ArrayList<SurveyOption>? = null
    private var survey: SurveyCOList? = null
    private var surveyIdFromQuestionListing: String? = null

    //    private var surveyFrom: String? = null
    private var startTime: Long = 0

    //    private var showStats: String? = ""
    override fun layoutResource(): Int {
        return R.layout.fragment_anonymous_survey
    }

    override fun viewModelType(): Class<SurveyViewModel> {
        return SurveyViewModel::class.java
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dashboardViewModel = ViewModelProvider(
            requireActivity(), viewModelFactory
        )[DashboardViewModel::class.java]

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startTime = System.currentTimeMillis()

        surveyIdFromQuestionListing = AnonymousSurveyFragmentArgs.fromBundle(
            requireArguments()
        ).surveyId

//        surveyFrom = AnonymousSurveyFragmentArgs.fromBundle(
//            requireArguments()
//        ).surveyFrom

        /*showStats = AnonymousSurveyFragmentArgs.fromBundle(
            requireArguments()
        ).showStats*/

        viewModel.getSurveyData(surveyIdFromQuestionListing!!)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<AnonymousSurveyResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                        survey = apiResponse.body.surveyCOList[0]

                        survey!!.surveyIdFromQuestionListing = surveyIdFromQuestionListing
                        //                    preferenceHelper.putValue(KEY_SELECTED_SURVEY, new Gson().toJson(apiResponse.body.getSurveyCOList().get(0)));
                        binding.tvSurveyQuestion.text = apiResponse.body.surveyCOList[0].surveyText
                        val options = apiResponse.body.surveyCOList[0].surveyOption
                        val optionIds = apiResponse.body.surveyCOList[0].surveyOptionIds
                        val min = options.size.coerceAtMost(optionIds.size)
                        surveyOptionList = ArrayList(min)
                        for (i in 0 until min) {
                            surveyOptionList!!.add(SurveyOption(optionIds[i], options[i]))
                        }

                        when {
                            survey?.showStats == "N" && survey?.userAnserForSurvey == "N" -> {
                                inIt(
                                    surveyIdFromQuestionListing!!, apiResponse.body.surveyCOList[0].surveytype.equals(
                                        "SingleOption", ignoreCase = true
                                    )
                                )
                            }

                            survey?.showStats == "N" && survey?.userAnserForSurvey == "Y" -> {
                                inIt(
                                    surveyIdFromQuestionListing!!, apiResponse.body.surveyCOList[0].surveytype.equals(
                                        "SingleOption", ignoreCase = true
                                    )
                                )
                                showAlert(
                                    "Thank You",
                                    "You have submitted response to this survey. Statistics for this survey is not available.",
                                    "Go Back"
                                ) {
                                    navController.popBackStack()
                                }
                            }

                            survey?.showStats == "Y" && survey?.userAnserForSurvey == "N" -> {
                                inIt(
                                    surveyIdFromQuestionListing!!, apiResponse.body.surveyCOList[0].surveytype.equals(
                                        "SingleOption", ignoreCase = true
                                    )
                                )
                            }

                            survey?.showStats == "Y" && survey?.userAnserForSurvey == "Y" -> {
                                val navBuilder = NavOptions.Builder()
                                val navOptions = navBuilder.setPopUpTo(R.id.nav_anonymous_survey, true).build()

                                navController.navigate(
                                    AnonymousSurveyFragmentDirections.actionAnonymousSurveyFragmentToStatisticsSurveyFragment(
                                        survey!!
                                    ), navOptions
                                )
                            }
                        }

                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1)) || apiResponse.errorMessage.contains(
                            getString(R.string.unable_to_connect_host_message3)
                        ))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "getSurveyData")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getSurveyData", apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun inIt(surveyId: String, isSingleChoice: Boolean) {
        binding.rvSurvey.layoutManager = LinearLayoutManager(requireActivity())
        if (isSingleChoice) {
            singleChoiceAdapter = SingleChoiceAdapter(requireActivity(), surveyOptionList!!)
            binding.rvSurvey.adapter = singleChoiceAdapter
        } else {
            multiChoiceAdapter = MultiChoiceAdapter(requireActivity(), surveyOptionList!!)
            binding.rvSurvey.adapter = multiChoiceAdapter
        }
        binding.btnSurveySubmit.visibility = View.VISIBLE
        binding.btnSurveySubmit.setOnClickListener(ViewClickListener {

            val endTime = System.currentTimeMillis()
            val timeSpentOnScreen = (endTime - startTime) / 1000.0
            AppUtilities.showLog("timeSpentOnScreen", "timeSpentOnScreen : $timeSpentOnScreen seconds")


            var isOptionSelected = false
            isOptionSelected = if (isSingleChoice) {
                singleChoiceAdapter!!.selected != null
            } else {
                multiChoiceAdapter!!.selected.isNotEmpty()
            }
            if (!isOptionSelected) {
                val response = preferenceHelper!!.getString(Constants.KEY_EXPRESSIONS_RESPONSE)
                val expressionDataResponseApiResponse = Gson().fromJson(response, ExpressionDataResponse::class.java)
                val survey = expressionDataResponseApiResponse.appColorCO?.commonDataMap!!.survey
                showAlert(survey!!.titleForNoSelectionAlert, survey.textForNoSelectionAlert)
                return@ViewClickListener
            }
            val anonymousSurveyUpdateRequest = AnonymousSurveyUpdateRequest()
            val selectedIds = StringBuilder()
            val ids = StringBuilder()
            if (isSingleChoice) {
                for (surveyOption in surveyOptionList!!) {
                    ids.append(surveyOption.id)
                    ids.append(",")
                }
                anonymousSurveyUpdateRequest.optionId = singleChoiceAdapter!!.selected!!.id + ","
                anonymousSurveyUpdateRequest.otherText = singleChoiceAdapter!!.selected!!.name
                //                preferenceHelper.putValue(KEY_OPTION_IDS, String.valueOf(ids));
//                preferenceHelper.putValue(KEY_SELECTED_OPTION_ID, singleChoiceAdapter.getSelected().getId());
            } else {
                for (surveyOption in multiChoiceAdapter!!.all) {
                    if (surveyOption.isSelected) {
                        selectedIds.append(surveyOption.id)
                        selectedIds.append(",")
                    }
                    ids.append(surveyOption.id)
                    ids.append(",")
                }
                anonymousSurveyUpdateRequest.optionId = selectedIds.toString()
                //                preferenceHelper.putValue(KEY_SELECTED_OPTION_ID, selectedIds.toString());
            }
            anonymousSurveyUpdateRequest.surveyId = surveyId
            anonymousSurveyUpdateRequest.screenId = "9"
            anonymousSurveyUpdateRequest.serviceName = "updateSurveyResult"
            anonymousSurveyUpdateRequest.timeSpentOnScreen = timeSpentOnScreen.toString()

            viewModel.getSurveyUpdate(anonymousSurveyUpdateRequest)
                .observe(viewLifecycleOwner) { apiResponse: ApiResponse<AnonymousSurveyUpdateResponse> ->
                    if (apiResponse.loading) {
                        showProgress()
                    } else if (apiResponse.isSuccess && apiResponse.body != null) {
                        hideProgress()
                        if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
//                        SurveyCOList item = new Gson().fromJson(preferenceHelper.getString(KEY_SELECTED_SURVEY), SurveyCOList.class);

                            when (survey?.showStats) {
                                "N" -> {
                                    showAlert(
                                        "Thank You",
                                        "You have submitted response to this survey. Statistics for this survey is not available.",
                                        "Go Back"
                                    ) {
                                        navController.popBackStack()
                                    }
                                }

                                else -> {
                                    val selectedSurvey = preferenceHelper!!.getString(Constants.KEY_SELECTED_SURVEY)
                                    if (!TextUtils.isEmpty(selectedSurvey)) {
                                        val selectedSurveyCOList = Gson().fromJson(selectedSurvey, SurveyCOList::class.java)
                                        if (selectedSurveyCOList != null && selectedSurveyCOList.id.equals(
                                                survey!!.id, ignoreCase = true
                                            )
                                        ) {
                                            selectedSurveyCOList.userAnserForSurvey = "Y"
                                            preferenceHelper!!.putValue(
                                                Constants.KEY_SELECTED_SURVEY, Gson().toJson(selectedSurveyCOList)
                                            )
                                        }
                                    }

                                    val navBuilder = NavOptions.Builder()
                                    val navOptions = navBuilder.setPopUpTo(R.id.nav_anonymous_survey, true).build()

                                    navController.navigate(
                                        AnonymousSurveyFragmentDirections.actionAnonymousSurveyFragmentToStatisticsSurveyFragment(
                                            survey!!
                                        ), navOptions
                                    )

                                }
                            }

                        } else {
                            showAlert(apiResponse.body.errorMessage)
                        }
                    } else {
                        hideProgress()
                        showAlert(apiResponse.errorMessage)
                        if (apiResponse.errorMessage != null && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1)) || apiResponse.errorMessage.contains(
                                getString(R.string.unable_to_connect_host_message3)
                            ))
                        ) {
                            if (BuildConfig.DEBUG) {
                                AppUtilities.showLog("Network Error", "getSurveyUpdate")
                            }
                            (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                                "getSurveyUpdate", apiResponse.code.toString()
                            )
                        }
                    }
                }
        })
    }
}