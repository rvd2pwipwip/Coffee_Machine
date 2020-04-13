package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.event.AuthenticationStatusUpdateEvent;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.ui.fragments.FullScreenDialogFragment;
import com.stingray.qello.firetv.android.utils.Preferences;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class SubscribeNowFragment extends FullScreenDialogFragment {
    public static final String TAG = SubscribeNowFragment.class.getSimpleName();

    private Button loginButton;
    private Button startFreeTrialButton;
    private Button cancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subscribe_offer, container);

        loginButton = view.findViewById(R.id.login_button);
        startFreeTrialButton = view.findViewById(R.id.start_free_trial_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        handleViewVisibility();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        loginButton.setOnClickListener(v -> ContentBrowser.getInstance(getActivity())
                .loginActionTriggered(getActivity()));
        startFreeTrialButton.setOnClickListener(v -> ContentBrowser.getInstance(getActivity())
                .switchToScreen(ContentBrowser.PURCHASE_SCREEN, null, null));
        cancelButton.setOnClickListener(v -> removeFromActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void removeFromActivity() {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.remove(this);
        ft.commit();
    }

    private void handleViewVisibility() {
        if (Preferences.getBoolean(PreferencesConstants.IS_LOGGED_IN)) {
            loginButton.setVisibility(View.GONE);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onAuthenticationStatusUpdateEvent(AuthenticationStatusUpdateEvent
                                                          authenticationStatusUpdateEvent) {
        getActivity().runOnUiThread(this::handleViewVisibility);
    }
}
