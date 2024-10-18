package com.onourem.android.activity.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.onourem.android.activity.BuildConfig
import com.onourem.android.activity.R
import com.onourem.android.activity.arch.fragment.AbstractBaseDialogBindingFragment
import com.onourem.android.activity.arch.helper.ApiResponse
import com.onourem.android.activity.databinding.DialogMoodBinding
import com.onourem.android.activity.models.UpdateMoodResponse
import com.onourem.android.activity.models.UserExpressionList
import com.onourem.android.activity.prefs.SharedPreferenceHelper
import com.onourem.android.activity.ui.DashboardActivity
import com.onourem.android.activity.ui.dashboard.mood.adapters.MoodsChoiceAdapter
import com.onourem.android.activity.ui.games.viewmodel.UserActionViewModel
import com.onourem.android.activity.ui.utils.AppUtilities
import com.onourem.android.activity.ui.utils.Constants
import com.onourem.android.activity.ui.utils.LocalMoods.getAllMoods
import com.onourem.android.activity.ui.utils.listners.ViewClickListener
import com.onourem.android.activity.viewmodel.DashboardViewModel
import java.util.*
import javax.inject.Inject

class MoodsDialogFragment :
    AbstractBaseDialogBindingFragment<DashboardViewModel, DialogMoodBinding>() {

    private var userActionViewModel: UserActionViewModel? = null
    private var startTime: Long = 0


    @JvmField
    @Inject
    var sharedPreferenceHelper: SharedPreferenceHelper? = null
    public override fun viewModelType(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

    override fun layoutResource(): Int {
        return R.layout.dialog_mood
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userActionViewModel = ViewModelProvider(requireActivity(), viewModelFactory)[UserActionViewModel::class.java]

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        viewModel.setIsMoodsDialogShowing(true)

        setAdapter()
        startTime = System.currentTimeMillis()


//        userActionViewModel!!.counsellingMutableLiveData.observe(viewLifecycleOwner) { action: Action<Any?>? ->
//            if (action != null && action.actionType != ActionType.DISMISS) {
//                userActionViewModel!!.actionConsumedCounselling()
//                if (action.actionType == ActionType.ONLINE) {
//                    actionOnline(action)
//                } else if (action.actionType == ActionType.OFFLINE) {
//                    actionOffline(action)
//                }else if (action.actionType == ActionType.NOT_REQUIRED) {
//                    actionNotRequired(action)
//                }
//            }
//        }

//        viewModel.setDismissMoodsDialogConsumed();

//        viewModel.getDismissMoodsDialog().observe(getViewLifecycleOwner(), isDismiss -> {
//            if (isDismiss != null  && isDismiss.equalsIgnoreCase("true")) {
//                viewModel.setDismissMoodsDialogConsumed();
//                dismiss();
//            }
//        });
//        if (expressionDataResponse != null) {
//            setAdapter();
//            //Log.d("Tessy", expressionDataResponse.getUserExpressionList().toString());
//        } else {
//            viewModel.getMoodExpressions().observe(getViewLifecycleOwner(), expressionDataResponseApiResponse -> {
//                if (expressionDataResponseApiResponse.isSuccess() && expressionDataResponseApiResponse.body != null) {
//                    if (expressionDataResponseApiResponse.body.getErrorCode().equalsIgnoreCase("000")) {
//                        SurveyCOList newSurveyCOList = expressionDataResponseApiResponse.body.getSurveyCOList().get(0);
////                        expressionDataResponseApiResponseeApiResponse.body.getSurveyCOList().get(0).setUserAnserForSurvey("Y");
//                        String oldSurveyData = sharedPreferenceHelper.getString(KEY_SELECTED_SURVEY);
//                        if (!TextUtils.isEmpty(oldSurveyData)) {
//                            SurveyCOList oldSurveyCOList = new Gson().fromJson(oldSurveyData, SurveyCOList.class);
//                            if (oldSurveyCOList == null || !newSurveyCOList.getId().equalsIgnoreCase(oldSurveyCOList.getId()))
//                                sharedPreferenceHelper.putValue(KEY_SELECTED_SURVEY, new Gson().toJson(newSurveyCOList));
//                        } else {
//                            sharedPreferenceHelper.putValue(KEY_SELECTED_SURVEY, new Gson().toJson(newSurveyCOList));
//                        }
//                        sharedPreferenceHelper.putValue(KEY_EXPRESSIONS_RESPONSE, new Gson().toJson(expressionDataResponseApiResponse.body));
////                        sharedPreferenceHelper.putValue(KEY_FORCE_UPDATE_VERSION, expressionDataResponseApiResponse.body.getForceAndAdviceUpgrade().getAndroidForceUpgradeVersion());
////                        sharedPreferenceHelper.putValue(KEY_TIME_MILIES_LAST_MOOD_SYNC, Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
////                        sharedPreferenceHelper.putValue(KEY_ANDROID_FORCE_UPGRADE_VERSION, expressionDataResponseApiResponse.body.getForceAndAdviceUpgrade().getAndroidForceUpgradeVersion());
////                        sharedPreferenceHelper.putValue(KEY_ANDROID_ADVICE_UPGRADE_VERSION, expressionDataResponseApiResponse.body.getForceAndAdviceUpgrade().getAndroidAdviceUpgradeVersion());
////                        sharedPreferenceHelper.putValue(KEY_MESSAGE_UPDATE_VERSION, expressionDataResponseApiResponse.body.getForceAndAdviceUpgrade().getAndroidNewVersionMessage());
////                        sharedPreferenceHelper.putValue(KEY_TITLE_UPDATE_VERSION, expressionDataResponseApiResponse.body.getForceAndAdviceUpgrade().getScreenTitle());
//
//                        setAdapter(expressionDataResponseApiResponse.body);
//                    } else {
//                        showAlert(expressionDataResponseApiResponse.body.getErrorMessage(), v -> dismiss());
//                    }
//                } else if (!expressionDataResponseApiResponse.loading) {
//                    showAlert(expressionDataResponseApiResponse.errorMessage, v -> dismiss());
//                    if (expressionDataResponseApiResponse.errorMessage != null
//                            && (expressionDataResponseApiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
//                            || expressionDataResponseApiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))) {
//                        if (BuildConfig.DEBUG) {
//                            AppUtilities.showLog("Network Error", "getExpressionAndAppData");
//                        }
//                        if (getFragmentContext() != null)
//                            ((DashboardActivity) getFragmentContext()).addNetworkErrorUserInfo("getExpressionAndAppData", String.valueOf(expressionDataResponseApiResponse.code));
//                    }
//                }
//            });
//        }
    }


    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun setAdapter() {
        val userExpressionList: List<UserExpressionList> = getAllMoods(
            sharedPreferenceHelper!!
        )
        binding.ivInfo.setOnClickListener(ViewClickListener { v: View? -> showAlert("Emotional health is critical for a happy life. We ask you once everyday, 'How are you feeling?' to make you pay attention to it. No one can see your answer without your permission. Learn more about it in the WatchList section.") })
        binding.tvSurveyQuestion.text = "How are you feeling today?"
        binding.rvMoodsList.layoutManager = GridLayoutManager(requireActivity(), 4)
        val expressionsAdapter = MoodsChoiceAdapter(requireActivity(), userExpressionList)
        binding.rvMoodsList.adapter = expressionsAdapter
        expressionsAdapter.setOnItemClickListener {
            updateMood(it!!)
        }
        Objects.requireNonNull(binding.edtSearchQuery)
            .addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    expressionsAdapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable) {}
            })

