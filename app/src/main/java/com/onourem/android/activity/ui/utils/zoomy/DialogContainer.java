package com.onourem.android.activity.ui.utils.zoomy;

import android.app.Dialog;
import android.view.ViewGroup;


public class DialogContainer implements TargetContainer {

    private final Dialog mDialog;

    DialogContainer(Dialog dialog) {
        this.mDialog = dialog;
    }

    @Override
    public final ViewGroup getDecorView() {
        return mDialog.getWindow() != null ? (ViewGroup) mDialog.getWindow().getDecorView() : null;
    }
}
