package com.stingray.qello.android.firetv.login.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.android.utils.Helpers;
import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;
import com.amazon.identity.auth.device.authorization.api.AuthorizationListener;
import com.amazon.identity.auth.device.authorization.api.AuthzConstants;
import com.amazon.identity.auth.device.shared.APIListener;
import com.stingray.qello.android.firetv.login.R;

public class AccountCreationFragment extends Fragment {

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;
    private static final String TAG = AccountCreationFragment.class.getName();
    private String[] APP_SCOPES = new String[]{"profile"};

    private TextView usernameInput;
    private TextView passwordInput;
    private Button createButton;
    private ImageButton lwaButton;
    private AmazonAuthorizationManager amazonAuthManager;
    private ProgressBar mLogInProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);

        // Confirm that we have the correct API Key.
        try {
            amazonAuthManager = new AmazonAuthorizationManager(getActivity(), Bundle.EMPTY);
            //TODO new UL auth manager
        }
        catch (IllegalArgumentException e) {
            showAuthToast(getString(R.string.incorrect_api_key));
            Log.e(TAG, getString(R.string.incorrect_api_key), e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.create_account_layout, container, false);

        usernameInput = (TextView) view.findViewById(R.id.userName);
        passwordInput = (TextView) view.findViewById(R.id.password);
        createButton = (Button) view.findViewById(R.id.create_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO call UL
            }
        });

        // Setup the listener on the login button.
        lwaButton = (ImageButton) view.findViewById(R.id.login_with_amazon2);
        lwaButton.setVisibility(Button.VISIBLE);
        lwaButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                amazonAuthManager.authorize(APP_SCOPES, Bundle.EMPTY, new AccountCreationFragment.AuthListener());
            }
        });

        mLogInProgress = (ProgressBar) view.findViewById(R.id.progressBar2);

        return view;
    }

    private void showAuthToast(String authToastMessage) {

        Toast authToast = Toast.makeText(getActivity().getBaseContext(), authToastMessage, Toast
                .LENGTH_LONG);
        authToast.setGravity(Gravity.CENTER, 0, 0);
        authToast.show();
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
            amazonAuthManager.getToken(APP_SCOPES, new AccountCreationFragment.TokenListener());
        }

        /**
         * There was an error during the attempt to authorize the application.
         * Log the error, and reset the profile text view.
         *
         * @param ae The error that occurred during authorization.
         */
        @Override
        public void onError(final AuthError ae) {
            //TODO manage error
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
            //TODO manage cancel
        }

    }

    private class TokenListener implements APIListener {

        /**
         * Updates the profile view with data from the successful getProfile response.
         * Sets app state to logged in.
         */
        @Override
        public void onSuccess(Bundle response) {
            final String accessToken = response.getString(AuthzConstants.BUNDLE_KEY.TOKEN.val);
            //TODO CALL UL with access token
        }

        /**
         * Updates profile view to reflect that there was an error while retrieving profile
         * information.
         */
        @Override
        public void onError(AuthError ae) {
            Log.e(TAG, ae.getMessage(), ae);
        }
    }
}
