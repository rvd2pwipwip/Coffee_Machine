package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.ui.fragments.AppInfoDialog;
import com.stingray.qello.firetv.android.ui.interfaces.SingleViewProvider;
import com.stingray.qello.firetv.android.utils.Preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyAccountSettingsDialog extends AppInfoDialog {

    private static final String TAG = MyAccountSettingsDialog.class.getSimpleName();

    private final SimpleDateFormat ulDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final SimpleDateFormat printDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public void createFragment(final Activity activity, FragmentManager manager) {
        super.createFragment(activity, manager, "MyAccountSettingsFragment");
    }

    protected SingleViewProvider getSingleViewProvider(Context context) {
        boolean isLoggedIn = Preferences.getBoolean(PreferencesConstants.IS_LOGGED_IN);
        boolean hasSubscription = Preferences.getBoolean(PreferencesConstants.HAS_SUBSCRIPTION);

        SingleViewProvider singleViewProvider;

        if (isLoggedIn) {
            String email = Preferences.getString(PreferencesConstants.STINGRAY_EMAIL);
            if (hasSubscription) {
                singleViewProvider = getLoggedInWithSubViewProvider(email);
            } else {
                singleViewProvider = getLoggedInWithoutSubViewProvider(email);
            }
        } else {
            singleViewProvider = getLoggedOutViewProvider();
        }

        return singleViewProvider;
    }

    private SingleViewProvider getLoggedInWithoutSubViewProvider(String email) {
        return (context1, inflater, parent) -> {
            final View view = mActivity.getLayoutInflater().inflate(R.layout.my_account_layout_logged_nosub, parent);
            TextView emailView = view.findViewById(R.id.my_account_email);
            emailView.setText(email);

            Button freeTrialButton = view.findViewById(R.id.free_trial_button);
            freeTrialButton.setOnClickListener(v -> ContentBrowser.getInstance(mActivity)
                    .switchToScreen(ContentBrowser.PURCHASE_SCREEN, null, null));

            return view;
        };
    }

    private SingleViewProvider getLoggedInWithSubViewProvider(String email) {
        String subscriptionEnd = Preferences.getString(PreferencesConstants.SUBSCRIPTION_END_DATE);
        String formattedSubscriptionEndDate = null;

        if (subscriptionEnd != null && !subscriptionEnd.isEmpty()) {
            try {
                Date date = ulDateFormat.parse(subscriptionEnd);
                formattedSubscriptionEndDate = printDateFormat.format(date);
            } catch (ParseException e) {
                Log.e(TAG, "Failed to parse subscription end date", e);
            }
        }

        final String endDateText = (formattedSubscriptionEndDate != null)
                ? String.format(mActivity.getResources().getString(R.string.Account_PlanEnd), formattedSubscriptionEndDate)
                : null;

        return (context1, inflater, parent) -> {
            final View view = mActivity.getLayoutInflater().inflate(R.layout.my_account_layout, parent);

            TextView emailView = view.findViewById(R.id.my_account_email);
            emailView.setText(email);

            TextView subEndViewTextView = view.findViewById(R.id.my_account_subscription_end_date);
            if (endDateText != null) {
                subEndViewTextView.setText(endDateText);
                subEndViewTextView.setVisibility(View.VISIBLE);
            } else {
                subEndViewTextView.setVisibility(View.GONE);
            }

            return view;
        };
    }

    private SingleViewProvider getLoggedOutViewProvider() {
        return (context1, inflater, parent) -> {
            final View view = mActivity.getLayoutInflater().inflate(R.layout.my_account_layout_guest, parent);

            Button freeTrialButton = view.findViewById(R.id.free_trial_button);
            freeTrialButton.setOnClickListener(v -> ContentBrowser.getInstance(mActivity)
                    .switchToScreen(ContentBrowser.ACCOUNT_CREATION_SCREEN, null, null));

            return view;
        };
    }
}