//        RxTextView.textChanges(binding.edtSearchQuery)
//                .debounce(0, TimeUnit.SECONDS)
//                .subscribe(textChanged -> {
//                    requireActivity().runOnUiThread(() -> {
//
//                        if (binding.edtSearchQuery.hasFocus()) {
//                            if (expressionsAdapter.getFilter() != null) {
//                                expressionsAdapter.getFilter().filter(Objects.requireNonNull(binding.edtSearchQuery.getText()).toString().trim());
//                            }
//                        }
//
//                    });
//                });
        binding.ibClear.setOnClickListener(ViewClickListener { v: View? ->
            binding.edtSearchQuery.setText("")
            binding.edtSearchQuery.textAlignment = View.TEXT_ALIGNMENT_CENTER
            binding.edtSearchQuery.clearFocus()
            hideKeyboard()
            binding.ibClear.visibility = View.GONE
            binding.ivInfo.visibility = View.VISIBLE
            expressionsAdapter.filter.filter(
                Objects.requireNonNull(
                    binding.edtSearchQuery.text
                ).toString().trim { it <= ' ' })
        })
        binding.edtSearchQuery.setOnTouchListener { view1: View?, motionEvent: MotionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                // 100 is a fix value for the moment but you can change it
                // according to your view
                binding.edtSearchQuery.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                binding.ibClear.visibility = View.VISIBLE
                binding.ivInfo.visibility = View.GONE
                showKeyboard()
            }
            false
        }
    }

    private fun updateMood(item: UserExpressionList) {
        val endTime = System.currentTimeMillis()
        val timeSpentOnScreen = (endTime - startTime) / 1000.0
        AppUtilities.showLog("timeSpentOnScreen", "timeSpentOnScreen : $timeSpentOnScreen seconds")

        viewModel.setMoodsDialogShowingAfterOneDay(true)
        sharedPreferenceHelper!!.putValue(Constants.KEY_SELECTED_EXPRESSION, Gson().toJson(item))
        sharedPreferenceHelper!!.putValue(
            Constants.KEY_SELECTED_EXPRESSION_MESSAGE,
            item.expressionResponseMsg
        )
        sharedPreferenceHelper!!.putValue(
            Constants.KEY_TIME_MILIES_LAST_MOOD_SYNC, Calendar.getInstance(
                Locale.getDefault()
            ).timeInMillis
        )
        viewModel.setSelectedExpression(item)

        viewModel.updateUserMood(item.id, timeSpentOnScreen.toString())
            .observe(viewLifecycleOwner) { apiResponse: ApiResponse<UpdateMoodResponse> ->

                if (apiResponse.loading) {
                    showProgress()
                } else if (apiResponse.isSuccess && apiResponse.body != null) {
                    if (apiResponse.body.errorCode.equals("000", ignoreCase = true)) {
                        hideProgress()
//                    String response =  sharedPreferenceHelper.getString(KEY_SELECTED_EXPRESSION);
//                    long date = sharedPreferenceHelper.getLong(KEY_TIME_MILIES_LAST_MOOD_SYNC);
//
//                    if (TextUtils.isEmpty(response) || (date > 0 && AppUtilities.getDayDifference(new Date(date)) > 0)) {
//
//                        //((DashboardActivity) getFragmentContext()).moodsPopupShownAfterOneDay();
//                    }
//                    viewModel.setMoodsDialogShowingAfterOneDay(true);
//                    sharedPreferenceHelper.putValue(KEY_SELECTED_EXPRESSION, new Gson().toJson(item));
//                    sharedPreferenceHelper.putValue(KEY_SELECTED_EXPRESSION_MESSAGE, apiResponse.body.getTotalCountMessage());
//                    sharedPreferenceHelper.putValue(KEY_TIME_MILIES_LAST_MOOD_SYNC, Calendar.getInstance(Locale.getDefault()).getTimeInMillis());
//
//                    viewModel.setSelectedExpression(item);
//                    dismiss();
                        if (apiResponse.body.loginDayActivityInfoList != null && apiResponse.body.loginDayActivityInfoList!!.isNotEmpty()) {
                            viewModel.setLoginDayActivity(apiResponse.body.loginDayActivityInfoList!![0])
                        }

//                        Toast.makeText(fragmentContext, "Mood Updated", Toast.LENGTH_LONG).show()
                        dismiss()
                    } else {
                        hideProgress()
                        showAlert(apiResponse.body.errorMessage)
                    }
                } else if (!apiResponse.loading) {
                    hideProgress()
                    showAlert(
                        resources.getString(R.string.label_network_error),
                        apiResponse.errorMessage
                    )
                    if (apiResponse.errorMessage != null
                        && (apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message1))
                                || apiResponse.errorMessage.contains(getString(R.string.unable_to_connect_host_message3)))
                    ) {
                        if (BuildConfig.DEBUG) {
                            AppUtilities.showLog("Network Error", "updateUserMood")
                        }
                        (fragmentContext as DashboardActivity).addNetworkErrorUserInfo(
                            "updateUserMood",
                            apiResponse.code.toString()
                        )
                    }
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
        } catch (_: Exception) {
        }
    }

    companion object {
        //        MoodsDialogFragment moodsDialogFragment = new MoodsDialogFragment();
//        moodsDialogFragment.expressionDataResponse = expressionDataResponse;
        //    private ExpressionDataResponse expressionDataResponse;
        val instance: MoodsDialogFragment
            get() =//        MoodsDialogFragment moodsDialogFragment = new MoodsDialogFragment();
//        moodsDialogFragment.expressionDataResponse = expressionDataResponse;
                MoodsDialogFragment()
    }
}