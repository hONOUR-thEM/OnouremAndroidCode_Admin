package com.onourem.android.activity.ui.utils.listners;

public interface ProgressListener {

    void showProgress();

    void hideProgress();

    void showProgressWithText(String text, boolean showLineProgress);

}
