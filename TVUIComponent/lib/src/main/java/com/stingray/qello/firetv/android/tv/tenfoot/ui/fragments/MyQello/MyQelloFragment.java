package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.event.AuthenticationStatusUpdateEvent;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.utils.Helpers;
import com.stingray.qello.firetv.android.utils.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MyQelloFragment extends Fragment{

    private final String TAG = MyQelloFragment.class.getSimpleName();

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;

    private final EventBus mEventBus = EventBus.getDefault();

    private LinearLayout userBtnContainer;
    private LinearLayout loggedOutContainer;
    private Button loginButton;
    private Button startFreeTrialButton;
    private Button settingsButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus.register(this);
        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.my_qello_layout, container, false);

        userBtnContainer = view.findViewById(R.id.user_btn_container);
        loggedOutContainer = view.findViewById(R.id.logged_out_container);

        loginButton = view.findViewById((R.id.login_button));
        startFreeTrialButton = view.findViewById((R.id.free_trial_button));

        toggleAuthenticationViews(Preferences.getBoolean(PreferencesConstants.IS_LOGGED_IN));

        addListenerOnButton(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void addListenerOnButton(View view)  {
        settingsButton = view.findViewById(R.id.settings_button);
        settingsButton.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // TODO Fix me - Leo - Replace doesn't work if settings fragment was the last thing loaded on page switch
                Fragment settingsFragment = new SettingsFragment();
//                Fragment settingsFragment = fragmentManager.findFragmentByTag(SettingsFragment.class.getSimpleName());
//
//                if (settingsFragment == null) {
//                    settingsFragment = new SettingsFragment();
//                }

                fragmentTransaction.replace(R.id.my_qello_detail, settingsFragment, SettingsFragment.class.getSimpleName());
                fragmentTransaction.commit();
            }
        });

        Button historyButton = (Button) view.findViewById(R.id.history_button);
        historyButton.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Fragment historyFragment = fragmentManager.findFragmentByTag(HistoryFragment.class.getSimpleName());
                if(historyFragment == null) {
                    historyFragment = new HistoryFragment();
                }

                fragmentTransaction.replace(R.id.my_qello_detail, historyFragment, HistoryFragment.class.getSimpleName());
                fragmentTransaction.commit();
            }
        });

        Button favoritesButton = (Button) view.findViewById(R.id.favorites_button);
        favoritesButton.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                FragmentManager fragmentManager = this.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Fragment favoritesFragment = fragmentManager.findFragmentByTag(FavoritesFragment.class.getSimpleName());
                if(favoritesFragment == null) {
                    favoritesFragment = new FavoritesFragment();
                }

                fragmentTransaction.replace(R.id.my_qello_detail, favoritesFragment, FavoritesFragment.class.getSimpleName());
                fragmentTransaction.commit();
            }
        });

        startFreeTrialButton.setOnClickListener(v -> ContentBrowser.getInstance(getActivity())
                .switchToScreen(ContentBrowser.ACCOUNT_CREATION_SCREEN, null, null));

        loginButton.setOnClickListener(v -> ContentBrowser.getInstance(getActivity()).loginActionTriggered(getActivity()));
    }

    /**
     * Listener method to listen for authentication updates, it sets the status of
     * loginLogoutAction action used by the browse activities
     *
     * @param authenticationStatusUpdateEvent Event for update in authentication status.
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onAuthenticationStatusUpdateEvent(AuthenticationStatusUpdateEvent authenticationStatusUpdateEvent) {
        getActivity().runOnUiThread(() -> {
            toggleAuthenticationViews(Preferences.getBoolean(PreferencesConstants.IS_LOGGED_IN));
        });
    }

    private void toggleAuthenticationViews(boolean isLoggedIn) {
        if (loggedOutContainer != null) {
            if (settingsButton != null && loggedOutContainer.getVisibility() == View.GONE && !isLoggedIn) {
                settingsButton.requestFocus();
            }
            loggedOutContainer.setVisibility(mapToVisibility(!isLoggedIn));
        }

        if (userBtnContainer != null) {
            userBtnContainer.setVisibility(mapToVisibility(isLoggedIn));
        }
    }

    private int mapToVisibility(boolean isVisible) {
        if (isVisible) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }
}