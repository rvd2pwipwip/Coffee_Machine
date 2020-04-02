package com.stingray.qello.firetv.inapppurchase.communication.requestmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostSubscriptionRequest {
    // Specific naming required because doesn't follow snake case convention
    @JsonProperty("purchaseData")
    private final String purchaseData;
    @JsonProperty("deviceId")
    private final String deviceId;

    public PostSubscriptionRequest(String purchaseData, String deviceId) {
        this.purchaseData = purchaseData;
        this.deviceId = deviceId;
    }

    public String getPurchaseData() {
        return purchaseData;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
