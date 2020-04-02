package com.stingray.qello.firetv.inapppurchase.communication;

public class SvodSubscription {
    private final String productId;
    private final Boolean freeTrialAvailable;
    private final String recurrence;
    private final String recurrenceTitle;
    private final Boolean consumable;

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
