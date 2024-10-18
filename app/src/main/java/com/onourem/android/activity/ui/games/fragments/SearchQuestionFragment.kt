package com.onourem.android.activity.ui.games.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding4.widget.textChanges
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseViewModelBindingFragment
import com.onourem.android.activity.databinding.FragmentSearchQuestionBinding
import com.onourem.android.activity.models.FilterItem
import com.onourem.android.activity.models.LoginDayActivityInfoList
import com.onourem.android.activity.ui.games.adapters.QuestionGamesFilterAdapter
import com.onourem.android.activity.ui.games.adapters.SearchQuestionAdapter
import com.onourem.android.activity.ui.settings.SettingsViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.listners.OnItemClickListener
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import io.reactivex.rxjava3.functions.Consumer
import java.util.*
import java.util.concurrent.TimeUnit

class SearchQuestionFragment :
    AbstractBaseViewModelBindingFragment<SettingsViewModel, FragmentSearchQuestionBinding>() {
    private var quesGamesFilterAdapter: QuestionGamesFilterAdapter? = null
    private var selectedFilterIndex: Int = 0
    private var searchQuestionAdapter: SearchQuestionAdapter? = null
    override fun viewModelType(): Class<SettingsViewModel> {
        return SettingsViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.fragment_search_question
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    @SuppressLint("ClickableViewAccessibility", "CheckResult")
    private fun loadData() {
        binding.edtSearchQuery.textChanges()
            .debounce(2, TimeUnit.SECONDS)
            .subscribe(Consumer { textChanged: CharSequence? ->
                if (Objects.requireNonNull(
                        binding.edtSearchQuery.text
                    ).toString().trim({ it <= ' ' }).length >= 3
                ) {
                    requireActivity().runOnUiThread(Runnable {
                        if (binding.edtSearchQuery.hasFocus()) {
                            val loginDayActivityInfoLists: ArrayList<LoginDayActivityInfoList> =
                                ArrayList()
                            loginDayActivityInfoLists.add(LoginDayActivityInfoList())
                            loginDayActivityInfoLists.add(LoginDayActivityInfoList())
                            loginDayActivityInfoLists.add(LoginDayActivityInfoList())
                            loginDayActivityInfoLists.add(LoginDayActivityInfoList())
                            loginDayActivityInfoLists.add(LoginDayActivityInfoList())
                            loginDayActivityInfoLists.add(LoginDayActivityInfoList())
                            loginDayActivityInfoLists.add(LoginDayActivityInfoList())
                            loginDayActivityInfoLists.add(LoginDayActivityInfoList())
                            loginDayActivityInfoLists.add(LoginDayActivityInfoList())
                            setAdapter(loginDayActivityInfoLists)

//                                Toast.makeText(requireActivity(), "We will fire search service here..", Toast.LENGTH_SHORT).show();
                        }
                    })
                    //Log.d("TAG", "Stopped typing!");
                }
            })
        binding.ibClear.setOnClickListener(ViewClickListener(View.OnClickListener { v: View? ->
            binding.edtSearchQuery.setText("")
            binding.edtSearchQuery.textAlignment = View.TEXT_ALIGNMENT_CENTER
            binding.edtSearchQuery.clearFocus()
            AppUtilities.hideKeyboard(requireActivity())
            binding.ibClear.visibility = View.GONE
        }))
        binding.edtSearchQuery.setOnTouchListener(OnTouchListener { view1: View?, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                // 100 is a fix value for the moment but you can change it
                // according to your view
                binding.edtSearchQuery.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                binding.ibClear.visibility = View.VISIBLE
            }
            false
        })

//        binding.edtSearchQuery.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                if (s.toString().trim().length() >= 5) {
//
//                } else {
//                    binding.ibClear.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // TODO Auto-generated method stub
//
////                if (s.toString().trim().length() == 0) {
////                    binding.edtSearchQuery.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
////                } else {
////
////                    binding.edtSearchQuery.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cross_black_24dp, 0);
////                    binding.edtSearchQuery.setCompoundDrawablePadding(5);
////                }
//            }
//        });
        val filterItems: ArrayList<FilterItem> = ArrayList()
        filterItems.add(FilterItem("Friends Playing", "FFF", false, 0))
        filterItems.add(FilterItem("New", "AAA", false, 1))
        filterItems.add(FilterItem("Played", "ZZZ", false, 2))
        filterItems.add(FilterItem("My Qs", "YYY", false, 3))
        quesGamesFilterAdapter =
            QuestionGamesFilterAdapter(filterItems, OnItemClickListener { item: FilterItem ->
                //Toast.makeText(requireActivity(), "Clicked " + item.getTitle(), Toast.LENGTH_SHORT).show();
                item.isVisible = false
                quesGamesFilterAdapter!!.modifyItem(item.pos, item)
                when (item.id) {
                    "FFF" -> {
                        selectedFilterIndex = 0
                        //                    playGroup.setPlayGroupId("FFF");
                        binding.rvSearchResult.scrollToPosition(0)
                    }
                    "AAA" -> {
                        selectedFilterIndex = 1
                        //                    playGroup.setPlayGroupId("AAA");
                        binding.rvSearchResult.scrollToPosition(0)
                    }
                    "ZZZ" -> {
                        selectedFilterIndex = 2
                        //                    playGroup.setPlayGroupId("ZZZ");
                        binding.rvSearchResult.scrollToPosition(0)
                    }
                    "YYY" -> {
                        selectedFilterIndex = 3
                        //                    playGroup.setPlayGroupId("YYY");
                        binding.rvSearchResult.scrollToPosition(0)
                    }
                }
            })
        quesGamesFilterAdapter!!.selectedPos = selectedFilterIndex
        binding.rvQuesGamesFilter.layoutManager = LinearLayoutManager(
            requireActivity(),
            RecyclerView.HORIZONTAL,
            false
        )
        binding.rvQuesGamesFilter.adapter = quesGamesFilterAdapter

//        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
//        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        val layoutManager: LinearLayoutManager = LinearLayoutManager(requireActivity())
        binding.rvSearchResult.layoutManager = layoutManager
        //        binding.rvSearchResult.addItemDecoration(itemDecoration);
        layoutManager.orientation = RecyclerView.VERTICAL
    }

    private fun setAdapter(loginDayActivityInfoLists: ArrayList<LoginDayActivityInfoList>) {
        if (searchQuestionAdapter == null) {
            searchQuestionAdapter = SearchQuestionAdapter(
                loginDayActivityInfoLists,
                OnItemClickListener({ item: LoginDayActivityInfoList? -> })
            )
        } else {
            searchQuestionAdapter!!.notifyDataSetChanged()
        }
        binding.rvSearchResult.adapter = searchQuestionAdapter
    }
}