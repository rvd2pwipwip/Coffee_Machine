package com.stingray.qello.android.firetv.login;

import android.os.Bundle;
import android.util.Log;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.authorization.api.AuthorizationListener;
import com.amazon.identity.auth.device.authorization.api.AuthzConstants;
import com.amazon.identity.auth.device.shared.APIListener;
import com.stingray.qello.android.firetv.login.communication.IssueCodeCallable;
import com.stingray.qello.android.firetv.login.communication.SvodUserInfoCallable;
import com.stingray.qello.android.firetv.login.communication.requestmodel.IssueCodeRequestBody;
import com.stingray.qello.android.firetv.login.communication.requestmodel.IssueCodeResponse;
import com.stingray.qello.firetv.android.async.TokenCallable;
import com.stingray.qello.firetv.android.async.requestmodel.TokenRequestBody;
import com.stingray.qello.firetv.android.async.requestmodel.TokenResponse;
import com.stingray.qello.firetv.android.model.svod.SvodUserInfo;

import java.io.IOException;
import java.util.Date;

public class ULAuthManager {
    private static final String TAG = ULAuthManager.class.getName();
    final static String BUNDLE_SESSION_ID = "sessionId";
    final static String BUNDLE_ACCESS_TOKEN = AuthzConstants.BUNDLE_KEY.TOKEN.val;
    final static String BUNDLE_REFRESH_TOKEN = "refreshToken";
    final static String BUNDLE_ACCESS_TOKEN_EXPIRY_TIME_IN_MS = "access_token_expiry_time_in_ms";
    final static String BUNDLE_STINGRAY_EMAIL = "stingrayEmail";
    final static String BUNDLE_SUBSCRIPTION_PLAN = "subscriptionPlan";
    final static String BUNDLE_SUBSCRIPTION_END = "subscriptionEnd";
    final static String BUNDLE_USER_TRACKING_ID = "userTrackingId";

    public void authorize(String sessionId, String languageCode, String deviceId, AuthorizationListener authorizationListener) {
        IssueCodeRequestBody issueCodeRequestBody = new IssueCodeRequestBody(sessionId, deviceId);
        IssueCodeResponse issueCodeResponse = null;

        try {
            issueCodeResponse = new IssueCodeCallable(issueCodeRequestBody).call();
        } catch (Exception e) {
            Log.e(TAG, "Unexpected exception occured when attempting to retrieve the authorization code", e);
        }

        if (issueCodeResponse == null) {
            authorizationListener.onError(new AuthError("Failed to get authorization token", AuthError.ERROR_TYPE.ERROR_ACCESS_DENIED));
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(AuthzConstants.BUNDLE_KEY.AUTHORIZATION_CODE.val, issueCodeResponse.getCode());
            bundle.putString(BUNDLE_SESSION_ID, sessionId);

            authorizationListener.onSuccess(bundle);
        }
    }

    public void getToken(Bundle authorizeBundle, APIListener apiListener) {
        String authCode = authorizeBundle.getString(AuthzConstants.BUNDLE_KEY.AUTHORIZATION_CODE.val);
        String sessionId = authorizeBundle.getString(ULAuthManager.BUNDLE_SESSION_ID);

        TokenRequestBody tokenRequestBody = new TokenRequestBody(authCode);
        TokenResponse tokenResponse = new TokenCallable(tokenRequestBody).call();
        if (tokenResponse == null) {
            apiListener.onError(new AuthError("Failed to get access token", AuthError.ERROR_TYPE.ERROR_INVALID_GRANT));
        } else {
            Bundle bundle = addTokenResponse(new Bundle(), tokenResponse);
            bundle.putString(BUNDLE_SESSION_ID, sessionId);

            try {
                SvodUserInfo userInfo = new SvodUserInfoCallable(tokenResponse.getAccessToken()).call();
                if (userInfo != null) {
                    apiListener.onSuccess(addUserInfo(bundle, userInfo));
                } else {
                    apiListener.onError(new AuthError("Failed to get svod user info", AuthError.ERROR_TYPE.ERROR_INVALID_GRANT));
                }
            } catch (IOException e) {
                apiListener.onError(new AuthError("Failed to get svod user info", AuthError.ERROR_TYPE.ERROR_INVALID_GRANT));
            }
        }
    }

    private Bundle addTokenResponse(Bundle bundle, TokenResponse tokenResponse) {
        bundle.putString(BUNDLE_ACCESS_TOKEN, tokenResponse.getAccessToken());
        bundle.putString(BUNDLE_REFRESH_TOKEN, tokenResponse.getRefreshToken());

        long accessTokenExpiryTimeInMs = new Date().getTime() + tokenResponse.getExpiresInMS();
        bundle.putLong(BUNDLE_ACCESS_TOKEN_EXPIRY_TIME_IN_MS, accessTokenExpiryTimeInMs);

        return bundle;
    }

    private Bundle addUserInfo(Bundle bundle, SvodUserInfo userInfo) {
        bundle.putString(BUNDLE_STINGRAY_EMAIL, userInfo.getEmail());
        if (userInfo.getSubscription() != null) {
            bundle.putString(BUNDLE_SUBSCRIPTION_PLAN, userInfo.getSubscription().getPlan());
            bundle.putString(BUNDLE_SUBSCRIPTION_END, userInfo.getSubscription().getEndDate());
        }
        bundle.putString(BUNDLE_USER_TRACKING_ID, userInfo.getUniqueUserTrackingId());

        return bundle;
    }
}
