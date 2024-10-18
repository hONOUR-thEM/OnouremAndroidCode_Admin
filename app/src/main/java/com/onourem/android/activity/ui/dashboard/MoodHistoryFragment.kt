package com.onourem.android.activity.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.util.Pair
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.gson.Gson
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentMoodMappingLineBinding
import com.onourem.android.activity.models.GetUserHistoryResponse
import com.onourem.android.activity.models.LoginResponse
import com.onourem.android.activity.models.UserExpressionList
import com.onourem.android.activity.models.UserMoodHistory
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.audio.fragments.executeAsyncTask
import com.onourem.android.activity.ui.audio.fragments.runOnUiThread
import com.onourem.android.activity.ui.dashboard.mood.adapters.MoodLineAdapter
import com.onourem.android.activity.ui.utils.*
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class MoodHistoryFragment :
    AbstractBaseViewModelBindingFragment<DashboardViewModel, FragmentMoodMappingLineBinding>(),
    OnChartValueSelectedListener {

    private lateinit var dataList: MutableList<UserMoodHistory>
    private var index: Int = 0
    private var formatter: SimpleDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private var simpleDateFormat: SimpleDateFormat =
        SimpleDateFormat(Utilities.APP_DISPLAY_DATE_TIME_FORMAT, Locale.getDefault())
    private var lineEntries: ArrayList<Entry>? = null
    private var lineEntries2: ArrayList<Entry>? = null
    private var lineEntryModelArrayList: ArrayList<UserMoodHistory>? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var lineAdapter: MoodLineAdapter? = null

    private var localMoods: ArrayList<UserExpressionList>? = null
    private var localMoodsMap: HashMap<String, UserExpressionList>? = null

    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_mood_mapping_line
    }

    private var totalSurveyVoters = 0f
    var report = arrayOf(
        "Positive", "Negative", "C", "D", "E", "F", "G",
        "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
        "S", "T", "U", "V", "W", "X", "Y", "Z"
    )

    lateinit var lineXAxisValues: ArrayList<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        localMoodsMap = LocalMoods.getAllMoodsMap()
        dataList = ArrayList()
        getUserMoodHistory()

        binding.ivInfoBar.setOnClickListener(ViewClickListener { v: View? ->
            showAlert(
                "This shows in percentage how many times your mood has been positive and negative. Do not worry about if it is more positive or negative. Just record your mood daily, note down the reason to be able understand yourself better."
            )
        })

        binding.ivInfoLine.setOnClickListener(ViewClickListener { v: View? ->
            showAlert(
                "This chart shows how positive your mood has been on different days. Keep recording your mood daily to be able to analyze the patterns after a few days."
            )
        })

        binding.rvLine.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvLine.addItemDecoration(VerticalSpaceItemDecoration(5))


        lineAdapter =
            MoodLineAdapter(
                dataList, localMoodsMap
            ) { item: Pair<Int?, UserMoodHistory?>? -> }
        binding.rvLine.adapter = lineAdapter

    }

    private fun setBarChart(userMoodHistoryList: List<UserMoodHistory>) {
        binding.moodBarChart.setScaleEnabled(false)
        binding.moodBarChart.setPinchZoom(false)
        binding.moodBarChart.extraBottomOffset = 10f
        //        binding.moodBarChart.setXAxisRenderer(new MyXAxisRenderer(requireActivity(), binding.moodBarChart.getViewPortHandler(), binding.moodBarChart.getXAxis(), binding.moodBarChart.getTransformer(YAxis.AxisDependency.RIGHT)));
        val xAxis = binding.moodBarChart.xAxis
        xAxis.typeface = ResourcesCompat.getFont(requireActivity(), R.font.montserrat_bold)
        xAxis.textSize = 15f
        //        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels.subList(0, barDataSet.getValues().size())));
        xAxis.yOffset = 5f
        xAxis.xOffset = 5f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLinesBehindData(false)
        xAxis.granularity = 1f
        val yAxisRight = binding.moodBarChart.axisRight
        yAxisRight.setDrawGridLines(false)
        yAxisRight.axisMinimum = 0f
        yAxisRight.setDrawLabels(false)
        yAxisRight.setDrawGridLines(false)
        yAxisRight.setDrawAxisLine(false)
        val yAxisLeft = binding.moodBarChart.axisLeft
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.setDrawLabels(false)
        yAxisLeft.setDrawGridLines(false)
        yAxisLeft.setDrawAxisLine(false)
        val l = binding.moodBarChart.legend
        l.isEnabled = false
        binding.moodBarChart.notifyDataSetChanged()
        binding.moodBarChart.invalidate()
        setOverAllChartData(userMoodHistoryList)
    }

    private fun setOverAllChartData(userMoodHistoryList: List<UserMoodHistory>) {
        totalSurveyVoters = userMoodHistoryList.size.toFloat()
        var positiveCount = 0
        var negativeCount = 0
        userMoodHistoryList.forEach {
            if (it.positivity.toInt() > 0) {
                positiveCount++
            } else if (it.positivity.toInt() < 0) {
                negativeCount++
            }
        }
        val values = ArrayList<BarEntry>()
        values.add(BarEntry(0f, positiveCount.toFloat()))
        values.add(BarEntry(1f, negativeCount.toFloat()))
        //        for (int j = 0; j < surveyState.size(); j++) {
//            values.add(new BarEntry(j, surveyState.get(j)));
//            totalSurveyVoters += surveyState.get(j);
//        }
        binding.moodBarChart.setMaxVisibleValueCount(totalSurveyVoters.toInt())
        val barDataSet = BarDataSet(values, "")
        val overAllColors: MutableList<Int> = ArrayList()
        overAllColors.add(ContextCompat.getColor(requireActivity(), R.color.good_green_color))
        overAllColors.add(ContextCompat.getColor(requireActivity(), R.color.good_red_color))
        barDataSet.colors = overAllColors
        barDataSet.valueFormatter = PercentValueFormatter(totalSurveyVoters)
        configureChart(barDataSet, true)
    }

    private fun configureChart(barDataSet: BarDataSet, animateBar: Boolean) {
        val labels: List<String> = listOf(*report).subList(0, barDataSet.values.size)
        val xAxis = binding.moodBarChart.xAxis
        //        xAxis.setTypeface(ResourcesCompat.getFont(requireActivity(), R.font.montserrat_bold));
//        xAxis.setTextSize(15);
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barDataSet.isHighlightEnabled = false
        val barData = BarData(barDataSet)
        barData.setValueTextSize(11f)
        barData.barWidth = 0.5f
        val typeface = ResourcesCompat.getFont(requireActivity(), R.font.montserrat_bold)
        barData.setValueTypeface(typeface)


        runOnUiThread {
            // Stuff that updates the UI
            binding.moodBarChart.data = barData
//        if (animateBar) binding.moodBarChart.animateY(1000, Easing.EaseInOutQuart)
            //        binding.moodBarChart.setFitBars(true);
            binding.moodBarChart.description.isEnabled = false
            binding.moodBarChart.invalidate()
        }
    }

    private fun setLineChart(userMoodHistoryList: List<UserMoodHistory>) {

        val asReversed = userMoodHistoryList.asReversed()

        getEntries(asReversed)

        binding.lineChart.setOnChartValueSelectedListener(this)

        // no description text
        binding.lineChart.description.isEnabled = false

        // enable touch gestures
        binding.lineChart.setTouchEnabled(true)
        binding.lineChart.dragDecelerationFrictionCoef = 0.9f

        // enable scaling and dragging
        binding.lineChart.isDragEnabled = true
        binding.lineChart.setScaleEnabled(true)
        binding.lineChart.setDrawGridBackground(false)
        binding.lineChart.isHighlightPerDragEnabled = true


        // if disabled, scaling can be done on x- and y-axis separately
        binding.lineChart.setPinchZoom(true)

        // set an alternative background color
//
        binding.lineChart.extraBottomOffset = 10f
        //        binding.lineChart.setXAxisRenderer(new MyXAxisRenderer(requireActivity(), binding.lineChart.getViewPortHandler(), binding.lineChart.getXAxis(), binding.lineChart.getTransformer(YAxis.AxisDependency.RIGHT)));


        // get the legend (only possible after setting data)
        val l = binding.lineChart.legend

        // modify the legend ...
        l.form = Legend.LegendForm.LINE
        //        l.setTypeface(tfLight);
        l.textSize = 0.1f
        l.textColor = Color.BLACK
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(true)
        //        l.setYOffset(11f);
        l.isEnabled = false
        binding.lineChart.description.typeface =
            ResourcesCompat.getFont(requireActivity(), R.font.montserrat_regular)
        binding.lineChart.description.isEnabled = false
        binding.lineChart.description.xOffset = 10f
        binding.lineChart.description.yOffset = 10f
        val xAxis = binding.lineChart.xAxis
        //        xAxis.setTypeface(tfLight);
        xAxis.textColor = Color.BLACK
        xAxis.axisMaximum = lineXAxisValues.size.toFloat()
        xAxis.axisMinimum = 0f
        xAxis.typeface = ResourcesCompat.getFont(requireActivity(), R.font.montserrat_regular)
        xAxis.textSize = 12f
        //        xAxis.setYOffset(20);
//        xAxis.setXOffset(0);
        xAxis.axisLineWidth = 2f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawLabels(true)
        xAxis.setDrawGridLinesBehindData(false)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -40f
        xAxis.axisLineColor = Color.BLACK
        xAxis.setCenterAxisLabels(true)
        xAxis.valueFormatter = IndexAxisValueFormatter(lineXAxisValues)


//        xAxis.valueFormatter = LineChartXAxisValueFormatter(3, 2022)
//        binding.lineChart.setXAxisRenderer(
//            LineArrowXAxisRenderer(
//                fragmentContext,
//                binding.lineChart.viewPortHandler,
//                binding.lineChart.xAxis,
//                binding.lineChart.getTransformer(YAxis.AxisDependency.LEFT)
//            )
//        )
//        binding.lineChart.rendererLeftYAxis = LineArrowYAxisRenderer(
//            fragmentContext,
//            binding.lineChart.viewPortHandler,
//            binding.lineChart.axisLeft,
//            binding.lineChart.getTransformer(YAxis.AxisDependency.LEFT)
//        )
        binding.lineChart.setDrawGridBackground(false) // this is a must
        val leftAxis = binding.lineChart.axisLeft
        //        leftAxis.setTypeface(tfLight);
        leftAxis.typeface = ResourcesCompat.getFont(requireActivity(), R.font.montserrat_regular)
        leftAxis.axisMaximum = 6f
        leftAxis.axisMinimum = -6f
        leftAxis.textSize = 12f
        leftAxis.textColor = Color.BLACK
        leftAxis.setDrawZeroLine(false)
        //        leftAxis.setYOffset(0);
//        leftAxis.setXOffset(20);
        leftAxis.isGranularityEnabled = true
        leftAxis.granularity = 1f
        leftAxis.setDrawGridLines(false)
        leftAxis.axisLineWidth = 2f
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawLabels(false)
        leftAxis.axisLineColor = Color.BLACK
        leftAxis.setDrawGridLinesBehindData(false)

//
        val rightAxis = binding.lineChart.axisRight
        //        rightAxis.setTypeface(tfLight);
        rightAxis.textColor = Color.RED
        rightAxis.axisMaximum = 6f
        rightAxis.axisMinimum = -6f
        rightAxis.setDrawZeroLine(false)
        rightAxis.isGranularityEnabled = false
        rightAxis.setDrawGridLines(false)
        rightAxis.setDrawLabels(false)
        rightAxis.setDrawGridLinesBehindData(false)
        rightAxis.setDrawAxisLine(false)
        //
        binding.lineChart.setVisibleXRangeMaximum(10.toFloat())
        //        binding.lineChart.setVisibleYRangeMaximum(12, YAxis.AxisDependency.LEFT);
        binding.lineChart.moveViewToX(0f)
        binding.lineChart.isScaleYEnabled = false

        val set1: LineDataSet
        var set2: LineDataSet
        var set3: LineDataSet
        var set4: LineDataSet
        if (binding.lineChart.data != null &&
            binding.lineChart.data.dataSetCount > 0
        ) {
            set1 = binding.lineChart.data.getDataSetByIndex(0) as LineDataSet
            //            set2 = (LineDataSet) binding.lineChart.getData().getDataSetByIndex(1);
//            set3 = (LineDataSet) binding.lineChart.getData().getDataSetByIndex(2);
//            set4 = (LineDataSet) binding.lineChart.getData().getDataSetByIndex(3);
            set1.values = lineEntries
            //            set2.setValues(scatterEntries2);
//            set3.setValues(scatterEntries3);
//            set4.setValues(scatterEntries4);
            binding.lineChart.data.notifyDataChanged()
            binding.lineChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            lineEntryModelArrayList = ArrayList()
            set1 = LineDataSet(lineEntries, "Positivity")
            set1.axisDependency = YAxis.AxisDependency.LEFT
            set1.color = ContextCompat.getColor(fragmentContext, R.color.color_black)
            set1.setCircleColor(ContextCompat.getColor(fragmentContext, R.color.color_black))
            set1.lineWidth = 1.5f
            set1.circleRadius = 6f
            set1.highLightColor = ContextCompat.getColor(fragmentContext, R.color.color_black)
            set1.setDrawCircleHole(false)
            set1.mode = LineDataSet.Mode.CUBIC_BEZIER
            set1.isHighlightEnabled = false
//            for (entry in lineEntries!!) {
//                lineEntryModelArrayList!!.add(
//                    LineEntryModel(
//                        "https://ddlpcwro2ero9.cloudfront.net/images/appimages/tense3.png",
//                        "Friends who can see how you are feeling and vice versa. Add your top 20 to this list.",
//                        entry.data.toString()
//                    )
//                )
//            }

            val loginResponse =
                Gson().fromJson(
                    preferenceHelper!!.getString(Constants.KEY_USER_OBJECT),
                    LoginResponse::class.java
                )
            if (loginResponse != null) {
                binding.lineChart.description.text = loginResponse.firstName + "'s Mood Flow"
            }

//            set2 = new LineDataSet(scatterEntries2, "Positivity");
//
//            set2.setAxisDependency(YAxis.AxisDependency.LEFT);
//            set2.setColor(ContextCompat.getColor(getFragmentContext(), R.color.color_blue));
//            set2.setCircleColor(ContextCompat.getColor(getFragmentContext(), R.color.color_blue));
//            set2.setLineWidth(1.5f);
//            set2.setCircleRadius(6f);
//            set2.setHighLightColor(ContextCompat.getColor(getFragmentContext(), R.color.color_blue));
//            set2.setDrawCircleHole(false);
//            set2.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
//            set2 = new LineDataSet(scatterEntries2, "Quadrant 2");
//            set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
//            set2.setColor(Color.RED);
//            set2.setCircleColor(Color.WHITE);
//            set2.setLineWidth(2f);
//            set2.setCircleRadius(3f);
//            set2.setFillAlpha(65);
//            set2.setFillColor(Color.RED);
//            set2.setDrawCircleHole(false);
//            set2.setHighLightColor(Color.rgb(244, 117, 117));
//            //set2.setFillFormatter(new MyFillFormatter(900f));
//
//            set3 = new LineDataSet(scatterEntries3, "Quadrant 3");
//            set3.setAxisDependency(YAxis.AxisDependency.RIGHT);
//            set3.setColor(Color.YELLOW);
//            set3.setCircleColor(Color.WHITE);
//            set3.setLineWidth(2f);
//            set3.setCircleRadius(3f);
//            set3.setFillAlpha(65);
//            set3.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
//            set3.setDrawCircleHole(false);
//            set3.setHighLightColor(Color.rgb(244, 117, 117));
//
//            set4 = new LineDataSet(scatterEntries4, "Quadrant 4");
//            set4.setAxisDependency(YAxis.AxisDependency.RIGHT);
//            set4.setColor(Color.YELLOW);
//            set4.setCircleColor(Color.WHITE);
//            set4.setLineWidth(2f);
//            set4.setCircleRadius(3f);
//            set4.setFillAlpha(65);
//            set4.setFillColor(ColorTemplate.colorWithAlpha(Color.YELLOW, 200));
//            set4.setDrawCircleHole(false);
//            set4.setHighLightColor(Color.rgb(244, 117, 117));

            // create a data object with the data sets
            val data = LineData(set1)
            data.setValueTextColor(Color.WHITE)
            data.setValueTextSize(0f)

            // set data
            binding.lineChart.data = data


//            set1.setValueFormatter(new ValueFormatter() {
//
//                @Override
//                public String getPointLabel(Entry entry) {
////                return (String) entry.getData();
//                    return "Mood " + (int) entry.getX() + " May";
//                }
//
//            });

//            hideProgress()


        }
//        binding.lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
//            override fun onValueSelected(e: Entry, h: Highlight) {
////                Toast.makeText(getFragmentContext(), e.getData().toString() + " X=" + e.getX() + " Y=" + e.getY(), Toast.LENGTH_SHORT).show();
//                binding.lineChart.notifyDataSetChanged()
//                binding.lineChart.invalidate()
//                val userMoodHistory = e.data as UserMoodHistory
////                binding.rvLine.smoothScrollToPosition(e.x.toInt())
//
//                lineAdapter!!.setIdToHighlight(e.x.toInt())
//
//                val y: Float =
//                    binding.rvLine.y + binding.rvLine.getChildAt(e.x.toInt()).y
//
//                val x: Float =
//                    binding.rvLine.x + binding.rvLine.getChildAt(e.x.toInt()).x
//
//                binding.parent.smoothScrollTo(x.toInt(), y.toInt())
//                Toast.makeText(
//                    fragmentContext,
//                    userMoodHistory.expressionName + " on " + lineXAxisValues[e.x.toInt()],
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            override fun onNothingSelected() {}
//        })
    }

    fun getRandomNumberUsingNextInt(min: Int, max: Int): Int {
        val random = Random()
        return random.nextInt(max - min) + min
    }


    private fun getEntries(userMoodHistoryList: List<UserMoodHistory>) {
        lineEntries = ArrayList()
        lineXAxisValues = ArrayList()

        userMoodHistoryList.forEachIndexed { index, item ->

            simpleDateFormat.timeZone =
                TimeZone.getTimeZone(Utilities.getServerTimeZone(item.createdOn))
            val serverDate = simpleDateFormat.parse(item.createdOn)
            lineXAxisValues.add(formatter.format(serverDate!!))

            lineEntries!!.add(
                BarEntry(index.toFloat(), item.energy.toFloat(), item)
            )
        }
    }

    override fun onValueSelected(e: Entry, h: Highlight) {}

    override fun onNothingSelected() {}

    private fun getUserMoodHistory() {
        showProgress()
        // localMoods = LocalMoods.getAllMoods(preferenceHelper!!)


        viewModel.userMoodHistory().observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetUserHistoryResponse> ->
            if (apiResponse.loading) {
//                showProgress();
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {

                    binding.tvMessage.visibility = View.GONE
                    val list = apiResponse.body.userMoodHistoryList

                    setDataInBackground(list)

                } else {
                    hideProgress()
                }
            } else {
                hideProgress()
            }
        }
    }

    private fun setDataInBackground(list: List<UserMoodHistory>) {
        binding.tvMessage.visibility = View.VISIBLE
        setBackGroundBarChart(list)
//        setBackGroundLineChart(list)
//        setBackGroundRecyclerView(list)

//        MainScope().launch {
//            withContext(Dispatchers.Default) {
//                //TODO("Background processing...")
//                list.forEach {
//                    val mood = localMoodsMap!![it.expressionId]
//                    it.image = mood!!.moodImage
//                }
//                setBarChart(list)
//                setLineChart(list)
//            }
//            //TODO("Update UI here!")
//            dataList.addAll(list)
//            lineAdapter!!.notifyDataSetChanged()
//
//            binding.clLineChart.visibility = View.VISIBLE
//            binding.clBarChart.visibility = View.VISIBLE
//            binding.rvLine.visibility = View.VISIBLE
//            binding.tvMessage.visibility = View.GONE
//            binding.lineChart.setBackgroundColor(Color.TRANSPARENT) //set whatever color you prefer
//            binding.lineChart.setBackgroundColor(Color.WHITE)
//
//            hideProgress()
//
//
//        }

//        object : ExecutorBackgroundService() {
//            override fun onPreExecute() {
//                // before execution
//                showProgress()
//                binding.tvMessage.visibility = View.VISIBLE
//
//            }
//
//            override fun doInBackground() {
//                // background task here
//
//                setBarChart(list)
//                setLineChart(list)
//            }
//
//            override fun onPostExecute() {
//                // Ui task here
//
//                dataList.addAll(list)
//                lineAdapter!!.notifyDataSetChanged()
//
//                binding.clLineChart.visibility = View.VISIBLE
//                binding.clBarChart.visibility = View.VISIBLE
//                binding.rvLine.visibility = View.VISIBLE
//                binding.tvMessage.visibility = View.GONE
//                binding.lineChart.setBackgroundColor(Color.TRANSPARENT) //set whatever color you prefer
//                binding.lineChart.setBackgroundColor(Color.WHITE)
////                binding.lineChart.animateX(1500)
////                binding.moodBarChart.animateY(1000, Easing.EaseInOutQuart)
////                binding.moodBarChart.invalidate()
////                binding.lineChart.invalidate()
//
//
//                hideProgress()
//
//            }
//        }.execute()

    }

    private fun setBackGroundRecyclerView(list: List<UserMoodHistory>) {
        lifecycleScope.executeAsyncTask(onPreExecute = {
            showProgress()
        }, doInBackground = {
            getMoodListWithLocalMoodMap(list)
        }, onPostExecute = {
            hideProgress()
            binding.rvLine.visibility = View.VISIBLE
            dataList.addAll(it)
            lineAdapter!!.notifyDataSetChanged()
        })
    }

    private fun getMoodListWithLocalMoodMap(list: List<UserMoodHistory>): MutableList<UserMoodHistory> {
        val arrayList = ArrayList<UserMoodHistory>()
        list.forEach {
            val mood = localMoodsMap!![it.expressionId]
            it.image = mood!!.moodImage
            arrayList.add(it)
        }
        return arrayList
    }

    private fun setBackGroundLineChart(list: List<UserMoodHistory>) {
        lifecycleScope.executeAsyncTask(onPreExecute = {
            showProgress()
        }, doInBackground = {
            setLineChart(list)
        }, onPostExecute = {
            hideProgress()
            binding.clLineChart.visibility = View.VISIBLE
            binding.lineChart.setBackgroundColor(Color.TRANSPARENT) //set whatever color you prefer
            binding.lineChart.setBackgroundColor(Color.WHITE)
            setBackGroundRecyclerView(list)

        })
    }

    private fun setBackGroundBarChart(list: List<UserMoodHistory>) {
        lifecycleScope.executeAsyncTask(onPreExecute = {
            showProgress()
        }, doInBackground = {
            setBarChart(list)
        }, onPostExecute = {
            hideProgress()
            binding.clBarChart.visibility = View.VISIBLE
            setBackGroundLineChart(list)
        })
    }


}