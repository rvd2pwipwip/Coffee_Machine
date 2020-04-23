/**
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.stingray.qello.android.firetv.login.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;
import com.amazon.identity.auth.device.authorization.api.AuthorizationListener;
import com.amazon.identity.auth.device.authorization.api.AuthzConstants;
import com.amazon.identity.auth.device.shared.APIListener;
import com.stingray.qello.android.firetv.login.R;
import com.stingray.qello.android.firetv.login.ULAuthManager;
import com.stingray.qello.android.firetv.login.UserInfoBundle;
import com.stingray.qello.android.firetv.login.communication.AmazonLoginCallable;
import com.stingray.qello.android.firetv.login.communication.UserpassLoginCallable;
import com.stingray.qello.android.firetv.login.communication.requestmodel.AmazonLoginRequestBody;
import com.stingray.qello.android.firetv.login.communication.requestmodel.LoginResponse;
import com.stingray.qello.android.firetv.login.communication.requestmodel.UserpassLoginRequestBody;
import com.stingray.qello.android.firetv.login.fragments.ForgotPasswordFragment;
import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.event.AuthenticationStatusUpdateEvent;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.utils.Preferences;
import com.stingray.qello.firetv.auth.AuthenticationConstants;
import com.stingray.qello.firetv.utils.UserPreferencesRetriever;

import org.greenrobot.eventbus.EventBus;

/**
 * This activity allows users to login with amazon.
 */
public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getName();

    private String[] APP_SCOPES;

    private LinearLayout loginWithUP;
    private TextView usernameInput;
    private TextView passwordInput;
    private Button continueButton;
    private Button forgotPasswordButton;
    private ImageButton lwaButton;
    private AmazonAuthorizationManager amazonAuthManager;
    private ULAuthManager ulAuthManager;
    private ProgressBar mLogInProgress;
    private ObservableFactory observableFactory = new ObservableFactory();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Confirm that we have the correct API Key.
        try {
            amazonAuthManager = new AmazonAuthorizationManager(this, Bundle.EMPTY);
        } catch (IllegalArgumentException e) {
            showAuthToast(getString(R.string.incorrect_api_key));
            Log.e(TAG, getString(R.string.incorrect_api_key), e);
        }

        setContentView(R.layout.login_dialog_layout);

        APP_SCOPES = new String[]{getString(R.string.profile_Login)};

        //For universal login
        ulAuthManager = new ULAuthManager();

        initializeUI();
    }

    /**
     * Initializes all of the UI elements in the activity.
     */
    private void initializeUI() {

        View backButton = findViewById(R.id.nav_back_button);
        backButton.setOnClickListener(v -> finishAfterTransition());

        loginWithUP = findViewById(R.id.login_with_up);
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);

