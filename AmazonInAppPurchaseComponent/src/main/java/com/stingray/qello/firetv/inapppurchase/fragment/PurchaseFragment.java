package com.stingray.qello.firetv.inapppurchase.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.model.event.ProgressOverlayDismissEvent;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.ui.fragments.ProgressDialogFragment;
import com.stingray.qello.firetv.android.ui.fragments.RemoteMarkdownFileFragment;
import com.stingray.qello.firetv.android.utils.Helpers;
import com.stingray.qello.firetv.android.utils.Preferences;
import com.stingray.qello.firetv.inapppurchase.PurchaseHelper;
import com.stingray.qello.firetv.inapppurchase.R;
import com.stingray.qello.firetv.inapppurchase.communication.GetSubscriptionsCallable;
import com.stingray.qello.firetv.inapppurchase.communication.requestmodel.SubscriptionsResponse;
import com.stingray.qello.firetv.inapppurchase.communication.requestmodel.SvodSubscription;
import com.stingray.qello.firetv.inapppurchase.sku.SkuUIData;
import com.stingray.qello.firetv.inapppurchase.sku.SkuUIDataProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.stingray.qello.firetv.inapppurchase.PurchaseHelper.ERROR_DURING_PURCHASE_FLOW;
import static com.stingray.qello.firetv.inapppurchase.PurchaseHelper.PURCHASE_FLOW_COMPLETED;

public class PurchaseFragment extends Fragment {

    private static final int ACTIVITY_ENTER_TRANSITION_FADE_DURATION = 1500;
    private static final String TAG = PurchaseFragment.class.getName();

    private final ObservableFactory observableFactory = new ObservableFactory();
    private final SkuUIDataProvider skuUIDataProvider = new SkuUIDataProvider();

    private PurchaseHelper purchaseHelper;

    private LinearLayout purchaseItemsLayout;
    private View errorSubscriptionView;
    private View restorePurchaseButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helpers.handleActivityEnterFadeTransition(getActivity(), ACTIVITY_ENTER_TRANSITION_FADE_DURATION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subscription_layout, container, false);

        errorSubscriptionView = view.findViewById(R.id.error_subscriptions_load);
        purchaseItemsLayout = view.findViewById(R.id.purchase_items);
        restorePurchaseButton = view.findViewById(R.id.sub_restore_purchase_btn);

