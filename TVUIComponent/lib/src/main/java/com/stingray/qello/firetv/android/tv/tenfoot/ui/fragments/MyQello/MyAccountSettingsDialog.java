package com.stingray.qello.firetv.android.tv.tenfoot.ui.fragments.MyQello;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
import com.stingray.qello.firetv.android.tv.tenfoot.R;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.ui.fragments.AppInfoDialog;
import com.stingray.qello.firetv.android.ui.interfaces.SingleViewProvider;
import com.stingray.qello.firetv.android.utils.Preferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MyAccountSettingsDialog extends AppInfoDialog {

    private static final String TAG = MyAccountSettingsDialog.class.getSimpleName();

    public void createFragment(final Activity activity, FragmentManager manager) {
        super.createFragment(activity, manager,"MyAccountSettingsFragment");
    }

    protected SingleViewProvider getSingleViewProvider(Context context) {
        boolean isLoggedIn = Preferences.getBoolean(PreferencesConstants.IS_LOGGED_IN);
        boolean hasSubscription = Preferences.getBoolean(PreferencesConstants.HAS_SUBSCRIPTION);

        SingleViewProvider singleViewProvider;

        if (isLoggedIn) {
            String email = Preferences.getString(PreferencesConstants.STINGRAY_EMAIL);

            if (hasSubscription) {
                // TODO Get the price and recurrence
                // TODO Format date
                String subscriptionEnd = Preferences.getString(PreferencesConstants.SUBSCRIPTION_END_DATE);
                String formattedSubscriptionEnd = subscriptionEnd;
                String subscriptionPrice = "9.99$";
                String subscriptionRecurrence = "MONTHLY";

                singleViewProvider = (context1, inflater, parent) -> {
                    final View view = mActivity.getLayoutInflater().inflate(R.layout.my_account_layout, parent);

                    TextView emailView = view.findViewById(R.id.my_account_email);
                    emailView.setText(email);

                    TextView subEndView = view.findViewById(R.id.my_account_sub_end);
                    subEndView.setText(formattedSubscriptionEnd);
                    TextView subRecurrenceView = view.findViewById(R.id.my_account_sub_rec);
                    subRecurrenceView.setText(subscriptionRecurrence);
                    TextView subPriceView = view.findViewById(R.id.my_account_sub_price);
                    subPriceView.setText(subscriptionPrice);

                    return view;
                };
            } else {
                singleViewProvider = (context1, inflater, parent) -> {
                    final View view = mActivity.getLayoutInflater().inflate(R.layout.my_account_layout_logged_nosub, parent);
                    TextView emailView = view.findViewById(R.id.my_account_email);
                    emailView.setText(email);

                    Button freeTrialButton = view.findViewById(R.id.free_trial_button);
                    freeTrialButton.setOnClickListener(v -> ContentBrowser.getInstance(mActivity)
                            .switchToScreen(ContentBrowser.PURCHASE_SCREEN, null, null));

                    return view;
                };
            }
        } else {
            singleViewProvider = (context1, inflater, parent) -> {
                final View view = mActivity.getLayoutInflater().inflate(R.layout.my_account_layout_guest, parent);

                Button freeTrialButton = view.findViewById(R.id.free_trial_button);
                freeTrialButton.setOnClickListener(v -> ContentBrowser.getInstance(mActivity)
                        .switchToScreen(ContentBrowser.ACCOUNT_CREATION_SCREEN, null, null));

                return view;
            };
        }

        return singleViewProvider;
    }

}
