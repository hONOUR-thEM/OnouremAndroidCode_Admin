package com.onourem.android.activity.ui.report;

import com.onourem.android.activity.R;
import com.onourem.android.activity.arch.fragment.AbstractBaseFragment;

public class ReportProblemFragment extends AbstractBaseFragment<ReportProblemViewModel> {

    public ReportProblemFragment() {
        // Required empty public constructor
    }

    @Override
    protected int layoutResource() {
        return R.layout.fragment_report_problem;
    }

    @Override
    public Class<ReportProblemViewModel> viewModelType() {
        return ReportProblemViewModel.class;
    }
}