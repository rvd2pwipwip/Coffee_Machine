package com.stingray.qello.firetv.inapppurchase.communication;

public class SvodSubscription {
    private String productId;
    private Boolean freeTrialAvailable;
    private String recurrence;
    private String recurrenceTitle;
    private Boolean consumable;

    // For Deserialization
    private SvodSubscription() {

    }

    public SvodSubscription(String productId, Boolean freeTrialAvailable, String recurrence, String recurrenceTitle, Boolean consumable) {
        this.productId = productId;
        this.freeTrialAvailable = freeTrialAvailable;
        this.recurrence = recurrence;
        this.recurrenceTitle = recurrenceTitle;
        this.consumable = consumable;
    }

    public String getProductId() {
        return productId;
    }

    public Boolean getFreeTrialAvailable() {
        return freeTrialAvailable;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public String getRecurrenceTitle() {
        return recurrenceTitle;
    }

    public Boolean getConsumable() {
        return consumable;
    }
}
