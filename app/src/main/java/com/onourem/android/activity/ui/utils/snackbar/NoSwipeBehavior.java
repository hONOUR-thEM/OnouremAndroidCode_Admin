package com.onourem.android.activity.ui.utils.snackbar;

import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;

public class NoSwipeBehavior extends BaseTransientBottomBar.Behavior {

    @Override
    public boolean canSwipeDismissView(View child) {
        return false;
    }
}