//        // TODO Remove
//        usernameInput.setText("lf1@sd-i.ca");
//        passwordInput.setText("12345678");

        continueButton = findViewById(R.id.continue_btn);
        continueButton.setOnClickListener(v -> {
            setLoggingInState(true);
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();

            UserpassLoginRequestBody requestBody = new UserpassLoginRequestBody(username, password,
                    UserPreferencesRetriever.getLanguageCode(),
                    UserPreferencesRetriever.getDeviceId(this)
            );

            observableFactory.createDetached(new UserpassLoginCallable(requestBody))
                    .subscribe(
                            LoginActivity.this::callULAuthorize,
                            throwable -> {
                                Log.e(TAG, "Failed to call user pass login", throwable);
                                setResultAndReturn(throwable);
                            });
        });

        // Setup the listener on the login button.
        lwaButton = findViewById(R.id.login_with_amazon);
        lwaButton.setVisibility(Button.VISIBLE);
        lwaButton.setOnClickListener(v -> {
            setLoggingInState(true);
            amazonAuthManager.authorize(APP_SCOPES, Bundle.EMPTY, new AuthListener());
        });

        forgotPasswordButton = findViewById(R.id.forget_password_btn);
        forgotPasswordButton.setOnClickListener(v -> {
            Fragment forgotPasswordFragment = getFragmentManager().findFragmentByTag(TAG);

            if (forgotPasswordFragment == null) {
                forgotPasswordFragment = new ForgotPasswordFragment();
            }

            Bundle bundle = new Bundle();
            if (usernameInput != null && usernameInput.getText() != null) {
                bundle.putString(ForgotPasswordFragment.ARG_EMAIL, usernameInput.getText().toString());
            }
            forgotPasswordFragment.setArguments(bundle);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(forgotPasswordFragment, ForgotPasswordFragment.TAG);
            ft.addToBackStack(ForgotPasswordFragment.TAG);
            ft.commit();
        });

        mLogInProgress = findViewById(R.id.log_in_progress);
    }

    /**
     * Sets the state of the application to reflect that the user is not currently authorized.
     */
    private void setLoggedOutState() {
        lwaButton.setVisibility(Button.VISIBLE);
        Preferences.setLoggedOutState();
        EventBus.getDefault().post(new AuthenticationStatusUpdateEvent(false));
    }

    /**
     * Sets the state of the application to reflect that the user is currently authorized.
     */
    private void setLoggedInState(UserInfoBundle userInfoBundle) {
        loginWithUP.setVisibility(LinearLayout.GONE);
        lwaButton.setVisibility(Button.GONE);
        Preferences.setLoggedInState(
                userInfoBundle.getSessionId(),
                userInfoBundle.getAccessToken(),
                userInfoBundle.getRefreshToken(),
                userInfoBundle.getSubscriptionPlan(),
                userInfoBundle.getUserTrackingId(),
                userInfoBundle.getSubscriptionEnd(),
                userInfoBundle.getStingrayEmail()
        );
        EventBus.getDefault().post(new AuthenticationStatusUpdateEvent(true));
        setLoggingInState(false);
    }

    /**
     * Turns on/off display elements which indicate that the user is currently in the process of
     * logging in.
     *
     * @param loggingIn Whether or not the user is currently in the process of logging in.
     */
    private void setLoggingInState(final boolean loggingIn) {

        if (loggingIn) {
            loginWithUP.setVisibility(Button.GONE);
            mLogInProgress.setVisibility(ProgressBar.VISIBLE);
        }
        else {
            if (!Preferences.getBoolean(PreferencesConstants.IS_LOGGED_IN)) {
                loginWithUP.setVisibility(Button.VISIBLE);
            }
            mLogInProgress.setVisibility(ProgressBar.GONE);
        }
    }

    private void showAuthToast(String authToastMessage) {

        Toast authToast = Toast.makeText(getApplicationContext(), authToastMessage, Toast
                .LENGTH_LONG);
        authToast.setGravity(Gravity.CENTER, 0, 0);
        authToast.show();
    }

    private void setResultAndReturn(Throwable throwable) {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(AuthenticationConstants.ERROR_CATEGORY, AuthenticationConstants.AUTHENTICATION_ERROR_CATEGORY);
        bundle.putSerializable(AuthenticationConstants.ERROR_CAUSE, throwable);
        setResult(RESULT_CANCELED, intent.putExtra(AuthenticationConstants.ERROR_BUNDLE, bundle));
        finishAfterTransition();
    }

    private void callULAuthorize(LoginResponse response) {
        if (response != null) {
            ulAuthManager.authorize(
                    response.getSessionId(),
                    UserPreferencesRetriever.getLanguageCode(),
                    UserPreferencesRetriever.getDeviceId(this),
                    new AuthListener()
            );
        } else {
            setResultAndReturn(new Throwable("Failed to login"));
        }
    }

    /**
     * {@link AuthorizationListener} which is passed in to authorize calls made on the {@link
     * AmazonAuthorizationManager} member.
     * Starts getToken workflow if the authorization was successful, or displays a toast if the
     * user cancels authorization.
     */
    private class AuthListener implements AuthorizationListener {

        /**
         * Authorization was completed successfully.
         * Display the profile of the user who just completed authorization.
         *
         * @param response The bundle containing authorization response. Not used.
         */
        @Override
        public void onSuccess(Bundle response) {
            String authCode = response.getString(AuthzConstants.BUNDLE_KEY.AUTHORIZATION_CODE.val);

            if (authCode == null) {
                amazonAuthManager.getToken(APP_SCOPES, new TokenListener());
            } else {
                ulAuthManager.getToken(response, new TokenListener());
            }
        }

        /**
         * There was an error during the attempt to authorize the application.
         * Log the error, and reset the profile text view.
         *
         * @param ae The error that occurred during authorization.
         */
        @Override
        public void onError(final AuthError ae) {
            runOnUiThread(() -> {
                showAuthToast(getString(R.string.error_during_auth));
                setLoggingInState(false);
                setLoggedOutState();
                LoginActivity.this.setResultAndReturn(ae.getCause());
            });
        }

        /**
         * Authorization was cancelled before it could be completed.
         * A toast is shown to the user, to confirm that the operation was cancelled, and the
         * profile text view is reset.
         *
         * @param cause The bundle containing the cause of the cancellation. Not used.
         */
        @Override
        public void onCancel(Bundle cause) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLoggedOutState();
                    showAuthToast(getString(R.string.auth_cancelled));
                }
            });
        }

    }

    private class TokenListener implements APIListener {

        /**
         * Updates the profile view with data from the successful getProfile response.
         * Sets app state to logged in.
         */
        @Override
        public void onSuccess(Bundle response) {
            final AuthzConstants.FUTURE_TYPE amazonFutureType = (AuthzConstants.FUTURE_TYPE) response.get(AuthzConstants.BUNDLE_KEY.FUTURE.val);

            if (amazonFutureType != null) {
                if ( AuthzConstants.FUTURE_TYPE.SUCCESS.equals(amazonFutureType)) {
                    String accessToken = response.getString(AuthzConstants.BUNDLE_KEY.TOKEN.val);

                    AmazonLoginRequestBody requestBody = new AmazonLoginRequestBody(accessToken,
                            UserPreferencesRetriever.getLanguageCode(),
                            UserPreferencesRetriever.getDeviceId(LoginActivity.this)
                    );
                    observableFactory.createDetached(new AmazonLoginCallable(requestBody))
                            .subscribe(
                                    LoginActivity.this::callULAuthorize,
                                    throwable -> {
                                        Log.e(TAG, "Failed to call amazon login", throwable);
                                        setResultAndReturn(throwable);
                                    }
                            );
                } else {
                    onError(new AuthError("Failed to login authenticate with Amazon", AuthError.ERROR_TYPE.ERROR_INVALID_GRANT));
                }
            } else {
                runOnUiThread(() -> setLoggedInState(new UserInfoBundle(response)));
                setResult(RESULT_OK);
                finishAfterTransition();
            }
        }

        /**
         * Updates profile view to reflect that there was an error while retrieving profile
         * information.
         */
        @Override
        public void onError(AuthError ae) {
            Log.e(TAG, ae.getMessage(), ae);
            runOnUiThread(() -> {
                setLoggedOutState();
                setLoggingInState(false);
            });
            setResultAndReturn(ae);
        }
    }
}