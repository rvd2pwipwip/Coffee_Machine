package com.stingray.qello.android.firetv.login.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.authorization.api.AuthorizationListener;
import com.amazon.identity.auth.device.shared.APIListener;
import com.stingray.qello.android.firetv.login.R;
import com.stingray.qello.android.firetv.login.ULAuthManager;
import com.stingray.qello.android.firetv.login.UserInfoBundle;
import com.stingray.qello.android.firetv.login.activities.LoginActivity;
import com.stingray.qello.android.firetv.login.communication.UserpassCreateCallable;
import com.stingray.qello.android.firetv.login.communication.requestmodel.UserpassCreateRequestBody;
import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.event.AuthenticationStatusUpdateEvent;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.ui.fragments.RemoteMarkdownFileFragment;
import com.stingray.qello.firetv.android.utils.Helpers;
import com.stingray.qello.firetv.android.utils.Preferences;
import com.stingray.qello.firetv.auth.AuthenticationConstants;
import com.stingray.qello.firetv.utils.UserPreferencesRetriever;

import org.greenrobot.eventbus.EventBus;

import static android.app.Activity.RESULT_CANCELED;

public class AccountCreationFragment extends Fragment {
    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;
    private static final String TAG = AccountCreationFragment.class.getName();

    private TextView usernameInput;
    private TextView passwordInput;
    private Button createButton;
//    private ImageButton lwaButton;
    private View switchToLoginButton;
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

        View backButton = view.findViewById(R.id.nav_back_button);
        backButton.setOnClickListener(v -> getActivity().finishAfterTransition());

        usernameInput = view.findViewById(R.id.userName);
        passwordInput = view.findViewById(R.id.password);

        Activity activity = getActivity();

        String termsTag = getResources().getString(R.string.terms_settings_fragment_tag);
        String termsUrl = getResources().getString(R.string.terms_settings_url);

        String privacyTag = getResources().getString(R.string.privacy_settings_fragment_tag);
        String privacyUrl = getResources().getString(R.string.privacy_settings_url);

        Button termsButton = view.findViewById(R.id.create_account_terms);
        termsButton.setOnClickListener(v -> new RemoteMarkdownFileFragment()
                .createFragment(activity, activity.getFragmentManager(), termsTag, termsUrl));

        Button privacyButton = view.findViewById(R.id.create_account_pp);
        privacyButton.setOnClickListener(v -> new RemoteMarkdownFileFragment()
                .createFragment(activity, activity.getFragmentManager(), privacyTag, privacyUrl));

        String languageCode = UserPreferencesRetriever.getLanguageCode();
        String deviceId = UserPreferencesRetriever.getDeviceId(getActivity());

        createButton = view.findViewById(R.id.create_button);
        createButton.setOnClickListener(v -> {
            setLoggingInState(true);
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();

            UserpassCreateRequestBody requestBody = new UserpassCreateRequestBody(username, password, languageCode, deviceId);

            observableFactory.createDetached(new UserpassCreateCallable(requestBody))
                    .subscribe(
                            response -> ulAuthManager.authorize(response.getSessionId(), languageCode, deviceId, new AuthListener()),
                            throwable -> onFailure(getString(R.string.CreateAccount_GenericError), throwable)
                    );
        });

        // Setup the listener on the login button.
//        lwaButton = view.findViewById(R.id.login_with_amazon2);
//        lwaButton.setVisibility(Button.VISIBLE);
//        lwaButton.setOnClickListener(v -> amazonAuthManager.authorize(APP_SCOPES, Bundle.EMPTY, new AuthListener()));

        // Setup the listener on the login button.
        switchToLoginButton = view.findViewById(R.id.log_in_btn);
        switchToLoginButton.setVisibility(Button.VISIBLE);

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        switchToLoginButton.setOnClickListener(v -> {
            startActivity(intent);
        });

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
//        lwaButton.setVisibility(Button.VISIBLE);
        Preferences.setLoggedOutState();
        EventBus.getDefault().post(new AuthenticationStatusUpdateEvent(false));
    }

    /**
     * Sets the state of the application to reflect that the user is currently authorized.
     */
    private void setLoggedInState(UserInfoBundle userInfoBundle) {
        createButton.setVisibility(LinearLayout.GONE);
//        lwaButton.setVisibility(Button.GONE);
        Preferences.setLoggedInState(
                userInfoBundle.getSessionId(),
                userInfoBundle.getAccessToken(),
                userInfoBundle.getAccessTokenExpiryTimeInMs(),
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
            ulAuthManager.getToken(response, new TokenListener());
        }

        @Override
        public void onError(final AuthError ae) {
            onFailure(getString(R.string.CreateAccount_GenericError), ae);
        }
        @Override
        public void onCancel(Bundle cause) {
            onFailure(getString(R.string.CreateAccount_GenericError), new Throwable("Hit a state that should never happen"));
        }
    }

    private class TokenListener implements APIListener {
        @Override
        public void onSuccess(Bundle response) {
            getActivity().runOnUiThread(() -> setLoggedInState(new UserInfoBundle(response)));
            EventBus.getDefault().post(new AuthenticationStatusUpdateEvent(true));

            Fragment communicationPreferencesFragment = getFragmentManager().findFragmentByTag(CommunicationPreferencesFragment.TAG);

            if (communicationPreferencesFragment == null) {
                communicationPreferencesFragment = new CommunicationPreferencesFragment();
            }

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            ft.replace(R.id.main_account_frame, communicationPreferencesFragment, CommunicationPreferencesFragment.TAG);
            ft.commit();
        }

        @Override
        public void onError(AuthError ae) {
            onFailure(getString(R.string.CreateAccount_GenericError), ae);
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
        getActivity().finishAfterTransition();
    }
}
