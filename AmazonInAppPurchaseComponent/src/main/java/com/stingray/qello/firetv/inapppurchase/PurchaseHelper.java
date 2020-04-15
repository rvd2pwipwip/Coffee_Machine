/**
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * A copy of the License is located at
 * <p>
 * http://aws.amazon.com/apache2.0/
 * <p>
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.stingray.qello.firetv.inapppurchase;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.model.event.ProgressOverlayDismissEvent;
import com.stingray.qello.firetv.android.module.ModuleManager;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.ui.fragments.ProgressDialogFragment;
import com.stingray.qello.firetv.android.utils.Preferences;
import com.stingray.qello.firetv.auth.IAuthentication;
import com.stingray.qello.firetv.inapppurchase.communication.PostSubscriptionCallable;
import com.stingray.qello.firetv.inapppurchase.communication.requestmodel.PostSubscriptionRequest;
import com.stingray.qello.firetv.purchase.IPurchase;
import com.stingray.qello.firetv.purchase.PurchaseManager;
import com.stingray.qello.firetv.purchase.PurchaseManagerListener;
import com.stingray.qello.firetv.purchase.model.Response;
import com.stingray.qello.firetv.utils.UserPreferencesRetriever;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Helper class to perform purchase related actions.
 */
public class PurchaseHelper {
    private static final String TAG = PurchaseHelper.class.getName();
    private PurchaseManager mPurchaseManager;
    private IAuthentication mIAuthentication;
    private final Context mContext;
    private final ObservableFactory observableFactory = new ObservableFactory();

    /**
     * Result key.
     */
    public static final String RESULT = "RESULT";

    /**
     * Result SKU key.
     */
    public static final String RESULT_SKU = "RESULT_SKU";

    /**
     * Result validity key.
     */
    public static final String RESULT_VALIDITY = "RESULT_VALIDITY";

    public static final String PURCHASE_FLOW_COMPLETED = "PURCHASE_FLOW_COMPLETED";

    public static final String ERROR_DURING_PURCHASE_FLOW = "ERROR_DURING_PURCHASE_FLOW";

    /**
     * Constructor. Initializes member variables and configures the purchase system.
     *
     * @param context The context.
     */
    public PurchaseHelper(Context context, List<Map<String, String>> skuSet) {
        this.mContext = context;
        initializePurchaseSystem(skuSet);
    }

    /**
     * Method to initialize purchase system
     */
    private void initializePurchaseSystem(List<Map<String, String>> skuSet) {

        // The purchase system should be initialized by the module initializer, if there is no
        // initializer available that means the purchase system is not needed.
        IPurchase purchaseSystem = (IPurchase) ModuleManager.getInstance()
                .getModule(
                        IPurchase.class.getSimpleName())
                .getImpl(true);


        // Get default Auth interface without creating a new one.
        try {
            mIAuthentication = (IAuthentication) ModuleManager.getInstance()
                    .getModule(IAuthentication.class.getSimpleName())
                    .getImpl(true);
        } catch (Exception e) {
            Log.e(TAG, "No Auth Interface interface attached.", e);
        }


        if (purchaseSystem == null) {
            Log.i(TAG, "Purchase system not registered.");
            return;
        }


        // Register the purchase system received via ModuleManager and configure the purchase
        // listener.
        this.mPurchaseManager = PurchaseManager.getInstance(mContext.getApplicationContext());
        try {
            PurchaseManagerListener purchaseManagerListener = new PurchaseManagerListener() {
                @Override
                public void onRegisterSkusResponse(Response response) {
                    if (response == null || !Response.Status.SUCCESSFUL.equals(response.getStatus())) {
                        setSubscription(false, null);
                        Log.e(TAG, "Register products failed " + response);
                    } else {
                        Log.d(TAG, String.format("Register products complete. [%s]", skuSet));
                    }
                }

                @Override
                public void onValidPurchaseResponse(Response response, boolean validity, String sku) {
                    Log.e(TAG, "You should not hit here!!!");
                }
            };

            mPurchaseManager.init(purchaseSystem, purchaseManagerListener, skuSet);
        } catch (Exception e) {
            Log.e(TAG, "Could not configure the purchase system. ", e);
        }
    }

    /**
     * Sets the subscription data in Preferences.
     */
    private void setSubscription(boolean isSubscribed, String sku) {
        // Trigger update of userInfo
        mIAuthentication.isUserLoggedIn(mContext, new IAuthentication.ResponseHandler() {
            @Override
            public void onSuccess(Bundle extras) {
                Preferences.setBoolean(PreferencesConstants.CONFIG_PURCHASE_VERIFIED, isSubscribed);
                if (sku != null) {
                    Preferences.setString(PreferencesConstants.CONFIG_PURCHASED_SKU, sku);
                }
            }

            @Override
            public void onFailure(Bundle extras) {
                String format = "Failed to update subscription state isSubscribed [%s], sku [%s]" +
                        "failed to refresh user info state. Returned bundle [%s].";
                Preferences.setBoolean(PreferencesConstants.GET_USER_INFO_CALL_REQUIRED, true);
                Log.e(TAG, String.format(format, isSubscribed, sku, extras));
            }
        });
    }

