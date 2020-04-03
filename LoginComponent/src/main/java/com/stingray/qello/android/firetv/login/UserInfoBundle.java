package com.stingray.qello.android.firetv.login;

import android.os.Bundle;

public class UserInfoBundle {
    private final String stingrayEmail;
    private final String accessToken;
    private final String refreshToken;
    private final String expiresIn;
    private final String subscriptionPlan;
    private final String subscriptionEnd;
    private final String userTrackingId;

    public UserInfoBundle(Bundle bundle) {
        this.stingrayEmail = bundle.getString(ULAuthManager.BUNDLE_STINGRAY_EMAIL);
        this.accessToken = bundle.getString(ULAuthManager.BUNDLE_ACCESS_TOKEN);
        this.refreshToken = bundle.getString(ULAuthManager.BUNDLE_REFRESH_TOKEN);
        this.expiresIn = bundle.getString(ULAuthManager.BUNDLE_EXPIRES_IN);
        this.subscriptionPlan = bundle.getString(ULAuthManager.BUNDLE_SUBSCRIPTION_PLAN);
        this.subscriptionEnd = bundle.getString(ULAuthManager.BUNDLE_SUBSCRIPTION_END);
        this.userTrackingId = bundle.getString(ULAuthManager.BUNDLE_USER_TRACKING_ID);
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

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public String getSubscriptionEnd() {
        return subscriptionEnd;
    }

    public String getUserTrackingId() { return userTrackingId; }
}
