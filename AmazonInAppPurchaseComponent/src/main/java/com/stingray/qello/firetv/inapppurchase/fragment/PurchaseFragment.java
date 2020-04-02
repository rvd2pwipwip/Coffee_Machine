package com.stingray.qello.firetv.inapppurchase.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.utils.Helpers;
import com.stingray.qello.firetv.inapppurchase.R;
import com.stingray.qello.firetv.inapppurchase.communication.SubscriptionsCallable;
import com.stingray.qello.firetv.inapppurchase.communication.SvodSubscription;

import java.util.Arrays;
import java.util.List;

public class PurchaseFragment extends Fragment {

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;
    private static final String TAG = PurchaseFragment.class.getName();

    private final ObservableFactory observableFactory = new ObservableFactory();

    private LinearLayout purchaseItemsLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subscription_layout, container, false);

        View errorSubscriptionView = view.findViewById(R.id.error_subscriptions_load);
        purchaseItemsLayout = view.findViewById(R.id.purchase_items);

        observableFactory.create(new SubscriptionsCallable())
                .subscribe(response -> {
                    errorSubscriptionView.setVisibility(View.GONE);

                    for (SvodSubscription sub : response.getSubscriptionOffers()) {
                        boolean isMonthly = sub.getRecurrence().equalsIgnoreCase("MONTHLY");
                        LinearLayout purchaseItemLayout;
                        if (isMonthly) {
                            // TODO Use in app purchase interface to get sku info
                            purchaseItemLayout = (LinearLayout) inflater.inflate(R.layout.purchase_item, purchaseItemsLayout, false);

                            TextView recurrenceView = purchaseItemLayout.findViewById(R.id.recurrence);
                            recurrenceView.setText(sub.getRecurrenceTitle());
                            TextView priceTextView = purchaseItemLayout.findViewById(R.id.price_text);
                            priceTextView.setText("11.99");
                            TextView commentView = purchaseItemLayout.findViewById(R.id.comment);
                            commentView.setText("per month after trial");
                        } else {
                            purchaseItemLayout = (LinearLayout) inflater.inflate(R.layout.purchase_item_rebate, purchaseItemsLayout, false);

                            TextView recurrenceView = purchaseItemLayout.findViewById(R.id.recurrence);
                            recurrenceView.setText(sub.getRecurrenceTitle());
                            TextView priceTextView = purchaseItemLayout.findViewById(R.id.price_text);
                            priceTextView.setText("99.99$");
                            TextView originalPrice = purchaseItemLayout.findViewById(R.id.strikethrough_price);
                            originalPrice.setText("143.88$");
                            TextView commentView = purchaseItemLayout.findViewById(R.id.comment);
                            commentView.setText("No free trial");
                            TextView savingsTitle = purchaseItemLayout.findViewById(R.id.percentage_savings_title);
                            savingsTitle.setText("Save");
                            TextView savings = purchaseItemLayout.findViewById(R.id.percentage_savings);
                            savings.setText("30%");
                        }
                        purchaseItemsLayout.addView(purchaseItemLayout);
                    }
                }, throwable -> {
                    Log.e(TAG, "Failed to load subscriptions. Displaying error message", throwable);
                    errorSubscriptionView.setVisibility(View.VISIBLE);
                });

        return view;
    }

    private void showAuthToast(String authToastMessage) {

        Toast authToast = Toast.makeText(getActivity().getBaseContext(), authToastMessage, Toast
                .LENGTH_LONG);
        authToast.setGravity(Gravity.CENTER, 0, 0);
        authToast.show();
    }

}