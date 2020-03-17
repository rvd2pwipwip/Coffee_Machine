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
package com.stingray.qello.android.firetv.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.amazon.android.utils.Preferences;
import com.amazon.auth.AuthenticationConstants;
import com.amazon.auth.IAuthentication;
import com.stingray.qello.android.firetv.login.activities.LoginActivity;


/**
 * This class implements {@link IAuthentication} in respect to the Login with Amazon SDK.
 */
public class LoginWithULAuthentication implements IAuthentication {

    final static String IMPL_CREATOR_NAME = LoginWithULAuthentication.class.getSimpleName();
    private static final String TAG = LoginWithULAuthentication.class.getName();
    private static final String IS_LOGGED_IN = "isLoggedIn";

    private Context mContext;

    /**
     * This method is used for configuration.
     *
     * @param context Context The application context.
     */
    @Override
    public void init(Context context) {
        mContext = context;
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

        final Bundle bundle = new Bundle();


         populateErrorBundle(bundle, AuthenticationConstants
                                    .AUTHENTICATION_ERROR_CATEGORY);
                            responseHandler.onFailure(bundle);

        // responseHandler.onSuccess(bundle);

        //TODO Call UL

/*        // Use the auth manager to check if the user token is valid.
        mAuthManager.getToken(mScopes, new APIListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                // If the token is null then return false.
                if (bundle.get(mContext.getString(R.string.COM_AMAZON_IDENTITY_AUTH_DEVICE_AUTHORIZATION_TOKEN)) == null) {
                    populateErrorBundle(bundle, AuthenticationConstants
                            .AUTHENTICATION_ERROR_CATEGORY);
                    responseHandler.onFailure(bundle);
                }
                else {
                    responseHandler.onSuccess(bundle);

                }
            }

            @Override
            public void onError(AuthError authError) {
                // There is some other auth issue.
                populateErrorBundle(bundle, String.valueOf(authError.getCategory()));
                responseHandler.onFailure(bundle);
            }
        });*/
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

        final Bundle bundle = new Bundle();

        //TODO call UL
        Preferences.setBoolean(IS_LOGGED_IN, false);
        responseHandler.onSuccess(bundle);

        /*mAuthManager.clearAuthorizationState(new APIListener() {
            @Override
            public void onSuccess(Bundle results) {

                Preferences.setBoolean(IS_LOGGED_IN, false);
                responseHandler.onSuccess(bundle);
            }

            @Override
            public void onError(AuthError authError) {

                Log.e(TAG, "Error clearing authorization state.", authError);
                populateErrorBundle(bundle, String.valueOf(authError.getCategory()));
                responseHandler.onFailure(bundle);
            }
        });*/
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
        errorBundle.putString(
                AuthenticationConstants.ERROR_CATEGORY,
                errorCategory);
        bundle.putBundle(
                AuthenticationConstants.ERROR_BUNDLE, errorBundle);
    }
}
