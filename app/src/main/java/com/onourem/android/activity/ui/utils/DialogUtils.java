package com.onourem.android.activity.ui.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.onourem.android.activity.R;

import java.util.Objects;

public class DialogUtils {
    private static final String TAG = "DialogUtils";
    private static ProgressDialog _pd;


    public static ProgressDialog startProgressDialog(Context context) {
//        Log.v(TAG, "DialogUtils _pd::" + _pd);
        _pd = null;
        _pd = ProgressDialog.show(context, null, null);
        _pd.setContentView(R.layout.layout_progress_dialog);
        Objects.requireNonNull(_pd.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        _pd.setCancelable(false);
        _pd.show();

        return _pd;
    }


    public static void showToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}
