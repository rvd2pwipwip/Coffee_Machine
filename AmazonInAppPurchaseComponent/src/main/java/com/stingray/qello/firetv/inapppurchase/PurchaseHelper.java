/**
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
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
     * Event bus reference.
     */
    private final EventBus mEventBus = EventBus.getDefault();


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

    /**
     * Constructor. Initializes member variables and configures the purchase system.
     *
     * @param context        The context.
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
            mPurchaseManager.init(purchaseSystem, new PurchaseManagerListener() {
                @Override
                public void onRegisterSkusResponse(Response response) {
                    if (response == null || !Response.Status.SUCCESSFUL.equals(response.getStatus())) {
                        setSubscription(false, null);
                        Log.e(TAG, "Register products failed " + response);
                    } else {
                        // If there is a valid receipt available in the system, set content browser
                        // variable as true.
                        String sku = mPurchaseManager.getPurchasedSku();
                        if (sku == null) {
                            setSubscription(false, null);
                        }
                        else {
                            setSubscription(true, sku);
                        }
                        Log.d(TAG, "Register products complete.");
                    }
                }

                @Override
                public void onValidPurchaseResponse(Response response, boolean validity,
                                                    String sku) {

                    Log.e(TAG, "You should not hit here!!!");
                }
            }, skuSet);
        }
        catch (Exception e) {
            Log.e(TAG, "Could not configure the purchase system. ", e);
        }
    }

    /**
     * Sets the subscription data in Preferences.
     */
    private void setSubscription(boolean subscribe, String sku) {
        // Trigger update of userInfo
        mIAuthentication.isUserLoggedIn(mContext, new IAuthentication.ResponseHandler() {
            @Override
            public void onSuccess(Bundle extras) {
                Preferences.setBoolean(PreferencesConstants.CONFIG_PURCHASE_VERIFIED, subscribe);
                if (sku != null) {
                    Preferences.setString(PreferencesConstants.CONFIG_PURCHASED_SKU, sku);
                }
            }

            @Override
            public void onFailure(Bundle extras) {
                String format = "Failed to update subscription state isSubscribed [%s], sku [%s]" +
                        "failed to refresh user info state. Returned bundle [%s].";
                Preferences.setBoolean(PreferencesConstants.GET_USER_INFO_CALL_REQUIRED, true);
                Log.e(TAG, String.format(format, subscribe, sku, extras));
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
    private void handleOnValidPurchaseResponse(Subscriber subscriber,
                                               Response response,
                                               boolean validity,
                                               String sku) {

        Bundle resultBundle = new Bundle();

        if (response != null && Response.Status.SUCCESSFUL.equals(response.getStatus())) {
            PostSubscriptionRequest request = new PostSubscriptionRequest(mPurchaseManager.getReceipt(sku).getReceiptId(), "aDeviceId");
            observableFactory.create(new PostSubscriptionCallable(request))
                    .subscribe(aVoid -> {
                        Log.d(TAG, "purchase succeeded " + response);
                        setSubscription(validity, sku);
                        resultBundle.putString(RESULT_SKU, sku);
                        resultBundle.putBoolean(RESULT_VALIDITY, validity);
                        // TODO Validate
                        //AnalyticsHelper.trackPurchaseResult(sku, validity);
                        handleSuccessCase(subscriber, resultBundle);
                    }, throwable -> {
                        Log.e(TAG, "Failed to complete purchase", throwable);
                        // TODO Validate
                        //AnalyticsHelper.trackError(TAG, "Purchase failed "+response);
                        setSubscription(false, null);
                        resultBundle.putBoolean(RESULT_VALIDITY, false);
                        handleFailureCase(subscriber, resultBundle);
                    });
        }
        else {
            // TODO Validate
            //AnalyticsHelper.trackError(TAG, "Purchase failed "+response);
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
            public void onValidPurchaseResponse(Response response,
                                                boolean validity,
                                                String sku) {

                handleOnValidPurchaseResponse(subscriber,
                                              response,
                                              validity,
                                              sku);
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

    /**
     * Handle purchase chain.
     *
     * @param activity Activity.
     * @param sku      Sku name.
     */
    public void handlePurchaseChain(Activity activity, String sku) {
        triggerProgress(activity);
        purchaseSkuObservable(sku)
                .subscribeOn(Schedulers.newThread()) //this needs to be first make sure
                .observeOn(AndroidSchedulers.mainThread()) //this needs to be last to
                        // make sure rest is running on separate thread.
                .subscribe(resultBundle -> {
                    Log.i(TAG, "isPurchaseValid subscribe called");
                    EventBus.getDefault().post(new ProgressOverlayDismissEvent(true));
                }, throwable -> {
                    EventBus.getDefault().post(new ProgressOverlayDismissEvent(true));
                    Log.e(TAG, "isPurchaseValid onError called", throwable);
                    // TODO Validate
//                    ErrorHelper.injectErrorFragment(activity, ErrorUtils.ERROR_CATEGORY
//                            .NETWORK_ERROR, (errorDialogFragment, errorButtonType, errorCategory)
//                                                            -> {
//                        errorDialogFragment.dismiss();
//                    });
                });
    }

    /**
     * Triggers the progress fragment.
     *
     * @param activity Activity instance.
     */
    private void triggerProgress(Activity activity) {

        ProgressDialogFragment.createAndShow(activity, mContext.getString(R.string.loading));
    }
}