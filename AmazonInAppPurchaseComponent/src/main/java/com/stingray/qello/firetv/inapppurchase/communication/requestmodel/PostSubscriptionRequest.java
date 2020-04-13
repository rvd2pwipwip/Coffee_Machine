package com.stingray.qello.firetv.inapppurchase.communication.requestmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PostSubscriptionRequest {
    private final PurchaseData purchaseData;
    private final String deviceId;

    public PostSubscriptionRequest(PurchaseData purchaseData, String deviceId) {
        this.purchaseData = purchaseData;
        this.deviceId = deviceId;
    }

    public PurchaseData getPurchaseData() {
        return purchaseData;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public static class PurchaseData {
        // Specific naming required because doesn't follow snake case convention
        @JsonProperty("userId")
        private String userId;
        @JsonProperty("receiptId")
        private String receiptId;

        public PurchaseData(String userId, String receiptId) {
            this.userId = userId;
            this.receiptId = receiptId;
        }

        public String getUserId() {
            return userId;
        }

        public String getReceiptId() {
            return receiptId;
        }
    }
}
