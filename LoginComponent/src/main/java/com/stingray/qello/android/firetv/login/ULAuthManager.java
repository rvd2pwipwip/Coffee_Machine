package com.stingray.qello.android.firetv.login;

import android.os.Bundle;

import com.amazon.identity.auth.device.AuthError;
import com.amazon.identity.auth.device.authorization.api.AuthorizationListener;
import com.amazon.identity.auth.device.authorization.api.AuthzConstants;
import com.amazon.identity.auth.device.shared.APIListener;
import com.stingray.qello.android.firetv.login.communication.IssueCodeCallable;
import com.stingray.qello.android.firetv.login.communication.SvodUserInfoCallable;
import com.stingray.qello.android.firetv.login.communication.TokenCallable;
import com.stingray.qello.android.firetv.login.communication.requestmodel.IssueCodeRequestBody;
import com.stingray.qello.android.firetv.login.communication.requestmodel.IssueCodeResponse;
import com.stingray.qello.android.firetv.login.communication.requestmodel.TokenRequestBody;
import com.stingray.qello.android.firetv.login.communication.requestmodel.TokenResponse;
import com.stingray.qello.firetv.android.model.svod.SvodUserInfo;

import java.io.IOException;

public class ULAuthManager {
    private static final String TAG = ULAuthManager.class.getName();

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
        TokenRequestBody tokenRequestBody = new TokenRequestBody(authorizationCode, "123", null);
        TokenResponse tokenResponse = new TokenCallable(tokenRequestBody).call();
        if (tokenResponse == null) {
            apiListener.onError(new AuthError("Failed to get access token", AuthError.ERROR_TYPE.ERROR_INVALID_GRANT));
        } else {
            try {
                SvodUserInfo userInfo = new SvodUserInfoCallable(tokenResponse.getAccessToken()).call();
                if (userInfo != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(AuthzConstants.BUNDLE_KEY.TOKEN.val, tokenResponse.getAccessToken());
                    bundle.putString("subscriptionPlan", userInfo.getSubscription().getPlan());
                    apiListener.onSuccess(bundle);
                } else {
                    apiListener.onError(new AuthError("Failed to get svod user info", AuthError.ERROR_TYPE.ERROR_INVALID_GRANT));
                }
            } catch (IOException e) {
                apiListener.onError(new AuthError("Failed to get svod user info", AuthError.ERROR_TYPE.ERROR_INVALID_GRANT));
            }
        }
    }
}
