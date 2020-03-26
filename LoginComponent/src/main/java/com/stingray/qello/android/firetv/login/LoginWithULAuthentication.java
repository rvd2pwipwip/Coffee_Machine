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
package com.stingray.qello.android.firetv.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.amazon.identity.auth.device.authorization.api.AuthzConstants;
import com.stingray.qello.android.firetv.login.activities.LoginActivity;
import com.stingray.qello.android.firetv.login.communication.SvodUserInfoCallable;
import com.stingray.qello.firetv.android.async.ObservableFactory;
import com.stingray.qello.firetv.android.ui.constants.PreferencesConstants;
import com.stingray.qello.firetv.android.utils.Preferences;
import com.stingray.qello.firetv.auth.AuthenticationConstants;
import com.stingray.qello.firetv.auth.IAuthentication;


/**
 * This class implements {@link IAuthentication} in respect to the Login with Amazon SDK.
 */
public class LoginWithULAuthentication implements IAuthentication {

    final static String IMPL_CREATOR_NAME = LoginWithULAuthentication.class.getSimpleName();
    private static final String TAG = LoginWithULAuthentication.class.getName();

    private Context mContext;
    private ULAuthManager ulAuthManager;
    private ObservableFactory observableFactory;

    /**
     * This method is used for configuration.
     *
     * @param context Context The application context.
     */
    @Override
    public void init(Context context) {
        mContext = context;
        ulAuthManager = new ULAuthManager();
        observableFactory = new ObservableFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthenticationCanBeDoneLater() {

        return false;
    }

    /**
     * This method is used to get the activity used for auth.
     *
     * @param context The context required to create the intent.
     * @return {@link LoginActivity}
     */
    @Override
    public Intent getAuthenticationActivityIntent(Context context) {

        return new Intent(context, LoginActivity.class);
    }

    /**
     * This method checks the status of the users Auth status.
     *
     * @param context         The context to check if user is logged in.
     * @param responseHandler The callback interface.
     */
    @Override
    public void isUserLoggedIn(Context context, final ResponseHandler responseHandler) {
        final Bundle errorBundle = new Bundle();
        populateErrorBundle(errorBundle, AuthenticationConstants.AUTHENTICATION_ERROR_CATEGORY);
        ResponseHandler logoutResponseHandler = new LogoutResponseHandler();

        String accessToken = Preferences.getString(PreferencesConstants.ACCESS_TOKEN);

        if (!accessToken.isEmpty()) {
            observableFactory.createDetached(new SvodUserInfoCallable(accessToken))
                    .doOnError(throwable -> {
                        logout(context, logoutResponseHandler);
                        // A fail means the user is not authenticated
                        responseHandler.onFailure(errorBundle);
                    })
                    .subscribe(userInfo -> {
                        if (userInfo.getSubscription() == null) {
                            logout(context, logoutResponseHandler);
                            // A fail means the user is not authenticated
                            responseHandler.onFailure(errorBundle);
                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putString(AuthzConstants.BUNDLE_KEY.TOKEN.val, accessToken);
                            bundle.putString("subscriptionPlan", userInfo.getSubscription().getPlan());
                            responseHandler.onSuccess(bundle);
                        }
                    });
        } else {
            responseHandler.onFailure(errorBundle);
        }
    }

    /**
     * This method does not apply to Login with Amazon.
     *
     * @param context         The context to check for authorization.
     * @param resourceId      The id of the resource to verify authorization.
     * @param responseHandler The callback interface.
     */
    @Override
    public void isResourceAuthorized(Context context, String resourceId, ResponseHandler responseHandler) {
        responseHandler.onSuccess(new Bundle());
    }

    /**
     * This method handles logout calls.
     * An AuthManager is needed to make the logout call.
     *
     * @param context         The context to logout the user.
     * @param responseHandler The callback interface.
     */
    @Override
    public void logout(Context context, final ResponseHandler responseHandler) {
        Preferences.setLoggedOutState();
        responseHandler.onSuccess(new Bundle());
    }

    /**
     * This concept does not exist with Login with Amazon, so we will leave it blank.
     */
    @Override
    public void cancelAllRequests() {

    }

    /**
     * Bundle to be sent on failures other than Authentication and Authorization
     *
     * @param errorCategory error category received
     * @param bundle        Bundle to populate
     */
    private void populateErrorBundle(Bundle bundle, String errorCategory) {

        Bundle errorBundle = new Bundle();
        errorBundle.putString(AuthenticationConstants.ERROR_CATEGORY, errorCategory);
        bundle.putBundle(AuthenticationConstants.ERROR_BUNDLE, errorBundle);
    }

     private static class LogoutResponseHandler implements ResponseHandler {
        @Override
        public void onSuccess(Bundle extras) {
            Log.i(TAG, "Logout Success");
            // Nothing to do
        }

        @Override
        public void onFailure(Bundle extras) {
            Log.i(TAG, "Logout Failed.");
            // Should never fail
        }
    };
}
