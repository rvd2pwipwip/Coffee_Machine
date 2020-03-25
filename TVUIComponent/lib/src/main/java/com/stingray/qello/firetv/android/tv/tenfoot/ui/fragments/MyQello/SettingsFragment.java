package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.contentbrowser.helper.AuthHelper;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.ui.fragments.RemoteMarkdownFileFragment;
import com.stingray.qello.firetv.android.utils.Helpers;
import com.stingray.qello.firetv.android.utils.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SettingsFragment extends Fragment {

    private final String TAG = SettingsFragment.class.getSimpleName();

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;

    private Button loginButton;
    private Button logoutButton;
    private Button startFreeTrialButton;
    private final EventBus mEventBus = EventBus.getDefault();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus.register(this);
        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_layout, container, false);

        loginButton = view.findViewById((R.id.login_button));
        logoutButton = view.findViewById((R.id.logout_button));
        startFreeTrialButton = view.findViewById((R.id.free_trial_button));

        toggleLoginLogout(Preferences.getBoolean(PreferencesConstants.IS_LOGGED_IN));

        addListenerOnButton(view);
        return view;
    }

    public void addListenerOnButton(View view) {
        Button faqButton = (Button) view.findViewById(R.id.faq_button);
        faqButton.setOnClickListener(v -> new RemoteMarkdownFileFragment()
                .createFragment(getActivity(), getActivity().getFragmentManager(), getActivity().getString(com.stingray.qello.firetv.utils.R.string.faq_settings_fragment_tag), "https://legal.stingray.com/en/qello-faq/markdown"));


        Button contactUsButton = (Button) view.findViewById(R.id.contact_us_button);
        contactUsButton.setOnClickListener(v -> new ContactUsSettingsDialog()
                .createFragment(getActivity(), getActivity().getFragmentManager()));

        Button aboutButton = (Button) view.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(v -> new AboutSettingsDialog()
                .createFragment(getActivity(), getActivity().getFragmentManager()));

        loginButton.setOnClickListener(v -> ContentBrowser.getInstance(getActivity()).loginActionTriggered(getActivity()));
        logoutButton.setOnClickListener(v -> ContentBrowser.getInstance(getActivity()).logoutActionTriggered());

        startFreeTrialButton.setOnClickListener(v -> ContentBrowser.getInstance(getActivity())
                .switchToScreen(ContentBrowser.ACCOUNT_CREATION_SCREEN, null,
                        null)
        );
    }

    /**
     * Listener method to listen for authentication updates, it sets the status of
     * loginLogoutAction action used by the browse activities
     *
     * @param authenticationStatusUpdateEvent Event for update in authentication status.
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onAuthenticationStatusUpdateEvent(AuthHelper.AuthenticationStatusUpdateEvent
                                                          authenticationStatusUpdateEvent) {
        if (loginButton != null && logoutButton != null) {
            toggleLoginLogout(authenticationStatusUpdateEvent.isUserAuthenticated());
        }
    }

    private void toggleLoginLogout(boolean isLoggedIn) {
        if (isLoggedIn) {
            startFreeTrialButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
        } else {
            startFreeTrialButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
    }

}