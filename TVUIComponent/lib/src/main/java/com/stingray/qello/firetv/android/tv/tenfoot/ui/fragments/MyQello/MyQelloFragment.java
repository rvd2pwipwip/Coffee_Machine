package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v17.leanback.widget.ImageCardView;
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

import java.util.ArrayList;
import java.util.List;

public class MyQelloFragment extends Fragment{

    private final String TAG = MyQelloFragment.class.getSimpleName();

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;

    private final EventBus mEventBus = EventBus.getDefault();

    private LinearLayout userBtnContainer;
    private LinearLayout loggedOutContainer;
    private Button loginButton;
    private Button startFreeTrialButton;
    private Button settingsButton;
    private Button historyButton;
    private Button favoritesButton;
    private View detailsView;
    private View parentView;

    private View memoryView = null;
    private List<View> memorableViews = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBus.register(this);
        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView =  inflater.inflate(R.layout.my_qello_layout, container, false);

        userBtnContainer = parentView.findViewById(R.id.user_btn_container);
        loggedOutContainer = parentView.findViewById(R.id.logged_out_container);

        loginButton = parentView.findViewById((R.id.my_qello_login_button));
        startFreeTrialButton = parentView.findViewById((R.id.my_qello_free_trial_button));
        settingsButton = parentView.findViewById(R.id.my_qello_settings_button);
        historyButton = parentView.findViewById(R.id.my_qello_history_button);
        favoritesButton = parentView.findViewById(R.id.my_qello_favorites_button);
        detailsView = parentView.findViewById(R.id.my_qello_detail);

        toggleAuthenticationViews(Preferences.getBoolean(PreferencesConstants.IS_LOGGED_IN));

        memorableViews.add(settingsButton);
        memorableViews.add(historyButton);
        memorableViews.add(favoritesButton);

        return parentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        addListeners(view);

        view.getViewTreeObserver().addOnGlobalFocusChangeListener((oldFocus, newFocus) -> {
            Integer oldFocusId = (oldFocus != null) ? oldFocus.getId() : null;
            boolean isNewInParent = parentView.findViewById(newFocus.getId()) != null;
            boolean isOldInParent = oldFocusId != null && parentView.findViewById(oldFocusId) != null;
            if (isOldInParent || oldFocus instanceof ImageCardView) {
                boolean isNewInDetails = detailsView.findViewById(newFocus.getId()) != null || newFocus instanceof ImageCardView;
                boolean isOldInDetails = detailsView.findViewById(oldFocus.getId()) != null || oldFocus instanceof ImageCardView;

                boolean isOldMemorable = memorableViews.contains(oldFocus);

                boolean leavingDetails = !isNewInDetails && isOldInDetails;
                boolean enteringDetails = !isOldInDetails && isNewInDetails;

                if (enteringDetails) {
                    if (isOldMemorable) {
                        memoryView = oldFocus;
                    } else {
                        settingsButton.requestFocus();
                    }
                }

                if (memoryView != null) {
                    memoryView.setBackground(getResources().getDrawable(R.drawable.button_bg_stroke));

                    if (leavingDetails) {
                        memoryView.requestFocus();
                        memoryView = null;
                    }
                }
            } else if (isNewInParent) {
                settingsButton.requestFocus();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void addListeners(View view)  {
        settingsButton.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                // TODO Fix me - Leo - Replace doesn't work if settings fragment was the last thing loaded on page switch
                Fragment settingsFragment = new SettingsFragment();
//                Fragment settingsFragment = getFragmentManager().findFragmentByTag(SettingsFragment.class.getSimpleName());
//
//                if (settingsFragment == null) {
//                    settingsFragment = new SettingsFragment();
//                }

                loadToDetails(settingsFragment, HistoryFragment.class.getSimpleName());
            }
        });

        historyButton.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                Fragment historyFragment = getFragmentManager().findFragmentByTag(HistoryFragment.class.getSimpleName());
                if(historyFragment == null) {
                    historyFragment = new HistoryFragment();
                }

                loadToDetails(historyFragment, HistoryFragment.class.getSimpleName());
            }
        });

        favoritesButton.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                Fragment favoritesFragment = getFragmentManager().findFragmentByTag(FavoritesFragment.class.getSimpleName());
                if(favoritesFragment == null) {
                    favoritesFragment = new FavoritesFragment();
                }

                loadToDetails(favoritesFragment, FavoritesFragment.class.getSimpleName());
            }
        });

        startFreeTrialButton.setOnClickListener(v -> ContentBrowser.getInstance(getActivity())
                .switchToScreen(ContentBrowser.ACCOUNT_CREATION_SCREEN, null, null));

        loginButton.setOnClickListener(v -> ContentBrowser.getInstance(getActivity()).loginActionTriggered(getActivity()));
    }

    private void loadToDetails(Fragment fragment, String TAG) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.my_qello_detail, fragment, TAG);
        fragmentTransaction.commit();
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