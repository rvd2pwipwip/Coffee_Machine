package com.stingray.qello.android.firetv.login.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stingray.qello.android.firetv.login.R;
import com.stingray.qello.firetv.android.utils.Helpers;

public class ForgotPasswordFragment extends DialogFragment {
    public static final String TAG = ForgotPasswordFragment.class.getName();
    public static final String ARG_EMAIL = "EMAIL";

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.forgot_password_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        EditText emailEditText = view.findViewById(R.id.forgot_pass_email);
        emailEditText.setText(arguments.getString(ARG_EMAIL, ""));

        Button submitButton = view.findViewById(R.id.forget_password_submit_btn);
        submitButton.setOnClickListener(v -> {
            // TODO Implement
            Toast authToast = Toast.makeText(getActivity(), "Not Implemented", Toast.LENGTH_LONG);
            authToast.setGravity(Gravity.CENTER, 0, 0);
            authToast.show();
        });

        Button cancelButton = view.findViewById(R.id.forget_password_cancel_btn);
        cancelButton.setOnClickListener(v -> {
            getActivity().getFragmentManager().popBackStack();
        });
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), getTheme());
        final Window window = dialog.getWindow();

        if (window != null) {
            final WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.CENTER;

            Display display = window.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            wlp.width = size.x;
            wlp.height = size.y;

            window.setAttributes(wlp);

        }

        return dialog;
    }
}
