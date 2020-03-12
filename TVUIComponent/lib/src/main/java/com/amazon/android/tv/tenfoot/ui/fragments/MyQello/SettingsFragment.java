package com.amazon.android.tv.tenfoot.ui.fragments.MyQello;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.amazon.android.contentbrowser.ContentBrowser;
import com.amazon.android.contentbrowser.helper.AuthHelper;
import com.amazon.android.tv.tenfoot.R;
import com.amazon.android.ui.fragments.ContactUsSettingsFragment;
import com.amazon.android.ui.fragments.FAQSettingsFragment;
import com.amazon.android.ui.fragments.LogoutSettingsFragment;
import com.amazon.android.utils.Helpers;
import com.amazon.android.utils.Preferences;
import com.amazon.auth.AuthenticationConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SettingsFragment extends Fragment {

    private final String TAG = SettingsFragment.class.getSimpleName();

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;

    private Button loginLogOutButton;
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
        View view =  inflater.inflate(R.layout.settings_layout, container, false);

        loginLogOutButton = (Button) view.findViewById((R.id.login_logout_button));
        startFreeTrialButton = (Button) view.findViewById((R.id.free_trial_button));

        String text = "Login";
        if (Preferences.getBoolean("isLoggedIn")) {
            text = "Logout";
        }
        loginLogOutButton.setText(text);

        addListenerOnButton(view);
        return view;
    }

    public void addListenerOnButton(View view)  {
        Button faqButton = (Button) view.findViewById(R.id.faq_button);
        faqButton.setOnClickListener(v -> new FAQSettingsFragment()
                .createFragment(getActivity(), getActivity().getFragmentManager()));

        Button contactUsButton = (Button) view.findViewById(R.id.contact_us_button);
        contactUsButton.setOnClickListener(v -> new ContactUsSettingsFragment()
                .createFragment(getActivity(), getActivity().getFragmentManager()));

        //TODO fix
        Button aboutButton = (Button) view.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(v -> new ContactUsSettingsFragment()
                .createFragment(getActivity(), getActivity().getFragmentManager()));

        loginLogOutButton.setOnClickListener(v -> ContentBrowser.getInstance(getActivity()).loginLogoutActionTriggered(getActivity()));

    }

    /**
     * Listener method to listen for authentication updates, it sets the status of
     * loginLogoutAction action used by the browse activities
     *
     * @param authenticationStatusUpdateEvent Event for update in authentication status.
     */
    @Subscribe
    public void onAuthenticationStatusUpdateEvent(AuthHelper.AuthenticationStatusUpdateEvent
                                                          authenticationStatusUpdateEvent) {

        if (loginLogOutButton != null) {
            String text = authenticationStatusUpdateEvent.isUserAuthenticated() ?
                    "Logout" :
                    "Login";
            loginLogOutButton.setText(text);
        }


    }

}