        observableFactory.create(new GetSubscriptionsCallable())
                .subscribe(getSubscriptionLoadAction(inflater), throwable -> {
                    Log.e(TAG, "Failed to load subscriptions. Displaying error message", throwable);
                    errorSubscriptionView.setVisibility(View.VISIBLE);
                });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!verifyAuthorized()) {
            return;
        }

        restorePurchaseButton.setOnClickListener(new RestorePurchaseOnClickListener());

        String termsTag = getResources().getString(R.string.terms_settings_fragment_tag);
        String termsUrl = getResources().getString(R.string.terms_settings_url);

        String privacyTag = getResources().getString(R.string.privacy_settings_fragment_tag);
        String privacyUrl = getResources().getString(R.string.privacy_settings_url);

        Button termsButton = view.findViewById(R.id.terms_button);
        termsButton.setOnClickListener(v -> new RemoteMarkdownFileFragment()
                .createFragment(getActivity(), getActivity().getFragmentManager(), termsTag, termsUrl));

        Button privacyButton = view.findViewById(R.id.privacy_button);
        privacyButton.setOnClickListener(v -> new RemoteMarkdownFileFragment()
                .createFragment(getActivity(), getActivity().getFragmentManager(), privacyTag, privacyUrl));

        Button backButton = view.findViewById(R.id.nav_back_button);
        backButton.setOnClickListener(v -> getActivity().finishAfterTransition());
    }

    private class RestorePurchaseOnClickListener implements  View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!verifyAuthorized()) {
                return;
            }

            String userTrackingId = Preferences.getString(PreferencesConstants.USER_TRACKING_ID);
            String sku = purchaseHelper.getPurchasedSku();

            if (sku != null) {
                purchaseHelper.saveSubscription(true, sku, new PurchaseHelper.Callback() {
                    @Override
                    public void onSuccess(Bundle bundle) {
                        Log.e(TAG, String.format("Succeeded in binding to bind purchase of SKU [%s] to user [%s]", sku, userTrackingId));
                        showAuthToast("Successfully restored your purchase.");
                    }

                    @Override
                    public void onError(Bundle bundle) {
                        Log.e(TAG, String.format("Failed to binding purchase of SKU [%s] to user [%s]", sku, userTrackingId));
                        showAuthToast("Restore purchase failed because the receipt was rejected.");
                    }
                });
            } else {
                showAuthToast("Could not find any valid purchased receipts");
            }
        }
    }

    private Action1<SubscriptionsResponse> getSubscriptionLoadAction(LayoutInflater inflater) {
        return (response) -> {
            errorSubscriptionView.setVisibility(View.GONE);

            List<Map<String, String>> skuSet = new ArrayList<>();

            for (SvodSubscription sub : response.getSubscriptionOffers()) {
                Map<String, String> sku = new HashMap<>();

                sku.put("sku", sub.getProductId());
                sku.put("productType", "SUBSCRIBE");
                sku.put("purchaseSku", sub.getProductId());

                skuSet.add(sku);
            }

            purchaseHelper = new PurchaseHelper(this.getActivity(), skuSet);

            for (SvodSubscription sub : response.getSubscriptionOffers()) {
                try {
                    SkuUIData skuUIData = skuUIDataProvider.getSkuUIData(sub.getProductId());
                    boolean isMonthly = skuUIData.getRecurrence().equalsIgnoreCase("MONTHLY");

                    LinearLayout purchaseItemLayout;
                    if (isMonthly) {
                        View monthlyView = inflater.inflate(R.layout.purchase_item, purchaseItemsLayout, false);
                        purchaseItemLayout = bindMonthlyItem(sub, skuUIData, (LinearLayout) monthlyView);
                    } else {
                        View yearlyView = inflater.inflate(R.layout.purchase_item_rebate, purchaseItemsLayout, false);
                        purchaseItemLayout = bindYearlyItem(sub, skuUIData, (LinearLayout) yearlyView);
                    }

                    purchaseItemLayout.setOnClickListener(new PurchaseItemOnClickListener(sub.getProductId()));
                    purchaseItemsLayout.addView(purchaseItemLayout);
                } catch (SkuUIDataProvider.SkuDataNotFoundException e) {
                    Log.e(TAG, String.format("Failed to display sku [%s]", sub.getProductId()));
                }
            }
        };
    }


    private class PurchaseItemOnClickListener implements View.OnClickListener {
        private final String sku;

        public PurchaseItemOnClickListener(String sku) {
            this.sku = sku;
        }

        @Override
        public void onClick(View v) {
            if (!verifyAuthorized()) {
                return;
            }

            ProgressDialogFragment.createAndShow(getActivity(), getActivity().getString(R.string.loading));
            purchaseHelper.purchaseSkuObservable(sku)
                    .subscribeOn(Schedulers.newThread()) //this needs to be first make sure
                    .observeOn(AndroidSchedulers.mainThread()) //this needs to be last to
                    .subscribe(resultBundle -> {
                        Log.i(TAG, "isPurchaseValid subscribe called");
                        EventBus.getDefault().post(new ProgressOverlayDismissEvent(true));

                        if (resultBundle.getBoolean(PURCHASE_FLOW_COMPLETED)) {
                            showAuthToast("Purchase successfully completed");
                            getActivity().finishAfterTransition();
                        } else if (resultBundle.getBoolean(ERROR_DURING_PURCHASE_FLOW))  {
                            showAuthToast("Failed to complete purchase");
                        } else {
                            // Do nothing
                        }
                    }, throwable -> {
                        EventBus.getDefault().post(new ProgressOverlayDismissEvent(true));
                        showAuthToast("Purchase failed to complete");
                        Log.e(TAG, "isPurchaseValid onError called", throwable);
                    });
        }
    }

    private LinearLayout bindMonthlyItem(SvodSubscription sub, SkuUIData skuUIData, LinearLayout purchaseItemLayout) {
        TextView recurrenceView = purchaseItemLayout.findViewById(R.id.recurrence);
        recurrenceView.setText(sub.getRecurrenceTitle());
        TextView priceTextView = purchaseItemLayout.findViewById(R.id.price_text);
        priceTextView.setText(skuUIData.getPrice());
        TextView commentView = purchaseItemLayout.findViewById(R.id.comment);
        commentView.setText(skuUIData.getCommentView());

        return purchaseItemLayout;
    }

    private LinearLayout bindYearlyItem(SvodSubscription sub, SkuUIData skuUIData, LinearLayout purchaseItemLayout) {
        TextView recurrenceView = purchaseItemLayout.findViewById(R.id.recurrence);
        recurrenceView.setText(sub.getRecurrenceTitle());
        TextView priceTextView = purchaseItemLayout.findViewById(R.id.price_text);
        priceTextView.setText(skuUIData.getPrice());
        TextView originalPrice = purchaseItemLayout.findViewById(R.id.strikethrough_price);
        originalPrice.setText(skuUIData.getOriginalPrice());
        TextView commentView = purchaseItemLayout.findViewById(R.id.comment);
        commentView.setText(skuUIData.getCommentView());
        TextView savingsTitle = purchaseItemLayout.findViewById(R.id.percentage_savings_title);
        savingsTitle.setText(skuUIData.getSavingsTitle());
        TextView savings = purchaseItemLayout.findViewById(R.id.percentage_savings);
        savings.setText(skuUIData.getSavingsPercentage());

        return purchaseItemLayout;
    }

    private boolean verifyAuthorized() {
        boolean isLoggedIn = Preferences.getBoolean(PreferencesConstants.IS_LOGGED_IN);
        if (!isLoggedIn) {
            getActivity().finish();
            showAuthToast("Access to this page is restricted to logged in users.");
        }
        return isLoggedIn;
    }

    private void showAuthToast(String authToastMessage) {
        Toast toast = Toast.makeText(getActivity(), authToastMessage, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
