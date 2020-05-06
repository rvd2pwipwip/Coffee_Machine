package com.stingray.qello.android.firetv.login;

import android.os.Bundle;

public class UserInfoBundle {
    private final String stingrayEmail;
    private final String accessToken;
    private final String refreshToken;
    private final long accessTokenExpiryTimeInMs;
    private final String subscriptionPlan;
    private final String subscriptionEnd;
    private final String userTrackingId;
    private final String sessionId;

    public UserInfoBundle(Bundle bundle) {
        this.stingrayEmail = bundle.getString(ULAuthManager.BUNDLE_STINGRAY_EMAIL);
        this.accessToken = bundle.getString(ULAuthManager.BUNDLE_ACCESS_TOKEN);
        this.refreshToken = bundle.getString(ULAuthManager.BUNDLE_REFRESH_TOKEN);
        this.accessTokenExpiryTimeInMs = bundle.getLong(ULAuthManager.BUNDLE_ACCESS_TOKEN_EXPIRY_TIME_IN_MS);
        this.subscriptionPlan = bundle.getString(ULAuthManager.BUNDLE_SUBSCRIPTION_PLAN);
        this.subscriptionEnd = bundle.getString(ULAuthManager.BUNDLE_SUBSCRIPTION_END);
        this.userTrackingId = bundle.getString(ULAuthManager.BUNDLE_USER_TRACKING_ID);
        this.sessionId = bundle.getString(ULAuthManager.BUNDLE_SESSION_ID);
    }

    public String getStingrayEmail() {
        return stingrayEmail;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getAccessTokenExpiryTimeInMs() {
        return accessTokenExpiryTimeInMs;
    }

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public String getSubscriptionEnd() {
        return subscriptionEnd;
    }

    public String getUserTrackingId() { return userTrackingId; }

    public String getSessionId() {
        return sessionId;
    }
}
