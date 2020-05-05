package com.stingray.qello.android.firetv.login.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.stingray.qello.android.firetv.login.R;
import com.stingray.qello.android.firetv.login.communication.ForgotPasswordCallable;
import com.stingray.qello.android.firetv.login.communication.requestmodel.ForgotPasswordRequestBody;
import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.ui.fragments.FullScreenDialogFragment;

import java.util.Locale;

public class ForgotPasswordFragment extends FullScreenDialogFragment {
    public static final String TAG = ForgotPasswordFragment.class.getName();
    public static final String ARG_EMAIL = "EMAIL";

    private ObservableFactory observableFactory = new ObservableFactory();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ProgressBar progressBar = view.findViewById(R.id.forgot_password_progress);
        Button submitButton = view.findViewById(R.id.forget_password_submit_btn);

        emailEditText.setText(arguments.getString(ARG_EMAIL, ""));
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                submitButton.setEnabled(true);
            }
        });

        submitButton.setOnClickListener(v -> {
            String languageTag = Locale.getDefault().toLanguageTag();
            String email = emailEditText.getText().toString();
            ForgotPasswordRequestBody forgotPasswordRequestBody = new ForgotPasswordRequestBody(email, languageTag);
            progressBar.setVisibility(View.VISIBLE);

            observableFactory.create(new ForgotPasswordCallable(forgotPasswordRequestBody))
                    .subscribe(voidObject -> {
                        showToast(R.string.ResetPassword_EmailSentMesage);
                        dismiss();
                        progressBar.setVisibility(View.GONE);
                    }, throwable -> {
                        Log.e(TAG, "Forgot password call failed", throwable);
                        if (throwable instanceof ForgotPasswordCallable.EmailDoesntExistException) {
                            showToast(R.string.ResetPassword_EmailNotExist);
                            submitButton.setEnabled(false);
                        } else {
                            showToast(R.string.BaseViewController_ErrorMessage);
                        }
                        progressBar.setVisibility(View.GONE);
                    });
        });

        Button cancelButton = view.findViewById(R.id.forget_password_cancel_btn);
        cancelButton.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }

    private void showToast(int id) {
        Toast authToast = Toast.makeText(getActivity(), getResources().getText(id), Toast.LENGTH_LONG);
        authToast.setGravity(Gravity.CENTER, 0, 0);
        authToast.show();
    }
}
