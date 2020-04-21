package com.stingray.qello.firetv.android.ui.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.stingray.qello.firetv.utils.R;

public abstract class FullScreenDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_NoTitleBar_Fullscreen);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setWindowAnimations(R.style.fullscreen_dialog_animation);
        }

        return dialog;
    }
}
