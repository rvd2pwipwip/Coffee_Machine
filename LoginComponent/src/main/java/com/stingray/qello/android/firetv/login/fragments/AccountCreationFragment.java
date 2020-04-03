package com.stingray.qello.android.firetv.login.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.stingray.qello.android.firetv.login.communication.UserpassCreateCallable;
import com.stingray.qello.android.firetv.login.communication.UserpassLoginCallable;
import com.stingray.qello.android.firetv.login.communication.requestmodel.UserpassCreateRequestBody;
import com.stingray.qello.android.firetv.login.communication.requestmodel.UserpassLoginRequestBody;
import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.event.AuthenticationStatusUpdateEvent;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.utils.Helpers;
import com.stingray.qello.firetv.android.utils.Preferences;
import com.stingray.qello.firetv.auth.AuthenticationConstants;

import org.greenrobot.eventbus.EventBus;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AccountCreationFragment extends Fragment {
    // TODO Get lang and deviceId from system
    private final static String HARDCODED_LANGUAGE = "en";
    private final static String HARDCODED_DEVICE_ID = "aDeviceId";

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;
    private static final String TAG = AccountCreationFragment.class.getName();
    private String[] APP_SCOPES = new String[]{"profile"};

    private TextView usernameInput;
    private TextView passwordInput;
    private Button createButton;
    private ImageButton lwaButton;
    private Button termsButton;
    private Button privacyButton;
    private AmazonAuthorizationManager amazonAuthManager;
    private ProgressBar mLogInProgress;
    private ULAuthManager ulAuthManager;
    private ObservableFactory observableFactory = new ObservableFactory();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);

        ulAuthManager = new ULAuthManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.create_account_layout, container, false);

        usernameInput = view.findViewById(R.id.userName);
        passwordInput = view.findViewById(R.id.password);
//      For Testing
//        usernameInput.setText("clf2@sd-i.ca");
//        passwordInput.setText("12345678");

        createButton = view.findViewById(R.id.create_button);
        createButton.setOnClickListener(v -> {
            setLoggingInState(true);
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();

            UserpassCreateRequestBody requestBody = new UserpassCreateRequestBody(username, password, HARDCODED_LANGUAGE, HARDCODED_DEVICE_ID);

            observableFactory.createDetached(new UserpassCreateCallable(requestBody))
                    .subscribe(
                            response -> ulAuthManager.authorize(response.getSessionId(), HARDCODED_LANGUAGE, HARDCODED_DEVICE_ID, new AuthListener()),
                            throwable -> onFailure(getString(R.string.error_during_create_account), throwable)
                    );
        });

        // Setup the listener on the login button.
        lwaButton = view.findViewById(R.id.login_with_amazon2);
        lwaButton.setVisibility(Button.VISIBLE);
        //lwaButton.setOnClickListener(v -> amazonAuthManager.authorize(APP_SCOPES, Bundle.EMPTY, new AuthListener()));

        mLogInProgress = view.findViewById(R.id.progressBar2);

        return view;
    }

    private void showAuthToast(String authToastMessage) {

        Toast authToast = Toast.makeText(getActivity().getBaseContext(), authToastMessage, Toast
                .LENGTH_LONG);
        authToast.setGravity(Gravity.CENTER, 0, 0);
        authToast.show();
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
        createButton.setVisibility(LinearLayout.GONE);
        lwaButton.setVisibility(Button.GONE);
        Preferences.setLoggedInState(
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

    private void setLoggingInState(final boolean loggingIn) {

        if (loggingIn) {
            createButton.setVisibility(Button.GONE);
            mLogInProgress.setVisibility(ProgressBar.VISIBLE);
        }
        else {
            if (!Preferences.getBoolean(PreferencesConstants.IS_LOGGED_IN)) {
                createButton.setVisibility(Button.VISIBLE);
            }
            mLogInProgress.setVisibility(ProgressBar.GONE);
        }
    }

    private class AuthListener implements AuthorizationListener {
        @Override
        public void onSuccess(Bundle response) {
            String authCode = response.getString(AuthzConstants.BUNDLE_KEY.AUTHORIZATION_CODE.val);
            ulAuthManager.getToken(authCode, new TokenListener());
        }

        @Override
        public void onError(final AuthError ae) {
            onFailure(getString(R.string.error_during_auth), ae);
        }
        @Override
        public void onCancel(Bundle cause) {
            onFailure(getString(R.string.error_during_auth), new Throwable("Hit a state that should never happen"));
        }
    }

    private class TokenListener implements APIListener {
        @Override
        public void onSuccess(Bundle response) {
                getActivity().runOnUiThread(() -> setLoggedInState(new UserInfoBundle(response)));
                EventBus.getDefault().post(new AuthenticationStatusUpdateEvent(true));
                getActivity().setResult(RESULT_OK);
                getActivity().finish();
        }
        @Override
        public void onError(AuthError ae) {
            onFailure(getString(R.string.error_during_auth), ae);
        }
    }

    private void onFailure(String message, Throwable throwable) {
        getActivity().runOnUiThread(() -> {
            showAuthToast(message);
            setLoggingInState(false);
            setLoggedOutState();
        });

        EventBus.getDefault().post(new AuthenticationStatusUpdateEvent(false));

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(AuthenticationConstants.ERROR_CATEGORY, AuthenticationConstants.AUTHENTICATION_ERROR_CATEGORY);
        bundle.putSerializable(AuthenticationConstants.ERROR_CAUSE, throwable);
        getActivity().setResult(RESULT_CANCELED, intent.putExtra(AuthenticationConstants.ERROR_BUNDLE, bundle));
        getActivity().finish();
    }
}
