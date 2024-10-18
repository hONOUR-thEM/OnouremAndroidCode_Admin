package com.onourem.android.activity.ui.utils.progress;

import static com.onourem.android.activity.ui.utils.Constants.DIM_AMOUNT;
import static com.onourem.android.activity.ui.utils.Constants.PROGRESS_DIM_AMOUNT;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import com.onourem.android.activity.R;

public class OnouremProgressDialog {

    private final Dialog dialog;
    private final MaterialTextView txtLoading;
    private final LinearProgressIndicator linearProgressIndicator;

    public OnouremProgressDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_progress_dialog);
        dialog.setCancelable(false);
        txtLoading = dialog.findViewById(R.id.txtLoading);
        linearProgressIndicator = dialog.findViewById(R.id.linearProgressView);
        if (dialog.getWindow() != null) {
            dialog.getWindow()
                    .setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void showDialog() {
        if (dialog != null && !dialog.isShowing()) {
            txtLoading.setText("Loading");
            txtLoading.setVisibility(View.VISIBLE);
            linearProgressIndicator.setVisibility(View.GONE);
            if (!dialog.isShowing()) {
                dialog.show();
                WindowManager.LayoutParams windowParams = dialog.getWindow().getAttributes();
                windowParams.dimAmount = PROGRESS_DIM_AMOUNT;
                windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            }
        }

    }

    public void showDialogWithText(String text, boolean showLineProgress) {
        if (dialog != null && !dialog.isShowing()) {
            txtLoading.setVisibility(View.VISIBLE);
            if (showLineProgress) {
                linearProgressIndicator.setVisibility(View.VISIBLE);
            } else {
                linearProgressIndicator.setVisibility(View.GONE);
            }
            txtLoading.setText(text);
            if (!dialog.isShowing()) {
                dialog.show();
                WindowManager.LayoutParams windowParams = dialog.getWindow().getAttributes();
                windowParams.dimAmount = PROGRESS_DIM_AMOUNT;
                windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            }
        }
    }

    public void hideDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.cancel();
    }

    public void setProgress(int percent) {
        if (linearProgressIndicator.getVisibility() == View.VISIBLE) {
            linearProgressIndicator.setProgressCompat(percent, true);
        }
    }
}
