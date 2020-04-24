package com.stingray.qello.android.firetv.login.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.stingray.qello.android.firetv.login.R;
import com.stingray.qello.android.firetv.login.communication.CommunicationPreferencesCallable;
import com.stingray.qello.android.firetv.login.communication.requestmodel.CommunicationPreferencesRequestBody;
import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.ui.fragments.ContactUsSettingsDialog;
import com.stingray.qello.firetv.android.utils.Helpers;
import com.stingray.qello.firetv.android.utils.Preferences;
import com.stingray.qello.firetv.inapppurchase.activities.PurchaseActivity;
import com.stingray.qello.firetv.utils.UserPreferencesRetriever;

public class CommunicationPreferencesFragment extends Fragment {
    public static final String TAG = CommunicationPreferencesFragment.class.getName();

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;

    private Button communicationAccept;
    private Button communicationDecline;
    private Button communicationContactUs;

    private ObservableFactory observableFactory = new ObservableFactory();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.communication_pref_layout, container, false);

        communicationAccept = view.findViewById(R.id.communication_accept);
        communicationDecline = view.findViewById(R.id.communication_decline);
        communicationContactUs = view.findViewById(R.id.communication_contact_us);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String sessionId = Preferences.getString(PreferencesConstants.SESSION_ID);
        String languageCode = UserPreferencesRetriever.getLanguageCode();
        String deviceId = UserPreferencesRetriever.getDeviceId(getActivity());

        communicationAccept.setOnClickListener(v -> {
            performCommunicationsCall(sessionId, true, deviceId, languageCode);
        });

        communicationDecline.setOnClickListener(v -> {
            performCommunicationsCall(sessionId, false, deviceId, languageCode);
        });

        communicationContactUs.setOnClickListener(v -> new ContactUsSettingsDialog()
                .createFragment(getActivity(), getActivity().getFragmentManager()));
    }

    private void performCommunicationsCall(String sessionId, boolean isOptIn, String deviceId, String language) {
        CommunicationPreferencesRequestBody request = new CommunicationPreferencesRequestBody(
                sessionId, deviceId, isOptIn, language
        );

        observableFactory.create(new CommunicationPreferencesCallable(request))
                .subscribe(voidObj -> {
                    Intent intent = new Intent(getActivity(), PurchaseActivity.class);
                    startActivity(intent);
                }, throwable -> {
                    Log.e(TAG, "Failed to update communication preferences", throwable);
                    Toast authToast = Toast.makeText(getActivity().getBaseContext(),
                            "Unable to update communication preferences", Toast.LENGTH_LONG);
                    authToast.setGravity(Gravity.CENTER, 0, 0);
                    authToast.show();

                    Intent intent = new Intent(getActivity(), PurchaseActivity.class);
                    startActivity(intent);
                });
    }
}
