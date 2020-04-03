///**
// * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * A copy of the License is located at
// *
// *     http://aws.amazon.com/apache2.0/
// *
// * or in the "license" file accompanying this file. This file is distributed
// * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
// * express or implied. See the License for the specific language governing
// * permissions and limitations under the License.
// */
//package com.stingray.qello.firetv.android.contentbrowser.helper;
//
//import com.stingray.qello.firetv.android.contentbrowser.ContentBrowser;
//import com.stingray.qello.firetv.android.contentbrowser.R;
//import com.stingray.qello.firetv.android.event.PurchaseUpdateEvent;
//import com.stingray.qello.firetv.android.model.content.Content;
//import com.stingray.qello.firetv.android.model.event.ProgressOverlayDismissEvent;
//import com.stingray.qello.firetv.android.module.ModuleManager;
//import com.stingray.qello.firetv.android.recipe.Recipe;
//import com.stingray.qello.firetv.android.ui.fragments.ProgressDialogFragment;
//import com.stingray.qello.firetv.android.utils.ErrorUtils;
//import com.stingray.qello.firetv.android.utils.FileHelper;
//import com.stingray.qello.firetv.android.utils.Preferences;
//import com.stingray.qello.firetv.purchase.IPurchase;
//import com.stingray.qello.firetv.purchase.PurchaseManager;
//import com.stingray.qello.firetv.purchase.PurchaseManagerListener;
//import com.stingray.qello.firetv.purchase.PurchaseUtils;
//import com.stingray.qello.firetv.purchase.model.Response;
//
//import org.greenrobot.eventbus.EventBus;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Bundle;
//import android.util.Log;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import rx.Observable;
//import rx.Subscriber;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
///**
// * Helper class to perform purchase related actions.
// */
//public class PurchaseHelper {
//
//
//    public static final String CONFIG_PURCHASE_VERIFIED = "CONFIG_PURCHASE_VERIFIED";
//    public static final String CONFIG_PURCHASED_SKU = "CONFIG_PURCHASED_SKU";
//
//    private static final String TAG = PurchaseHelper.class.getName();
//    private PurchaseUtils purchaseUtils = new PurchaseUtils();
//
//    public static final String ACTIONS = "actions";
//    private final ContentBrowser mContentBrowser;
//    private PurchaseManager mPurchaseManager;
//    private final Context mContext;
//    private final Map<String, String> mActionsMap;
//    private String mDailyPassSKU;
//    private String mSubscriptionSKU;
//    /**
//     * Event bus reference.
//     */
//    private final EventBus mEventBus = EventBus.getDefault();
//
//
//    /**
//     * Result key.
//     */
//    public static final String RESULT = "RESULT";
//
//    /**
//     * Result SKU key.
//     */
//    public static final String RESULT_SKU = "RESULT_SKU";
//
//    /**
//     * Result validity key.
//     */
//    public static final String RESULT_VALIDITY = "RESULT_VALIDITY";
//
//    /**
//     * Constructor. Initializes member variables and configures the purchase system.
//     *
//     * @param context        The context.
//     * @param contentBrowser The content browser.
//     */
//    public PurchaseHelper(Context context, ContentBrowser contentBrowser) {
//
//        this.mContentBrowser = contentBrowser;
//        this.mContext = context;
//        this.mActionsMap = new HashMap<>();
//
//        //If Iap is disabled, do not proceed with initializing purchase system
//        if (!contentBrowser.isIapDisabled()) {
//            initializePurchaseSystem();
//        }
//    }
//
//    /**
//     * Method to initialize purchase system
//     */
//    private void initializePurchaseSystem() {
//
//        // The purchase system should be initialized by the module initializer, if there is no
//        // initializer available that means the purchase system is not needed.
//        IPurchase purchaseSystem = (IPurchase) ModuleManager.getInstance()
//                                                            .getModule(
//                                                                    IPurchase.class.getSimpleName())
//                                                            .getImpl(true);
//        if (purchaseSystem == null) {
//            Log.i(TAG, "Purchase system not registered.");
//            return;
//        }
//
//        try {
//            registerSkuForActions();
//        }
//        catch (Exception e) {
//            Log.e(TAG, "Could not register actions!!", e);
//        }
//
//        // Register the purchase system received via ModuleManager and configure the purchase
//        // listener.
//        this.mPurchaseManager = PurchaseManager.getInstance(mContext.getApplicationContext());
//        try {
//            List<Map<String, String>> skuSet = purchaseUtils.readSkusFromConfigFile(mContext);
//            mPurchaseManager.init(purchaseSystem, new PurchaseManagerListener() {
//                @Override
//                public void onRegisterSkusResponse(Response response) {
//
//                    if (response == null || !Response.Status.SUCCESSFUL.equals(response.getStatus
//                            ())) {
//                        Log.e(TAG, "Register products failed " + response);
//                    }
//                    else {
//                        // If there is a valid receipt available in the system, set content browser
//                        // variable as true.
//                        String sku = mPurchaseManager.getPurchasedSku();
//                        if (sku == null) {
//                            setSubscription(false, null);
//                        }
//                        else {
//                            setSubscription(true, sku);
//                        }
//                        Log.d(TAG, "Register products complete.");
//                    }
//                    mContentBrowser.updateContentActions();
//                }
//
//                @Override
//                public void onValidPurchaseResponse(Response response, boolean validity,
//                                                    String sku) {
//
//                    Log.e(TAG, "You should not hit here!!!");
//                }
//            }, skuSet);
//        }
//        catch (Exception e) {
//            Log.e(TAG, "Could not configure the purchase system. ", e);
//        }
//    }
//
//    /**
//     * Reads the SKUs from the configuration file and registers them in the system.
//     */
//    private void registerSkuForActions() throws IOException {
//
//        Recipe recipe = Recipe.newInstance(
//                FileHelper.readFile(mContext, mContext.getString(R.string.skus_file)));
//        Map<String, String> actions = (Map<String, String>) recipe.getMap().get(ACTIONS);
//        for (String key : actions.keySet()) {
//            mActionsMap.put(key, actions.get(key));
//        }
//        Log.d(TAG, "actions registered " + mActionsMap);
//        mSubscriptionSKU = mActionsMap.get("CONTENT_ACTION_SUBSCRIPTION");
//        mDailyPassSKU = mActionsMap.get("CONTENT_ACTION_DAILY_PASS");
//    }
//
//
//    /**
//     * Sets the subscription data in Preferences.
//     */
//    private void setSubscription(boolean subscribe, String sku) {
//
//        mContentBrowser.setSubscribed(subscribe);
//        //Send purchase status update event
//        mEventBus.post(new PurchaseUpdateEvent(subscribe));
//        Preferences.setBoolean(PurchaseHelper.CONFIG_PURCHASE_VERIFIED, subscribe);
//        if (sku != null) {
//            Preferences.setString(PurchaseHelper.CONFIG_PURCHASED_SKU, sku);
//        }
//    }
//
//    /**
//     * Handle success case of subscriber.
//     *
//     * @param subscriber Subscriber.
//     * @param extras     Result bundle.
//     */
//    private void handleSuccessCase(Subscriber subscriber, Bundle extras) {
//
//        extras.putBoolean(RESULT, true);
//        if (!subscriber.isUnsubscribed()) {
//            subscriber.onNext(extras);
//        }
//        subscriber.onCompleted();
//    }
//
//    /**
//     * Handle failure case of subscriber.
//     *
//     * @param subscriber Subscriber.
//     * @param extras     Result bundle.
//     */
//    private void handleFailureCase(Subscriber subscriber, Bundle extras) {
//
//        extras.putBoolean(RESULT, false);
//        if (!subscriber.isUnsubscribed()) {
//            subscriber.onNext(extras);
//        }
//        subscriber.onCompleted();
//    }
//
//    /**
//     * Handles the response for a valid purchase.
//     *
//     * @param subscriber Rx subscriber.
//     * @param response   IAP response.
//     * @param validity   Validity of the SKU.
//     * @param sku        SKU name.
//     */
//    private void handleOnValidPurchaseResponse(Subscriber subscriber,
//                                               Response response,
//                                               boolean validity,
//                                               String sku) {
//
//        Bundle resultBundle = new Bundle();
//
//        if (response != null && Response.Status.SUCCESSFUL.equals(response.getStatus())) {
//            Log.d(TAG, "purchase succeeded " + response);
//            setSubscription(validity, sku);
//            resultBundle.putString(RESULT_SKU, sku);
//            resultBundle.putBoolean(RESULT_VALIDITY, validity);
//            AnalyticsHelper.trackPurchaseResult(sku, validity);
//            handleSuccessCase(subscriber, resultBundle);
//        }
//        else {
//            AnalyticsHelper.trackError(TAG, "Purchase failed "+response);
//            resultBundle.putBoolean(RESULT_VALIDITY, false);
//            handleFailureCase(subscriber, resultBundle);
//        }
//    }
//
//    /**
//     * Create observable purchase manager listener.
//     *
//     * @param subscriber Rx subscriber.
//     * @return Purchase manager listener instance.
//     */
//    private PurchaseManagerListener createObservablePurchaseManagerListener(Subscriber subscriber) {
//
//        return new PurchaseManagerListener() {
//            @Override
//            public void onRegisterSkusResponse(Response response) {
//
//                Log.e(TAG, "You should not hit here!!!");
//            }
//
//            @Override
//            public void onValidPurchaseResponse(Response response,
//                                                boolean validity,
//                                                String sku) {
//
//                handleOnValidPurchaseResponse(subscriber,
//                                              response,
//                                              validity,
//                                              sku);
//            }
//        };
//    }
//
//    /**
//     * Purchase SKU observable.
//     *
//     * @param sku SKU name.
//     * @return Purchase SKU observable result.
//     */
//    public Observable<Bundle> purchaseSkuObservable(String sku) {
//
//        Log.v(TAG, "purchaseSku called:" + sku);
//
//        return Observable.create(subscriber -> mPurchaseManager
//                                  .purchaseSku(sku,
//                                               createObservablePurchaseManagerListener(
//                                                       subscriber))
//        );
//    }
//
//    /**
//     * Is purchase valid observable.
//     *
//     * @param purchasedSku Purchased sku name.
//     * @return Purchase valid observable result.
//     */
//    public Observable<Bundle> isPurchaseValidObservable(String purchasedSku) {
//
//        Log.v(TAG, "isPurchaseValid called:" + purchasedSku);
//
//        return Observable.create(subscriber -> mPurchaseManager
//                                  .isPurchaseValid(purchasedSku,
//                                                   createObservablePurchaseManagerListener(
//                                                           subscriber))
//        );
//    }
//
//    /**
//     * Is subscription valid observable.
//     *
//     * @return Subscription valid observable result.
//     */
//    public Observable<Bundle> isSubscriptionValidObservable() {
//
//        return isPurchaseValidObservable(mSubscriptionSKU)
//                .concatMap(resultBundle -> {
//                    if (resultBundle.getBoolean(RESULT) &&
//                            !resultBundle.getBoolean(RESULT_VALIDITY)) {
//                        return isPurchaseValidObservable(mDailyPassSKU);
//                    }
//                    else {
//                        return Observable.just(resultBundle);
//                    }
//                });
//    }
//
//    /**
//     * Handle purchase chain.
//     *
//     * @param activity Activity.
//     * @param sku      Sku name.
//     */
//    private void handlePurchaseChain(Activity activity, String sku) {
//
//        purchaseSkuObservable(sku)
//                .subscribeOn(Schedulers.newThread()) //this needs to be first make sure
//                .observeOn(AndroidSchedulers.mainThread()) //this needs to be last to
//                        // make sure rest is running on separate thread.
//                .subscribe(resultBundle -> {
//                    Log.e(TAG, "isPurchaseValid subscribe called");
//                    mContentBrowser.updateContentActions();
//                    EventBus.getDefault().post(new ProgressOverlayDismissEvent(true));
//                }, throwable -> {
//                    EventBus.getDefault().post(new ProgressOverlayDismissEvent(true));
//
//                    ErrorHelper.injectErrorFragment(activity, ErrorUtils.ERROR_CATEGORY
//                            .NETWORK_ERROR, (errorDialogFragment, errorButtonType, errorCategory)
//                                                            -> {
//                        errorDialogFragment.dismiss();
//                    });
//                });
//    }
//
//    /**
//     * Handles the action corresponding to the purchase buttons.
//     *
//     * @param activity Activity instance that triggered the action.
//     * @param content  The content triggered by the action.
//     * @param actionId The id of the action.
//     */
//    public void handleAction(Activity activity, Content content, int actionId) {
//
//        triggerProgress(activity);
//        if (actionId == ContentBrowser.CONTENT_ACTION_DAILY_PASS) {
//            handlePurchaseChain(activity, mDailyPassSKU);
//        }
//        else if (actionId == ContentBrowser.CONTENT_ACTION_SUBSCRIPTION) {
//            handlePurchaseChain(activity, mSubscriptionSKU);
//        }
//    }
//
//    /**
//     * Triggers the progress fragment.
//     *
//     * @param activity Activity instance.
//     */
//    private void triggerProgress(Activity activity) {
//
//        ProgressDialogFragment.createAndShow(activity, mContext.getString(R.string.loading));
//    }
//}