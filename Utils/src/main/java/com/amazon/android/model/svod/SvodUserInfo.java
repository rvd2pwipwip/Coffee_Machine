package com.amazon.android.model.svod;

public class SvodUserInfo {
    private String uniqueUserTrackingId;
    private String email;
    private Subscription subscription;
    private String freeTrialAvailable;
    private String accountLink;

    public String getUniqueUserTrackingId() {
        return uniqueUserTrackingId;
    }

    public String getEmail() {
        return email;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public String getFreeTrialAvailable() {
        return freeTrialAvailable;
    }

    public String getAccountLink() {
        return accountLink;
    }
}
