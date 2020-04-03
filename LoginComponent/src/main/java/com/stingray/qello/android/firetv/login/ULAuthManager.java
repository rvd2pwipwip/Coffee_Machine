package com.stingray.qello.android.firetv.login;

import android.os.Bundle;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.authorization.api.AuthorizationListener;
import com.amazon.identity.auth.device.authorization.api.AuthzConstants;
import com.amazon.identity.auth.device.shared.APIListener;
import com.stingray.qello.android.firetv.login.communication.IssueCodeCallable;
import com.stingray.qello.android.firetv.login.communication.SvodUserInfoCallable;
import com.stingray.qello.firetv.android.async.TokenCallable;
import com.stingray.qello.android.firetv.login.communication.requestmodel.IssueCodeRequestBody;
import com.stingray.qello.android.firetv.login.communication.requestmodel.IssueCodeResponse;
import com.stingray.qello.firetv.android.async.requestmodel.TokenRequestBody;
import com.stingray.qello.firetv.android.async.requestmodel.TokenResponse;
import com.stingray.qello.firetv.android.model.svod.SvodUserInfo;

import java.io.IOException;

public class ULAuthManager {
    private static final String TAG = ULAuthManager.class.getName();

    final static String BUNDLE_ACCESS_TOKEN = AuthzConstants.BUNDLE_KEY.TOKEN.val;
    final static String BUNDLE_REFRESH_TOKEN = "refreshToken";
    final static String BUNDLE_EXPIRES_IN = "expiresIn";
    final static String BUNDLE_STINGRAY_EMAIL = "stingrayEmail";
    final static String BUNDLE_SUBSCRIPTION_PLAN = "subscriptionPlan";
    final static String BUNDLE_SUBSCRIPTION_END = "subscriptionEnd";

    public void authorize(String sessionId, String languageCode, String deviceId, AuthorizationListener authorizationListener) {
        IssueCodeRequestBody issueCodeRequestBody = new IssueCodeRequestBody(sessionId, languageCode, deviceId);
        IssueCodeResponse issueCodeResponse = new IssueCodeCallable(issueCodeRequestBody).call();
        if (issueCodeResponse == null) {
            authorizationListener.onError(new AuthError("Failed to get authorization token", AuthError.ERROR_TYPE.ERROR_ACCESS_DENIED));
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(AuthzConstants.BUNDLE_KEY.AUTHORIZATION_CODE.val, issueCodeResponse.getCode());

            authorizationListener.onSuccess(bundle);
        }
    }

    public void getToken(String authorizationCode, APIListener apiListener) {
        TokenRequestBody tokenRequestBody = new TokenRequestBody(authorizationCode);
        TokenResponse tokenResponse = new TokenCallable(tokenRequestBody).call();
        if (tokenResponse == null) {
            apiListener.onError(new AuthError("Failed to get access token", AuthError.ERROR_TYPE.ERROR_INVALID_GRANT));
        } else {
            Bundle bundle = addTokenResponse(new Bundle(), tokenResponse);
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
        bundle.putString(BUNDLE_EXPIRES_IN, tokenResponse.getExpiresIn());

        return bundle;
    }

    private Bundle addUserInfo(Bundle bundle, SvodUserInfo userInfo) {
        bundle.putString(BUNDLE_STINGRAY_EMAIL, userInfo.getEmail());
        if (userInfo.getSubscription() != null) {
            bundle.putString(BUNDLE_SUBSCRIPTION_PLAN, userInfo.getSubscription().getPlan());
            bundle.putString(BUNDLE_SUBSCRIPTION_END, userInfo.getSubscription().getEndDate());
        }

        return bundle;
    }
}
