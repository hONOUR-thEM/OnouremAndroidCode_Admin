package com.onourem.android.activity.ui.reviewus;

import com.onourem.android.activity.R;
import com.onourem.android.activity.arch.fragment.AbstractBaseFragment;

public class ReviewUsFragment extends AbstractBaseFragment<ReviewUsViewModel> {

    public ReviewUsFragment() {
        // Required empty public constructor
    }

    @Override
    protected int layoutResource() {
        return R.layout.fragment_review_us;
    }

    @Override
    public Class<ReviewUsViewModel> viewModelType() {
        return ReviewUsViewModel.class;
    }
}