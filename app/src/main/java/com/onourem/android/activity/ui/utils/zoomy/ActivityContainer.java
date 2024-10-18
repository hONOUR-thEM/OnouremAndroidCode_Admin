package com.onourem.android.activity.ui.utils.zoomy;

import android.app.Activity;
import android.view.ViewGroup;


public class ActivityContainer implements TargetContainer {

    private final Activity mActivity;

    ActivityContainer(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public ViewGroup getDecorView() {
        return (ViewGroup) mActivity.getWindow().getDecorView();
    }
}
