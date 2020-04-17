package com.stingray.qello.firetv.inapppurchase.communication.requestmodel;

public class SvodSubscription {
    private String productId;
    private Boolean freeTrialAvailable;
    private String recurrence;
    private String recurrenceTitle;
    private Boolean consumable;
    private String price;
    private String currencySymbol;

    // For Deserialization
    private SvodSubscription() {

    }

    public SvodSubscription(String productId, Boolean freeTrialAvailable, String recurrence, String recurrenceTitle, Boolean consumable, String price, String currencySymbol) {
        this.productId = productId;
        this.freeTrialAvailable = freeTrialAvailable;
        this.recurrence = recurrence;
        this.recurrenceTitle = recurrenceTitle;
        this.consumable = consumable;
        this.price = price;
        this.currencySymbol = currencySymbol;
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

    public String getPrice() {
        return price;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public enum Recurrence {
        MONTHLY, YEARLY
    }
}
