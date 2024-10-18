package com.onourem.android.activity.ui.survey.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentStatisticSurveyBinding
import com.onourem.android.activity.models.AnonymousSurveyResponse
import com.onourem.android.activity.models.StatisticSurveyRequest
import com.onourem.android.activity.models.StatisticSurveyResponse
import com.onourem.android.activity.models.SurveyCOList
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.survey.adapters.StaticsQuestionsOptionAdapter
import com.onourem.android.activity.ui.survey.viewmodel.SurveyViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.MyXAxisRenderer
import com.onourem.android.activity.ui.utils.PercentValueFormatter
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import java.util.*
import javax.inject.Inject

class StatisticsSurveyFragment :
    AbstractBaseViewModelBindingFragment<SurveyViewModel, FragmentStatisticSurveyBinding>() {
    private val surveyState = ArrayList<Float>()
    private val ageState = ArrayList<FloatArray>()
    private val genderState = ArrayList<FloatArray>()

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    var report = arrayOf(
        "A", "B", "C", "D", "E", "F", "G",
        "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
        "S", "T", "U", "V", "W", "X", "Y", "Z"
    )
    private var totalSurveyVoters = 0f
    private var dialog: Dialog? = null
    override fun viewModelType(): Class<SurveyViewModel> {
        return SurveyViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_statistic_survey
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.statisticSurveyBarChart.setScaleEnabled(false)
        binding.statisticSurveyBarChart.setPinchZoom(false)
        binding.statisticSurveyBarChart.extraBottomOffset = 10f
        binding.statisticSurveyBarChart.setXAxisRenderer(
            MyXAxisRenderer(
                requireActivity(),
                binding.statisticSurveyBarChart.viewPortHandler,
                binding.statisticSurveyBarChart.xAxis,
                binding.statisticSurveyBarChart.getTransformer(YAxis.AxisDependency.RIGHT)
            )
        )
        val xAxis = binding.statisticSurveyBarChart.xAxis
        xAxis.typeface = ResourcesCompat.getFont(requireActivity(), R.font.montserrat_bold)
        xAxis.textSize = 15f
        //        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels.subList(0, barDataSet.getValues().size())));
        xAxis.yOffset = 20f
        xAxis.xOffset = 20f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLinesBehindData(true)
        xAxis.granularity = 1f
        val yAxisRight = binding.statisticSurveyBarChart.axisRight
        yAxisRight.setDrawGridLines(false)
        yAxisRight.axisMinimum = 0f
        yAxisRight.setDrawLabels(false)
        yAxisRight.setDrawGridLines(true)
        yAxisRight.setDrawAxisLine(false)
        val yAxisLeft = binding.statisticSurveyBarChart.axisLeft
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.setDrawLabels(false)
        yAxisLeft.setDrawGridLines(false)
        yAxisLeft.setDrawAxisLine(false)
        val l = binding.statisticSurveyBarChart.legend
        l.isEnabled = false
        binding.statisticSurveyBarChart.notifyDataSetChanged()
        binding.statisticSurveyBarChart.invalidate()
        val survey = StatisticsSurveyFragmentArgs.fromBundle(
            requireArguments()
        ).survey
        if (survey.surveyOption == null || survey.surveyOption.isEmpty()) {
            loadSurveyOptions(survey.id!!)
        } else {
            loadStatistics(survey)
        }
    }

    private fun loadSurveyOptions(id: String) {
        viewModel.getSurveyData(id)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<AnonymousSurveyResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        loadStatistics(apiResponse.body.surveyCOList[0])
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(getString(R.string.label_network_error), apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "getSurveyData")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getSurveyData",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun loadStatistics(survey: SurveyCOList) {
        val options = survey.surveyOption as ArrayList<String>
        val singleAdapter = StaticsQuestionsOptionAdapter(options)
        val layoutManager = LinearLayoutManager(requireActivity())
        layoutManager.orientation = RecyclerView.VERTICAL
        binding.rvSurvey.isNestedScrollingEnabled = false
        binding.rvSurvey.layoutManager = layoutManager
        binding.rvSurvey.adapter = singleAdapter
        binding.tvSurveyQuestion.text = survey.surveyText
        binding.btnOverall.setOnClickListener(ViewClickListener { v: View? -> setOverAllChartData() })
        binding.btnAge.setOnClickListener(ViewClickListener { v: View? ->
            setAgeChartData()
            openLegendTrayPopupForAge()
        })
        binding.btnGender.setOnClickListener(ViewClickListener { v: View? ->
            setGenderChartData()
            openLegendTrayPopup()
        })
        binding.btnShowAllServey.setOnClickListener(ViewClickListener { v: View? ->
            navController.navigate(
                StatisticsSurveyFragmentDirections.actionStatisticsSurveyFragmentToAllSurveysFragment()
            )
        })
        val ids = StringBuilder()
        for (surveyOption in survey.surveyOptionIds) {
            ids.append(surveyOption)
            ids.append(",")
        }
        val statisticSurveyRequest = StatisticSurveyRequest()
        statisticSurveyRequest.surveyId = Integer.valueOf(survey.id)
        statisticSurveyRequest.screenId = "10"
        statisticSurveyRequest.optionId = ids.toString()
        //        statisticSurveyRequest.setOptionId("12,11");
        statisticSurveyRequest.serviceName = "getSurveyGraphData"
        viewModel.getSurveyGraphData(statisticSurveyRequest)
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<StatisticSurveyResponse> ->
                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    hideProgress()
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        var graphDataList = ArrayList<String>()
                        graphDataList.clear()
                        graphDataList = apiResponse.body.graphDataList as ArrayList<String>
                        surveyState.clear()
                        ageState.clear()
                        genderState.clear()
                        for (graphData in graphDataList) {
                            val dataArray = graphData.split(",").toTypedArray()
                            surveyState.add(dataArray[0].toFloat())
                            val genderDataArray = floatArrayOf(
                                dataArray[1].toFloat(),
                                dataArray[2].toFloat(),
                                dataArray[3].toFloat(),
                                dataArray[4].toFloat()
                            )
                            val ageDataArray = floatArrayOf(
                                dataArray[5].toFloat(),
                                dataArray[6].toFloat(),
                                dataArray[7].toFloat(),
                                dataArray[8].toFloat(),
                                dataArray[9].toFloat()
                            )
                            ageState.add(ageDataArray)
                            genderState.add(genderDataArray)
                        }
                        //                    totalSurveyVoters = apiResponse.body.getTotalUserAnswerCount();
                        setOverAllChartData()
                    } else {
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else {
                    hideProgress()
                    showAlert(apiResponse.errorMessage)
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "getSurveyGraphData")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "getSurveyGraphData",
                            apiResponse.code.toString()
                        )
                    }
                }
            }
    }

    private fun configureChart(barDataSet: BarDataSet, animateBar: Boolean) {
        val labels: List<String> = Arrays.asList(*report).subList(0, barDataSet.values.size)
        val xAxis = binding.statisticSurveyBarChart.xAxis
        //        xAxis.setTypeface(ResourcesCompat.getFont(requireActivity(), R.font.montserrat_bold));
//        xAxis.setTextSize(15);
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barDataSet.isHighlightEnabled = false
        val barData = BarData(barDataSet)
        barData.setValueTextSize(11f)
        barData.barWidth = 0.5f
        val typeface = ResourcesCompat.getFont(requireActivity(), R.font.montserrat_bold)
        barData.setValueTypeface(typeface)
        binding.statisticSurveyBarChart.data = barData
        if (animateBar) binding.statisticSurveyBarChart.animateY(1000, Easing.EaseInOutQuart)
        //        binding.statisticSurveyBarChart.setFitBars(true);
        binding.statisticSurveyBarChart.description.isEnabled = false
        binding.statisticSurveyBarChart.invalidate()
    }

    private fun setOverAllChartData() {
        totalSurveyVoters = 0f
        val values = ArrayList<BarEntry>()
        for (j in surveyState.indices) {
            values.add(BarEntry(j.toFloat(), surveyState[j]))
            totalSurveyVoters += surveyState[j]
        }
        binding.statisticSurveyBarChart.setMaxVisibleValueCount(totalSurveyVoters.toInt())
        val barDataSet = BarDataSet(values, "")
        val overAllColors: MutableList<Int> = ArrayList()
        overAllColors.add(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
        overAllColors.add(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
        barDataSet.colors = overAllColors
        barDataSet.valueFormatter = PercentValueFormatter(totalSurveyVoters)
        configureChart(barDataSet, true)
    }

    private fun setAgeChartData() {
        val values = ArrayList<BarEntry>()
        for (j in ageState.indices) {
            values.add(BarEntry(j.toFloat(), ageState[j]))
        }
        val barDataSet = BarDataSet(values, "")
        barDataSet.setDrawIcons(false)
        barDataSet.setDrawValues(false)
        val ageColors: MutableList<Int> = ArrayList()
        ageColors.add(ContextCompat.getColor(requireActivity(), R.color.color_1))
        ageColors.add(ContextCompat.getColor(requireActivity(), R.color.color_2))
        ageColors.add(ContextCompat.getColor(requireActivity(), R.color.color_3))
        ageColors.add(ContextCompat.getColor(requireActivity(), R.color.color_4))
        ageColors.add(ContextCompat.getColor(requireActivity(), R.color.color_5))
        barDataSet.colors = ageColors
        configureChart(barDataSet, false)
    }

    private fun setGenderChartData() {
        val values = ArrayList<BarEntry>()
        for (j in genderState.indices) {
            values.add(BarEntry(j.toFloat(), genderState[j]))
        }
        val barDataSet = BarDataSet(values, "")
        val genderColors: MutableList<Int> = ArrayList()
        genderColors.add(ContextCompat.getColor(requireActivity(), R.color.color_1))
        genderColors.add(ContextCompat.getColor(requireActivity(), R.color.color_2))
        genderColors.add(ContextCompat.getColor(requireActivity(), R.color.color_3))
        genderColors.add(ContextCompat.getColor(requireActivity(), R.color.color_4))
        barDataSet.colors = genderColors
        barDataSet.setDrawIcons(false)
        barDataSet.setDrawValues(false)
        configureChart(barDataSet, false)
    }

    private fun openLegendTrayPopup() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
            dialog = null
        }
        dialog = Dialog(requireActivity(), R.style.SurveyLegendDialog)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.setContentView(R.layout.popup_legend_tray)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
        val layoutMain = dialog!!.findViewById<ConstraintLayout>(R.id.layoutMain)
        layoutMain.setOnClickListener(ViewClickListener { v: View? -> dialog!!.dismiss() })
        dialog!!.show()
    }

    private fun openLegendTrayPopupForAge() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
            dialog = null
        }
        dialog = Dialog(requireActivity(), R.style.SurveyLegendDialog)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.setContentView(R.layout.popup_legend_tray_age)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
        val layoutMain = dialog!!.findViewById<ConstraintLayout>(R.id.layoutMain)
        layoutMain.setOnClickListener(ViewClickListener { v: View? -> dialog!!.dismiss() })
        dialog!!.show()
    }
}