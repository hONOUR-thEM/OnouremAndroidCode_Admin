package com.onourem.android.activity.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.FragmentMoodMappingBinding
import com.onourem.android.activity.models.GetUserHistoryResponse
import com.onourem.android.activity.models.Mood
import com.onourem.android.activity.models.UserMoodHistory
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.dashboard.mood.adapters.MoodQuadrantAdapter
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import javax.inject.Inject

class MoodMappingFragment :
    AbstractBaseViewModelBindingFragment<DashboardViewModel, FragmentMoodMappingBinding>() {
    private var listFirst: ArrayList<UserMoodHistory>? = null
    private var listSecond: ArrayList<UserMoodHistory>? = null
    private var listThird: ArrayList<UserMoodHistory>? = null
    private var listFourth: ArrayList<UserMoodHistory>? = null
    private var allMoods: ArrayList<UserMoodHistory>? = null

    @JvmField
    @Inject
    var preferenceHelper: SharedPreferenceHelper? = null
    private var moodFirstQuadrantAdapter: MoodQuadrantAdapter? = null
    private var moodSecondQuadrantAdapter: MoodQuadrantAdapter? = null
    private var moodThirdQuadrantAdapter: MoodQuadrantAdapter? = null
    private var moodFourthQuadrantAdapter: MoodQuadrantAdapter? = null
    override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_mood_mapping
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserMoodHistory()

    }

    private fun getUserMoodHistory() {

        viewModel.userMoodHistory().observe(
            viewLifecycleOwner
        ) { apiResponse: ApiResponse<GetUserHistoryResponse> ->
            if (apiResponse.loading) {
                showProgress()
            } else if (apiResponse.isSuccess && apiResponse.body != null) {
                if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                    hideProgress()
                    createPrimaryCoordinates(apiResponse.body.userMoodHistoryList)
                } else {
                    hideProgress()
                }
            } else {
                hideProgress()
            }
        }
    }

    private fun createPrimaryCoordinates(userMoodHistoryList: List<UserMoodHistory>) {

        allMoods = ArrayList()
        listFirst = ArrayList()
        listSecond = ArrayList()
        listThird = ArrayList()
        listFourth = ArrayList()

        allMoods!!.addAll(userMoodHistoryList)

        for (mood in allMoods!!) {
            if (mood.positivity.toInt() > 0 && mood.energy.toInt() > 0) {
                mood.quadrant = "first"
                updateList(listFirst!!, mood)
            } else if (mood.positivity.toInt() < 0 && mood.energy.toInt() > 0) {
                mood.quadrant = "second"
                updateList(listSecond!!, mood)
            } else if (mood.positivity.toInt() < 0 && mood.energy.toInt() < 0) {
                mood.quadrant = "third"
                updateList(listThird!!, mood)
            } else if (mood.positivity.toInt() > 0 && mood.energy.toInt() < 0) {
                mood.quadrant = "fourth"
                updateList(listFourth!!, mood)
            }
        }

        setData()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setData() {
        binding.rvFirst.layoutManager = GridLayoutManager(requireActivity(), 5)
        binding.rvSecond.layoutManager = GridLayoutManager(requireActivity(), 5)
        binding.rvThird.layoutManager = GridLayoutManager(requireActivity(), 5)
        binding.rvFourth.layoutManager = GridLayoutManager(requireActivity(), 5)
        moodFirstQuadrantAdapter =
            MoodQuadrantAdapter(
                listFirst!!
            ) { item: UserMoodHistory ->
                Toast.makeText(
                    fragmentContext, item.expressionName, Toast.LENGTH_SHORT
                ).show()
            }
        binding.rvFirst.adapter = moodFirstQuadrantAdapter
        moodSecondQuadrantAdapter =
            MoodQuadrantAdapter(
                listSecond!!
            ) { item: UserMoodHistory ->
                Toast.makeText(
                    fragmentContext, item.expressionName, Toast.LENGTH_SHORT
                ).show()
            }
        binding.rvSecond.adapter = moodSecondQuadrantAdapter
        moodThirdQuadrantAdapter =
            MoodQuadrantAdapter(
                listThird!!
            ) { item: UserMoodHistory ->
                Toast.makeText(
                    fragmentContext, item.expressionName, Toast.LENGTH_SHORT
                ).show()
            }
        binding.rvThird.adapter = moodThirdQuadrantAdapter
        moodFourthQuadrantAdapter =
            MoodQuadrantAdapter(
                listFourth!!
            ) { item: UserMoodHistory ->
                Toast.makeText(
                    fragmentContext, item.expressionName, Toast.LENGTH_SHORT
                ).show()
            }
        binding.rvFourth.adapter = moodFourthQuadrantAdapter


//        allMoods.addAll(listFirst);
//        allMoods.addAll(listSecond);
//        allMoods.addAll(listThird);
//        allMoods.addAll(listFourth);
        val arrayAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, allMoods!!)
        binding.edtSearchQuery.setAdapter(arrayAdapter)
        binding.ibClear.setOnClickListener(ViewClickListener { v: View? ->
            binding.edtSearchQuery.setText("")
            binding.edtSearchQuery.textAlignment = View.TEXT_ALIGNMENT_CENTER
            binding.edtSearchQuery.clearFocus()
            hideKeyboard()
            binding.ibClear.visibility = View.GONE
            binding.ivInfo.visibility = View.VISIBLE
        })
        binding.edtSearchQuery.setOnTouchListener { view1: View?, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                // 100 is a fix value for the moment but you can change it
                // according to your view
                binding.edtSearchQuery.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                binding.ibClear.visibility = View.VISIBLE
                binding.ivInfo.visibility = View.GONE
                //showKeyboard();
            }
            false
        }
        binding.edtSearchQuery.onItemClickListener =
            AdapterView.OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                // Toast.makeText(getFragmentContext(), binding.edtSearchQuery.getAdapter().getItem(position).toString(), Toast.LENGTH_SHORT).show();
                val mood = binding.edtSearchQuery.adapter.getItem(position) as UserMoodHistory
                hideKeyboard()
                when (mood.quadrant) {
                    "first" -> highlightMood(
                        mood,
                        listFirst,
                        moodFirstQuadrantAdapter!!
                    )
                    "second" -> highlightMood(
                        mood,
                        listSecond,
                        moodSecondQuadrantAdapter!!
                    )
                    "third" -> highlightMood(
                        mood,
                        listThird,
                        moodThirdQuadrantAdapter!!
                    )
                    "fourth" -> highlightMood(
                        mood,
                        listFourth,
                        moodFourthQuadrantAdapter!!
                    )
                }
            }
    }

    private fun highlightMood(
        mood: UserMoodHistory?,
        items: ArrayList<UserMoodHistory>?,
        adapter: MoodQuadrantAdapter
    ) {
        updateList(items!!, mood!!)

        for (i in items.indices) {
            val item = items[i]
            if (item.positivity == mood.positivity && item.energy == mood.energy) {
                adapter.moodToHighlight(i, mood.expressionName)
                break
            }
        }
    }

    private fun showKeyboard() {
        binding.edtSearchQuery.clearFocus()
        binding.edtSearchQuery.requestFocus()
        //        binding.edtSearchQuery.performClick()
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.edtSearchQuery, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        try {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.edtSearchQuery.windowToken, 0)
            binding.edtSearchQuery.setText("")
            binding.edtSearchQuery.textAlignment = View.TEXT_ALIGNMENT_CENTER
            binding.edtSearchQuery.clearFocus()
            binding.ibClear.visibility = View.GONE
            binding.ivInfo.visibility = View.VISIBLE
        } catch (e: Exception) {
        }
    }

    private fun updateList(list: ArrayList<UserMoodHistory>, mood: UserMoodHistory) {
        var isMoodFound = false
        for (i in 0 until list.size) {
            val value = list[i]
            if (value.positivity.toInt() == mood.positivity.toInt() && value.energy.toInt() == mood.energy.toInt()) {
                value.expressionName = mood.expressionName
                value.quadrant = mood.quadrant
                list[i] = value
                isMoodFound = true
                break
            }
        }
        if (!isMoodFound) {
            list.add(mood)
        }
    }

    fun findIndex(arr: ArrayList<Mood>, item: Mood): Int {
        return arr.indexOf(item)
    }
}