    /**
     * Handle success case of subscriber.
     *
     * @param subscriber Subscriber.
     * @param extras     Result bundle.
     */
    private void handleSuccessCase(Subscriber subscriber, Bundle extras) {

        extras.putBoolean(RESULT, true);
        if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(extras);
        }
        subscriber.onCompleted();
    }

    /**
     * Handle failure case of subscriber.
     *
     * @param subscriber Subscriber.
     * @param extras     Result bundle.
     */
    private void handleFailureCase(Subscriber subscriber, Bundle extras) {

        extras.putBoolean(RESULT, false);
        if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(extras);
        }
        subscriber.onCompleted();
    }

    /**
     * Handles the response for a valid purchase.
     *
     * @param subscriber Rx subscriber.
     * @param response   IAP response.
     * @param validity   Validity of the SKU.
     * @param sku        SKU name.
     */
    private void handleOnValidPurchaseResponse(Subscriber subscriber, Response response, boolean validity, String sku) {
        if (response != null && validity && Response.Status.SUCCESSFUL.equals(response.getStatus())) {
            Log.d(TAG, "Purchase succeeded " + response);
            saveSubscription(validity, sku, new Callback() {
                @Override
                public void onSuccess(Bundle bundle) {
                    String userTrackingId = Preferences.getString(PreferencesConstants.USER_TRACKING_ID);
                    Log.e(TAG, String.format("Succeeded in binding to bind purchase of SKU [%s] to user [%s]", sku, userTrackingId));
                    handleSuccessCase(subscriber, bundle);
                }

                @Override
                public void onError(Bundle bundle) {
                    String userTrackingId = Preferences.getString(PreferencesConstants.USER_TRACKING_ID);
                    Log.e(TAG, String.format("Failed to binding purchase of SKU [%s] to user [%s]", sku, userTrackingId));
                    handleFailureCase(subscriber, bundle);
                }
            });
        } else {
            Bundle resultBundle = new Bundle();
            resultBundle.putBoolean(RESULT_VALIDITY, false);
            handleFailureCase(subscriber, resultBundle);
        }
    }

    /**
     * Create observable purchase manager listener.
     *
     * @param subscriber Rx subscriber.
     * @return Purchase manager listener instance.
     */
    private PurchaseManagerListener createObservablePurchaseManagerListener(Subscriber subscriber) {
        return new PurchaseManagerListener() {
            @Override
            public void onRegisterSkusResponse(Response response) {
                Log.e(TAG, "You should not hit here!!!");
            }

            @Override
            public void onValidPurchaseResponse(Response response, boolean validity, String sku) {
                handleOnValidPurchaseResponse(subscriber, response, validity, sku);
            }
        };
    }

    /**
     * Purchase SKU observable.
     *
     * @param sku SKU name.
     * @return Purchase SKU observable result.
     */
    public Observable<Bundle> purchaseSkuObservable(String sku) {

        Log.v(TAG, "purchaseSku called:" + sku);

        return Observable.create(subscriber ->
                mPurchaseManager.purchaseSku(sku, createObservablePurchaseManagerListener(subscriber))
        );
    }

    public String getPurchasedSku() {
        return mPurchaseManager.getPurchasedSku();
    }

    public void saveSubscription(boolean validity, String sku, Callback callback) {
        Bundle resultBundle = new Bundle();

        PostSubscriptionRequest.PurchaseData purchaseData = new PostSubscriptionRequest.PurchaseData(
                mPurchaseManager.getUserData().getUserId(),
                mPurchaseManager.getReceipt(sku).getReceiptId()
        );

        PostSubscriptionRequest request = new PostSubscriptionRequest(purchaseData, UserPreferencesRetriever.getDeviceId(mContext));
        observableFactory.create(new PostSubscriptionCallable(request))
                .subscribe(aVoid -> {
                    setSubscription(validity, sku);
                    resultBundle.putString(RESULT_SKU, sku);
                    resultBundle.putBoolean(RESULT_VALIDITY, validity);
                    resultBundle.putBoolean(PURCHASE_FLOW_COMPLETED, true);
                    callback.onSuccess(resultBundle);
                }, throwable -> {
                    String userTrackingId = Preferences.getString(PreferencesConstants.USER_TRACKING_ID);
                    Log.e(TAG, String.format("Failed to bind purchase of SKU [%s] to user [%s]", sku, userTrackingId), throwable);
                    setSubscription(false, null);
                    resultBundle.putBoolean(RESULT_VALIDITY, false);
                    resultBundle.putBoolean(PURCHASE_FLOW_COMPLETED, false);
                    resultBundle.putBoolean(ERROR_DURING_PURCHASE_FLOW, true);
                    callback.onError(resultBundle);
                });
    }

    public interface Callback {
        void onSuccess(Bundle bundle);

        void onError(Bundle bundle);
    }
}