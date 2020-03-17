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

import com.amazon.android.utils.Preferences;
import com.amazon.auth.AuthenticationConstants;
import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;
import com.amazon.identity.auth.device.authorization.api.AuthorizationListener;
import com.amazon.identity.auth.device.authorization.api.AuthzConstants;
import com.amazon.identity.auth.device.shared.APIListener;
import com.stingray.qello.android.firetv.login.R;

/**
 * This activity allows users to login with amazon.
 */
public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getName();

    private String[] APP_SCOPES;
    private static final String IS_LOGGED_IN = "isLoggedIn";

    private LinearLayout loginWithUP;
    private TextView usernameInput;
    private TextView passwordInput;
    private Button continueButton;
    private ImageButton lwaButton;
    private AmazonAuthorizationManager amazonAuthManager;
    private ProgressBar mLogInProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Confirm that we have the correct API Key.
        try {
            amazonAuthManager = new AmazonAuthorizationManager(this, Bundle.EMPTY);
            //TODO new UL auth manager
        }
        catch (IllegalArgumentException e) {
            showAuthToast(getString(R.string.incorrect_api_key));
            Log.e(TAG, getString(R.string.incorrect_api_key), e);
        }
        setContentView(R.layout.login_dialog_layout);

        APP_SCOPES = new String[]{getString(R.string.profile_Login)};

        initializeUI();
    }

    /**
     * Initializes all of the UI elements in the activity.
     */
    private void initializeUI() {

        loginWithUP = (LinearLayout) findViewById(R.id.login_with_up);
        usernameInput = (TextView) findViewById(R.id.username_input);
        passwordInput = (TextView) findViewById(R.id.password_input);
        continueButton = (Button) findViewById(R.id.continue_btn);
        continueButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setLoggingInState(true);
                setLoggedInState();
                //TODO Login with UL
                setResult(RESULT_OK);
                finish();
            }
        });

        // Setup the listener on the login button.
        lwaButton = (ImageButton) findViewById(R.id.login_with_amazon);
        lwaButton.setVisibility(Button.VISIBLE);
        lwaButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setLoggingInState(true);
                amazonAuthManager.authorize(APP_SCOPES, Bundle.EMPTY, new AuthListener());
            }
        });

        mLogInProgress = (ProgressBar) findViewById(R.id.log_in_progress);
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
            amazonAuthManager.getToken(APP_SCOPES, new TokenListener());
        }

        /**
         * There was an error during the attempt to authorize the application.
         * Log the error, and reset the profile text view.
         *
         * @param ae The error that occurred during authorization.
         */
        @Override
        public void onError(final AuthError ae) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showAuthToast(getString(R.string.error_during_auth));
                    setLoggingInState(false);
                    LoginActivity.this
                            .setResultAndReturn(
                                    ae.getCause(),
                                    AuthenticationConstants.AUTHENTICATION_ERROR_CATEGORY);
                }
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
                    showAuthToast(getString(R.string.auth_cancelled));
                }
            });
        }

    }

    /**
     * Sets the state of the application to reflect that the user is not currently authorized.
     */
    private void setLoggedOutState() {
        lwaButton.setVisibility(Button.VISIBLE);
        Preferences.setBoolean(IS_LOGGED_IN, false);
    }

    /**
     * Sets the state of the application to reflect that the user is currently authorized.
     */
    private void setLoggedInState() {
        loginWithUP.setVisibility(LinearLayout.GONE);
        lwaButton.setVisibility(Button.GONE);
        Preferences.setBoolean(IS_LOGGED_IN, true);
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
            lwaButton.setVisibility(Button.GONE);
            mLogInProgress.setVisibility(ProgressBar.VISIBLE);
        }
        else {
            if (!Preferences.getBoolean(IS_LOGGED_IN)) {
                lwaButton.setVisibility(Button.VISIBLE);
            }
            mLogInProgress.setVisibility(ProgressBar.GONE);
        }
    }

    /**
     * This method handles toasts messages.
     *
     * @param authToastMessage The message to be posted.
     */
    private void showAuthToast(String authToastMessage) {

        Toast authToast = Toast.makeText(getApplicationContext(), authToastMessage, Toast
                .LENGTH_LONG);
        authToast.setGravity(Gravity.CENTER, 0, 0);
        authToast.show();
    }

    /**
     * {@link AuthListener} which is passed in to the {@link AmazonAuthorizationManager}
     * getProfile api call.
     */
    private class TokenListener implements APIListener {

        /**
         * Updates the profile view with data from the successful getProfile response.
         * Sets app state to logged in.
         */
        @Override
        public void onSuccess(Bundle response) {

            final String accessToken = response.getString(AuthzConstants.BUNDLE_KEY.TOKEN.val);
            //TODO CALL UL with access token
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLoggedInState();
                }
            });
            setResult(RESULT_OK);
            finish();
        }

        /**
         * Updates profile view to reflect that there was an error while retrieving profile
         * information.
         */
        @Override
        public void onError(AuthError ae) {

            Log.e(TAG, ae.getMessage(), ae);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLoggingInState(false);
                }
            });
        }
    }

    /**
     * Set the corresponding extras and finish this activity.
     *
     * @param throwable Contains detailed info about the cause of error.
     * @param category  The error cause.
     */
    private void setResultAndReturn(Throwable throwable, String category) {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(AuthenticationConstants.ERROR_CATEGORY, category);
        bundle.putSerializable(AuthenticationConstants.ERROR_CAUSE, throwable);
        setResult(RESULT_CANCELED, intent.putExtra(AuthenticationConstants.ERROR_BUNDLE, bundle));
        finish();
    }
}