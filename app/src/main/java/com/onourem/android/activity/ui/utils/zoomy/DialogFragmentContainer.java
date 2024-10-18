package com.onourem.android.activity.ui.utils.zoomy;

import android.app.DialogFragment;

public class DialogFragmentContainer extends DialogContainer {

    DialogFragmentContainer(DialogFragment dialog) {
        super(dialog.getDialog());
    }

}
