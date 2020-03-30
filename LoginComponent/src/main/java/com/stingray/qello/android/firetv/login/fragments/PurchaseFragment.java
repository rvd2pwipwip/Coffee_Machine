package com.stingray.qello.android.firetv.login.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stingray.qello.android.firetv.login.R;
import com.stingray.qello.android.firetv.login.communication.model.SvodSubscription;
import com.stingray.qello.firetv.android.utils.Helpers;

import java.util.Arrays;
import java.util.List;

public class PurchaseFragment extends Fragment {

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;
    private static final String TAG = PurchaseFragment.class.getName();

    private LinearLayout purchaseItemsLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.subscription_layout, container, false);

        purchaseItemsLayout = view.findViewById(R.id.purchase_items);

        // TODO Make call to AVC for skus
        List<SvodSubscription> svodSubscriptionList = Arrays.asList(
                new SvodSubscription("sc.rec.monthly.sub.3", true, "MONTHLY", "Monthly", false),
                new SvodSubscription("sc.rec.yearly.sub.1", false, "YEARLY", "Yearly", true)
        );

        for (SvodSubscription sub: svodSubscriptionList) {
            boolean isMonthly = sub.getRecurrence().equalsIgnoreCase("MONTHLY");

            // TODO Use in app purchase interface to get sku info
            String price = (isMonthly) ? "11.99" : "99.99";
            String comment = (isMonthly) ? "per month after trial" : "(No free trial)";

            // TODO Toggle layout based on recurrence
            LinearLayout purchaseItemLayout = (LinearLayout) inflater.inflate(R.layout.purchase_item, purchaseItemsLayout, false);

            TextView recurrenceView = purchaseItemLayout.findViewById(R.id.recurrence);
            recurrenceView.setText(sub.getRecurrenceTitle());
            TextView priceTextView = purchaseItemLayout.findViewById(R.id.price_text);
            priceTextView.setText(price);
            TextView commentView = purchaseItemLayout.findViewById(R.id.comment);
            commentView.setText(comment);

            purchaseItemsLayout.addView(purchaseItemLayout);
        }

        return view;
    }

    private void showAuthToast(String authToastMessage) {

        Toast authToast = Toast.makeText(getActivity().getBaseContext(), authToastMessage, Toast
                .LENGTH_LONG);
        authToast.setGravity(Gravity.CENTER, 0, 0);
        authToast.show();
    }

}
