package com.stingray.qello.firetv.inapppurchase.sku;

public class SkuUIData {
    private String recurrence;
    private String recurrenceTitle;
    private String price;
    private String originalPrice;
    private String commentView;
    private String savingsTitle;
    private String savingsPercentage;

    public SkuUIData(String price, String commentView) {
        this.recurrence = "MONTHLY";
        this.price = price;
        this.commentView = commentView;
    }
    public SkuUIData(String price, String originalPrice, String commentView, String savingsTitle, String savingsPercentage) {
        this.recurrence = "YEARLY";
        this.price = price;
        this.originalPrice = originalPrice;
        this.commentView = commentView;
        this.savingsTitle = savingsTitle;
        this.savingsPercentage = savingsPercentage;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public String getPrice() {
        return price;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public String getCommentView() {
        return commentView;
    }

    public String getSavingsTitle() {
        return savingsTitle;
    }

    public String getSavingsPercentage() {
        return savingsPercentage;
    }
}
